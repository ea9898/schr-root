package moscow.ptnl.app.infrastructure.repository.es;

import moscow.ptnl.app.domain.model.es.JournalRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * @author m.kachalov
 */
public interface JournalRequestRepository extends ElasticsearchRepository<JournalRequest, String>{
    
}
