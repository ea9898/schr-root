package moscow.ptnl.app.security.mapper;

import moscow.ptnl.app.security.UserContext;

/**
 *
 * @author m.kachalov
 */
public interface UserContextMapper<T> {
    
    UserContext map(T dto);
    
    T map(UserContext entity);
    
}