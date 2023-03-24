package moscow.ptnl.app.esu.sae;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import moscow.ptnl.app.config.ESConfiguration;
import moscow.ptnl.app.config.PersistenceConstraint;
import moscow.ptnl.app.domain.model.es.AnthropometryInfo;
import moscow.ptnl.app.domain.model.es.Immunodiagnostic;
import moscow.ptnl.app.domain.model.es.Immunodiagnostics;
import moscow.ptnl.app.domain.model.es.IndexEsuInput;
import moscow.ptnl.app.domain.model.es.PatientInfo;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.esu.sae.configuration.MockConfiguration;
import moscow.ptnl.app.esu.sae.configuration.PersistenceConfiguration;
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
import ru.mos.emias.anthropometry.anthropometryservice.v1.AnthropometryServicePortType;
import ru.mos.emias.anthropometry.anthropometryservice.v1.types.AnthropometryDataResultPage;
import ru.mos.emias.anthropometry.anthropometryservice.v1.types.AnthropometryItem;
import ru.mos.emias.anthropometry.anthropometryservice.v1.types.GetAnthropometryDataByPatientResponse;
import ru.mos.emias.consentinfo.consentinfoservice.v2.ConsentInfoServicePortType;
import ru.mos.emias.consentinfo.consentinfoservice.v2.types.FindConsentInfoResponse;
import ru.mos.emias.consentinfo.core.v2.CancelReason;
import ru.mos.emias.consentinfo.core.v2.ConsentForm;
import ru.mos.emias.consentinfo.core.v2.ConsentInfo;
import ru.mos.emias.consentinfo.core.v2.ConsentList;
import ru.mos.emias.consentinfo.core.v2.ConsentType;
import ru.mos.emias.consentinfo.core.v2.ImmunoDrugsTn;
import ru.mos.emias.consentinfo.core.v2.ImmunoKind;
import ru.mos.emias.consentinfo.core.v2.Infection;
import ru.mos.emias.consentinfo.core.v2.KindItem;
import ru.mos.emias.erp.patientservice.v2.ErpPatientPortTypeV2;
import ru.mos.emias.erp.patientservice.v2.Fault;
import ru.mos.emias.erp.patientservice.v2.types.Attachment;
import ru.mos.emias.erp.patientservice.v2.types.CodeWithName;
import ru.mos.emias.erp.patientservice.v2.types.GetPatientResponse;
import ru.mos.emias.erp.patientservice.v2.types.Patient;
import ru.mos.emias.erp.patientservice.v2.types.PersonalData;
import ru.mos.emias.erp.patientservice.v2.types.Policy;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.Rollback;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {PersistenceConfiguration.class, MockConfiguration.class, RestConfiguration.class,
        AsyncConfiguration.class, ESConfiguration.class})
@Transactional
@Rollback
public class IntegrationTest {

    @PersistenceContext(name = PersistenceConstraint.PU_NAME)
    private EntityManager entityManager;

    @Autowired
    private SchoolAttachmentEventProcessTask schoolAttachmentEventProcessTask;

    @Autowired
    private EsuInputCRUDRepository esuInputCRUDRepository;

    @Autowired
    private ExecutorService executor;

    @SpyBean
    private SettingService settingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IndexEsuInputRepository indexEsuInputRepository;

    @Autowired
    private StudentPatientDataRepository studentPatientDataRepository;

    @Autowired
    private TransactionTemplate transactions;

    @MockBean
    private ErpPatientPortTypeV2 erpPatientPortTypeV2;

    @MockBean
    private AnthropometryServicePortType anthropometryServicePortType;

    @MockBean
    private ConsentInfoServicePortType consentInfoServicePortType;

    private final String ukl = "test-ukl";
    private final String lastName = "test-lastName";
    private final String firstName = "test-firstName";
    private final String patronymic = "test-middleName";
    private final String birthDate = "2018-11-26";
    private final String genderCode = "MALE";

    private final String policyNumber = "test-policyNumber";
    private final String policyChangeDate = "2022-02-25T00:00:00.000000";
    private final String policyStatus = "test-policyStatus";
    private final String policyOMSType = "test-policyOMSType";

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

        //event (найдена запись по attachId, isActual == true, получена новая информация по operationDate)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/SchoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"124922913\"", "\"value\": \"69762569\""));
        }
        entityManager.flush();

        try {
            Future<?> future = executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            future.cancel(true);
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertNull(t.getError(), "id=" + t.getId());
            });
            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById("3987621809");
            Assertions.assertTrue(studentPatientDataOptional.isPresent(), "No patient with Id=3987621809");
            StudentPatientData patientData = studentPatientDataOptional.get();
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

        String studentPatientId = buildTestData("json/studentPatientData.json", 5); // build test data

        //event (найдена запись по attachId, isActual == true, получена старая информация по operationDate)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/SchoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"124922913\"", "\"value\": \"69762569\"")
                    .replace("\"operationDate\": \"2022-06-23T18:47:30.868+03:00\",", "\"operationDate\": \"2022-01-23T18:47:30.868+03:00\","));
        }
        entityManager.flush();

        try {
            Future<?> future = executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            future.cancel(true);
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertEquals("SCHR_107 - Получена более старая информация, чем содержится в индексе", t.getError());
            });
            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById("3987621809");
            Assertions.assertTrue(studentPatientDataOptional.isPresent(), "No patient with Id=3987621809");
            StudentPatientData patientData = studentPatientDataOptional.get();
            Assertions.assertEquals(1, patientData.getStudInfo().size());
            Assertions.assertEquals(69762569, patientData.getStudInfo().get(0).getAttachId());
        } finally {
            clearElastic(studentPatientId);
        }
    }

    @Test // Записи найдены (studInfo.attachId = $.entityData[*].attributes[?(@.name=="attachId")].value.value)
    public void test3() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        String studentPatientId = buildTestData("json/studentPatientData2.json", 5); //build test data

        //event (найдена запись по attachId, isActual == false, после удаления блока, остались другие блоки StudInfo в StudentPatientData)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/SchoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"124922913\"", "\"value\": \"69762569\"")
                    .replace("\"value\": \"true\"", "\"value\": \"false\""));
        }
        entityManager.flush();

        try {
            Future<?> future = executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            future.cancel(true);
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertNull(t.getError(), "id=" + t.getId());
            });
            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById("3987621809");
            Assertions.assertTrue(studentPatientDataOptional.isPresent(), "No patient with Id=3987621809");
            StudentPatientData patientData = studentPatientDataOptional.get();
            Assertions.assertEquals(1, patientData.getStudInfo().size());
            Assertions.assertEquals(123456789, patientData.getStudInfo().get(0).getAttachId());
        } finally {
            clearElastic(studentPatientId);
        }
    }

    @Test // Записи найдены (studInfo.attachId = $.entityData[*].attributes[?(@.name=="attachId")].value.value)
    public void test4() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        // Возраст пациента меньше, чем schoolAttachmentEventHandlerService.ageMax
        String studentPatientId = buildTestData("json/studentPatientData.json", 5); //build test data

        //event (найдена запись по attachId, isActual == false, после удаления блока, не осталось блоков StudInfo в StudentPatientData)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/SchoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"124922913\"", "\"value\": \"69762569\"")
                    .replace("\"value\": \"true\"", "\"value\": \"false\""));
        }
        entityManager.flush();

        try {
            Future<?> future = executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            future.cancel(true);
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertNull(t.getError(), "id=" + t.getId());
            });
            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById("3987621809");
            Assertions.assertTrue(studentPatientDataOptional.isPresent(), "No patient with Id=3987621809");
            StudentPatientData patientData = studentPatientDataOptional.get();
            Assertions.assertNull(patientData.getStudInfo());
        } finally {
            clearElastic(studentPatientId);
        }
    }

    @Test // Записи найдены (studInfo.attachId = $.entityData[*].attributes[?(@.name=="attachId")].value.value)
    public void test5() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        // Возраст пациента больше, чем schoolAttachmentEventHandlerService.ageMax
        String studentPatientId = buildTestData("json/studentPatientData.json", 25); //build test data

        //event (найдена запись по attachId, isActual == false, после удаления блока, не осталось блоков StudInfo в StudentPatientData)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/SchoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"124922913\"", "\"value\": \"69762569\"")
                    .replace("\"value\": \"true\"", "\"value\": \"false\""));
        }
        entityManager.flush();

        try {
            Future<?> future = executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            future.cancel(true);
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertNull(t.getError(), "id=" + t.getId());
            });
            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById("3987621809");
            Assertions.assertFalse(studentPatientDataOptional.isPresent());
        } finally {
            clearElastic(studentPatientId);
        }
    }

    @Test // Ошибка SCHR_106
    public void test6() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        // Возраст пациента меньше, чем schoolAttachmentEventHandlerService.ageMax
        String studentPatientId = buildTestData("json/studentPatientData.json", 5); //build test data

        //event (isActual == false) сперва выполнение этого event, чтобы в индекс не добавился новый блок StudInfo
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/SchoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"true\"", "\"value\": \"false\""));
        }
        entityManager.flush();

        try {
            Future<?> future = executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            future.cancel(true);
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertEquals("SCHR_106 - Запись, которую необходимо удалить, не найдена", t.getError());
            });
            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById("3987621809");
            Assertions.assertTrue(studentPatientDataOptional.isPresent(), "No patient with Id=3987621809");
            StudentPatientData patientData = studentPatientDataOptional.get();
            Assertions.assertEquals(1, patientData.getStudInfo().size());
            Assertions.assertEquals(69762569, patientData.getStudInfo().get(0).getAttachId()); // тот attachId, что уже был в индексе
        } finally {
            clearElastic(studentPatientId);
        }
    }

    @Test // Ошибка SCHR_106
    public void test7() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        // Возраст пациента меньше, чем schoolAttachmentEventHandlerService.ageMax
        String studentPatientId = buildTestData("json/studentPatientData.json", 5); //build test data

        //event (isActual == true)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/SchoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ","));
        }
        entityManager.flush();

        try {
            Future<?> future = executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            future.cancel(true);
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertNull(t.getError(), "id=" + t.getId());
            });
            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById("3987621809");
            Assertions.assertTrue(studentPatientDataOptional.isPresent(), "No patient with Id=3987621809");
            StudentPatientData patientData = studentPatientDataOptional.get();
            Assertions.assertEquals(2, patientData.getStudInfo().size());
            Assertions.assertEquals(69762569, patientData.getStudInfo().get(0).getAttachId()); // тот attachId, что уже был в индексе
            Assertions.assertEquals(124922913, patientData.getStudInfo().get(1).getAttachId()); // тот attachId, который прилетел в сообщении
        } finally {
            clearElastic(studentPatientId);
        }
    }

    @Test // Ошибка SCHR_106
    public void test8() throws ExecutionException, InterruptedException, IOException {
        clearEsuInputs();

        // Возраст пациента больше, чем schoolAttachmentEventHandlerService.ageMax
        String studentPatientId = buildTestData("json/studentPatientData.json", 25); //build test data

        //event (isActual == false)
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/SchoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"emiasId\": 18027361,", "\"emiasId\": " + studentPatientId + ",")
                    .replace("\"value\": \"true\"", "\"value\": \"false\""));
        }
        entityManager.flush();

        try {
            Future<?> future = executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            future.cancel(true);
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertEquals("SCHR_106 - Запись, которую необходимо удалить, не найдена", t.getError());
            });
            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById("3987621809");
            // Запись должны быть удалена из индекса, поскольку isActual == false, а возраст пациента больше ageMax
            Assertions.assertFalse(studentPatientDataOptional.isPresent());
        } finally {
            clearElastic(studentPatientId);
        }
    }

    @Test // Ошибка SCHR_108
    public void test9() throws Exception {
        Long patientId = 18027361L;

        clearEsuInputs();

        //event
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/SchoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n")));
        }
        entityManager.flush();

        Mockito.when(erpPatientPortTypeV2.getPatient(Mockito.any())).thenThrow(new Fault("Test Error"));

        try {
            Future<?> future = executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            future.cancel(true);
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertEquals("SCHR_108 - Ошибка при создании нового пациента Test Error", t.getError());
            });
        } finally {
            clearElastic(String.valueOf(patientId)); // patientId прилетевший в сообщении
        }
    }

    @Test // Документ НЕ найден в индексе student_patient_registry_alias, Система вызывает алгоритм A_SCHR_1
    public void test10() throws Exception {
        Long patientId = 18027361L;

        clearEsuInputs();

        //event
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/SchoolAttachmentEvent.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n")));
        }
        entityManager.flush();

        setPatientServiceResponse(patientId);
        setAnthropometryServiceResponse();
        setConsentInfoServiceResponse();

        try {
            Future<?> future = executor.submit(() -> Assertions.assertDoesNotThrow(() -> schoolAttachmentEventProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_12.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            future.cancel(true);
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
                Assertions.assertNull(t.getError(), "id=" + t.getId());
            });

            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById(String.valueOf(patientId));
            Assertions.assertTrue(studentPatientDataOptional.isPresent(), "No patient with Id=18027361");
            StudentPatientData patientData = studentPatientDataOptional.get();

            // Проверка patientInfo
            PatientInfo patientInfo = patientData.getPatientInfo();
            Assertions.assertEquals(patientId, patientInfo.getPatientId());
            Assertions.assertEquals(ukl, patientInfo.getUkl());
            Assertions.assertEquals(lastName, patientInfo.getLastName());
            Assertions.assertEquals(firstName, patientInfo.getFirstName());
            Assertions.assertEquals(patronymic, patientInfo.getPatronymic());
            Assertions.assertEquals(LocalDate.parse(birthDate), patientInfo.getBirthDate());
            Assertions.assertEquals("male", patientInfo.getGenderCode());
            Assertions.assertEquals(lastName + " " + firstName + " " + patronymic, patientInfo.getFullName());

            // Проверка attachments
            List<moscow.ptnl.app.domain.model.es.Attachment> attachments = patientData.getAttachments();
            Assertions.assertEquals(2, attachments.size());
            moscow.ptnl.app.domain.model.es.Attachment attachment = attachments.get(0);
            Assertions.assertEquals(11, attachment.getId());
            Assertions.assertEquals(12, attachment.getAreaId());
            Assertions.assertEquals(LocalDate.parse("2022-01-16"), attachment.getAttachBeginDate());
            Assertions.assertEquals(13, attachment.getMoId());
            Assertions.assertEquals(14, attachment.getMuId());
            Assertions.assertEquals("15", attachment.getAttachTypeCode());
            Assertions.assertEquals("test-15", attachment.getAttachTypeName());
            Assertions.assertEquals("1", attachment.getProcessOfAttachmentCode());
            Assertions.assertEquals("По территориальному принципу", attachment.getProcessOfAttachmentName());

            // Проверка policy
            moscow.ptnl.app.domain.model.es.Policy policy = patientData.getPolicy();
            Assertions.assertEquals(policyNumber, policy.getPolicyNumber());
            Assertions.assertEquals(LocalDateTime.parse(policyChangeDate), policy.getPolicyUpdateDate());
            Assertions.assertEquals(policyStatus, policy.getPolicyStatus());
            Assertions.assertEquals(policyOMSType, policy.getPolicyOMSType());

            // Проверка anthropometryInfo
            List<AnthropometryInfo> anthropometryInfoList = patientData.getAnthropometryInfo();
            Assertions.assertEquals(2, anthropometryInfoList.size());
            AnthropometryInfo anthropometryInfo = anthropometryInfoList.get(0);
            Assertions.assertEquals(LocalDate.parse("2022-06-05"), anthropometryInfo.getMeasurementDate());
            Assertions.assertEquals("6cfff828-e8b2-4069-920d-4ebcdd28fa89", anthropometryInfo.getDocumentId());
            Assertions.assertEquals(3, anthropometryInfo.getMeasurementType());
            Assertions.assertEquals(54, anthropometryInfo.getMeasurementValue());
            Assertions.assertEquals("97", anthropometryInfo.getCentility());
            Assertions.assertEquals(47613265L, anthropometryInfo.getResultAssessmentId());

            // Проверка consentsInfo
            List<moscow.ptnl.app.domain.model.es.ConsentInfo> consentInfos = patientData.getConsentInfos();
            Assertions.assertEquals(2, consentInfos.size());
            moscow.ptnl.app.domain.model.es.ConsentInfo consentInfo = consentInfos.get(0);
            Assertions.assertEquals(11, consentInfo.getConsentId());
            Assertions.assertEquals("test-documentId1", consentInfo.getDocumentedConsent().getDocumentId());
            Assertions.assertEquals(LocalDate.parse("2020-09-25"), consentInfo.getDocumentedConsent().getCreateDate());
            Assertions.assertEquals(12, consentInfo.getDocumentedConsent().getLocationId());
            Assertions.assertEquals("test-locationName1", consentInfo.getDocumentedConsent().getLocationName());
            Assertions.assertTrue(consentInfo.getDocumentedConsent().getAllMedicalIntervention());

            List<Long> medInterventionIdList = consentInfo.getDocumentedConsent().getInterventionDetails().getMedInterventionId();
            Assertions.assertEquals(2, medInterventionIdList.size());
            Assertions.assertEquals(13, medInterventionIdList.get(0));
            Assertions.assertEquals(14, medInterventionIdList.get(1));

            Immunodiagnostics immunodiagnostics = consentInfo.getDocumentedConsent().getImmunodiagnostics();
            List<Immunodiagnostic> immunodiagnosticList = immunodiagnostics.getImmunodiagnostic();
            Assertions.assertEquals(1, immunodiagnosticList.size());

            Assertions.assertEquals(15, immunodiagnosticList.get(0).getImmunoTestKind().getImmunoKindCode());
            Assertions.assertEquals(16, immunodiagnosticList.get(0).getImmunoTestKind().getInfectionCode());

            List<Long> immunoDrugsTnCodeList = immunodiagnosticList.get(0).getImmunoDrugsTns().getImmunoDrugsTnCode();
            Assertions.assertEquals(1, immunoDrugsTnCodeList.size());
            Assertions.assertEquals(398, immunoDrugsTnCodeList.get(0));

            Assertions.assertEquals("test-representativeDocumentId1", consentInfo.getDocumentedConsent().getRepresentativeDocumentId());
            Assertions.assertTrue(consentInfo.getDocumentedConsent().getSignedByPatient());
            Assertions.assertEquals(17, consentInfo.getDocumentedConsent().getCancelReasonId());
            Assertions.assertEquals("test-cancelReasonOther1", consentInfo.getDocumentedConsent().getCancelReasonOther());
            Assertions.assertEquals(18, consentInfo.getDocumentedConsent().getMoId());
            Assertions.assertEquals("test-orgName1", consentInfo.getDocumentedConsent().getMoName());
            Assertions.assertEquals(LocalDateTime.parse("2020-08-08T00:00:00.000000"), consentInfo.getIssueDateTime());
            Assertions.assertEquals(19, consentInfo.getConsentFormId());
            Assertions.assertEquals(20, consentInfo.getConsentTypeId());
            Assertions.assertEquals(21, consentInfo.getRepresentativePhysicalId());
            Assertions.assertEquals(22, consentInfo.getRepresentativeLegalId());

        } finally {
            clearElastic(String.valueOf(patientId)); // id сохраненной сущности StudentPatientData
        }
    }

    private void setPatientServiceResponse(Long patientId) throws DatatypeConfigurationException, Fault {
        // Установка параметров для ответа сервиса ErpPatientPortTypeV2
        GetPatientResponse patientResponse = new GetPatientResponse();
        // Переменная patientInfo
        Patient patient = new Patient();
        patient.setEmiasId(patientId);
        patient.setUklErp(ukl);
        PersonalData personalData = new PersonalData();
        personalData.setLastName(lastName);
        personalData.setFirstName(firstName);
        personalData.setMiddleName(patronymic);
        personalData.setBirthDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(birthDate));
        CodeWithName gender = new CodeWithName();
        gender.setCode(genderCode);
        personalData.setGender(gender);
        patient.setPersonalData(personalData);

        // Переменная attachments
        Patient.Attachments attachments = new Patient.Attachments();
        List<Attachment> attachment = attachments.getAttachment();

        // Прикрепление 1 (подходит под условие isActual = true, code in (10, 20))
        Attachment attachment1 = new Attachment();
        attachment1.setIsActual(true);
        CodeWithName areaType1 = new CodeWithName();
        areaType1.setCode("10");
        attachment1.setAreaType(areaType1);

        attachment1.setAttachId(11L);
        attachment1.setAreaId(12L);
        attachment1.setAttachBeginDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2022-01-16"));
        attachment1.setMainMoId(13L);
        attachment1.setMuId(14L);
        CodeWithName attachType1 = new CodeWithName();
        attachType1.setCode("15");
        attachType1.setName("test-15");
        attachment1.setAttachType(attachType1);
        attachment1.setProcessOfAttachment("1");

        // Прикрепление 2 (подходит под условие isActual = true, code in (10, 20))
        Attachment attachment2 = new Attachment();
        attachment2.setIsActual(true);
        CodeWithName areaType2 = new CodeWithName();
        areaType2.setCode("20");
        attachment2.setAreaType(areaType2);

        attachment2.setAttachId(21L);
        attachment2.setAreaId(22L);
        attachment2.setAttachBeginDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2022-02-16"));
        attachment2.setMainMoId(23L);
        attachment2.setMuId(24L);
        CodeWithName attachType2 = new CodeWithName();
        attachType2.setCode("25");
        attachType2.setName("test-25");
        attachment2.setAttachType(attachType2);
        attachment2.setProcessOfAttachment("4");

        // Прикрепление 3 (НЕ подходит под условие isActual = true, code in (10, 20))
        Attachment attachment3 = new Attachment();
        attachment3.setIsActual(false);
        CodeWithName areaType3 = new CodeWithName();
        areaType3.setCode("10");
        attachment3.setAreaType(areaType3);

        attachment3.setAttachId(31L);
        attachment3.setAreaId(32L);
        attachment3.setAttachBeginDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2022-03-16"));
        attachment3.setMainMoId(33L);
        attachment3.setMuId(34L);
        CodeWithName attachType3 = new CodeWithName();
        attachType3.setCode("35");
        attachType3.setName("test-35");
        attachment3.setAttachType(attachType3);
        attachment3.setProcessOfAttachment("2");

        // Прикрепление 4 (НЕ подходит под условие isActual = true, code in (10, 20))
        Attachment attachment4 = new Attachment();
        attachment4.setIsActual(true);
        CodeWithName areaType4 = new CodeWithName();
        areaType4.setCode("30");
        attachment4.setAreaType(areaType4);
        attachment4.setAttachId(41L);
        attachment4.setAreaId(42L);
        attachment4.setAttachBeginDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2022-04-16"));
        attachment4.setMainMoId(43L);
        attachment4.setMuId(44L);
        CodeWithName attachType4 = new CodeWithName();
        attachType4.setCode("45");
        attachType4.setName("test-45");
        attachment4.setAttachType(attachType4);
        attachment4.setProcessOfAttachment("3");

        attachment.add(attachment1);
        attachment.add(attachment2);
        attachment.add(attachment3);
        attachment.add(attachment4);
        patient.setAttachments(attachments);

        // Переменная policy
        Patient.Policies policies = new Patient.Policies();
        List<Policy> policy = policies.getPolicy();
        Policy policy1 = new Policy();
        policy1.setPolicyNumber(policyNumber);
        policy1.setPolicyChangeDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(policyChangeDate));
        CodeWithName policyStatusCWN = new CodeWithName();
        policyStatusCWN.setCode(policyStatus);
        policy1.setPolicyStatus(policyStatusCWN);
        CodeWithName policyOMSTypeCWN = new CodeWithName();
        policyOMSTypeCWN.setCode(policyOMSType);
        policy1.setPolicyOMSType(policyOMSTypeCWN);
        policy.add(policy1);
        patient.setPolicies(policies);

        patientResponse.setPatient(patient);

        // Возвращаем ответ сервиса ErpPatientPortTypeV2
        Mockito.when(erpPatientPortTypeV2.getPatient(Mockito.any())).thenReturn(patientResponse);
    }

    private void setAnthropometryServiceResponse() throws DatatypeConfigurationException, ru.mos.emias.anthropometry.anthropometryservice.v1.Fault {
        // Установка параметров для ответа сервиса AnthropometryServicePortType
        GetAnthropometryDataByPatientResponse anthropometryResponse = new GetAnthropometryDataByPatientResponse();
        AnthropometryDataResultPage anthropometryDataResultPage = new AnthropometryDataResultPage();
        anthropometryDataResultPage.setTotalItemsCount(2);
        List<AnthropometryItem> measurement = anthropometryDataResultPage.getMeasurement();

        AnthropometryItem anthropometryItem1 = new AnthropometryItem();
        anthropometryItem1.setMeasurementDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2022-06-05"));
        anthropometryItem1.setDocumentID("6cfff828-e8b2-4069-920d-4ebcdd28fa89");
        anthropometryItem1.setMeasurementType(3);
        anthropometryItem1.setMeasurementValue(54);
        anthropometryItem1.setCentility("97");
        anthropometryItem1.setResultAssessmentID(47613265L);

        AnthropometryItem anthropometryItem2 = new AnthropometryItem();
        anthropometryItem2.setMeasurementDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2022-12-12"));
        anthropometryItem2.setDocumentID("79b6ca01-4a64-4cc4-ac12-dc16f46f9ec0");
        anthropometryItem2.setMeasurementType(2);
        anthropometryItem2.setMeasurementValue(54);
        anthropometryItem2.setCentility("25");
        anthropometryItem2.setResultAssessmentID(49168653L);

        measurement.add(anthropometryItem1);
        measurement.add(anthropometryItem2);
        anthropometryResponse.setResult(anthropometryDataResultPage);
        // Возвращаем ответ сервиса AnthropometryServicePortType
        Mockito.when(anthropometryServicePortType.getAnthropometryDataByPatient(Mockito.any())).thenReturn(anthropometryResponse);
    }

    private void setConsentInfoServiceResponse() throws DatatypeConfigurationException, ru.mos.emias.consentinfo.consentinfoservice.v2.Fault {
        // Установка параметров для ответа сервиса ConsentInfoServicePortType
        FindConsentInfoResponse consentInfoResponse = new FindConsentInfoResponse();
        FindConsentInfoResponse.Result findConsentInfoResponseResult = new FindConsentInfoResponse.Result();
        findConsentInfoResponseResult.setTotalItemsCount(2);

        // Каждый блок данных из массива //result/consentList/ns2:consentInfos добавляется в массив consentsInfo:
        List<ConsentList> consentList = findConsentInfoResponseResult.getConsentList();

        // Первый блок consentList
        ConsentList consentList1 = new ConsentList();
        ConsentList.ConsentInfos consentListConsentInfos1 = new ConsentList.ConsentInfos();
        List<ConsentInfo> consentInfoList1 = consentListConsentInfos1.getConsentInfo();
        ConsentInfo consentInfo1 = new ConsentInfo();
        consentInfo1.setId(11);

        consentInfo1.setDocumentId("test-documentId1");
        consentInfo1.setDocumentCreateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2020-09-25"));
        consentInfo1.setLocationId(12L);
        consentInfo1.setLocationName("test-locationName1");
        consentInfo1.setAllMedicalIntervention(true);

        // Группирующая сущность interventionDetails
        ConsentInfo.KindItems consentInfoKindItems1 = new ConsentInfo.KindItems();
        List<KindItem> kindItemList1 = consentInfoKindItems1.getKindItem();
        KindItem kindItem1 = new KindItem();
        kindItem1.setId(13);
        kindItemList1.add(kindItem1);
        KindItem kindItem2 = new KindItem();
        kindItem2.setId(14);
        kindItemList1.add(kindItem2);
        consentInfo1.setKindItems(consentInfoKindItems1);

        // Группирующая сущность immunodiagnostics
        ConsentInfo.Immunodiagnostics consentInfoImmunodiagnostics1 = new ConsentInfo.Immunodiagnostics();
        List<ConsentInfo.Immunodiagnostics.Immunodiagnostic> immunodiagnosticList1 = consentInfoImmunodiagnostics1.getImmunodiagnostic();

        ConsentInfo.Immunodiagnostics.Immunodiagnostic immunodiagnostic1 = new ConsentInfo.Immunodiagnostics.Immunodiagnostic();

        // Группирующая сущность immunoTestKind
        ConsentInfo.Immunodiagnostics.Immunodiagnostic.ImmunoTestKind immunoTestKind1 = new ConsentInfo.Immunodiagnostics.Immunodiagnostic.ImmunoTestKind();
        ImmunoKind immunoKind1 = new ImmunoKind();
        immunoKind1.setCode(15);
        immunoTestKind1.setImmunoKind(immunoKind1);
        Infection infection1 = new Infection();
        infection1.setCode(16);
        immunoTestKind1.setInfection(infection1);
        immunodiagnostic1.setImmunoTestKind(immunoTestKind1);

        // Группирующая сущность immunoDrugsTns
        ConsentInfo.Immunodiagnostics.Immunodiagnostic.ImmunoDrugsTns immunoDrugsTns1 = new ConsentInfo.Immunodiagnostics.Immunodiagnostic.ImmunoDrugsTns();
        List<ImmunoDrugsTn> immunoDrugsTnList1 = immunoDrugsTns1.getImmunoDrugsTn();
        ImmunoDrugsTn immunoDrugsTn1 = new ImmunoDrugsTn();
        immunoDrugsTn1.setCode("398");
        immunoDrugsTnList1.add(immunoDrugsTn1);
        immunodiagnostic1.setImmunoDrugsTns(immunoDrugsTns1);

        immunodiagnosticList1.add(immunodiagnostic1);
        consentInfo1.setImmunodiagnostics(consentInfoImmunodiagnostics1);

        consentInfo1.setRepresentativeDocumentId("test-representativeDocumentId1");
        consentInfo1.setSignedByPatient(true);
        CancelReason cancelReason1 = new CancelReason();
        cancelReason1.setId(17);
        consentInfo1.setCancelReason(cancelReason1);
        consentInfo1.setCancelReasonOther("test-cancelReasonOther1");
        consentInfo1.setOrgId(18L);
        consentInfo1.setOrgName("test-orgName1");

        consentInfo1.setIssueDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2020-08-08T00:00:00.000000"));
        ConsentForm consentForm1 = new ConsentForm();
        consentForm1.setId(19);
        consentList1.setConsentForm(consentForm1);
        ConsentType consentType1 = new ConsentType();
        consentType1.setId(20);
        consentInfo1.setConsentType(consentType1);
        consentInfo1.setRepresentativePhysicalId(21L);
        consentInfo1.setRepresentativeLegalId(22L);

        consentInfoList1.add(consentInfo1);
        consentList1.setConsentInfos(consentListConsentInfos1);

        // Второй блок consentList
        ConsentList consentList2 = new ConsentList();
        ConsentList.ConsentInfos consentListConsentInfos2 = new ConsentList.ConsentInfos();
        List<ConsentInfo> consentInfoList2 = consentListConsentInfos2.getConsentInfo();
        ConsentInfo consentInfo2 = new ConsentInfo();
        consentInfo2.setId(21);

        consentInfo2.setDocumentId("test-documentId2");
        consentInfo2.setDocumentCreateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2020-09-25"));
        consentInfo2.setLocationId(22L);
        consentInfo2.setLocationName("test-locationName2");
        consentInfo2.setAllMedicalIntervention(false);

        // Группирующая сущность interventionDetails
        ConsentInfo.KindItems consentInfoKindItems2 = new ConsentInfo.KindItems();
        List<KindItem> kindItemList2 = consentInfoKindItems2.getKindItem();
        KindItem kindItem12 = new KindItem();
        kindItem12.setId(23);
        kindItemList2.add(kindItem12);
        KindItem kindItem22 = new KindItem();
        kindItem22.setId(24);
        kindItemList2.add(kindItem22);
        consentInfo2.setKindItems(consentInfoKindItems2);

        // Группирующая сущность immunodiagnostics
        ConsentInfo.Immunodiagnostics consentInfoImmunodiagnostics2 = new ConsentInfo.Immunodiagnostics();
        List<ConsentInfo.Immunodiagnostics.Immunodiagnostic> immunodiagnosticList2 = consentInfoImmunodiagnostics2.getImmunodiagnostic();

        ConsentInfo.Immunodiagnostics.Immunodiagnostic immunodiagnostic2 = new ConsentInfo.Immunodiagnostics.Immunodiagnostic();

        // Группирующая сущность immunoTestKind
        ConsentInfo.Immunodiagnostics.Immunodiagnostic.ImmunoTestKind immunoTestKind2 = new ConsentInfo.Immunodiagnostics.Immunodiagnostic.ImmunoTestKind();
        ImmunoKind immunoKind2 = new ImmunoKind();
        immunoKind2.setCode(25);
        immunoTestKind2.setImmunoKind(immunoKind2);
        Infection infection2 = new Infection();
        infection2.setCode(26);
        immunoTestKind2.setInfection(infection2);
        immunodiagnostic2.setImmunoTestKind(immunoTestKind2);

        // Группирующая сущность immunoDrugsTns
        ConsentInfo.Immunodiagnostics.Immunodiagnostic.ImmunoDrugsTns immunoDrugsTns2 = new ConsentInfo.Immunodiagnostics.Immunodiagnostic.ImmunoDrugsTns();
        List<ImmunoDrugsTn> immunoDrugsTnList2 = immunoDrugsTns2.getImmunoDrugsTn();
        ImmunoDrugsTn immunoDrugsTn2 = new ImmunoDrugsTn();
        immunoDrugsTn2.setCode("320");
        immunoDrugsTnList2.add(immunoDrugsTn2);
        immunodiagnostic2.setImmunoDrugsTns(immunoDrugsTns2);

        immunodiagnosticList2.add(immunodiagnostic2);

        consentInfo2.setImmunodiagnostics(consentInfoImmunodiagnostics2);

        consentInfo2.setRepresentativeDocumentId("test-representativeDocumentId2");
        consentInfo2.setSignedByPatient(false);
        CancelReason cancelReason2 = new CancelReason();
        cancelReason2.setId(27);
        consentInfo2.setCancelReason(cancelReason2);
        consentInfo2.setCancelReasonOther("test-cancelReasonOther2");
        consentInfo2.setOrgId(28L);
        consentInfo2.setOrgName("test-orgName2");

        consentInfo2.setIssueDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2020-08-08T00:00:00.000000"));
        ConsentForm consentForm2 = new ConsentForm();
        consentForm2.setId(29);
        consentList2.setConsentForm(consentForm2);
        ConsentType consentType2 = new ConsentType();
        consentType2.setId(30);
        consentInfo2.setConsentType(consentType2);
        consentInfo2.setRepresentativePhysicalId(31L);
        consentInfo2.setRepresentativeLegalId(32L);

        consentInfoList2.add(consentInfo2);
        consentList2.setConsentInfos(consentListConsentInfos2);

        consentList.add(consentList1);
        consentList.add(consentList2);
        consentInfoResponse.setResult(findConsentInfoResponseResult);
        Mockito.when(consentInfoServicePortType.findConsentInfo(Mockito.any())).thenReturn(consentInfoResponse);
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
                LocalDateTime.now(), "1", TopicType.SCHOOL_ATTACHMENT_EVENT.getName(), text);
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
