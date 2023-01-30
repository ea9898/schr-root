package moscow.ptnl.app.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author m.kachalov
 */
@Component
public class ESHelper {
    
    private final static Logger LOG = LoggerFactory.getLogger(ESHelper.class);
    
    @Autowired
    private RestClient restClient;
    
    /**
     * Отправляет REST запрос в ElasticSearch.
     * 
     * @param requestMethod "GET", "POST"
     * @param uri "/index_name/_search"
     * @param dslQuery JSON-запрос (можно null)
     * @return тело ответа ElasticSearch
     * @throws RuntimeException 
     */
    public ESResponse getContent(String requestMethod, String uri, String dslQuery) throws RuntimeException { 
        Response response;
        try {
            Request request = new Request(requestMethod, uri);
            //RequestOptions.Builder options = RequestOptions.DEFAULT.toBuilder();
            //options.addHeader("Content-Type", "application/json; charset=utf-8");
            //options.addHeader("User-Agent", "curl/7.83.1");
            //request.setOptions(options.build());
            if (dslQuery != null && !dslQuery.isEmpty()) {
                request.setJsonEntity(dslQuery.trim());
            }
            response = restClient.performRequest(request);
        } catch (ResponseException e) {
            response = e.getResponse();            
        } catch (Exception e) {
            LOG.error("Ошибка выполнения запроса к ElasticSearch", e);
            throw new RuntimeException(e.getMessage());
        }  
        
        
        int statusCode = response.getStatusLine().getStatusCode();
        Optional<String> content = readContent(response.getEntity());
        
        if (statusCode == 200) {
            return new ESResponse(statusCode, content);
        } else {
            return new ESResponse(
                    statusCode, 
                    content.isPresent() ? content : Optional.ofNullable(response.getStatusLine().getReasonPhrase())
            );
        }
    }
    
    private static Optional<String> readContent(HttpEntity entity) {
        if (entity != null) {
            //try (InputStream instream = entity.getContent()) {
            //    return Optional.of(readContent(instream));
            //} catch (Exception e) {
            //    LOG.error("Ошибка чтения ответа ElasticSearch", e);
            //}
            try {
                return Optional.ofNullable(EntityUtils.toString(entity));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }
    
    private static String readContent(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = inputStream.read(buffer)) > 0;) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8);
    }
    
    public static class ESResponse {
        
        private final int statusCode;
        private final Optional<String> content;
        
        public ESResponse(int statusCode, Optional<String> content) {
            this.statusCode = statusCode;
            this.content = content;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public Optional<String> getContent() {
            return content;
        }
        
        public boolean isSuccess() {
            return statusCode == 200;
        }
    }
    
}
