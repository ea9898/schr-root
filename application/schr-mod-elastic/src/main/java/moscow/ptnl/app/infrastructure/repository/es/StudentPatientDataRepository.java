package moscow.ptnl.app.infrastructure.repository.es;

import moscow.ptnl.app.domain.model.es.StudentPatientData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StudentPatientDataRepository extends ElasticsearchRepository<StudentPatientData, Long> {
}
