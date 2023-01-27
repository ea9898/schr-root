package moscow.ptnl.app.rs.dto;

import java.io.Serializable;

/**
 * Запрос содержащий блок "userContext" с информацией о вызывающем
 * метод клиенте.
 * 
 * @author m.kachalov
 */
public class UserContextRequest implements Serializable {
    
    //Контекста пользователя
    private UserContextObject userContext;
    
    public UserContextObject getUserContext() {
        return userContext;
    }

    public void setUserContext(UserContextObject userContext) {
        this.userContext = userContext;
    }
    
}
