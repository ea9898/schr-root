package moscow.ptnl.app.esu.la.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

import moscow.ptnl.app.config.ESConfiguration;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.la.handler.config.AsyncConfiguration;
import moscow.ptnl.app.la.handler.config.RestConfiguration;
import moscow.ptnl.app.la.handler.task.LastAnthropometryProcessTask;
import moscow.ptnl.app.config.PersistenceConstraint;
import moscow.ptnl.app.esu.la.handler.configuration.MockConfiguration;
import moscow.ptnl.app.esu.la.handler.configuration.PersistenceConfiguration;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    LastAnthropometryProcessTask lastAnthropometryProcessTask;

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
        String studentId;
        //build test data
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/studentPatientData.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StudentPatientData studentPatientData = objectMapper.readValue(reader, StudentPatientData.class);
            studentId = studentPatientData.getPatientInfo().getPatientId().toString();
            studentPatientData.setId(studentId);
            transactions.execute((s) -> studentPatientDataRepository.save(studentPatientData)).getId();
        }
        //event 1
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/LastAnthropometry-add.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n")));
        }
        //event 2
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/LastAnthropometry-remove.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n")));
        }
        //event 3
        try (InputStream inputStream = IntegrationTest.class.getClassLoader().getResourceAsStream("json/LastAnthropometry-remove.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            buildMessage(reader.lines().collect(Collectors.joining("\n")).replace("4, 2, 3", "10, 20"));
        }
        entityManager.flush();

        try {
            executor.submit(() -> Assertions.assertDoesNotThrow(() -> lastAnthropometryProcessTask.runTask()));
            Mockito.verify(settingService, Mockito.timeout(30000).times(2)).getSettingProperty(
                    Mockito.eq(PlannersEnum.I_SCHR_8.getPlannerName() + ".run.mode"), Mockito.any(), Mockito.anyBoolean());
            entityManager.flush();
            StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false).forEach(t -> {
                Assertions.assertEquals(EsuStatusType.PROCESSED, t.getStatus());

                if (t.getId() == 3L) {
                    Assertions.assertEquals("SCHR_106 - Запись, которую необходимо удалить, не найдена", t.getError());
                } else {
                    Assertions.assertNull(t.getError());
                }
            });
            List<StudentPatientData> studentPatientDataList = StreamSupport.stream(studentPatientDataRepository.findAll().spliterator(), false)
                    .filter(t -> Objects.equals(t.getPatientInfo().getPatientId(), 3987621809L))
                    .collect(Collectors.toList());
            Assertions.assertEquals(1, studentPatientDataList.size(), "Too many patients with Id=3987621809");
            StudentPatientData patientData = studentPatientDataList.get(0);
            Assertions.assertEquals(1, patientData.getAnthropometryInfo().size());
            patientData.getAnthropometryInfo().stream().findFirst().ifPresent(a -> {
                Assertions.assertEquals("низкорослость", a.getCentility());
                Assertions.assertEquals("872c407e-cbae-4db7-b8c9-34d2c5596253", a.getDocumentId());
                Assertions.assertEquals(LocalDate.of(2022, 12, 15), a.getMeasurementDate());
                Assertions.assertEquals(1, a.getMeasurementType());
                Assertions.assertEquals(55, a.getMeasurementValue());
                Assertions.assertEquals(162628369, a.getResultAssessmentId());
            });
        } finally {
            //Т.к. используем реальный сервис Elastic, нужно удалить созданные данные
            List<EsuInput> esuInputs = StreamSupport.stream(esuInputCRUDRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            indexEsuInputRepository.deleteAllById(esuInputs.stream().map(EsuInput::getEsId).collect(Collectors.toList()));
            studentPatientDataRepository.deleteById(studentId);
        }
    }

    private void buildMessage(String text) throws ExecutionException, InterruptedException {
        final IndexEsuInput indexEsuInput = new IndexEsuInput(10L,
                LocalDateTime.now(),"1", TopicType.LAST_ANTHROPOMETRY.getName(), text);
        EsuInput testInput = new EsuInput();
        testInput.setStatus(EsuStatusType.NEW);
        testInput.setDateCreated(LocalDateTime.now());
        testInput.setDateUpdated(LocalDateTime.now());
        testInput.setTopic(TopicType.LAST_ANTHROPOMETRY.getName());
        String esId = executor.submit(() -> indexEsuInputRepository.save(indexEsuInput)).get().getId();
        testInput.setEsId(esId);
        executor.submit(() -> esuInputCRUDRepository.save(testInput)).get();
    }
}
