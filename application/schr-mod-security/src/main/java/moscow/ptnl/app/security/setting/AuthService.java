package moscow.ptnl.app.security.setting;

import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mkachalov
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthService {
    
    private boolean enabled = true;
    private Map<String, AuthMethod> authMethods = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, AuthMethod> getAuthMethods() {
        return authMethods;
    }

    public void setAuthMethods(Map<String, AuthMethod> authMethods) {
        this.authMethods = authMethods;
    }
    
}
