package moscow.ptnl.schr.test;

import moscow.ptnl.app.config.ESConfiguration;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.infrastructure.repository.es.StudentPatientDataRepository;
import moscow.ptnl.schr.configuration.AnthropometryServiceConfiguration;
import moscow.ptnl.schr.configuration.AsyncConfiguration;
import moscow.ptnl.schr.configuration.CXFConfiguration;
import moscow.ptnl.schr.configuration.ConsentInfoServiceConfiguration;
import moscow.ptnl.schr.configuration.PatientServiceConfiguration;
import moscow.ptnl.schr.configuration.SchoolServiceConfiguration;
import moscow.ptnl.schr.service.AdminService;
import moscow.ptnl.schr.service.dto.SyncDataByPatientIdRequest;
import moscow.ptnl.schr.service.dto.SyncDataByPatientIdResponse;
import moscow.ptnl.schr.test.configuration.MockConfiguration;
import moscow.ptnl.schr.test.configuration.PersistenceConfiguration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {
        PersistenceConfiguration.class, MockConfiguration.class,
        AsyncConfiguration.class, CXFConfiguration.class, ESConfiguration.class,
        AnthropometryServiceConfiguration.class, ConsentInfoServiceConfiguration.class,
        PatientServiceConfiguration.class, SchoolServiceConfiguration.class
})
@Transactional
@Rollback
public class IntegrationTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private StudentPatientDataRepository studentPatientDataRepository;

    @Test
    public void test1() {
        Long emiasId = 18027361L;
        try {
            SyncDataByPatientIdRequest request = new SyncDataByPatientIdRequest();
            request.setPatientId(Collections.singletonList(emiasId));
            SyncDataByPatientIdResponse response = adminService.syncDataByPatientId(request);
            Assertions.assertTrue(response.isResult());
            Optional<StudentPatientData> studentPatientDataOptional = studentPatientDataRepository.findById(String.valueOf(emiasId));
            Assertions.assertTrue(studentPatientDataOptional.isPresent(), "No patient with Id=18027361");
            StudentPatientData patientData = studentPatientDataOptional.get();
            Assertions.assertEquals(String.valueOf(emiasId), patientData.getId());
        } catch (Throwable e) {
            Assertions.fail(e);
        } finally {
            clearElastic(emiasId.toString());
        }
    }

    private void clearElastic(String patientId) {
        // Т.к. используем реальный сервис Elastic, нужно удалить созданные данные
        studentPatientDataRepository.deleteById(patientId);
    }
}