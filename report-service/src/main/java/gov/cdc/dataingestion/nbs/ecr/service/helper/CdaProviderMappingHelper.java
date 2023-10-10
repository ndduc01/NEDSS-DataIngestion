package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaProviderMapper;
import gov.cdc.dataingestion.nbs.ecr.model.provider.ProviderFieldCheck;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaProviderMappingHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.*;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;
import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.ACT_CODE_DISPLAY_NAME;

public class CdaProviderMappingHelper implements ICdaProviderMappingHelper {
    ICdaMapHelper cdaMapHelper;
    public CdaProviderMappingHelper(ICdaMapHelper cdaMapHelper) {
        this.cdaMapHelper = cdaMapHelper;
    }


    public CdaProviderMapper mapToProviderTop(EcrSelectedRecord input, POCDMT000040Section clinicalDocument,
                                              String inv168, int performerComponentCounter, int componentCounter,
                                              int performerSectionCounter) throws EcrCdaXmlException {

        try {
            CdaProviderMapper mapper = new CdaProviderMapper();

            if(input.getMsgProviders() != null && !input.getMsgProviders().isEmpty()) {
                for(int i = 0; i < input.getMsgProviders().size(); i++) {
                    var fieldCheck = mapToProviderTopFieldCheck( input,
                                     clinicalDocument,
                                     i,
                                     inv168,
                                     performerSectionCounter,
                                     performerComponentCounter,
                                     componentCounter);

                    clinicalDocument = fieldCheck.getClinicalDocument();
                    inv168 = fieldCheck.getInv168();
                    performerSectionCounter = fieldCheck.getPerformerSectionCounter();
                    performerComponentCounter = fieldCheck.getPerformerComponentCounter();
                    componentCounter = fieldCheck.getComponentCounter();

                }
            }
            mapper.setClinicalSection(clinicalDocument);
            mapper.setPerformerSectionCounter(performerSectionCounter);
            mapper.setComponentCounter(componentCounter);
            mapper.setPerformerComponentCounter(performerComponentCounter);
            mapper.setInv168(inv168);
            return mapper;
        } catch ( Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }

    private POCDMT000040Section mapToProviderTopFieldCheckDocCheck(POCDMT000040Section clinicalDocument) {
        if (clinicalDocument.getTitle() == null) {
            clinicalDocument.addNewTitle();
        }

        if (clinicalDocument.getCode() == null) {
            clinicalDocument.addNewCode();
        }
        return clinicalDocument;

    }

    private POCDMT000040Section mapToProviderTopFieldCheckActCheck(POCDMT000040Section clinicalDocument, int performerSectionCounter) {
        if (clinicalDocument.getEntryArray(performerSectionCounter).getAct() == null) {
            clinicalDocument.getEntryArray(performerSectionCounter).addNewAct();
            clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewParticipant();
        } else {
            clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewParticipant();
        }
        return clinicalDocument;

    }
    private ProviderFieldCheck mapToProviderTopFieldCheck(EcrSelectedRecord input,
                                            POCDMT000040Section clinicalDocument,
                                            int i,
                                            String inv168,
                                            int performerSectionCounter,
                                            int performerComponentCounter,
                                            int componentCounter
                                            ) throws EcrCdaXmlException {
        if (input.getMsgProviders().get(i).getPrvAuthorId() != null
                && input.getMsgProviders().get(i).getPrvAuthorId().equalsIgnoreCase(inv168)) {
            // ignore
        }
        else {
            int c = 0;

            clinicalDocument =  mapToProviderTopFieldCheckDocCheck( clinicalDocument);

            if (performerComponentCounter < 1) {
                componentCounter++;
                performerComponentCounter = componentCounter;

                var nestedCode = CODE;
                if (nestedCode.contains("-")) {
                    nestedCode = nestedCode.replaceAll("-", ""); // NOSONAR
                }
                clinicalDocument.getCode().setCode(nestedCode);
                clinicalDocument.getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                clinicalDocument.getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                clinicalDocument.getCode().setDisplayName(CODE_DISPLAY_NAME);
                clinicalDocument.getTitle().set(cdaMapHelper.mapToStringData(CLINICAL_TITLE));
            }

            performerSectionCounter = clinicalDocument.getEntryArray().length;

            if ( clinicalDocument.getEntryArray().length == 0) {
                clinicalDocument.addNewEntry();
                performerSectionCounter = 0;
            }
            else {
                performerSectionCounter = clinicalDocument.getEntryArray().length;
                clinicalDocument.addNewEntry();
            }

            clinicalDocument = mapToProviderTopFieldCheckActCheck( clinicalDocument,  performerSectionCounter);

            POCDMT000040Participant2 out = clinicalDocument.getEntryArray(performerSectionCounter).getAct().getParticipantArray(0);
            POCDMT000040Participant2 output = this.cdaMapHelper.mapToPSN(
                    input.getMsgProviders().get(i),
                    out
            );
            clinicalDocument.getEntryArray(performerSectionCounter).getAct().setParticipantArray(0, output);

            clinicalDocument.getEntryArray(performerSectionCounter).setTypeCode(XActRelationshipEntry.COMP);
            clinicalDocument.getEntryArray(performerSectionCounter).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
            clinicalDocument.getEntryArray(performerSectionCounter).getAct().setMoodCode(XDocumentActMood.EVN);

            if (clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode() == null){
                clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewCode();
            }
            clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCode("PSN");
            clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
            clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
            clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName(ACT_CODE_DISPLAY_NAME);

        }

        ProviderFieldCheck fieldCheck = new ProviderFieldCheck();
        fieldCheck.setClinicalDocument(clinicalDocument);
        fieldCheck.setInv168(inv168);
        fieldCheck.setPerformerSectionCounter(performerSectionCounter);
        fieldCheck.setPerformerComponentCounter(performerComponentCounter);
        fieldCheck.setComponentCounter(componentCounter);
        return fieldCheck;
    }
}
