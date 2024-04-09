package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.constant.NBSConstantUtil;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.lookup.LookupMappingDto;
import gov.cdc.dataprocessing.model.dto.lookup.PrePopMappingDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.LookupMappingRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.NbsUiMetaDataRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.service.interfaces.ILookupService;
import gov.cdc.dataprocessing.service.interfaces.other.ICatchingValueService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LookupService implements ILookupService {

    private final LookupMappingRepository lookupMappingRepository;
    private final NbsUiMetaDataRepository nbsUiMetaDataRepository;
    private final ICatchingValueService catchingValueService;

    public LookupService(LookupMappingRepository lookupMappingRepository,
                         NbsUiMetaDataRepository nbsUiMetaDataRepository,
                         ICatchingValueService catchingValueService) {
        this.lookupMappingRepository = lookupMappingRepository;
        this.nbsUiMetaDataRepository = nbsUiMetaDataRepository;
        this.catchingValueService = catchingValueService;
    }

    public TreeMap<Object, Object> getToPrePopFormMapping(String formCd) throws DataProcessingException {
        TreeMap<Object, Object> returnMap = null;
        try {
            returnMap = (TreeMap<Object, Object>) OdseCache.toPrePopFormMapping.get(formCd);
            if (returnMap == null) {
                    Collection<LookupMappingDto> qColl = getPrePopMapping();
                    createPrePopToMap(qColl);
                }
                returnMap = (TreeMap<Object, Object>) OdseCache.toPrePopFormMapping.get(formCd);

            return returnMap;
        } catch (Exception ex) {
            throw new DataProcessingException("The to prepop caching failed for form Cd: " + formCd);
        }

    }

    public TreeMap<Object,Object>  getQuestionMap() {
        TreeMap<Object,Object> questionMap = null;

        if (OdseCache.map != null && OdseCache.map.size() > 0) {
            return (TreeMap<Object,Object>) OdseCache.map;

        }

        try {
                Collection<Object>  qColl = getPamQuestions();
                questionMap = createQuestionMap(qColl);

        } catch (Exception e) {
            e.printStackTrace();
        }

        OdseCache.map.putAll(questionMap);
        return questionMap;
    }


    public TreeMap<Object,Object>  getDMBQuestionMapAfterPublish() {
        TreeMap<Object,Object> dmbQuestionMap = null;

        try {
            var res =  nbsUiMetaDataRepository.findDmbQuestionMetaData();
            var res2 = nbsUiMetaDataRepository.findGenericQuestionMetaData();
            Collection<Object>  qColl = null;
            if (res.isPresent()) {
                qColl = res.get();
                if (res2.isPresent()) {
                    qColl.addAll(res2.get());
                }
            }


            dmbQuestionMap = createDMBQuestionMap(qColl);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(dmbQuestionMap != null)
            OdseCache.dmbMap.putAll(dmbQuestionMap);
        return dmbQuestionMap;
    }


    public void fillPrePopMap() {

        if (OdseCache.fromPrePopFormMapping == null || OdseCache.fromPrePopFormMapping.size() == 0) {
            try {
                    Collection<LookupMappingDto> qColl = retrievePrePopMapping();
                    createPrePopFromMap(qColl);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (OdseCache.toPrePopFormMapping == null || OdseCache.toPrePopFormMapping.size() == 0) {
            try {
                    Collection<LookupMappingDto> qColl = retrievePrePopMapping();
                    createPrePopToMap(qColl);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }


    private Collection<LookupMappingDto> retrievePrePopMapping () {
        var res = lookupMappingRepository.getLookupMappings();
        return res.orElseGet(ArrayList::new);
    }

    private static void createPrePopFromMap(Collection<LookupMappingDto> coll) throws Exception {
        int count = 0;
        int loopcount = 0;
        int sizecount = 0;
        String currentFormCode = "";
        String previousFormCode = "";

        TreeMap<Object, Object>[] map = new TreeMap[coll.size()];
        PrePopMappingDto qMetadata = null;
        try {
            if (coll != null && coll.size() > 0) {
                Iterator<LookupMappingDto> ite = coll.iterator();
                while (ite.hasNext()) {
                    sizecount++;
                    qMetadata = new PrePopMappingDto(ite.next());

                    if (qMetadata.getFromFormCd() != null) {

                        if (loopcount == 0) {
                            previousFormCode = qMetadata.getFromFormCd();
                            String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                    : qMetadata.getFromQuestionIdentifier();
                            String fromAns = qMetadata.getFromAnswerCode();
                            map[count] = new TreeMap<Object, Object>();
                            if (!fromQuestionId.equals("")) {
                                if (fromAns != null) {
                                    fromQuestionId = fromQuestionId + "$" + fromAns;
                                    map[count].put(fromQuestionId, qMetadata);
                                }
                                PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                qMetadata1.setFromAnswerCode(null);
                                map[count].put(qMetadata.getFromQuestionIdentifier(), qMetadata1);
                                loopcount++;
                            }

                        } else {
                            currentFormCode = qMetadata.getFromFormCd();
                            if (currentFormCode.equals(previousFormCode)) {
                                String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                        : qMetadata.getFromQuestionIdentifier();
                                String fromAns = qMetadata.getFromAnswerCode();
                                if (!fromQuestionId.equals("")) {
                                    if (fromAns != null) {
                                        fromQuestionId = fromQuestionId + "$" + fromAns;
                                        map[count].put(fromQuestionId, qMetadata);
                                    }
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                    qMetadata1.setFromAnswerCode(null);
                                    map[count].put(qMetadata.getFromQuestionIdentifier(), qMetadata1);
                                }

                            } else {
                                OdseCache.fromPrePopFormMapping.put(previousFormCode, map[count]);
                                count = count + 1;
                                String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                        : qMetadata.getFromQuestionIdentifier();
                                String fromAns = qMetadata.getFromAnswerCode();
                                if (!fromQuestionId.equals("")) {
                                    map[count] = new TreeMap<Object, Object>();
                                    if (fromAns != null) {
                                        fromQuestionId = fromQuestionId + "$" + fromAns;
                                        map[count].put(fromQuestionId, qMetadata);
                                    }
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                    qMetadata1.setFromAnswerCode(null);
                                    map[count].put(qMetadata.getFromQuestionIdentifier(), qMetadata1);
                                }

                            }
                            previousFormCode = currentFormCode;
                            loopcount++;
                        }

                    }
                    if (sizecount == coll.size()) {
                        OdseCache.fromPrePopFormMapping.put(qMetadata.getFromFormCd(), map[count]);
                    }

                }

            }
        } catch (Exception ex) {
            throw new DataProcessingException("The from prepop caching failed due to question label :"
                    + qMetadata.getFromQuestionIdentifier() + " in form cd :" + qMetadata.getFromFormCd());
        }

    }

    private static void createPrePopToMap(Collection<LookupMappingDto> coll) throws Exception {
        int count = 0;
        int loopcount = 0;
        int sizecount = 0;
        String currentFormCode = "";
        String previousFormCode = "";

        TreeMap<Object, Object>[] map = new TreeMap[coll.size()];
        PrePopMappingDto qMetadata = null;
        try {
            if (coll != null && coll.size() > 0) {
                Iterator<LookupMappingDto> ite = coll.iterator();
                while (ite.hasNext()) {
                    sizecount++;
                    qMetadata = new PrePopMappingDto(ite.next());

                    if (qMetadata.getToFormCd() != null) {

                        if (loopcount == 0) {
                            previousFormCode = qMetadata.getToFormCd();
                            String toQuestionId = qMetadata.getToQuestionIdentifier() == null ? ""
                                    : qMetadata.getToQuestionIdentifier();
                            String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                    : qMetadata.getFromQuestionIdentifier();
                            toQuestionId = toQuestionId+'^'+fromQuestionId;
                            String fromAns = qMetadata.getFromAnswerCode();
                            String toQuestionIdWithAns = null;
                            map[count] = new TreeMap<Object, Object>();
                            if (!toQuestionId.equals("")) {
                                if (fromAns != null) {
                                    toQuestionIdWithAns = toQuestionId + "$" + fromAns;
                                    map[count].put(toQuestionIdWithAns, qMetadata);
                                }
                                PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                qMetadata1.setToAnswerCode(null);
                                map[count].put(toQuestionId, qMetadata1);
                                loopcount++;
                            }

                        } else {
                            currentFormCode = qMetadata.getToFormCd();
                            if (currentFormCode.equals(previousFormCode)) {
                                String toQuestionId = qMetadata.getToQuestionIdentifier() == null ? ""
                                        : qMetadata.getToQuestionIdentifier();
                                String fromAns = qMetadata.getFromAnswerCode();
                                String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                        : qMetadata.getFromQuestionIdentifier();
                                String toQuestionIdWithAns = null;
                                toQuestionId = toQuestionId+'^'+fromQuestionId;
                                if (!toQuestionId.equals("")) {
                                    if (fromAns != null) {
                                        toQuestionIdWithAns = toQuestionId + "$" + fromAns;
                                        map[count].put(toQuestionIdWithAns, qMetadata);
                                    }
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                    qMetadata1.setToAnswerCode(null);
                                    map[count].put(toQuestionId, qMetadata1);
                                }

                            } else {
                                OdseCache.toPrePopFormMapping.put(previousFormCode, map[count]);
                                count = count + 1;
                                String toQuestionId = qMetadata.getToQuestionIdentifier() == null ? ""
                                        : qMetadata.getToQuestionIdentifier();
                                String fromAns = qMetadata.getFromAnswerCode();
                                String fromQuestionId = qMetadata.getFromQuestionIdentifier() == null ? ""
                                        : qMetadata.getFromQuestionIdentifier();
                                toQuestionId = toQuestionId+'^'+fromQuestionId;
                                String toQuestionIdWithAns = null;
                                map[count] = new TreeMap<Object, Object>();
                                if (!toQuestionId.equals("")) {
                                    if (fromAns != null) {
                                        toQuestionIdWithAns = toQuestionId + "$" + fromAns;
                                        map[count].put(toQuestionIdWithAns, qMetadata);
                                    }
                                    PrePopMappingDto qMetadata1 = (PrePopMappingDto)qMetadata.deepCopy();
                                    qMetadata1.setToAnswerCode(null);
                                    map[count].put(toQuestionId, qMetadata1);
                                }

                            }
                            previousFormCode = currentFormCode;
                            loopcount++;
                        }

                    }
                    if (sizecount == coll.size()) {
                        OdseCache.toPrePopFormMapping.put(qMetadata.getToFormCd(), map[count]);
                    }

                }

            }
        } catch (Exception ex) {
            throw new DataProcessingException("The to prepop caching failed due to question Identifier :"
                    + qMetadata.getToQuestionIdentifier() + " in form cd :" + qMetadata.getToFormCd());
        }

    }


//    public QuestionMap getQuestionMapEJBRef() throws Exception {
//        if (qMap == null) {
//            NedssUtils nu = new NedssUtils();
//            Object objref = nu.lookupBean(JNDINames.QUESTION_MAP_EJB);
//            if (objref != null) {
//                QuestionMapHome home = (QuestionMapHome) PortableRemoteObject
//                        .narrow(objref, QuestionMapHome.class);
//                qMap = home.create();
//            }
//        }
//        return qMap;
//    }


    private TreeMap<Object,Object> createDMBQuestionMap(Collection<Object>  coll) throws Exception{
        TreeMap<Object, Object> qCodeMap = new TreeMap<Object, Object>();
        int count =0;
        int loopcount=0;
        int sizecount=0;
        String currentFormCode="";
        String previousFormCode="";

        //For Demo Purpose CHOLERA Metadata
        TreeMap<Object,Object> qInvFormChlrMap = new TreeMap<Object,Object>();
        TreeMap<Object, Object>[] map ;
        map = new TreeMap[coll.size()];
        NbsQuestionMetadata qMetadata = null;
        try{
            if (coll != null && coll.size() > 0) {
                Iterator ite = coll.iterator();
                while (ite.hasNext()) {
                    sizecount++;
                    qMetadata = (NbsQuestionMetadata) ite.next();
                    String dataType = qMetadata.getDataType();
                    List<CodeValueGeneral> aList = new ArrayList<>();
                    if(dataType != null && dataType.equals(NEDSSConstant.NBS_QUESTION_DATATYPE_CODED_VALUE)){
                        aList = catchingValueService.getGeneralCodedValue(qMetadata.getCodeSetNm());
                        qMetadata.setAList(aList);
                    }

                    if(qMetadata.getInvestigationFormCd() != null){

                        if(loopcount==0){
                            previousFormCode = qMetadata.getInvestigationFormCd();
                            String questionId = qMetadata.getQuestionIdentifier() == null ? "" : qMetadata.getQuestionIdentifier();
                            String ldfPageId = qMetadata.getLdfPageId() == null ? "" : qMetadata.getLdfPageId();
                            String uiMetadataUid = qMetadata.getNbsUiMetadataUid() == null ? "" : qMetadata.getNbsUiMetadataUid().toString();
                            if(!questionId.equals("")) {
                                map[count] = new TreeMap<Object, Object>();
                                map[count].put(questionId ,qMetadata);
                                loopcount++;
                            }

                        }else{
                            currentFormCode = qMetadata.getInvestigationFormCd();
                            if(currentFormCode.equals(previousFormCode)){
                                String questionId = qMetadata.getQuestionIdentifier() == null ? "" : qMetadata.getQuestionIdentifier();
                                String ldfPageId = qMetadata.getLdfPageId() == null ? "" : qMetadata.getLdfPageId();
                                String uiMetadataUid = qMetadata.getNbsUiMetadataUid() == null ? "" : qMetadata.getNbsUiMetadataUid().toString();
                                if(!questionId.equals("")) {
                                    map[count].put(questionId ,qMetadata);
                                }

                            }else{
                                qCodeMap.put(previousFormCode, map[count]);
                                count= count+1;
                                String questionId = qMetadata.getQuestionIdentifier() == null ? "" : qMetadata.getQuestionIdentifier();
                                String ldfPageId = qMetadata.getLdfPageId() == null ? "" : qMetadata.getLdfPageId();
                                String uiMetadataUid = qMetadata.getNbsUiMetadataUid() == null ? "" : qMetadata.getNbsUiMetadataUid().toString();
                                if(!questionId.equals("")) {
                                    map[count] = new TreeMap<Object, Object>();
                                    map[count].put(questionId ,qMetadata);
                                }

                            }
                            previousFormCode = currentFormCode;
                            loopcount++;


                        }

                    }
                    if(sizecount==coll.size())
                    {
                        qCodeMap.put(qMetadata.getInvestigationFormCd(), map[count]);
                    }

                }

            }
        }
        catch(Exception ex){
            throw new DataProcessingException("The caching failed due to question label :" + qMetadata.getQuestionLabel()+" in form cd :"+ qMetadata.getInvestigationFormCd());
        }

        return qCodeMap;
    }


    private Collection<LookupMappingDto>  getPrePopMapping() throws DataProcessingException {

        try {
            Collection<LookupMappingDto>  prepopMapping = retrievePrePopMapping();
            return prepopMapping;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    private Collection<Object>  getPamQuestions() throws DataProcessingException {

        try {
            var res = nbsUiMetaDataRepository.findPamQuestionMetaData();
            Collection<Object>  questions = new ArrayList<>();
            if (res.isPresent()) {
                questions  = nbsUiMetaDataRepository.findPamQuestionMetaData().get();
            }
            return questions;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private TreeMap<Object,Object> createQuestionMap(Collection<Object>  coll) {
        TreeMap<Object,Object> qCodeMap = new TreeMap<Object,Object>();
        TreeMap<Object,Object> qInvFormRVCTMap = new TreeMap<Object,Object>();
        if (coll != null && coll.size() > 0) {
            Iterator<Object>  ite = coll.iterator();
            while (ite.hasNext()) {
                NbsQuestionMetadata qMetadata = (NbsQuestionMetadata) ite
                        .next();
                if (qMetadata.getInvestigationFormCd().equals(
                        NBSConstantUtil.INV_FORM_RVCT))
                    qInvFormRVCTMap.put(qMetadata.getQuestionIdentifier(),
                            qMetadata);
            }
            qCodeMap.put(NBSConstantUtil.INV_FORM_RVCT, qInvFormRVCTMap);
        }
        return qCodeMap;
    }


}

