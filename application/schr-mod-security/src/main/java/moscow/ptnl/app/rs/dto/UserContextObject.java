package moscow.ptnl.app.rs.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Контекст запроса пользователя.
 * 
 * @author m.kachalov
 */
public class UserContextObject implements Serializable {
    
    //Шифр системы-потребителя
    private String systemName;
    //Имя пользователя (логин)
    private String userName;
    //Коллекция полномочий пользователя (Полномочие)
    private List<Long> userRights;
    //Идентификатор исполнения должности пользователя
    private Long jobExecutionId;

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Long> getUserRights() {
        if (userRights == null) {
            userRights = new ArrayList<>();
        }
        return userRights;
    }

    public void setUserRights(List<Long> userRights) {
        this.userRights = userRights;
    }

    public Long getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }
    
    
}
