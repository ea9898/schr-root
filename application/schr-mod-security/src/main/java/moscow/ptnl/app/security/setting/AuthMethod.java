package moscow.ptnl.app.security.setting;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mkachalov
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthMethod {
    
    private boolean enabled = true;
    private List<Long> permissions = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Long> getPermissions() {
        return permissions;
    }
    
    public long[] getAccessPermissions() {
        long[] accessPermissions = new long[permissions.size()];
        for (int i = 0; i < accessPermissions.length; i++) {
            accessPermissions[i] = permissions.get(i);
        }
        return accessPermissions;
    }

    public void setPermissions(List<Long> permissions) {
        this.permissions = permissions;
    }
    
}
