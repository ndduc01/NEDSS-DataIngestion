package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaCaseMapper;
import gov.cdc.dataingestion.nbs.ecr.model.MessageAnswer;
import gov.cdc.dataingestion.nbs.ecr.model.MultiSelect;
import gov.cdc.dataingestion.nbs.ecr.model.cases.*;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaCaseMappingHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedCase;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgCaseAnswerDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgCaseAnswerRepeatDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgCaseParticipantDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.nedss.phdc.cda.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

import javax.xml.namespace.QName;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper.GetStringsBeforeCaret;
import static gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper.GetStringsBeforePipe;

public class CdaCaseMappingHelper implements ICdaCaseMappingHelper {

    ICdaMapHelper cdaMapHelper;
    public CdaCaseMappingHelper(ICdaMapHelper cdaMapHelper) {
        this.cdaMapHelper = cdaMapHelper;
    }


    //region CASE
    public CdaCaseMapper mapToCaseTop(EcrSelectedRecord input, POCDMT000040ClinicalDocument1 clinicalDocument,
                                      int componentCounter, int clinicalCounter, int componentCaseCounter,
                                      String inv168) throws EcrCdaXmlException {

        try {
            CdaCaseMapper mapper = new CdaCaseMapper();
            /**
             * CASE - 1st PHASE TESTED
             * **/
            if(!input.getMsgCases().isEmpty()) {
                clinicalDocument =  checkCaseStructComponent(clinicalDocument);

                // componentCounter should be zero initially
                for(int i = 0; i < input.getMsgCases().size(); i++) {
                    if (componentCounter < 0) {
                        componentCounter++;
                        var c = 0;

                        CdaCaseComponent caseComponent = checkCaseStructComponentWithSectionAndIndex(clinicalDocument, c);
                        clinicalDocument = caseComponent.getClinicalDocument();
                        c = caseComponent.getComponentIndex();

                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getId().setRoot(ROOT_ID);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getId().setExtension(inv168);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getId().setAssigningAuthorityName("LR");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCode("55752-0");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem(CODE_SYSTEM);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName(CODE_SYSTEM_NAME);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Clinical Information");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle().set(cdaMapHelper.mapToStringData("CLINICAL INFORMATION"));

                        componentCounter = c;

                    }
                    componentCaseCounter = componentCounter;
                    clinicalCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                    POCDMT000040StructuredBody output = clinicalDocument.getComponent().getStructuredBody();

                    var mappedCase = mapToCase(clinicalCounter, input.getMsgCases().get(i), output);
                    clinicalDocument.getComponent().setStructuredBody(mappedCase);
                    componentCounter = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length - 1;
                }

            }

            mapper.setClinicalDocument(clinicalDocument);
            mapper.setClinicalCounter(clinicalCounter);
            mapper.setComponentCounter(componentCounter);
            mapper.setComponentCaseCounter(componentCaseCounter);
            mapper.setInv168(inv168);
            return mapper;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }
    //endregion


    private POCDMT000040StructuredBody mapToCase(int entryCounter, EcrSelectedCase caseDto, POCDMT000040StructuredBody output) throws XmlException, ParseException, EcrCdaXmlException {
        int componentCaseCounter=output.getComponentArray().length -1;
        int repeats = 0;

        int counter= entryCounter;

        for (Map.Entry<String, Object> entry : caseDto.getMsgCase().getDataMap().entrySet()) {
            String name = entry.getKey();

            String value = null;
            if (entry.getValue() != null) {
                value = entry.getValue().toString();
            }

            if (checkInvalidField(name, caseDto)) {
                // do nothing
            }
            else if (value != null && !value.isEmpty()) {
                String questionId= "";

                questionId = this.cdaMapHelper.mapToQuestionId(name);


                if (name.equalsIgnoreCase("invConditionCd")) {
                    repeats = (int) caseDto.getMsgCase().getInvConditionCd().chars().filter(x -> x == '^').count();
                }


                if (repeats > 1) {
                    int c = 0;
                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length == 0) {
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    } else {
                        c = output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    }

                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation() == null) {
                        output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).addNewObservation();
                    }


                    var element = output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation();
                    var obs = mapTripletToObservation(
                            value,
                            questionId,
                            element
                    );
                    output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).setObservation(obs);
                    repeats = 0;
                }
                else {
                    int c = 0;
                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length == 0) {
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    } else {
                        c = output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                        output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                    }

                    if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation() == null) {
                        output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).addNewObservation();
                    }
                    var element = output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation();
                    POCDMT000040Observation obs = this.cdaMapHelper.mapToObservation(
                            questionId,
                            value,
                            element
                    );
                    output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).setObservation(obs);
                }
                counter++;
            }
        }


        int questionGroupCounter=0;
        int answerGroupCounter=0;
        String OldQuestionId=CHANGE;
        int sectionCounter = 0;
        int repeatComponentCounter=0;


        if (caseDto.getMsgCaseParticipants().size() > 0
                || caseDto.getMsgCaseAnswers().size() > 0 || caseDto.getMsgCaseAnswerRepeats().size() > 0) {

            /**CASE PARTICIPANT**/
            var participantModel = mapCaseParticipant(caseDto, output, componentCaseCounter, counter);
            output = participantModel.getStructuredBody();
            componentCaseCounter = participantModel.getComponentCaseCounter();
            counter = participantModel.getCounter();

            /**CASE ANSWER**/
            var caseAnsModel = mapCaseAnswer(caseDto, output, componentCaseCounter, counter, OldQuestionId);
            output = caseAnsModel.getStructuredBody();
            componentCaseCounter = caseAnsModel.getComponentCaseCounter();

            /**CASE PARTICIPANT REPEAT**/
            var caseParRepeatModel = mapCaseParticipantRepeat(caseDto, output, repeatComponentCounter,
                    componentCaseCounter, answerGroupCounter, questionGroupCounter, sectionCounter);
            output = caseParRepeatModel.getStructuredBody();

        }
        return output;
    }

    private CdaCaseParticipantRepeat mapCaseParticipantRepeat(EcrSelectedCase caseDto, POCDMT000040StructuredBody output,
                                                        int repeatComponentCounter, int componentCaseCounter,
                                                        int answerGroupCounter, int questionGroupCounter,
                                                        int sectionCounter) throws EcrCdaXmlException {
        CdaCaseParticipantRepeat model = new CdaCaseParticipantRepeat();
        if (caseDto.getMsgCaseAnswerRepeats() != null) {
            for(int i = 0; i < caseDto.getMsgCaseAnswerRepeats().size(); i++) {
                if (repeatComponentCounter == 0) {
                    componentCaseCounter++;
                    repeatComponentCounter = 1;
                    output.addNewComponent().addNewSection();
                }

                var out = output.getComponentArray(componentCaseCounter).getSection();

                var ot = mapToMultiSelect(caseDto.getMsgCaseAnswerRepeats().get(i),
                        answerGroupCounter, questionGroupCounter, sectionCounter, out);

                answerGroupCounter = ot.getAnswerGroupCounter();
                questionGroupCounter = ot.getQuestionGroupCounter();
                sectionCounter = ot.getSectionCounter();

                output.getComponentArray(componentCaseCounter).setSection(ot.getComponent());
            }
        }

        model.setStructuredBody(output);
        model.setRepeatComponentCounter(repeatComponentCounter);
        model.setComponentCaseCounter(componentCaseCounter);
        model.setAnswerGroupCounter(answerGroupCounter);
        model.setQuestionGroupCounter(questionGroupCounter);
        model.setSectionCounter(sectionCounter);
        return model;
    }

    private CdaCaseAnswer mapCaseAnswer(EcrSelectedCase caseDto, POCDMT000040StructuredBody output,
                               int componentCaseCounter, int counter, String oldQuestionId) throws EcrCdaXmlException {
        CdaCaseAnswer caseModel = new CdaCaseAnswer();
        if (caseDto.getMsgCaseAnswers() != null) {
            for(int i = 0; i < caseDto.getMsgCaseAnswers().size(); i++) {
                var out = output.getComponentArray(componentCaseCounter);
                var res = mapToMessageAnswer(
                        caseDto.getMsgCaseAnswers().get(i),
                        oldQuestionId,
                        counter,
                        out );

                oldQuestionId = res.getQuestionSeq();
                counter = res.getCounter();
                output.setComponentArray(componentCaseCounter, res.getComponent());
            }
        }

        caseModel.setComponentCaseCounter(componentCaseCounter);
        caseModel.setStructuredBody(output);
        caseModel.setCounter(counter);
        caseModel.setOldQuestionId(oldQuestionId);

        return caseModel;
    }

    private CdaCaseParticipant mapCaseParticipant(EcrSelectedCase caseDto, POCDMT000040StructuredBody output,
                                   int componentCaseCounter, int counter) throws EcrCdaXmlException {
        CdaCaseParticipant model = new CdaCaseParticipant();
        if (caseDto.getMsgCaseParticipants() != null) {
            for(int i = 0; i < caseDto.getMsgCaseParticipants().size(); i++) {
                int c = 0;
                if (output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length == 0) {
                    output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                }
                else {
                    c = output.getComponentArray(componentCaseCounter).getSection().getEntryArray().length;
                    output.getComponentArray(componentCaseCounter).getSection().addNewEntry();
                }

                if (!output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).isSetObservation()) {
                    output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).addNewObservation();
                }

                var element = output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).getObservation();

                POCDMT000040Observation out = mapToObsFromParticipant(
                        caseDto.getMsgCaseParticipants().get(i),
                        element
                );
                output.getComponentArray(componentCaseCounter).getSection().getEntryArray(c).setObservation(out);
                counter++;
            }
        }

        model.setStructuredBody(output);
        model.setComponentCaseCounter(componentCaseCounter);
        model.setCounter(counter);
        return model;
    }

    private POCDMT000040Observation mapTripletToObservation(String invConditionCd, String questionId, POCDMT000040Observation output) {
        output.setClassCode("OBS");
        output.setMoodCode(XActMoodDocumentObservation.EVN);
        List<String> repeats = GetStringsBeforePipe(invConditionCd);

        String tripletCodedValue =  "";
        PhdcQuestionLookUpDto questionLookUpDto = cdaMapHelper.mapToCodedQuestionType(questionId);
        output.getCode().setCode(questionLookUpDto.getQuesCodeSystemCd());
        output.getCode().setCodeSystem(questionLookUpDto.getQuesCodeSystemDescTxt());
        output.getCode().setDisplayName(questionLookUpDto.getQuesDisplayName());

        for(int i = 0; i < repeats.size(); i++) {
            if (repeats.size() == 1) {
                tripletCodedValue = invConditionCd;
            } else {
                tripletCodedValue = repeats.get(i);
            }
            var caretStringList = GetStringsBeforeCaret(repeats.get(i));

            if (tripletCodedValue.length() > 0 && caretStringList.size() == 4) {
                String code = caretStringList.get(0);
                String displayName = caretStringList.get(1);
                String CODE_SYSTEM_NAME = caretStringList.get(2);
                String codeSystem = caretStringList.get(3);
                int c = 0;
                if (output.getValueArray().length == 0) {
                    output.addNewValue();
                }
                else {
                    c = output.getValueArray().length;
                    output.addNewValue();
                }

                CE ce = CE.Factory.newInstance();
                ce.setCode(code);
                ce.setCodeSystem(codeSystem);
                ce.setCodeSystemName(CODE_SYSTEM_NAME);
                ce.setDisplayName(displayName);
                output.getValueArray(c).set(ce);
            }
        }

        return output;

    }

    private POCDMT000040Observation mapToObsFromParticipant(EcrMsgCaseParticipantDto in, POCDMT000040Observation out) throws EcrCdaXmlException {
        String localId = "";
        String questionCode ="";

        if (in.getAnswerTxt() != null && !in.getAnswerTxt().isEmpty()) {
            localId = in.getAnswerTxt();
        }

        if (in.getQuestionIdentifier() != null && !in.getQuestionIdentifier().isEmpty()) {
            questionCode = in.getQuestionIdentifier();
        }

        return this.cdaMapHelper.mapToObservation(questionCode, localId, out);
    }


    private MessageAnswer mapToMessageAnswer(EcrMsgCaseAnswerDto in, String questionSeq, int counter, POCDMT000040Component3 out) throws EcrCdaXmlException {
        String dataType="";
        int sequenceNbr = 0;

        MessageAnswer model = new MessageAnswer();
        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = getValueFromMap(entry);

            if (name.equals(COL_DATA_TYPE) && !in.getDataType().isEmpty()) {
                dataType = in.getDataType();
            }
            else if (name.equals(COL_SEQ_NBR) && !in.getSeqNbr().isEmpty()) {
                sequenceNbr = out.getSection().getEntryArray(counter).getObservation().getValueArray().length;
            }
            else if (dataType.equalsIgnoreCase(DATA_TYPE_CODE) || dataType.equalsIgnoreCase(COUNTY)) {
                out = setMessageAnswerArrayValue(name, in, sequenceNbr, counter, out);
            }

            else if (dataType.equalsIgnoreCase("TEXT") || dataType.equalsIgnoreCase(DATA_TYPE_NUMERIC)) {
                out = setMessageAnswerAnsText(name, in, out, counter);
            }
            else if (dataType.equalsIgnoreCase("DATE")) {
                out = setMessageAnswerDate(name, in, out, counter, value);
            }

            if (!in.getQuestionIdentifier().isEmpty()) {
                var caseAnsQuesIdentifier = setMessageAnswerQuestionIdentifier(in, out,
                        questionSeq, counter, sequenceNbr);
                out = caseAnsQuesIdentifier.getComponent();
                questionSeq = caseAnsQuesIdentifier.getQuestionSeq();
                counter = caseAnsQuesIdentifier.getCounter();
                sequenceNbr = caseAnsQuesIdentifier.getSequenceNbr();
            }
            else if (!in.getQuesCodeSystemCd().isEmpty()) {
                out.getSection().getEntryArray(counter).getObservation().getCode().setCodeSystem(in.getQuesCodeSystemCd());
            }
            else if (!in.getQuesCodeSystemDescTxt().isEmpty()) {
                out.getSection().getEntryArray(counter).getObservation().getCode().setCodeSystemName(in.getQuesCodeSystemDescTxt());
            }
            else if (!in.getQuesDisplayTxt().isEmpty()) {
                out.getSection().getEntryArray(counter).getObservation().getCode().setDisplayName(in.getQuesDisplayTxt());
            }

        }

        model.setQuestionSeq(questionSeq);
        model.setCounter(counter);
        model.setComponent(out);
        return model;
    }

    private String getValueFromMap(Map.Entry<String, Object> entry) {
        String value = null;
        if (entry.getValue() != null) {
            value = entry.getValue().toString();
        }
        return value;
    }
    private CdaCaseAnsQuesIdentifier setMessageAnswerQuestionIdentifier(EcrMsgCaseAnswerDto in, POCDMT000040Component3 out,
                                                    String questionSeq, int counter,
                                                    int sequenceNbr) {
        CdaCaseAnsQuesIdentifier model = new CdaCaseAnsQuesIdentifier();
        if (in.getQuestionIdentifier().equalsIgnoreCase(questionSeq)) {
            // ignore
        }
        else {
            if (questionSeq.equalsIgnoreCase(CHANGE)) {
                // ignore
            }
            else {
                counter++;
                sequenceNbr = 0;
            }
            questionSeq = in.getQuestionIdentifier();

            if (out.getSection().getEntryArray().length - 1 < counter) {
                out.getSection().addNewEntry().addNewObservation().addNewCode();
            }
            out.getSection().getEntryArray(counter).getObservation().setClassCode("OBS");
            out.getSection().getEntryArray(counter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
            out.getSection().getEntryArray(counter).getObservation().getCode().setCode(in.getQuestionIdentifier());
        }

        model.setComponent(out);
        model.setCounter(counter);
        model.setSequenceNbr(sequenceNbr);
        model.setQuestionSeq(questionSeq);
        return model;
    }

    private POCDMT000040Component3 setMessageAnswerDate(String name, EcrMsgCaseAnswerDto in, POCDMT000040Component3 out, int counter,
                                      String value) throws EcrCdaXmlException {
        if (name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()) {
            var element = out.getSection().getEntryArray(counter).getObservation().getValueArray(0);
            XmlCursor cursor = element.newCursor();
            cursor.toFirstAttribute();
            cursor.insertAttributeWithValue(new QName(NAME_SPACE_URL, "type"), "TS");
            cursor.insertAttributeWithValue(value, ""); // As per your code, it's empty
            var ot = cdaMapHelper.mapToTsType(in.getAnswerTxt()).toString();
            cursor.setAttributeText(new QName("", value), ot);
            cursor.dispose();
        }

        return out;
    }

    private POCDMT000040Component3 setMessageAnswerAnsText(String name, EcrMsgCaseAnswerDto in, POCDMT000040Component3 out, int counter) {
        if (name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()) {
            var element = out.getSection().getEntryArray(counter).getObservation();
            var ot = cdaMapHelper.mapToSTValue(in.getAnswerTxt(), element);
            out.getSection().getEntryArray(counter).setObservation((POCDMT000040Observation) ot);
        }
        return out;
    }
    private POCDMT000040Component3 setMessageAnswerArrayValue(String name, EcrMsgCaseAnswerDto in,
                                            int sequenceNbr, int counter,
                                            POCDMT000040Component3 out) {
        CE ce = CE.Factory.newInstance();
        if (name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()) {
            ce.getTranslationArray(0).setCode(in.getAnswerTxt());
        }
        else if (name.equals(COL_ANS_CODE_SYSTEM_CD) && !in.getAnsCodeSystemCd().isEmpty()) {
            ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
        }
        else if (name.equals(COL_ANS_CODE_SYSTEM_DESC_TXT) && !in.getAnsCodeSystemDescTxt().isEmpty()) {
            ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
        }
        else if (name.equals(COL_ANS_DISPLAY_TXT) && !in.getAnsDisplayTxt().isEmpty()) {
            ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
        }
        else if (name.equals(COL_ANS_TO_CODE) && !in.getAnsToCode().isEmpty()) {
            ce.setCode(in.getAnsToCode());
        }
        else if (name.equals(COL_ANS_TO_CODE_SYSTEM_CD) && !in.getAnsToCodeSystemCd().isEmpty()) {
            ce.setCodeSystem(in.getAnsToCodeSystemCd());
        }
        else if (name.equals(COL_ANS_TO_CODE_SYSTEM_DESC_TXT) && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
            ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
        }
        else if (name.equals(COL_ANS_TO_DISPLAY_NM) && !in.getAnsToDisplayNm().isEmpty()) {
            if(ce.getTranslationArray(0).getDisplayName().equals("OTH^")) {
                ce.setDisplayName(ce.getTranslationArray(0).getDisplayName());
            }
            else {
                ce.setDisplayName(in.getAnsToDisplayNm());
            }
        }
        out.getSection().getEntryArray(counter).getObservation().getValueArray(sequenceNbr).set(ce);
        return out;
    }

    private POCDMT000040Section checkClinicalSectionCode(POCDMT000040Section out) {
        if (out.getCode() == null) {
            out.addNewCode();
        }
        if (out.getTitle() == null) {
            out.addNewTitle();
        }
        return out;
    }

    private MultiSelect mapToMultiSelect(EcrMsgCaseAnswerRepeatDto in,
                                         int answerGroupCounter,
                                         int questionGroupCounter,
                                         int sectionCounter, POCDMT000040Section out) throws EcrCdaXmlException {

        out = checkClinicalSectionCode(out);

        out.getCode().setCode("1234567-RPT");
        out.getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
        out.getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
        out.getCode().setDisplayName("Generic Repeating Questions Section");
        out.getTitle().set(cdaMapHelper.mapToStringData("REPEATING QUESTIONS"));
        int componentCounter = 0;
        String dataType="DATE";
        int seqNbr = 0;
        int questionGroupSeqNbr = 0;
        int answerGroupSeqNbr = 0;
        String questionIdentifier="";

        MultiSelect model = new MultiSelect();

        if (out.getEntryArray().length == 0) {
            out.addNewEntry().addNewOrganizer();

        } else {
            sectionCounter = out.getEntryArray().length;
            out.addNewEntry().addNewOrganizer();
        }

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name= entry.getKey();
            String value= entry.getValue().toString();


            if (name.equalsIgnoreCase(COL_QUES_GROUP_SEQ_NBR)) {
                questionGroupSeqNbr = Integer.valueOf(in.getQuestionGroupSeqNbr());
            }
            else if (name.equalsIgnoreCase(COL_ANS_GROUP_SEQ_NBR)) {

                CdaCaseMultiGroupSeqNumber param = new CdaCaseMultiGroupSeqNumber();
                param.setOut(out);
                param.setSectionCounter(sectionCounter);
                param.setAnswerGroupSeqNbr(answerGroupSeqNbr);
                param.setAnswerGroupCounter(answerGroupCounter);
                param.setQuestionGroupSeqNbr(questionGroupSeqNbr);
                param.setQuestionGroupCounter(questionGroupCounter);
                param.setComponentCounter(componentCounter);
                var multiGroupModel = mapMultiSelectAnsGroupSeqNumber(param, in);

                out = multiGroupModel.getOut();
                sectionCounter = multiGroupModel.getSectionCounter();
                answerGroupSeqNbr = multiGroupModel.getAnswerGroupSeqNbr();
                answerGroupCounter = multiGroupModel.getAnswerGroupCounter();
                questionGroupSeqNbr = multiGroupModel.getQuestionGroupSeqNbr();
                questionGroupCounter = multiGroupModel.getQuestionGroupCounter();
                componentCounter = multiGroupModel.getComponentCounter();
            }
            else if (name.equalsIgnoreCase(COL_DATA_TYPE)) {
                dataType = in.getDataType();
            }
            else if (name.equalsIgnoreCase(COL_SEQ_NBR)) {
                seqNbr = Integer.valueOf(in.getSeqNbr());
            }

            if(dataType.equalsIgnoreCase(DATA_TYPE_CODE) || dataType.equalsIgnoreCase("CODED_COUNTY")){
                out = mapMultiSelectCodedCounty(
                         name,
                         in,
                         out,
                         sectionCounter,
                         componentCounter,
                         seqNbr);
            }
            else if ((dataType.equalsIgnoreCase("TEXT") || dataType.equalsIgnoreCase(DATA_TYPE_NUMERIC)) &&
                    name.equals(COL_ANS_TXT)) {
                out = mapMultiSelectDataNumericType(in,
                        out,
                        questionIdentifier, sectionCounter, componentCounter
                );
            }
            else if(dataType.equalsIgnoreCase("DATE")){
                out = mapMultiSelectDate( in, out, name, value, sectionCounter, componentCounter);
            }

            if(name.equals(COL_QUES_IDENTIFIER)){
                out = mapMultiSelectQuesIdentifier( in, out, sectionCounter, componentCounter);
                questionIdentifier= value;
            }
            else if(name.equals(COL_QUES_CODE_SYSTEM_CD)){
                out = mapMultiSelectCodeSystemQues( in, out, sectionCounter, componentCounter);
            }
            else if(name.equals(COL_QUES_CODE_SYSTEM_DESC_TXT)){
                out = mapMultiSelectCodeSystemDescQues(in, out, sectionCounter, componentCounter);
            }
            else if(name.equals(COL_QUES_DISPLAY_TXT)){
                out = mapMultiSelectDisplayQues(in, out, sectionCounter, componentCounter);
            }
        }

        model.setAnswerGroupCounter(answerGroupCounter);
        model.setQuestionGroupCounter(questionGroupCounter);
        model.setSectionCounter(sectionCounter);
        model.setComponent(out);
        return model;
    }

    private POCDMT000040Section mapMultiSelectDisplayQues(EcrMsgCaseAnswerRepeatDto in,
                                                                 POCDMT000040Section out,
                                                                 int sectionCounter,
                                                                 int componentCounter) {
        if (out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation() == null) {
            out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).addNewObservation().addNewCode();
        } else {
            out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewCode();
        }
        out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                .getCode().setDisplayName(in.getQuesDisplayTxt());
        return out;
    }

    private POCDMT000040Section mapMultiSelectCodeSystemDescQues(EcrMsgCaseAnswerRepeatDto in,
                                                             POCDMT000040Section out,
                                                             int sectionCounter,
                                                             int componentCounter) {
        if (out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation() == null) {
            out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).addNewObservation().addNewCode();
        } else {
            out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewCode();
        }
        out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                .getCode().setCodeSystemName(in.getQuesCodeSystemDescTxt());

        return out;
    }

    private POCDMT000040Section mapMultiSelectCodeSystemQues(EcrMsgCaseAnswerRepeatDto in,
                                                             POCDMT000040Section out,
                                                             int sectionCounter,
                                                             int componentCounter) {
        if (out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation() == null) {
            out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).addNewObservation().addNewCode();
        } else {
            out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewCode();
        }
        out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                .getCode().setCodeSystem(in.getQuesCodeSystemCd());
        return out;
    }

    private POCDMT000040Section mapMultiSelectQuesIdentifier(EcrMsgCaseAnswerRepeatDto in,
                                                             POCDMT000040Section out,
                                                             int sectionCounter,
                                                             int componentCounter) {
        if (out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation() == null) {
            out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).addNewObservation().addNewCode();
        } else {
            out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewCode();
        }
        out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation()
                .getCode().setCode(in.getQuestionIdentifier());
        return out;
    }

    private POCDMT000040Section mapMultiSelectDate(EcrMsgCaseAnswerRepeatDto in,
                                    POCDMT000040Section out,
                                    String name,
                                    String value,
                                    int sectionCounter,
                                    int componentCounter) throws EcrCdaXmlException {
        if(name.equals(COL_ANS_TXT)){
            int c = 0;

            var size = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray().length;

            if (size == 0) {
                out.getEntryArray(sectionCounter).getOrganizer().addNewComponent();
            } else {
                componentCounter = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray().length;
                out.getEntryArray(sectionCounter).getOrganizer().addNewComponent();
            }

            out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).addNewObservation();


            if (out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray().length == 0) {
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewValue();
            } else {
                c = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray().length;
                out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewValue();
            }
            var element = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(c);
            XmlCursor cursor = element.newCursor();
            cursor.toFirstChild();
            cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "TS");
            cursor.setAttributeText(new QName("", value), null);
            if (name.equals(COL_ANS_TXT)) {
                String newValue = cdaMapHelper.mapToTsType(in.getAnswerTxt()).toString();
                cursor.setAttributeText(new QName("", value), newValue);
            }
            cursor.dispose();
        }

        return out;
    }
    private POCDMT000040Section mapMultiSelectDataNumericType(EcrMsgCaseAnswerRepeatDto in,
                                               POCDMT000040Section out,
                                               String questionIdentifier,
                                               int sectionCounter,
                                               int componentCounter
                                               ) {
        if(questionIdentifier.equalsIgnoreCase("NBS243") ||
                questionIdentifier.equalsIgnoreCase("NBS290")) {
            var element = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();
            var ot = cdaMapHelper.mapToObservationPlace(
                    in.getAnswerTxt(),
                    element);
            out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation) ot);
        }
        else {
            var element = out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();
            var ot = cdaMapHelper.mapToSTValue(
                    in.getAnswerTxt(),
                    element);
            out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation)ot);
        }

        return out;
    }

    private POCDMT000040Section mapMultiSelectCodedCounty(
            String name,
            EcrMsgCaseAnswerRepeatDto in,
            POCDMT000040Section out,
            int sectionCounter,
            int componentCounter,
            int seqNbr) {
        CE ce = CE.Factory.newInstance();
        if (name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()) {
            ce.getTranslationArray(0).setCode(in.getAnswerTxt());
        }
        else if (name.equals(COL_ANS_CODE_SYSTEM_CD) && !in.getAnsCodeSystemCd().isEmpty()) {
            ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
        }
        else if (name.equals(COL_ANS_CODE_SYSTEM_DESC_TXT) && !in.getAnsCodeSystemDescTxt().isEmpty()) {
            ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
        }
        else if (name.equals(COL_ANS_DISPLAY_TXT) && !in.getAnsDisplayTxt().isEmpty()) {
            ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
        }
        else if (name.equals(COL_ANS_TO_CODE) && !in.getAnsToCode().isEmpty()) {
            ce.setCode(in.getAnsToCode());
        }
        else if (name.equals(COL_ANS_TO_CODE_SYSTEM_CD) && !in.getAnsToCodeSystemCd().isEmpty()) {
            ce.setCodeSystem(in.getAnsToCodeSystemCd());
        }
        else if (name.equals(COL_ANS_TO_CODE_SYSTEM_DESC_TXT) && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
            ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
        }
        else if (name.equals(COL_ANS_TO_DISPLAY_NM) && !in.getAnsToDisplayNm().isEmpty()) {
            ce.setDisplayName(in.getAnsToDisplayNm());

        }

        out.getEntryArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(seqNbr).set(ce);

        return out;
    }

    private CdaCaseMultiGroupSeqNumber mapMultiSelectAnsGroupSeqNumber(CdaCaseMultiGroupSeqNumber param, EcrMsgCaseAnswerRepeatDto in) {
        if (param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer() == null) {
            param.getOut().getEntryArray(param.getSectionCounter()).addNewOrganizer();
        }

        param.setAnswerGroupSeqNbr(Integer.valueOf(in.getAnswerGroupSeqNbr()));
        if((param.getAnswerGroupSeqNbr() == param.getAnswerGroupCounter()) && (param.getQuestionGroupSeqNbr() == param.getQuestionGroupCounter())){
            param.setComponentCounter(param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().getComponentArray().length);
        }
        else {
            if (param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().getCode() == null) {
                param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().addNewCode();
            }

            if (param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().getStatusCode() == null) {
                param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().addNewStatusCode();
            }

            param.setQuestionGroupCounter(param.getQuestionGroupSeqNbr());
            param.setAnswerGroupCounter(param.getAnswerGroupSeqNbr());

            param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().getCode().setCode(String.valueOf(param.getQuestionGroupSeqNbr()));
            param.getOut().getEntryArray(param.getSectionCounter()).setTypeCode(XActRelationshipEntry.COMP);
            param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);;
            param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().setMoodCode("EVN");
            param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().getStatusCode().setCode("completed");;
            param.setComponentCounter(0);
        }

        if (param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().getComponentArray().length == 0) {
            param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().addNewComponent().addNewObservation();
        } else {
            param.setComponentCounter(param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().getComponentArray().length);
            param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().addNewComponent().addNewObservation();
        }

        param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().getComponentArray(param.getComponentCounter()).getObservation().setClassCode("OBS");
        param.getOut().getEntryArray(param.getSectionCounter()).getOrganizer().getComponentArray(param.getComponentCounter()).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);

        return  param;
    }


    private POCDMT000040ClinicalDocument1 checkCaseStructComponent(
            POCDMT000040ClinicalDocument1 clinicalDocument
    ) {
        if (clinicalDocument.getComponent() == null) {
            clinicalDocument.addNewComponent().addNewStructuredBody().addNewComponent();

        }
        else {
            if (!clinicalDocument.getComponent().isSetStructuredBody()) {
                clinicalDocument.getComponent().addNewStructuredBody();
            }
            else {
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                }
            }
        }
        return clinicalDocument;
    }

    private CdaCaseComponent checkCaseStructComponentWithSectionAndIndex(
            POCDMT000040ClinicalDocument1 clinicalDocument,
            int c
    ) {
        CdaCaseComponent caseComponent = new CdaCaseComponent();
        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
            clinicalDocument.getComponent().getStructuredBody().addNewComponent();
        } else {
            c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
            clinicalDocument.getComponent().getStructuredBody().addNewComponent();
        }


        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).addNewSection().addNewId();
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
        }
        else {
            if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getId() == null) {
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewId();
            }
            if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode() == null) {
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
            }
            if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle() == null) {
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
            }
        }

        caseComponent.setComponentIndex(c);
        caseComponent.setClinicalDocument(clinicalDocument);
        return caseComponent;
    }

    private boolean checkInvalidField(String name, EcrSelectedCase caseDto) {
        boolean patLocalIdFailedCheck = (name.equalsIgnoreCase(PAT_LOCAL_ID_CONST) && caseDto.getMsgCase().getPatLocalId() == null)
                || (name.equalsIgnoreCase(PAT_LOCAL_ID_CONST)  && caseDto.getMsgCase().getPatLocalId() != null && caseDto.getMsgCase().getPatLocalId().isEmpty());
        boolean patInvEffTimeFailedCheck = name.equalsIgnoreCase("invEffectiveTime")  && caseDto.getMsgCase().getInvEffectiveTime() == null;
        boolean patInvAuthorIdFailedCheck = (name.equalsIgnoreCase("invAuthorId") && caseDto.getMsgCase().getInvAuthorId() == null)
                || (name.equalsIgnoreCase("invAuthorId") && caseDto.getMsgCase().getInvAuthorId() != null && caseDto.getMsgCase().getInvAuthorId().isEmpty());
        return patLocalIdFailedCheck || patInvEffTimeFailedCheck || patInvAuthorIdFailedCheck;
    }
}
