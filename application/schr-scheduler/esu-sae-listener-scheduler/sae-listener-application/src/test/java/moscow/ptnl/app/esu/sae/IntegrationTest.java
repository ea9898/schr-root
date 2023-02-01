package moscow.ptnl.app.esu.sae;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import moscow.ptnl.app.config.ESConfiguration;
import moscow.ptnl.app.config.PersistenceConstraint;
import moscow.ptnl.app.domain.model.es.IndexEsuInput;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.esu.sae.listener.config.AsyncConfiguration;
import moscow.ptnl.app.esu.sae.listener.config.RestConfiguration;
import moscow.ptnl.app.esu.sae.listener.task.SchoolAttachmentEventProcessTask;
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.repository.EsuInputCRUDRepository;
import moscow.ptnl.app.repository.es.IndexEsuInputRepository;
import moscow.ptnl.domain.entity.esu.EsuInput;
import moscow.ptnl.domain.entity.esu.EsuStatusType;
import moscow.ptnl.domain.service.SettingService;
import moscow.ptnl.schr.repository.SettingsCRUDRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { PersistenceConfiguration.class, MockConfiguration.class, RestConfiguration.class,
        AsyncConfiguration.class, ESConfiguration.class })
@Transactional
public class IntegrationTest {

    @PersistenceContext(name = PersistenceConstraint.PU_NAME)
    EntityManager entityManager;

    @Autowired
    SchoolAttachmentEventProcessTask schoolAttachmentEventProcessTask;

    @Autowired
    EsuInputCRUDRepository esuInputCRUDRepository;

    @Autowired
    ExecutorService executor;

    @SpyBean
    SettingService settingService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SettingsCRUDRepository settingsCRUDRepository;

    @Autowired
    IndexEsuInputRepository indexEsuInputRepository;

    @Autowired
    StudentPatientDataRepository studentPatientDataRepository;

    @Autowired
    TransactionTemplate transactions;

    @BeforeAll
    public static void init(@Qualifier(PersistenceConstraint.DS_BEAN_NAME) DataSource dataSource) throws LiquibaseException, SQLException {
        //Setting up Database
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
        Liquibase liquibase = new Liquibase("changelog/master.xml",
                new FileSystemResourceAccessor("../../../database"), database);
        liquibase.update("");
    }

    @Test // Записи найдены (studInfo.attachId = $.entityData[*].attributes[?(@.name=="attachId")].value.value)
    public void test1() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        String studentPatientId = buildTestData("json/studentPatientData.json", 5); // build test data

        //event 1 (найдена запись по attachId, isActual == true, получена новая информация по operationDate)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/schoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"124922913\"", "\"value\": \"69762569\""));
        }
        //event 2 (найдена запись по attachId, isActual == true, получена старая информация по operationDate)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/schoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"124922913\"", "\"value\": \"69762569\"")
                    .replace("\"operationDate\": \"2022-06-23T18:47:30.868+03:00\",", "\"operationDate\": \"2022-01-23T18:47:30.868+03:00\","));
        }
        entityManager.flush();

        try {
            executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                if (t.getId() == 1L) {
                    Assertions.assertNull(t.getError(), "id=" + t.getId());
                } else if (t.getId() == 2L) {
                    Assertions.assertEquals("SCHR_107 - Получена более старая информация, чем содержится в индексе", t.getError());
                }
            });
            List<StudentPatientData> studentPatientDataList = StreamSupport.stream(studentPatientDataRepository.findAll().spliterator(), false)
                    .filter(t -> Objects.equals(t.getPatientInfo().getPatientId(), 3987621809L))
                    .collect(Collectors.toList());
            Assertions.assertEquals(1, studentPatientDataList.size(), "Too many patients with Id=3987621809");
            StudentPatientData patientData = studentPatientDataList.get(0);
            Assertions.assertEquals(1, patientData.getStudInfo().size());
            Assertions.assertEquals(69762569, patientData.getStudInfo().get(0).getAttachId());
            Assertions.assertEquals(17411929190L, patientData.getStudInfo().get(0).getAreaId()); // тот areaId, который прилетел в сообщении
        } finally {
            clearElastic(studentPatientId);
        }
    }

    @Test // Записи найдены (studInfo.attachId = $.entityData[*].attributes[?(@.name=="attachId")].value.value)
    public void test2() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        String studentPatientId = buildTestData("json/studentPatientData2.json", 5); //build test data

        //event 1 (найдена запись по attachId, isActual == false, после удаления блока, остались другие блоки StudInfo в StudentPatientData)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/schoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"124922913\"", "\"value\": \"69762569\"")
                    .replace("\"value\": \"true\"", "\"value\": \"false\""));
        }
        entityManager.flush();

        try {
            executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertNull(t.getError(), "id=" + t.getId());
            });
            List<StudentPatientData> studentPatientDataList = StreamSupport.stream(studentPatientDataRepository.findAll().spliterator(), false)
                    .filter(t -> Objects.equals(t.getPatientInfo().getPatientId(), 3987621809L))
                    .collect(Collectors.toList());
            Assertions.assertEquals(1, studentPatientDataList.size(), "Too many patients with Id=3987621809");
            StudentPatientData patientData = studentPatientDataList.get(0);
            Assertions.assertEquals(1, patientData.getStudInfo().size());
            Assertions.assertEquals(123456789, patientData.getStudInfo().get(0).getAttachId());
        } finally {
            clearElastic(studentPatientId);
        }
    }

    @Test // Записи найдены (studInfo.attachId = $.entityData[*].attributes[?(@.name=="attachId")].value.value)
    public void test3() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        // Возраст пациента меньше, чем schoolAttachmentEventHandlerService.ageMax
        String studentPatientId = buildTestData("json/studentPatientData.json", 5); //build test data

        //event 1 (найдена запись по attachId, isActual == false, после удаления блока, не осталось блоков StudInfo в StudentPatientData)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/schoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"124922913\"", "\"value\": \"69762569\"")
                    .replace("\"value\": \"true\"", "\"value\": \"false\""));
        }
        entityManager.flush();

        try {
            executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertNull(t.getError(), "id=" + t.getId());
            });
            List<StudentPatientData> studentPatientDataList = StreamSupport.stream(studentPatientDataRepository.findAll().spliterator(), false)
                    .filter(t -> Objects.equals(t.getPatientInfo().getPatientId(), 3987621809L))
                    .collect(Collectors.toList());
            Assertions.assertEquals(1, studentPatientDataList.size(), "Too many patients with Id=3987621809");
            StudentPatientData patientData = studentPatientDataList.get(0);
            Assertions.assertNull(patientData.getStudInfo());
        } finally {
            clearElastic(studentPatientId);
        }
    }


    @Test // Записи найдены (studInfo.attachId = $.entityData[*].attributes[?(@.name=="attachId")].value.value)
    public void test4() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        // Возраст пациента больше, чем schoolAttachmentEventHandlerService.ageMax
        String studentPatientId = buildTestData("json/studentPatientData.json", 25); //build test data

        //event 1 (найдена запись по attachId, isActual == false, после удаления блока, не осталось блоков StudInfo в StudentPatientData)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/schoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"124922913\"", "\"value\": \"69762569\"")
                    .replace("\"value\": \"true\"", "\"value\": \"false\""));
        }
        entityManager.flush();

        try {
            executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                if (t.getId() == 1L) {
                    Assertions.assertNull(t.getError(), "id=" + t.getId());
                } else if (t.getId() == 2L) {
                    Assertions.assertEquals("SCHR_107 - Получена более старая информация, чем содержится в индексе", t.getError());
                }
            });
            List<StudentPatientData> studentPatientDataList = StreamSupport.stream(studentPatientDataRepository.findAll().spliterator(), false)
                    .filter(t -> Objects.equals(t.getPatientInfo().getPatientId(), 3987621809L))
                    .collect(Collectors.toList());
            Assertions.assertEquals(0, studentPatientDataList.size(), "Too many patients with Id=3987621809");
        } finally {
            clearElastic(studentPatientId);
        }
    }

    @Test // Ошибка SCHR_106
    public void test5() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        // Возраст пациента меньше, чем schoolAttachmentEventHandlerService.ageMax
        String studentPatientId = buildTestData("json/studentPatientData.json", 5); //build test data

        //event 1 (isActual == false) сперва выполнение этого event, чтобы в индекс не добавился новый блок StudInfo
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/schoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"true\"", "\"value\": \"false\""));
        }
        //event 2 (isActual == true)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/schoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n")).replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ","));
        }
        entityManager.flush();

        try {
            executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                if (t.getId() == 1L) {
                    Assertions.assertEquals("SCHR_106 - Запись, которую необходимо удалить, не найдена", t.getError());
                } else if (t.getId() == 2L) {
                    Assertions.assertNull(t.getError(), "id=" + t.getId());
                }
            });
            List<StudentPatientData> studentPatientDataList = StreamSupport.stream(studentPatientDataRepository.findAll().spliterator(), false)
                    .filter(t -> Objects.equals(t.getPatientInfo().getPatientId(), 3987621809L))
                    .collect(Collectors.toList());
            Assertions.assertEquals(1, studentPatientDataList.size(), "Too many patients with Id=3987621809");
            StudentPatientData patientData = studentPatientDataList.get(0);
            Assertions.assertEquals(2, patientData.getStudInfo().size());
            Assertions.assertEquals(69762569, patientData.getStudInfo().get(0).getAttachId()); // тот attachId, что уже был в индексе
            Assertions.assertEquals(124922913, patientData.getStudInfo().get(1).getAttachId()); // тот attachId, который прилетел в сообщении
        } finally {
            clearElastic(studentPatientId);
        }
    }

    @Test // Ошибка SCHR_106
    public void test6() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        // Возраст пациента больше, чем schoolAttachmentEventHandlerService.ageMax
        String studentPatientId = buildTestData("json/studentPatientData.json", 25); //build test data

        //event 1 (isActual == false)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/schoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"true\"", "\"value\": \"false\""));
        }
        entityManager.flush();

        try {
            executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertEquals("SCHR_106 - Запись, которую необходимо удалить, не найдена", t.getError());
            });
            List<StudentPatientData> studentPatientDataList = StreamSupport.stream(studentPatientDataRepository.findAll().spliterator(), false)
                    .filter(t -> Objects.equals(t.getPatientInfo().getPatientId(), 3987621809L))
                    .collect(Collectors.toList());
            // Запись должны быть удалена из индекса, поскольку isActual == false, а возраст пациента больше ageMax
            Assertions.assertEquals(0, studentPatientDataList.size(), "Too many patients with Id=3987621809");
        } finally {
            clearElastic(studentPatientId);
        }
    }

    private void clearEsuInputs() {
        // Чтобы действия тестов не влияли друг на друга
        esuInputCRUDRepository.deleteAll();
    }

    private void clearElastic(String patientId) {
        // Т.к. используем реальный сервис Elastic, нужно удалить созданные данные
        List<EsuInput> esuInputs = StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        indexEsuInputRepository.deleteAllById(esuInputs.stream().map(EsuInput::getEsId).collect(Collectors.toList()));
        studentPatientDataRepository.deleteById(patientId);
    }

    private String buildTestData(String file, long years) throws IOException {
        String studentPatientId;
        // Создаём документ в индексе student_patient_registry_alias из данных в studentPatientData.json
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream(file)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StudentPatientData studentPatientData = objectMapper.readValue(reader, StudentPatientData.class);
            // Это тот patientId по которому, в алгоритме будет осуществляться поиск в индексе student_patient_registry_alias
            studentPatientId = studentPatientData.getPatientInfo().getPatientId().toString();
            studentPatientData.setId(studentPatientId);
            studentPatientData.getPatientInfo().setBirthDate(LocalDate.now().minusYears(years)); // для проверки возраста в алгоритме
            Objects.requireNonNull(transactions.execute((s) -> studentPatientDataRepository.save(studentPatientData)));
        }
        return studentPatientId;
    }

    private void buildMessage(String text) throws ExecutionException, InterruptedException {
        final IndexEsuInput indexEsuInput = new IndexEsuInput(10L,
                LocalDateTime.now(),"1", TopicType.SCHOOL_ATTACHMENT_EVENT.getName(), text);
        EsuInput testInput = new EsuInput();
        testInput.setStatus(EsuStatusType.NEW);
        testInput.setDateCreated(LocalDateTime.now());
        testInput.setDateUpdated(LocalDateTime.now());
        testInput.setTopic(TopicType.SCHOOL_ATTACHMENT_EVENT.getName());
        String esId = executor.submit(() -> indexEsuInputRepository.save(indexEsuInput)).get().getId();
        testInput.setEsId(esId);
        executor.submit(() -> esuInputCRUDRepository.save(testInput)).get();
    }
}
