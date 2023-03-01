package moscow.ptnl.schr.service;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import moscow.ptnl.schr.error.ServiceException;
import moscow.ptnl.schr.service.dto.SyncDataByPatientIdRequest;
import moscow.ptnl.schr.service.dto.SyncDataByPatientIdResponse;

@Path("/")
public interface AdminService {
    
    String SERVICE_IMPL = "AdminService";
    
    @POST
    @Path("/syncDataByPatientId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON+ "; charset=UTF-8")
    SyncDataByPatientIdResponse syncDataByPatientId(SyncDataByPatientIdRequest request) throws ServiceException;
    
}
