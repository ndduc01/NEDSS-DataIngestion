package gov.cdc.dataprocessing.service.implementation.manager;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.NbsInterfaceStatus;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.container.ObservationContainer;
import gov.cdc.dataprocessing.model.container.PageProxyContainer;
import gov.cdc.dataprocessing.model.container.PamProxyContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleAlgorothmManagerDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.NbsInterfaceRepository;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.service.implementation.investigation.DecisionSupportService;
import gov.cdc.dataprocessing.service.implementation.other.CachingValueService;
import gov.cdc.dataprocessing.service.interfaces.auth.ISessionProfileService;
import gov.cdc.dataprocessing.service.interfaces.other.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.other.IDataExtractionService;
import gov.cdc.dataprocessing.service.interfaces.other.IHandleLabService;
import gov.cdc.dataprocessing.service.interfaces.other.ILabProcessingService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerAggregationService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationMatchingService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import gov.cdc.dataprocessing.service.interfaces.organization.IOrganizationService;
import gov.cdc.dataprocessing.service.interfaces.person.IPersonService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.ManagerUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import static gov.cdc.dataprocessing.constant.ManagerEvent.EVENT_ELR;
@Service
@Slf4j
public class ManagerService implements IManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

    private final IObservationService observationService;
    private final IPersonService patientService;
    private final IOrganizationService organizationService;
    private final IProgramAreaService programAreaJurisdictionService;
    private final ILabProcessingService labProcessingService;

    private final IPublicHealthCaseService publicHealthCaseService;
    private final IEdxLogService edxLogService;

    private final IHandleLabService handleLabService;

    private final IDataExtractionService dataExtractionService;

    private final NbsInterfaceRepository nbsInterfaceRepository;

    private final ICatchingValueService cachingValueService;

    private final CacheManager cacheManager;

    private final ISessionProfileService sessionProfileService;

    private final IObservationMatchingService observationMatchingService;
    private final DecisionSupportService decisionSupportService;

    private final ManagerUtil managerUtil;

    private final IManagerAggregationService managerAggregationService;
    @Autowired
    public ManagerService(IObservationService observationService,
                          IPersonService patientService,
                          IOrganizationService organizationService,
                          IProgramAreaService programAreaJurisdictionService,
                          ILabProcessingService labProcessingService,
                          IPublicHealthCaseService publicHealthCaseService,
                          IEdxLogService edxLogService, IHandleLabService handleLabService,
                          IDataExtractionService dataExtractionService,
                          NbsInterfaceRepository nbsInterfaceRepository,
                          CachingValueService cachingValueService,
                          CacheManager cacheManager,
                          ISessionProfileService sessionProfileService,
                          IObservationMatchingService observationMatchingService,
                          DecisionSupportService decisionSupportService, ManagerUtil managerUtil,
                          IManagerAggregationService managerAggregationService) {
        this.observationService = observationService;
        this.patientService = patientService;
        this.organizationService = organizationService;
        this.programAreaJurisdictionService = programAreaJurisdictionService;
        this.labProcessingService = labProcessingService;
        this.publicHealthCaseService = publicHealthCaseService;
        this.edxLogService = edxLogService;
        this.handleLabService = handleLabService;
        this.dataExtractionService = dataExtractionService;
        this.nbsInterfaceRepository = nbsInterfaceRepository;
        this.cachingValueService = cachingValueService;
        this.cacheManager = cacheManager;
        this.sessionProfileService = sessionProfileService;
        this.observationMatchingService = observationMatchingService;
        this.decisionSupportService = decisionSupportService;
        this.managerUtil = managerUtil;
        this.managerAggregationService = managerAggregationService;
    }

    @Transactional
    public Object processDistribution(String eventType, String data) throws DataProcessingConsumerException {
        Object result = new Object();
        AuthUser profile = sessionProfileService.getSessionProfile("data-processing");
        if (profile != null) {
            AuthUtil.setGlobalAuthUser(profile);
            switch (eventType) {
                case EVENT_ELR:
                    result = processingELR(data);
                    break;
                default:
                    break;
            }
            AuthUtil.setGlobalAuthUser(null);
            return result;
        } else {
            throw new DataProcessingConsumerException("Invalid User");
        }

    }

    public Object processingHealthCase(String data) throws DataProcessingConsumerException {
        //TODO logic to for Health Case Flow
        Object result = new Object();
        try {
            //TODO: Public Health Case
            var healthCase = publicHealthCaseService.processingPublicHealthCase();

            //TODO: Auto Investigation
            var autoInvestigation = publicHealthCaseService.processingAutoInvestigation();
            return result;
        } catch (Exception e) {
            throw new DataProcessingConsumerException(e.getMessage(), result);
        }
    }

    public Object processingHandleLab(String data)  throws DataProcessingConsumerException {
        //TODO logic to for Health Case Flow
        Object result = new Object();
        try {
            //TODO: Handling Lab Code
            boolean isLabReviewed = true;
            boolean isActExist = true;
            if (isLabReviewed) {
                result = handleLabService.processingReviewedLab();
            }
            else {
                if (isActExist) {
                    result = handleLabService.processingNonReviewLabWithAct();
                }
                else {
                    result = handleLabService.processingNonReviewLabWithoutAct();
                }
            }

            //TODO: Auto Investigation
            return result;
        } catch (Exception e) {
            throw new DataProcessingConsumerException(e.getMessage(), result);
        }
    }

    public void processingEdxLog(String data) throws EdxLogException {
        edxLogService.processingLog();
    }

    private Object processingELR(String data) throws DataProcessingConsumerException {
        NbsInterfaceModel nbsInterfaceModel = null;
        Object result = new Object();
        try {

            Gson gson = new Gson();



            EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
            edxLabInformationDto.setStatus(NbsInterfaceStatus.Success);
            edxLabInformationDto.setUserName(AuthUtil.authUser.getUserId());

            //TODO: uncomment when deploy
            nbsInterfaceModel = gson.fromJson(data, NbsInterfaceModel.class);


            //TODO: uncomment when debug
//             nbsInterfaceModel = nbsInterfaceRepository.findById(Integer.valueOf(data)).get();
//             nbsInterfaceModel.setObservationUid(null);

            edxLabInformationDto.setNbsInterfaceUid(nbsInterfaceModel.getNbsInterfaceUid());
            //loadAndInitCachedValue();

            CompletableFuture<Void> cacheLoadingFuture = loadAndInitCachedValueAsync();
            cacheLoadingFuture.join();


            LabResultProxyContainer labResultProxyContainer = dataExtractionService.parsingDataToObject(nbsInterfaceModel, edxLabInformationDto);

            edxLabInformationDto.setLabResultProxyContainer(labResultProxyContainer);

            if(nbsInterfaceModel.getObservationUid() !=null && nbsInterfaceModel.getObservationUid()>0) {
                edxLabInformationDto.setRootObserbationUid(nbsInterfaceModel.getObservationUid());
            }
            Long aPersonUid = null;

            ObservationDto observationDto;

            // Checking for matching observation
            managerAggregationService.processingObservationMatching(edxLabInformationDto, labResultProxyContainer, aPersonUid);


            // This process patient, provider, nok, and organization. Then it will update both parsedData and edxLabInformationDto accordingly
            managerAggregationService.serviceAggregationAsync(labResultProxyContainer, edxLabInformationDto);


            // Hit when Obs is matched
            if(edxLabInformationDto.isLabIsUpdateDRRQ() || edxLabInformationDto.isLabIsUpdateDRSA())
            {
                managerUtil.setPersonUIDOnUpdate(aPersonUid, labResultProxyContainer);
            }
            edxLabInformationDto.setLabResultProxyContainer(labResultProxyContainer);

            String nbsOperation = edxLabInformationDto.isLabIsCreate() ? "ADD" : "EDIT";

            ObservationContainer orderTest = managerUtil.getObservationWithOrderDomainCode(labResultProxyContainer);

            String programAreaCd = orderTest.getTheObservationDto().getProgAreaCd();
            String jurisdictionCd = orderTest.getTheObservationDto().getJurisdictionCd();


            observationDto = observationService.processingLabResultContainer(labResultProxyContainer);

            if(edxLabInformationDto.isLabIsCreate()){
                edxLabInformationDto.setLabIsCreateSuccess(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_2);
            }

            edxLabInformationDto.setLocalId(observationDto.getLocalId());
            edxLabInformationDto.getEdxActivityLogDto().setBusinessObjLocalId(observationDto.getLocalId());
            edxLabInformationDto.setRootObserbationUid(observationDto.getObservationUid());

            if (observationDto.getProgAreaCd() != null && SrteCache.programAreaCodesMap.containsKey(observationDto.getProgAreaCd())) {
                edxLabInformationDto.setProgramAreaName(SrteCache.programAreaCodesMap.get(observationDto.getProgAreaCd()));
            }

            if(observationDto.getJurisdictionCd() != null && SrteCache.jurisdictionCodeMap.containsKey(observationDto.getJurisdictionCd())) {
                String jurisdictionName = SrteCache.jurisdictionCodeMap.get(observationDto.getJurisdictionCd());
                edxLabInformationDto.setJurisdictionName(jurisdictionName);
            }


            if(edxLabInformationDto.isLabIsCreateSuccess()&&(edxLabInformationDto.getProgramAreaName()==null
                    || edxLabInformationDto.getJurisdictionName()==null))
            {
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_1);
            }



            //TODO: Producing msg for Next Step
            // kafkaManagerProducer.sendData(healthCaseTopic, data);



            nbsInterfaceModel.setObservationUid(observationDto.getObservationUid().intValue());
            nbsInterfaceModel.setRecordStatusCd("COMPLETED_V2");
            nbsInterfaceRepository.save(nbsInterfaceModel);
            System.out.println("DONE");
            return result;
        } catch (Exception e) {
            if (nbsInterfaceModel != null) {
                nbsInterfaceModel.setRecordStatusCd("FAILED_V2");
                nbsInterfaceRepository.save(nbsInterfaceModel);
                System.out.println("ERROR");
            }

            throw new DataProcessingConsumerException(e.getMessage(), result);

        }
    }

    private Object initiatingInvestigationAndPublicHealthCase(
            LabResultProxyContainer labResultProxyContainer, 
            EdxLabInformationDto edxLabInformationDto,
            ObservationDto observationDto) throws DataProcessingException {


        if (edxLabInformationDto.isLabIsUpdateDRRQ()) {
            edxLabInformationDto.setLabIsUpdateSuccess(true);
            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_15);
        } else if (edxLabInformationDto.isLabIsUpdateDRSA()) {
            edxLabInformationDto.setLabIsUpdateSuccess(true);
            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_22);
        }

        if (edxLabInformationDto.isLabIsCreate()) {
            if (observationDto.getJurisdictionCd() != null && observationDto.getProgAreaCd() != null) {

                // This logic here determine whether logic is mark as review or not
                decisionSupportService.validateProxyVO(labResultProxyContainer, edxLabInformationDto);

                PageActProxyVO pageActProxyVO = null;
                PamProxyContainer pamProxyVO = null;
                PublicHealthCaseVO publicHealthCaseVO = null;
                Long phcUid = null;
                if (edxLabInformationDto.getAction() != null && edxLabInformationDto.getAction().equalsIgnoreCase(DecisionSupportConstants.MARK_AS_REVIEWED)) {
                    //Check for user security to mark as review lab
//                    checkSecurity(nbsSecurityObj, edxLabInformationDto, NBSBOLookup.OBSERVATIONLABREPORT, NBSOperationLookup.MARKREVIEWED, programAreaCd, jurisdictionCd);


                    //TODO: 3rd Flow
                    //hL7CommonLabUtil.markAsReviewedHandler(observationDto.getObservationUid(), edxLabInformationDto);
                    if (edxLabInformationDto.getAssociatedPublicHealthCaseUid() != null && edxLabInformationDto.getAssociatedPublicHealthCaseUid().longValue() > 0) {
                        edxLabInformationDto.setPublicHealthCaseUid(edxLabInformationDto.getAssociatedPublicHealthCaseUid());
                        edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_21);
                        edxLabInformationDto.setLabAssociatedToInv(true);
                    } else {
                        edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_11);
                    }

                }
                else if (edxLabInformationDto.getObject() != null)
                {
                    //Check for user security to create investigation
//                    checkSecurity(nbsSecurityObj, edxLabInformationDto, NBSBOLookup.INVESTIGATION, NBSOperationLookup.ADD, programAreaCd, jurisdictionCd);
                    if (edxLabInformationDto.getObject() instanceof PageProxyContainer) {
                        pageActProxyVO = (PageActProxyVO) edxLabInformationDto.getObject();
                        publicHealthCaseVO = pageActProxyVO.getPublicHealthCaseVO();
                    }
                    else
                    {
                        pamProxyVO = (PamProxyContainer) edxLabInformationDto.getObject();
                        publicHealthCaseVO = pamProxyVO.getPublicHealthCaseVO();
                    }

                    if (publicHealthCaseVO.getErrorText() != null)
                    {
                        //TODO: 3rd Flow
                        // requiredFieldError(publicHealthCaseVO.getErrorText(), edxLabInformationDto);
                    }

                    if (pageActProxyVO != null && observationDto.getJurisdictionCd() != null && observationDto.getProgAreaCd() != null)
                    {
                        //TODO: 3rd Flow
                        /*
                        Object object = nedssUtils.lookupBean(JNDINames.PAGE_PROXY_EJB);
                        PageProxyHome pageProxyHome = (PageProxyHome) javax.rmi.PortableRemoteObject.narrow(object, PageProxyHome.class);
                        PageProxyContainer pageProxy = pageProxyHome.create();
                        phcUid = pageProxy.setPageProxyWithAutoAssoc(NEDSSConstant.CASE, pageActProxyVO,
                                edxLabInformationDto.getRootObserbationUid(), NEDSSConstant.LABRESULT_CODE, null);
                        pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setPublicHealthCaseUid(phcUid);
                        */
                        edxLabInformationDto.setInvestigationSuccessfullyCreated(true);
                        edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_3);
                        edxLabInformationDto.setPublicHealthCaseUid(phcUid);
                        edxLabInformationDto.setLabAssociatedToInv(true);
                    }
                    else if (observationDto.getJurisdictionCd() != null && observationDto.getProgAreaCd() != null)
                    {
                        //TODO: 3rd Flow
                        /*
                        Object object = nedssUtils.lookupBean(JNDINames.PAM_PROXY_EJB);
                        PamProxyHome pamProxyHome = (PamProxyHome) javax.rmi.PortableRemoteObject.narrow(object, PamProxyHome.class);
                        PamProxyContainer pamProxy = pamProxyHome.create();
                        phcUid = pamProxy.setPamProxyWithAutoAssoc(pamProxyVO, edxLabInformationDto.getRootObserbationUid(),
                                NEDSSConstant.LABRESULT_CODE);
                        */
                        pamProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setPublicHealthCaseUid(phcUid);
                        edxLabInformationDto.setInvestigationSuccessfullyCreated(true);
                        edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_3);
                        edxLabInformationDto.setPublicHealthCaseUid(phcUid);
                        edxLabInformationDto.setLabAssociatedToInv(true);
                    }

                    if(edxLabInformationDto.getAction().equalsIgnoreCase(DecisionSupportConstants.CREATE_INVESTIGATION_WITH_NND_VALUE)){
                        //TODO: 3rd Flow
                        //Check for user security to create notification
                        //checkSecurity(nbsSecurityObj, edxLabInformationDto, NBSBOLookup.NOTIFICATION, NBSOperationLookup.CREATE, programAreaCd, jurisdictionCd);
                        /*
                        EDXActivityDetailLogDto edxActivityDetailLogDT = EdxCommonHelper.sendNotification(publicHealthCaseVO, edxLabInformationDto.getNndComment());
                        edxActivityDetailLogDT.setRecordType(EdxELRConstant.ELR_RECORD_TP);
                        edxActivityDetailLogDT.setRecordName(EdxELRConstant.ELR_RECORD_NM);
                        ArrayList<Object> details = (ArrayList<Object>)edxLabInformationDto.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails();
                        if(details==null){
                            details = new ArrayList<Object>();
                        }
                        details.add(edxActivityDetailLogDT);
                        edxLabInformationDto.getEdxActivityLogDto().setEDXActivityLogDTWithVocabDetails(details);
                        if(edxActivityDetailLogDT.getLogType()!=null && edxActivityDetailLogDT.getLogType().equals(EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure.name())){
                            if(edxActivityDetailLogDT.getComment()!=null && edxActivityDetailLogDT.getComment().indexOf(EdxELRConstant.MISSING_NOTF_REQ_FIELDS)!=-1){
                                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_8);
                                edxLabInformationDto.setNotificationMissingFields(true);
                            }
                            else{
                                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_10);
                            }
                            throw new DataProcessingException("MISSING NOTI REQUIRED: "+edxActivityDetailLogDT.getComment());
                        }else{
                            //edxLabInformationDto.setNotificationSuccessfullyCreated(true);
                            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_6);
                        }
                        */
                    }

                }
            }
        }
        return null;
    }
    private void loadAndInitCachedValue() throws DataProcessingException {

        if (SrteCache.loincCodesMap.isEmpty()) {
            cachingValueService.getAOELOINCCodes();
        }
        if (SrteCache.raceCodesMap.isEmpty()) {
            cachingValueService.getRaceCodes();
        }
        if (SrteCache.programAreaCodesMap.isEmpty()) {
            cachingValueService.getAllProgramAreaCodes();
        }
        if (SrteCache.jurisdictionCodeMap.isEmpty()) {
            cachingValueService.getAllJurisdictionCode();
        }
        if (SrteCache.jurisdictionCodeMapWithNbsUid.isEmpty()) {
            cachingValueService.getAllJurisdictionCodeWithNbsUid();
        }
        if (SrteCache.programAreaCodesMapWithNbsUid.isEmpty()) {
            cachingValueService.getAllProgramAreaCodesWithNbsUid();
        }
        if (SrteCache.elrXrefsList.isEmpty()) {
            cachingValueService.getAllElrXref();
        }


        var cache = cacheManager.getCache("srte");
        if (cache != null) {
            Cache.ValueWrapper valueWrapper;
            valueWrapper = cache.get("loincCodes");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof TreeMap) {
                    SrteCache.loincCodesMap = (TreeMap<String, String>) cachedObject;
                }
            }

            valueWrapper = cache.get("raceCodes");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof TreeMap) {
                    SrteCache.raceCodesMap = (TreeMap<String, String>) cachedObject;
                }
            }

            valueWrapper = cache.get("programAreaCodes");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof TreeMap) {
                    SrteCache.programAreaCodesMap = (TreeMap<String, String>) cachedObject;
                }
            }

            valueWrapper = cache.get("jurisdictionCode");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof TreeMap) {
                    SrteCache.jurisdictionCodeMap = (TreeMap<String, String>) cachedObject;
                }
            }

            valueWrapper = cache.get("programAreaCodesWithNbsUid");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof TreeMap) {
                    SrteCache.programAreaCodesMapWithNbsUid = (TreeMap<String, Integer>) cachedObject;
                }
            }

            valueWrapper = cache.get("jurisdictionCodeWithNbsUid");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof TreeMap) {
                    SrteCache.jurisdictionCodeMapWithNbsUid = (TreeMap<String, Integer>) cachedObject;
                }
            }

            valueWrapper = cache.get("elrXref");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof TreeMap) {
                    SrteCache.elrXrefsList = (List<ElrXref>) cachedObject;
                }
            }
        }
    }



    private CompletableFuture<Void> loadAndInitCachedValueAsync() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            if (SrteCache.loincCodesMap.isEmpty()) {
                try {
                    cachingValueService.getAOELOINCCodes();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.raceCodesMap.isEmpty()) {
                try {
                    cachingValueService.getRaceCodes();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.programAreaCodesMap.isEmpty()) {
                try {
                    cachingValueService.getAllProgramAreaCodes();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.jurisdictionCodeMap.isEmpty()) {
                try {
                    cachingValueService.getAllJurisdictionCode();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.jurisdictionCodeMapWithNbsUid.isEmpty()) {
                try {
                    cachingValueService.getAllJurisdictionCodeWithNbsUid();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.programAreaCodesMapWithNbsUid.isEmpty()) {
                try {
                    cachingValueService.getAllProgramAreaCodesWithNbsUid();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.elrXrefsList.isEmpty()) {
                try {
                    cachingValueService.getAllElrXref();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.coInfectionConditionCode.isEmpty()) {
                try {
                    cachingValueService.getAllOnInfectionConditionCode();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.conditionCodes.isEmpty() || SrteCache.investigationFormConditionCode.isEmpty()) {
                try {
                    cachingValueService.getAllConditionCode();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            // Retrieve cached values using Cache.ValueWrapper
            var cache = cacheManager.getCache("srte");
            if (cache != null) {
                Cache.ValueWrapper valueWrapper;
                valueWrapper = cache.get("loincCodes");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.loincCodesMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("raceCodes");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.raceCodesMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("programAreaCodes");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.programAreaCodesMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("jurisdictionCode");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.jurisdictionCodeMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("programAreaCodesWithNbsUid");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.programAreaCodesMapWithNbsUid = (TreeMap<String, Integer>) cachedObject;
                    }
                }

                valueWrapper = cache.get("jurisdictionCodeWithNbsUid");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.jurisdictionCodeMapWithNbsUid = (TreeMap<String, Integer>) cachedObject;
                    }
                }

                valueWrapper = cache.get("elrXref");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof List) {
                        SrteCache.elrXrefsList = (List<ElrXref>) cachedObject;
                    }
                }


                valueWrapper = cache.get("coInfectionConditionCode");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof List) {
                        SrteCache.coInfectionConditionCode = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("conditionCode");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof List) {
                        SrteCache.conditionCodes = (List<ConditionCode>) cachedObject;

                        // Populate Code for Investigation Form
                        for (ConditionCode obj : SrteCache.conditionCodes) {
                            SrteCache.investigationFormConditionCode.put(obj.getConditionCd(), obj.getInvestigationFormCd());
                        }

                    }
                }

            }
        });

        return future;
    }


}
