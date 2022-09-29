package moscow.ptnl.schr.repository.es;

import moscow.ptnl.schr.model.es.TestObject;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * @author m.kachalov
 */
public interface TestObjectRepository extends ElasticsearchRepository<TestObject, Long> {
    
}
