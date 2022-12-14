package moscow.ptnl.app.esu;

import moscow.ptnl.app.model.TopicType;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс реализуемый компонентом который должен быть в приложении.
 * 
 * @author m.kachalov
 */
public interface EsuTopicHelper {
    
    /**
     * Список топиков ЕСУ которые читает приложение.
     * 
     * @return 
     */
    List<TopicType> topics();

    String getParamsSuffix();
    
    /**
     * Поиск топика по имени.
     * 
     * @param topicName имя искомого топика
     * @return 
     */
    default Optional<TopicType> find(String topicName) {
        for(TopicType t : topics()) {
            if (t.getName().equals(topicName)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }
    
}