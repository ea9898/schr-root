package moscow.ptnl.schr.service;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import moscow.ptnl.schr.error.ServiceException;
import moscow.ptnl.schr.service.dto.SearchByDslRequest;
import moscow.ptnl.schr.service.dto.SearchByDslResponse;

/**
 *
 * @author m.kachalov
 */
@Path("/")
public interface StudentPatientDataSearchService {
    
    String SERVICE_IMPL_V1 = "StudentPatientDataSearchService-V1";
    
    @POST
    @Path("/searchByDsl")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON+ "; charset=UTF-8")
    SearchByDslResponse searchByDsl(SearchByDslRequest request) throws ServiceException;
    
}
