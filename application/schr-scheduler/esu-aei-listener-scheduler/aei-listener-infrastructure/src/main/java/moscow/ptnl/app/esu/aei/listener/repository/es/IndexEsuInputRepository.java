package moscow.ptnl.app.esu.aei.listener.repository.es;

import moscow.ptnl.app.esu.aei.listener.model.es.IndexEsuInput;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * @author sorlov
 */
public interface IndexEsuInputRepository extends ElasticsearchRepository<IndexEsuInput, Long> {
    
}
