package moscow.ptnl.schr.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import moscow.ptnl.app.domain.model.es.StudentPatientData;
import moscow.ptnl.app.helper.ESHelper;
import moscow.ptnl.schr.error.ErrorReason;
import moscow.ptnl.schr.model.DslQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author m.kachalov
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class StudentPatientRegistryDAO {
    
    private final static Logger LOG = LoggerFactory.getLogger(StudentPatientRegistryDAO.class);
    
    @Autowired
    private ESHelper esHelper;
    
    
    private final ObjectMapper objectMapper = new ObjectMapper();
      
    
    public DslQueryResult executeDslQuery(final String dslQuery) {
        DslQueryResult result = new DslQueryResult();
        String content;
        //2. Система выполняет GET запрос к хранилищу Elasticsearch
        try {
            ESHelper.ESResponse response = esHelper.getContent("POST", "/" + StudentPatientData.INDEX_NAME + "/_search", dslQuery);
            if (!response.isSuccess()) {
                if (response.getContent().isPresent()) {
                    result.setError(ErrorReason.BAD_REQUEST, response.getContent().get());
                    return result;
                } else {
                    result.setError(ErrorReason.INTERNAL_SERVER_ERROR, "Пустой ответ ElasticSearch");
                    return result;
                }
            } 
            content = response.getContent().get();
        } catch (Exception e) {
            LOG.error("Ошибка выполнения запроса к ElasticSearch", e);
            result.setError(ErrorReason.INTERNAL_SERVER_ERROR, e.getMessage());
            return result;
        } 
        //3. Система осуществляет парсинг сообщения полученного на шаге 2
        try {
            JsonNode node = objectMapper.readTree(content);
            JsonNode totalNode = node.at("/hits/total/value");
            if (totalNode.isMissingNode()) {
                result.setError(ErrorReason.BAD_REQUEST, content);
                return result;
            }
            result.setTotalCount(totalNode.asLong());
            JsonNode hitsNode = node.at("/hits/hits");
            if (!hitsNode.isMissingNode() && hitsNode.isArray()) {
                for (final JsonNode itemNode : hitsNode) {
                    result.getItems().add(itemNode.toString());
                }
            }
        } catch (Exception e) {
            result.setError(ErrorReason.INTERNAL_SERVER_ERROR, e.getMessage());
            LOG.error("Ошибка парсинга ответа ElasticSearch", e);
        }
        return result;
    }
    
    /*
    public DslQueryResult executeDslQuery(String dslQuery) {  
        DslQueryResult result = new DslQueryResult();
        try {            
            QueryStringQuery query = QueryBuilders.queryString().query(dslQuery).build();
            NativeQuery nquery = new NativeQueryBuilder()
                .withQuery(query._toQuery())
                .build();
            SearchHits<StudentPatientData> hits = esTemplate
                .search(nquery, StudentPatientData.class);
            
            result.setTotalCount(hits.getTotalHits());
            
            if (result.getTotalCount() != null) {
                hits.forEach(h -> {
                    try {
                        String jsonString = objectMapper.writeValueAsString(h.getContent());
                        result.getItems().add(jsonString);
                    } catch (Exception e) {
                        LOG.error("Ошибка трансформации JSON", e);
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception err) {
            LOG.error("Ошибка выполнения запроса к ElasticSearch", err);
            result.setError(err);
        }
        
        return result;
    }
    */
}
