package moscow.ptnl.app.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserContext implements Serializable {

    //Шифр системы-потребителя
    private String systemName;

    //Имя пользователя (логин)
    private String userName;

    //Коллекция полномочий пользователя 
    private List<Long> userRights = new ArrayList<>();

    //Идентификатор исполнения должности пользователя
    private Long jobExecutionId;

    private String hostIp;

    private String hostName;

    public UserContext() {
    }

    public UserContext(String systemName, String userName, List<Long> userRights, Long jobExecutionId, String hostIp, String hostName) {
        this.systemName = systemName;
        this.userName = userName;
        this.userRights = userRights;
        this.jobExecutionId = jobExecutionId;
        this.hostIp = hostIp;
        this.hostName = hostName;
    }

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

    public void setJobExecutionId(long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
