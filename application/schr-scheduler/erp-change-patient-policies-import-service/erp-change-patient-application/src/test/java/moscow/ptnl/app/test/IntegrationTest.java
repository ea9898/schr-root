package moscow.ptnl.app.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import moscow.ptnl.app.config.ESConfiguration;
import moscow.ptnl.app.config.PersistenceConstraint;
import moscow.ptnl.app.domain.model.es.Policy;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.ecpp.task.ErpChangePatientPoliciesProcessTask;
import moscow.ptnl.app.erp.change.patient.policies.config.AsyncConfiguration;
import moscow.ptnl.app.erp.change.patient.policies.config.RestConfiguration;
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.app.model.PlannersEnum;
import moscow.ptnl.app.model.TopicType;
import moscow.ptnl.app.model.es.IndexEsuInput;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
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
    ErpChangePatientPoliciesProcessTask erpChangePatientPoliciesProcessTask;

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

    @Test
    public void test1() throws ExecutionException, InterruptedException, IOException {
        Long studentId;
        //build test data
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/studentPatientData.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StudentPatientData studentPatientData = objectMapper.readValue(reader, StudentPatientData.class);
            studentId = studentPatientData.getPatientInfo().getPatientId();
            studentPatientData.setId(studentId.toString());
            transactions.execute((s) -> studentPatientDataRepository.save(studentPatientData)).getId();
        }

        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/ErpChangePatientPoliciesPolicyStatusD.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"operationDate\": \"2022-10-18T14:21:14.196+03:00\",", String.format("\"operationDate\": \"%s\",", LocalDateTime.now().toString())));
        }
        entityManager.flush();

        try {
            executor.submit(() -> Assertions.assertDoesNotThrow(() -> erpChangePatientPoliciesProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(1))
                    .getSettingProperty(Mockito.eq(PlannersEnum.I_SCHR_6.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());

            entityManager.flush();

            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.NEW, t.getStatus());
                Assertions.assertNull(t.getError());
            });

            List<StudentPatientData> studentPatientDataList = StreamSupport.stream(studentPatientDataRepository.findAll().spliterator(), false)
                    .filter(t -> Objects.equals(t.getPatientInfo().getPatientId(), studentId))
                    .collect(Collectors.toList());
            Assertions.assertEquals(1, studentPatientDataList.size());

            StudentPatientData patientData = studentPatientDataList.get(0);
            Policy policy = new Policy();
            policy.setPolicyStatus("D");
            policy.setPolicyNumber("7700008184090530");

            patientData.setPolicy(policy);

            Assertions.assertEquals("D", policy.getPolicyStatus());
            Assertions.assertEquals("7700008184090530", policy.getPolicyNumber());
        } finally {
            //Т.к. используем реальный сервис Elastic, нужно удалить созданные данные
            List<EsuInput> esuInputs = StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            indexEsuInputRepository.deleteAllById(esuInputs.stream().map(EsuInput::getEsId).collect(Collectors.toList()));
            studentPatientDataRepository.deleteById(studentId.toString());
        }
    }

    @Test
    public void test2() throws ExecutionException, InterruptedException, IOException {
        Long studentId;
        //build test data
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/studentPatientData.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StudentPatientData studentPatientData = objectMapper.readValue(reader, StudentPatientData.class);
            studentId = studentPatientData.getPatientInfo().getPatientId();
            studentPatientData.setId(studentId.toString());
            transactions.execute((s) -> studentPatientDataRepository.save(studentPatientData)).getId();
        }

        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/ErpChangePatientPoliciesPolicyStatusN.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"operationDate\": \"2022-10-18T14:21:14.196+03:00\",", String.format("\"operationDate\": \"%s\",", LocalDateTime.now().toString())));
        }
        entityManager.flush();

        try {
            executor.submit(() -> Assertions.assertDoesNotThrow(() -> erpChangePatientPoliciesProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(1))
                    .getSettingProperty(Mockito.eq(PlannersEnum.I_SCHR_6.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());

            entityManager.flush();

            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.NEW, t.getStatus());
                Assertions.assertNull(t.getError());
            });

            List<StudentPatientData> studentPatientDataList = StreamSupport.stream(studentPatientDataRepository.findAll().spliterator(), false)
                    .filter(t -> Objects.equals(t.getPatientInfo().getPatientId(), studentId))
                    .collect(Collectors.toList());
            Assertions.assertEquals(1, studentPatientDataList.size());

            StudentPatientData patientData = studentPatientDataList.get(0);
            Policy policy = new Policy();
            policy.setPolicyStatus("N");
            policy.setPolicyNumber("7700008184090530");

            patientData.setPolicy(policy);

            Assertions.assertEquals("N", policy.getPolicyStatus());
//            Assertions.assertEquals("2023-01-17T14:09:50.827", policy.getPolicyUpdateDate().toString());
            Assertions.assertEquals("7700008184090530", policy.getPolicyNumber());
        } finally {
            //Т.к. используем реальный сервис Elastic, нужно удалить созданные данные
            List<EsuInput> esuInputs = StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            indexEsuInputRepository.deleteAllById(esuInputs.stream().map(EsuInput::getEsId).collect(Collectors.toList()));
            studentPatientDataRepository.deleteById(studentId.toString());
        }
    }

    @Test
    public void test3() throws ExecutionException, InterruptedException, IOException {
        Long studentId;
        //build test data
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/studentPatientData.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StudentPatientData studentPatientData = objectMapper.readValue(reader, StudentPatientData.class);
            studentId = studentPatientData.getPatientInfo().getPatientId();
            studentPatientData.setId(studentId.toString());
            transactions.execute((s) -> studentPatientDataRepository.save(studentPatientData)).getId();
        }

        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/patientInfoPolicyIsAfterPolicyChangeDate.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n"))
                    .replace("\"operationDate\": \"2022-10-18T14:21:14.196+03:00\",", String.format("\"operationDate\": \"%s\",", LocalDateTime.now().toString())));
        }
        entityManager.flush();

        try {
            executor.submit(() -> Assertions.assertDoesNotThrow(() -> erpChangePatientPoliciesProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(1))
                    .getSettingProperty(Mockito.eq(PlannersEnum.I_SCHR_6.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());

            entityManager.flush();

            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.NEW, t.getStatus());
                Assertions.assertNull(t.getError());
            });

            List<StudentPatientData> studentPatientDataList = StreamSupport.stream(studentPatientDataRepository.findAll().spliterator(), false)
                    .filter(t -> Objects.equals(t.getPatientInfo().getPatientId(), studentId))
                    .collect(Collectors.toList());
            Assertions.assertEquals(1, studentPatientDataList.size());

            StudentPatientData patientData = studentPatientDataList.get(0);
            Policy policy = new Policy();
            policy.setPolicyUpdateDate(LocalDateTime.of(2019, 1, 17, 14, 9, 50, 827));
            patientData.setPolicy(policy);

            Assertions.assertEquals("2019-01-17T14:09:50.000000827", policy.getPolicyUpdateDate().toString());
        } finally {
            //Т.к. используем реальный сервис Elastic, нужно удалить созданные данные
            List<EsuInput> esuInputs = StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            indexEsuInputRepository.deleteAllById(esuInputs.stream().map(EsuInput::getEsId).collect(Collectors.toList()));
            studentPatientDataRepository.deleteById(studentId.toString());
        }
    }

    private void buildMessage(String text) throws ExecutionException, InterruptedException {
        final IndexEsuInput indexEsuInput = new IndexEsuInput(10L,
                LocalDateTime.now(), "1", TopicType.ERP_CHANGE_PATIENT_POLICIES.getName(), text);
        EsuInput testInput = new EsuInput();
        testInput.setStatus(EsuStatusType.NEW);
        testInput.setDateCreated(LocalDateTime.now());
        testInput.setDateUpdated(LocalDateTime.now());
        testInput.setTopic(TopicType.ERP_CHANGE_PATIENT_POLICIES.getName());
        String esId = executor.submit(() -> indexEsuInputRepository.save(indexEsuInput)).get().getId();
        testInput.setEsId(esId);
        executor.submit(() -> esuInputCRUDRepository.save(testInput)).get();
    }
}
