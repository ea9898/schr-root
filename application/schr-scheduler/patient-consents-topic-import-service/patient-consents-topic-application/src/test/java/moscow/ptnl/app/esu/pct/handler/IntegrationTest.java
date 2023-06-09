package moscow.ptnl.app.esu.pct.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

import moscow.ptnl.app.config.ESConfiguration;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.config.PersistenceConstraint;
import moscow.ptnl.app.esu.pct.handler.configuration.AsyncConfiguration;
import moscow.ptnl.app.esu.pct.handler.configuration.MockConfiguration;
import moscow.ptnl.app.esu.pct.handler.configuration.PersistenceConfiguration;
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.domain.model.es.IndexEsuInput;
import moscow.ptnl.app.patient.consents.topic.config.RestConfiguration;
import moscow.ptnl.app.patient.consents.topic.task.PatientConsentsTopicProcessTask;
import moscow.ptnl.app.repository.EsuInputCRUDRepository;
import moscow.ptnl.app.repository.es.IndexEsuInputRepository;
import moscow.ptnl.domain.entity.esu.EsuInput;
import moscow.ptnl.domain.entity.esu.EsuStatusType;
import moscow.ptnl.domain.service.SettingService;

import moscow.ptnl.schr.repository.SettingsCRUDRepository;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import javax.sql.DataSource;
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
public class IntegrationTest {

    @PersistenceContext(name = PersistenceConstraint.PU_NAME)
    EntityManager entityManager;

    @Autowired
    PatientConsentsTopicProcessTask patientConsentsTopicProcessTask;

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

//    @Test
//    public void test1() throws ExecutionException, InterruptedException, IOException {
//        /*
//         * org.h2.jdbc.JdbcSQLNonTransientConnectionException:
//         *  База данных уже закрыта (чтобы отключить автоматическое закрытие базы данных при останове JVM, добавьте ";DB_CLOSE_ON_EXIT=FALSE" в URL)
//         * Database is already closed (to disable automatic closing at VM shutdown, add ";DB_CLOSE_ON_EXIT=FALSE" to the db URL) [90121-214]
//         * */
//        String studentId;
//        //build test data
//        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/studentPatientData.json")) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//            StudentPatientData studentPatientData = objectMapper.readValue(reader, StudentPatientData.class);
//            studentId = studentPatientData.getPatientInfo().getPatientId().toString();
//            studentPatientData.setId(studentId);
//            transactions.execute((s) -> studentPatientDataRepository.save(studentPatientData)).getId();
//        }
//        //event 1
//        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/ConsentsInfo_1.json")) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//            buildMessage(reader.lines().collect(Collectors.joining("\n")));
//        }
//        //event 2
//        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/ConsentsInfo_1.json")) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//            buildMessage(reader.lines().collect(Collectors.joining("\n")).replace("\"patientId\": 3987621809,", "\"patientId\": 827628764,"));
//        }
//        //event 3
//        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/ConsentsInfo_1.json")) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//            buildMessage(reader.lines().collect(Collectors.joining("\n"))
//                    .replace("\"issueDateTime\": \"2022-12-20T00:00:00\",", "\"issueDateTime\": \"2000-01-20T13:00:00\","));
//        }
//        entityManager.flush();
//
//        try {
//            executor.submit(() -> Assertions.assertDoesNotThrow(() -> patientConsentsTopicProcessTask.runTask()));
//            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
//                    Mockito.eq(PlannersEnum.I_SCHR_10.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
//
//            Thread.sleep(1000);
//
//            entityManager.flush();
//
//
//            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
//                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());
//
//                if (t.getId() == 2L) {
//                    Assertions.assertEquals("SCHR_104 - Пациент с идентификатором 827628764 не найден в системе", t.getError());
//                } else if (t.getId() == 1L) {
//                    Assertions.assertNull(t.getError(), "id=" + t.getId());
//                } else {
//                    Assertions.assertEquals("SCHR_107 - Получена более старая информация, чем содержится в индексе", t.getError());
//                }
//            });
//
//            List<StudentPatientData> studentPatientDataList = StreamSupport.stream(studentPatientDataRepository.findAll().spliterator(), false)
//                    .filter(t -> Objects.equals(t.getPatientInfo().getPatientId(), 3987621809L))
//                    .collect(Collectors.toList());
//            Assertions.assertEquals(1, studentPatientDataList.size(), "Too many patients with Id=3987621809");
//            StudentPatientData patientData = studentPatientDataList.get(0);
//
//            Assertions.assertEquals(LocalDate.of(2018, 5, 7), patientData.getPatientInfo().getBirthDate());
//            Assertions.assertEquals(1, patientData.getConsentInfos().size());
//            Assertions.assertEquals(50699767L, patientData.getConsentInfos().get(0).getConsentId());
//            Assertions.assertEquals(LocalDateTime.of(2022, 12, 20, 0, 0), patientData.getConsentInfos().get(0).getIssueDateTime());
//            Assertions.assertArrayEquals(List.of(158370312L).toArray(), patientData.getConsentInfos().get(0).getDocumentedConsent().getInterventionDetails().getMedInterventionId().toArray());
//            Assertions.assertEquals(1, patientData.getConsentInfos().get(0).getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic().size());
//            Assertions.assertArrayEquals(List.of(158370694L).toArray(), patientData.getConsentInfos().get(0).getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic().get(0).getImmunoDrugsTns().getImmunoDrugsTnCode().toArray());
//            Assertions.assertEquals(158370689L, patientData.getConsentInfos().get(0).getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic().get(0).getImmunoTestKind().getImmunoKindCode());
//            Assertions.assertEquals(158370685L, patientData.getConsentInfos().get(0).getDocumentedConsent().getImmunodiagnostics().getImmunodiagnostic().get(0).getImmunoTestKind().getInfectionCode());
//        } finally {
//            //Т.к. используем реальный сервис Elastic, нужно удалить созданные данные
//            List<EsuInput> esuInputs = StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false)
//                    .collect(Collectors.toList());
//            indexEsuInputRepository.deleteAllById(esuInputs.stream().map(EsuInput::getEsId).collect(Collectors.toList()));
//            studentPatientDataRepository.deleteById(studentId);
//        }
//    }

    private void buildMessage(String text) throws ExecutionException, InterruptedException {
        final IndexEsuInput indexEsuInput = new IndexEsuInput(10L,
                LocalDateTime.now(), "1", TopicType.PATIENT_CONSENTS_TOPIC.getName(), text);
        EsuInput testInput = new EsuInput();
        testInput.setStatus(EsuStatusType.NEW);
        testInput.setDateCreated(LocalDateTime.now());
        testInput.setDateUpdated(LocalDateTime.now());
        testInput.setTopic(TopicType.PATIENT_CONSENTS_TOPIC.getName());
        String esId = executor.submit(() -> indexEsuInputRepository.save(indexEsuInput)).get().getId();
        testInput.setEsId(esId);
        executor.submit(() -> esuInputCRUDRepository.save(testInput)).get();
    }
}
