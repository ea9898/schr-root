package moscow.ptnl.schr.model.es;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 *
 * @author m.kachalov
 */
@Document(indexName = TestObject.INDEX_NAME)
public class TestObject {
    
    public static final String INDEX_NAME = "testindex";
    
    @Id
    private Long id;
    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
}
