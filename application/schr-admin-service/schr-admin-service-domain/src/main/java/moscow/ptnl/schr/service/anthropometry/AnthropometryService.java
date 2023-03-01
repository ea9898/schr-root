package moscow.ptnl.schr.service.anthropometry;

import java.util.List;

public interface AnthropometryService {

    List<AnthropometryInfoDTO> getAnthropometryDataByPatient(Long emiasId) throws Exception;

}
