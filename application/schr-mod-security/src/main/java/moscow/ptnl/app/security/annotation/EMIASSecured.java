package moscow.ptnl.app.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для методов, доступ к которым должен быть ограничен исходя из
 * настроек безопасности.
 * 
 * @author mkachalov
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EMIASSecured {

    Class<? extends Exception> faultClass();
}
