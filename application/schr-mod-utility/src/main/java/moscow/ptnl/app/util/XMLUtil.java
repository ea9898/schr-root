package moscow.ptnl.app.util;

import java.io.StringReader;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBIntrospector;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author m.kachalov
 */
public final class XMLUtil {
    
    private static final Logger LOG = LoggerFactory.getLogger(XMLUtil.class);
    
    /**
     * Десериализация XML-строки в Java-объект (JAXB).
     * Для работы пакет с классом должен содержать одно из двух:
     * ObjectFactory.class или jaxb.index
     * 
     * @param <T> 
     * @param type корневой класс 
     * @param xmlData строка в формате XML
     * @return
     * @throws Exception
     */
    public static <T> T jaxbUnmarshall(Class<T> type, String xmlData) throws Exception {
        try {
            JAXBContext context = JAXBContext.newInstance(type.getPackage().getName(), type.getClassLoader());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) JAXBIntrospector.getValue(unmarshaller.unmarshal(new StreamSource(new StringReader(xmlData))));
        } catch (Exception e) {
            LOG.error("unmarshalling moscow.ptnl.domain.error", e);
            throw new Exception(e);
        }
    }
    
    /**
     * Десериализация XML-строки в Java-объект (JAXB).
     * 
     * @param <T> 
     * @param xmlData строка в формате XML
     * @param types корневой класс и другие нужные классы
     * @return
     * @throws Exception
     */
    public static <T> T jaxbUnmarshall(String xmlData, Class<T> ... types) throws Exception {
        try {
            JAXBContext context = JAXBContext.newInstance(types);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) JAXBIntrospector.getValue(unmarshaller.unmarshal(new StreamSource(new StringReader(xmlData))));
        } catch (Exception e) {
            LOG.error("unmarshalling moscow.ptnl.domain.error", e);
            throw new Exception(e);
        }
    }
    
}
