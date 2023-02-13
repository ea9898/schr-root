package moscow.ptnl.app.esu.sae.listener.service;

import javax.xml.datatype.DatatypeFactory;
import moscow.ptnl.app.esu.sae.listener.service.consent.ConsentInfoDTO;
import moscow.ptnl.app.esu.sae.listener.service.consent.ConsentInfoService;
import moscow.ptnl.app.util.service.BusinessUtil;
import ru.mos.emias.consentinfo.consentinfoservice.v2.ConsentInfoServicePortType;
import ru.mos.emias.consentinfo.consentinfoservice.v2.Fault;
import ru.mos.emias.consentinfo.consentinfoservice.v2.types.FindConsentInfoRequest;
import ru.mos.emias.consentinfo.consentinfoservice.v2.types.FindConsentInfoResponse;
import ru.mos.emias.consentinfo.core.v2.ConsentInfo;
import ru.mos.emias.consentinfo.core.v2.ConsentList;
import ru.mos.emias.consentinfo.core.v2.ImmunoDrugsTn;
import ru.mos.emias.consentinfo.core.v2.KindItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

@Component
public class ConsentInfoServiceImpl implements ConsentInfoService {

    @Autowired
    private ConsentInfoServicePortType consentInfoServicePortType;

    @Override
    public List<ConsentInfoDTO> findConsentInfo(Long emiasId) throws Exception {

        FindConsentInfoRequest request = new FindConsentInfoRequest();
        request.setPatientId(emiasId);
        request.setIssueDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

        try {
            List<ConsentInfoDTO> consentInfoDTOList = new ArrayList<>();

            FindConsentInfoResponse response = consentInfoServicePortType.findConsentInfo(request);
            if (response.getResult().getTotalItemsCount() != 0) {
                for (ConsentList consent : response.getResult().getConsentList()) {
                    for (ConsentInfo consentInfo : consent.getConsentInfos().getConsentInfo()) {
                        ConsentInfoDTO consentInfoDTO = new ConsentInfoDTO();
                        consentInfoDTO.setConsentId(consentInfo.getId());

                        ConsentInfoDTO.DocumentedConsent documentedConsent = new ConsentInfoDTO.DocumentedConsent();
                        documentedConsent.setDocumentId(consentInfo.getDocumentId());
                        documentedConsent.setCreateDate( BusinessUtil.convertGregorianToLocalDate(consentInfo.getDocumentCreateDate()));
                        documentedConsent.setLocationId(consentInfo.getLocationId());
                        documentedConsent.setLocationName(consentInfo.getLocationName());
                        documentedConsent.setAllMedicalIntervention(consentInfo.isAllMedicalIntervention());

                        List<Long> interventionDetails = new ArrayList<>();
                        ConsentInfo.KindItems kindItems = consentInfo.getKindItems();
                        if (Objects.nonNull(kindItems)) {
                            for (KindItem kindItem : kindItems.getKindItem()) {
                                interventionDetails.add(kindItem.getId());
                            }
                        }
                        documentedConsent.setInterventionDetails(interventionDetails);

                        List<ConsentInfoDTO.DocumentedConsent.Immunodiagnostics> dtoImmunodiagnosticsList = new ArrayList<>();
                        if (Objects.nonNull(consentInfo.getImmunodiagnostics())) {
                            for (ConsentInfo.Immunodiagnostics.Immunodiagnostic immunodiagnostic : consentInfo.getImmunodiagnostics().getImmunodiagnostic()) {
                                ConsentInfoDTO.DocumentedConsent.Immunodiagnostics dtoImmunodiagnostics = new ConsentInfoDTO.DocumentedConsent.Immunodiagnostics();

                                if (Objects.nonNull(immunodiagnostic.getImmunoTestKind())) {
                                    dtoImmunodiagnostics.setImmunoKindCode(immunodiagnostic.getImmunoTestKind().getImmunoKind().getCode());
                                    dtoImmunodiagnostics.setInfectionCode(immunodiagnostic.getImmunoTestKind().getInfection().getCode());
                                }

                                List<Long> immunoDrugsTnCodeList = new ArrayList<>();
                                if (Objects.nonNull(immunodiagnostic.getImmunoDrugsTns())) {
                                    for (ImmunoDrugsTn immunoDrugsTn : immunodiagnostic.getImmunoDrugsTns().getImmunoDrugsTn()) {
                                        immunoDrugsTnCodeList.add(Long.valueOf(immunoDrugsTn.getCode()));
                                    }
                                }
                                dtoImmunodiagnostics.setImmunoDrugsTnCodeList(immunoDrugsTnCodeList);
                                dtoImmunodiagnosticsList.add(dtoImmunodiagnostics);
                            }
                        }
                        documentedConsent.setImmunodiagnostics(dtoImmunodiagnosticsList);

                        documentedConsent.setRepresentativeDocumentId(consentInfo.getRepresentativeDocumentId());
                        documentedConsent.setSignedByPatient(consentInfo.isSignedByPatient());
                        documentedConsent.setCancelReasonId(consentInfo.getCancelReason().getId());
                        documentedConsent.setCancelReasonOther(consentInfo.getCancelReasonOther());
                        documentedConsent.setMoId(consentInfo.getOrgId());
                        documentedConsent.setMoName(consentInfo.getOrgName());
                        consentInfoDTO.setDocumentedConsent(documentedConsent);
                        consentInfoDTO.setIssueDate(BusinessUtil.convertGregorianToLocalDateTime(consentInfo.getIssueDate()));
                        consentInfoDTO.setConsentFormId(consent.getConsentForm().getId());
                        consentInfoDTO.setConsentTypeId(consentInfo.getConsentType().getId());
                        consentInfoDTO.setRepresentativePhysicalId(consentInfo.getRepresentativePhysicalId());
                        consentInfoDTO.setRepresentativeLegalId(consentInfo.getRepresentativeLegalId());

                        consentInfoDTOList.add(consentInfoDTO);
                    }
                }
            }
            return consentInfoDTOList;
        } catch (Fault fault) {
            throw new Exception(fault.getMessage());
        }
    }
}
