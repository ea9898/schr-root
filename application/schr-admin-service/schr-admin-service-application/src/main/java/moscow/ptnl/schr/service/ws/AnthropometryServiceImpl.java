package moscow.ptnl.schr.service.ws;

import moscow.ptnl.schr.service.anthropometry.AnthropometryInfoDTO;
import moscow.ptnl.schr.service.anthropometry.AnthropometryService;
import moscow.ptnl.app.util.service.BusinessUtil;
import ru.mos.emias.anthropometry.anthropometryservice.v1.AnthropometryServicePortType;
import ru.mos.emias.anthropometry.anthropometryservice.v1.Fault;
import ru.mos.emias.anthropometry.anthropometryservice.v1.types.AnthropometryItem;
import ru.mos.emias.anthropometry.anthropometryservice.v1.types.GetAnthropometryDataByPatientRequest;
import ru.mos.emias.anthropometry.anthropometryservice.v1.types.GetAnthropometryDataByPatientResponse;
import ru.mos.emias.anthropometry.anthropometryservice.v1.types.KeyValuePair;
import ru.mos.emias.anthropometry.anthropometryservice.v1.types.Options;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnthropometryServiceImpl implements AnthropometryService {

    @Autowired
    private AnthropometryServicePortType anthropometryServicePortType;

    @Override
    public List<AnthropometryInfoDTO> getAnthropometryDataByPatient(Long emiasId) throws Exception {
        GetAnthropometryDataByPatientRequest request = new GetAnthropometryDataByPatientRequest();
        request.setPatientId(emiasId);

        Options options = new Options();
        List<KeyValuePair> entry = options.getEntry();
        KeyValuePair keyValuePair = new KeyValuePair();
        keyValuePair.setValue("actual");
        keyValuePair.setKey("1");
        entry.add(keyValuePair);
        request.setOptions(options);

        try {
            List<AnthropometryInfoDTO> anthropometryInfoDTOList = new ArrayList<>();

            GetAnthropometryDataByPatientResponse response = anthropometryServicePortType.getAnthropometryDataByPatient(request);
            List<AnthropometryItem> measurementList = response.getResult().getMeasurement();
            for (AnthropometryItem measurement : measurementList) {
                AnthropometryInfoDTO anthropometryInfoDTO = new AnthropometryInfoDTO();
                anthropometryInfoDTO.setMeasurementDate(BusinessUtil.convertGregorianToLocalDate(measurement.getMeasurementDate()));
                anthropometryInfoDTO.setDocumentId(measurement.getDocumentID());
                anthropometryInfoDTO.setMeasurementType(measurement.getMeasurementType());
                anthropometryInfoDTO.setMeasurementValue((float) measurement.getMeasurementValue());
                anthropometryInfoDTO.setCentility(measurement.getCentility());
                anthropometryInfoDTO.setResultAssessmentId(measurement.getResultAssessmentID());
                anthropometryInfoDTOList.add(anthropometryInfoDTO);
            }
            return anthropometryInfoDTOList;
        } catch (Fault fault) {
            throw new Exception(fault.getMessage());
        }
    }

}
