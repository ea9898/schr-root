package moscow.ptnl.schr.test;

import moscow.ptnl.app.config.ESConfiguration;
import moscow.ptnl.schr.model.es.TestObject;
import moscow.ptnl.schr.repository.es.TestObjectRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 *
 * @author m.kachalov
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { 
    ESConfiguration.class
})
public class TestObjectTest {
    
    @Autowired
    private ElasticsearchTemplate esTemplate;
    
    @Autowired
    private TestObjectRepository repository;
    
    @BeforeEach
    public void before() {
        if (!esTemplate.indexOps(IndexCoordinates.of(TestObject.INDEX_NAME)).exists()) {
            esTemplate.indexOps(IndexCoordinates.of(TestObject.INDEX_NAME)).create();
        }
    }
    
    @AfterEach
    public void after() {
        esTemplate.indexOps(IndexCoordinates.of(TestObject.INDEX_NAME)).delete();
    }
    
    @Test
    public void testSave() {
        TestObject localObj = new TestObject();
        localObj.setId(1L);
        localObj.setTitle("title");
        TestObject esObj = repository.save(localObj);

        assertNotNull(esObj.getId());
        assertEquals(esObj.getTitle(), localObj.getTitle());
    }
    
}
