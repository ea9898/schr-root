package moscow.ptnl.schr.service.consent;

import java.util.List;

public interface ConsentInfoService {

    List<ConsentInfoDTO> findConsentInfo(Long emiasId) throws Exception;

}
