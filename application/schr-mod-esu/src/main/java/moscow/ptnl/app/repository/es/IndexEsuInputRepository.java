package moscow.ptnl.app.repository.es;

import moscow.ptnl.app.domain.model.es.IndexEsuInput;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * @author sorlov
 */
public interface IndexEsuInputRepository extends ElasticsearchRepository<IndexEsuInput, String> {
    
}
