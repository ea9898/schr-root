package moscow.ptnl.app.metrics.bind;

import io.micrometer.core.instrument.binder.MeterBinder;

import javax.management.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Родительский класс для получения данных из MBean.
 * 
 * @author m.kachalov
 */
public abstract class JMXMetrics implements MeterBinder {

    private final MBeanServer mBeanServer;
    private final Set<String> patterns; //шаблоны для фильтрации нужных MBean
    private Map<ObjectName, Map<String, MBeanAttributeInfo>> attributesInfo;
    
    public JMXMetrics(MBeanServer mBeanServer, String[] patterns){
        this.mBeanServer = mBeanServer;
        this.patterns = new HashSet<>(Arrays.asList(patterns));
        this.attributesInfo = new HashMap<>();
        this.getMBeans().forEach(name -> {
            try {
                attributesInfo.put(name, getMBeanAttributesInfo(name)); 
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    
    protected Map<String, MBeanAttributeInfo> getMBeanAttributes() {
        Map<String, MBeanAttributeInfo> attributes = new HashMap<>();
        getMBeans().forEach(b -> {
            try {
                attributes.putAll(getMBeanAttributesInfo(b));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return attributes;
    }
    
    protected abstract String[] getPropertiesNames();
    
    /**
     * Коллекция имен MBean выбранных по шаблонам.
     * 
     * @return 
     */
    protected Set<ObjectName> getMBeans() {
        Set<ObjectName> beans = new HashSet<>();
        this.patterns.forEach(p -> {
            try {
                beans.addAll(getMBeansByPattern(p));
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        return beans;
    }
    
    /**
     * 
     * @param pattern cинтаксис шаблона не RegExp, пример паттерна для всех MBean: "*.*:*"
     * @return
     * @throws MalformedObjectNameException
     */
    protected Set<ObjectName> getMBeansByPattern(String pattern) throws Exception {
        return getMBeanServer().queryNames(new ObjectName(pattern), null)
                .stream()
                .collect(Collectors.toSet());
    }
        
    protected Map<String, MBeanAttributeInfo> getMBeanAttributesInfo(ObjectName beanObjectName) throws Exception {
        MBeanInfo beanInfo = getMBeanServer().getMBeanInfo(beanObjectName);
        Map<String, MBeanAttributeInfo> attributes = new HashMap<>();
        for(MBeanAttributeInfo a : beanInfo.getAttributes()) {
            attributes.put(a.getName(), a);
        }
        return attributes; 
    }
    
    
    
    /**
     * Возвращает текущие значения атрибутов.
     * 
     * @param beanObjectName
     * @param attributesNames
     * @return 
     */
    protected Map<String, Object> getAttributesValues(ObjectName beanObjectName, String[] attributesNames) {
        Map<String, Object> attributes = new HashMap<>();
        try {
            for(String attributeName : attributesNames) {
                try {
                    attributes.put(attributeName, getMBeanServer().getAttribute(beanObjectName, attributeName)); //пришлось так не скопом, из-за того что в зазных MBean разные наборы свойств
                } catch (Exception e) {}
            }
            /*
            AttributeList attributeList = getMBeanServer().getAttributes(beanObjectName, attributesNames);
            attributeList.asList().forEach(a -> {
                System.out.println("name: " + a.getName());
                attributes.put(a.getName(), a.getValue());
            });
            */
        } catch (Exception e) {
            e.printStackTrace();            
        } 
        return attributes;
    }
    
    //TODO https://github.com/prometheus/jmx_exporter/blob/master/collector/src/main/java/io/prometheus/jmx/JmxScraper.java
    protected String processBeanValue(MBeanAttributeInfo info, Object value) {
        return String.valueOf(value);
    }
    
    public Map<String, Object> getValues(ObjectName beanName) {
        return getAttributesValues(beanName, getPropertiesNames());
    }

    /**
     * @return the MBeanServer
     */
    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }   

    public Set<ObjectName> getMBeansNames() {
        return attributesInfo.keySet();
    }
    
    public Map<String, MBeanAttributeInfo> getAttributesInfo(ObjectName beanObjectName) {
        return attributesInfo.get(beanObjectName);
    }
    
    public Optional<MBeanAttributeInfo> getAttributeInfo(ObjectName beanName, String name) {
        try {
            MBeanAttributeInfo info = getAttributesInfo(beanName).get(name);
            return (info != null) ? Optional.of(info): Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    
    /**
     * Переводит запись из "верблюжьей нотации", в "змеинную".
     * Пример: "requestCount" -&gt; "request_count"
     * Сильно упрощенный метод (сломается на нетипичных строках).
     * 
     * @param camelCaseText
     * @return 
     */
    protected static String toSnakeCase(String camelCaseText) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i< camelCaseText.length(); i++) {
            char c = camelCaseText.charAt(i);
            if (Character.isLetter(c) && Character.isUpperCase(c)) {
                if (i > 0 && i < camelCaseText.length() - 1) {
                    s.append('_');
                }
                s.append(Character.toLowerCase(c));
            } else {
                s.append(c);
            }
        }
        return s.toString();
    }
    
}
