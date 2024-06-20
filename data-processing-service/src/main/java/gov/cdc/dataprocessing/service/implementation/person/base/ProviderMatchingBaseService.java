package gov.cdc.dataprocessing.service.implementation.person.base;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.model.Coded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
public class ProviderMatchingBaseService extends MatchingBaseService{
    private static final Logger logger = LoggerFactory.getLogger(ProviderMatchingBaseService.class);

    public ProviderMatchingBaseService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CachingValueService cachingValueService,
            PrepareAssocModelHelper prepareAssocModelHelper) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, cachingValueService, prepareAssocModelHelper);
    }

    protected String telePhoneTxtProvider(PersonContainer personContainer) {
        String nameTeleStr = null;
        String carrot = "^";

        if (personContainer.getTheEntityLocatorParticipationDtoCollection() != null
                && personContainer.getTheEntityLocatorParticipationDtoCollection().size() > 0) {
            for (EntityLocatorParticipationDto entLocPartDT : personContainer.getTheEntityLocatorParticipationDtoCollection()) {
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getClassCd().equals(NEDSSConstant.TELE)) {
                    if (entLocPartDT.getCd() != null && entLocPartDT.getCd().equals(NEDSSConstant.PHONE)) {
                        TeleLocatorDto teleLocDT = entLocPartDT.getTheTeleLocatorDto();
                        if (teleLocDT != null && teleLocDT.getPhoneNbrTxt() != null && !teleLocDT.getPhoneNbrTxt().equals(""))
                            nameTeleStr = carrot + teleLocDT.getPhoneNbrTxt();

                    }
                }
            }
        }
        if (nameTeleStr != null)
        {
            nameTeleStr = getNameStringForProvider(personContainer) + nameTeleStr;
        }
        return nameTeleStr;
    }
    // Creating string for name and address for providers
    protected String nameAddressStreetOneProvider(PersonContainer personContainer) {
        String nameAddStr = null;
        String carrot = "^";
        if (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && !personContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
            for (EntityLocatorParticipationDto entLocPartDT : personContainer.getTheEntityLocatorParticipationDtoCollection()) {
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getClassCd().equals(NEDSSConstant.POSTAL)) {
                    if (entLocPartDT.getCd() != null
                            && entLocPartDT.getCd().equals(NEDSSConstant.OFFICE_CD)
                            && entLocPartDT.getUseCd() != null
                            && entLocPartDT.getUseCd().equals(NEDSSConstant.WORK_PLACE)) {
                        PostalLocatorDto postLocDT = entLocPartDT.getThePostalLocatorDto();
                        if (postLocDT != null) {
                            if ((postLocDT.getStreetAddr1() != null && !postLocDT.getStreetAddr1().equals(""))
                                    && (postLocDT.getCityDescTxt() != null && !postLocDT.getCityDescTxt().equals(""))
                                    && (postLocDT.getStateCd() != null && !postLocDT.getStateCd().equals(""))
                                    && (postLocDT.getZipCd() != null && !postLocDT.getZipCd().equals(""))) {
                                nameAddStr = carrot
                                        + postLocDT.getStreetAddr1() + carrot
                                        + postLocDT.getCityDescTxt() + carrot
                                        + postLocDT.getStateCd() + carrot
                                        + postLocDT.getZipCd();
                            }
                        }
                    }
                }
            }

        }
        if (nameAddStr != null)
            nameAddStr = getNameStringForProvider(personContainer) + nameAddStr;
        return nameAddStr;
    }
    protected Long processingProvider(PersonContainer personContainer, String businessObjLookupName, String businessTriggerCd) throws DataProcessingException {
        try {
            boolean callOrgHashCode= false;
            if(personContainer.isItNew() && personContainer.getThePersonDto().isItNew() && personContainer.getThePersonDto().getElectronicInd().equalsIgnoreCase("Y")
                    && !personContainer.getThePersonDto().isCaseInd()){
                callOrgHashCode= true;
                personContainer.getThePersonDto().setEdxInd("Y");
            }
            long personUid= persistingProvider(personContainer, "PROVIDER", businessTriggerCd );

            if(callOrgHashCode){
                try {
                    personContainer.getThePersonDto().setPersonUid(personUid);
                    /**
                     * THIS CODE HAS THING TO DO WITH ORGANIZATION
                     * */
                    setProvidertoEntityMatch(personContainer);
                } catch (Exception e) {
                    logger.error("EntityControllerEJB.setProvider method exception thrown for matching criteria:"+e);
                    throw new DataProcessingException("EntityControllerEJB.setProvider method exception thrown for matching criteria:"+e);
                }
            }
            return personUid;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }
    private Long persistingProvider(PersonContainer personContainer, String businessObjLookupName, String businessTriggerCd) throws DataProcessingException  {
        Long personUID ;
        String localId ;
        boolean isELRCase = false;
        try {
            localId = personContainer.getThePersonDto().getLocalId();
            if (localId == null) {
                personContainer.getThePersonDto().setEdxInd("Y");
                isELRCase = true;
            }

            Collection<EntityLocatorParticipationDto> collParLocator ;
            Collection<RoleDto> colRole;
            Collection<ParticipationDto> colPar ;


            collParLocator = personContainer.getTheEntityLocatorParticipationDtoCollection();
            if (collParLocator != null) {
                getEntityHelper().iterateELPDTForEntityLocatorParticipation(collParLocator);
                personContainer.setTheEntityLocatorParticipationDtoCollection(collParLocator);
            }

            colRole = personContainer.getTheRoleDtoCollection();
            if (colRole != null) {
                getEntityHelper().iterateRDT(colRole);
                personContainer.setTheRoleDtoCollection(colRole);
            }
            colPar = personContainer.getTheParticipationDtoCollection();
            if (colPar != null) {
                getEntityHelper().iteratePDTForParticipation(colPar);
                personContainer.setTheParticipationDtoCollection(colPar);
            }

            getPatientRepositoryUtil().preparePersonNameBeforePersistence(personContainer);

            if (personContainer.isItNew()) {
                Person p = getPatientRepositoryUtil().createPerson(personContainer);
                personUID = p.getPersonUid();
            }
            else {
                getPatientRepositoryUtil().updateExistingPerson(personContainer);
                personUID = personContainer.getThePersonDto().getPersonUid();

            }


        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return personUID;

    }
    private void setProvidertoEntityMatch(PersonContainer personContainer) throws Exception {

        Long entityUid = personContainer.getThePersonDto().getPersonUid();
        String identifier ;
        int identifierHshCd;
        List<String> identifierList;
        identifierList = getIdentifierForProvider(personContainer);
        if (!identifierList.isEmpty()) {
            for (String s : identifierList) {
                identifier = s;
                if (identifier != null) {
                    identifier = identifier.toUpperCase();
                }
                if (identifier != null) {
                    identifierHshCd = identifier.hashCode();
                    EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
                    edxEntityMatchDto.setEntityUid(entityUid);
                    edxEntityMatchDto.setTypeCd(NEDSSConstant.PRV);
                    edxEntityMatchDto.setMatchString(identifier);
                    edxEntityMatchDto.setMatchStringHashCode((long) identifierHshCd);
                    try {
                        getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDto);
                    } catch (Exception e) {
                        logger.error("Error in creating the EdxEntityMatchDT with identifier:" + identifier + " " + e.getMessage());
                        throw new DataProcessingException(e.getMessage(), e);
                    }
                }

            }

        }

        // Matching with name and address with street address1 alone
        String nameAddStrSt1;
        int nameAddStrSt1hshCd = 0;
        nameAddStrSt1 = nameAddressStreetOneProvider(personContainer);
        if (nameAddStrSt1 != null) {
            nameAddStrSt1 = nameAddStrSt1.toUpperCase();
            nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
        }

        // Continue for name Telephone with no extension
        String nameTelePhone ;
        int nameTelePhonehshCd = 0;
        nameTelePhone = telePhoneTxtProvider(personContainer);
        if (nameTelePhone != null) {
            nameTelePhone = nameTelePhone.toUpperCase();
            nameTelePhonehshCd = nameTelePhone.hashCode();
        }

        EdxEntityMatchDto edxEntityMatchDto = null;
        // Create the name and address with no street 2(only street1)
        if (nameAddStrSt1 != null) {
            edxEntityMatchDto = new EdxEntityMatchDto();
            edxEntityMatchDto.setEntityUid(entityUid);
            edxEntityMatchDto.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDto.setMatchString(nameAddStrSt1);
            edxEntityMatchDto.setMatchStringHashCode((long)nameAddStrSt1hshCd);
            try {
                getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDto);
            } catch (Exception e) {
                logger.error("Error in creating the EdxEntityMatchDT with nameAddStrSt1:" + nameAddStrSt1 + " " + e.getMessage());
                throw new DataProcessingException(e.getMessage(), e);
            }

        }
        // Create the name and address with nameTelePhone
        if (nameTelePhone != null) {
            edxEntityMatchDto = new EdxEntityMatchDto();
            edxEntityMatchDto.setEntityUid(entityUid);
            edxEntityMatchDto.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDto.setMatchString(nameTelePhone);
            edxEntityMatchDto.setMatchStringHashCode((long)nameTelePhonehshCd);
            try {
                getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDto);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        if (edxEntityMatchDto != null) {
            getPatientRepositoryUtil().updateExistingPersonEdxIndByUid(edxEntityMatchDto.getEntityUid());
        }

    }
    private List<String> getIdentifierForProvider(PersonContainer personContainer) throws DataProcessingException {
        String carrot = "^";
        List<String> identifierList = new ArrayList<>();
        String identifier = null;
        Collection<EntityIdDto> newEntityIdDtoColl = new ArrayList<>();
        try{
            if (personContainer.getTheEntityIdDtoCollection() != null
                    && personContainer.getTheEntityIdDtoCollection().size() > 0) {
                Collection<EntityIdDto> entityIdDtoColl = personContainer.getTheEntityIdDtoCollection();
                for (EntityIdDto entityIdDto : entityIdDtoColl) {
                    if ((entityIdDto.getStatusCd().equalsIgnoreCase(NEDSSConstant.STATUS_ACTIVE))) {
                        if ((entityIdDto.getRootExtensionTxt() != null)
                                && (entityIdDto.getTypeCd() != null)
                                && (entityIdDto.getAssigningAuthorityCd() != null)
                                && (entityIdDto.getAssigningAuthorityDescTxt() != null)
                                && (entityIdDto.getAssigningAuthorityIdType() != null)) {
                            identifier = entityIdDto.getRootExtensionTxt()
                                    + carrot + entityIdDto.getTypeCd() + carrot
                                    + entityIdDto.getAssigningAuthorityCd()
                                    + carrot
                                    + entityIdDto.getAssigningAuthorityDescTxt()
                                    + carrot + entityIdDto.getAssigningAuthorityIdType();
                        } else {
                            try {

//                                Coded coded = new Coded();
//                                coded.setCode(entityIdDT.getAssigningAuthorityCd());
//                                coded.setCodesetName(NEDSSConstant.EI_AUTH_PRV);
//                                coded.setCodesetTableName(DataTable.CODE_VALUE_GENERAL);
//                                NotificationSRTCodeLookupTranslationDAOImpl lookupDAO = new NotificationSRTCodeLookupTranslationDAOImpl();
//                                lookupDAO.retrieveSRTCodeInfo(coded);

                                Coded coded = new Coded();
                                coded.setCode(entityIdDto.getAssigningAuthorityCd());
                                coded.setCodesetName(NEDSSConstant.EI_AUTH);
                                coded.setCodesetTableName("Code_value_general");

                                //TODO: This call out to code value general Repos and Caching the recrod
//                                NotificationSRTCodeLookupTranslationDAOImpl lookupDAO = new NotificationSRTCodeLookupTranslationDAOImpl();
//                                lookupDAO.retrieveSRTCodeInfo(coded);

//                                var codedValueGenralList = getCachingValueService().findCodeValuesByCodeSetNmAndCode(coded.getCodesetName(), coded.getCode());


                                if (entityIdDto.getRootExtensionTxt() != null
                                        && entityIdDto.getTypeCd() != null
                                        && coded.getCode() != null
                                        && coded.getCodeDescription() != null
                                        && coded.getCodeSystemCd() != null) {
                                    identifier = entityIdDto.getRootExtensionTxt()
                                            + carrot + entityIdDto.getTypeCd() + carrot
                                            + coded.getCode() + carrot
                                            + coded.getCodeDescription() + carrot
                                            + coded.getCodeSystemCd();
                                }


                            } catch (Exception ex) {
                                String errorMessage = "The assigning authority "
                                        + entityIdDto.getAssigningAuthorityCd()
                                        + " does not exists in the system. ";
                                logger.debug(ex.getMessage() + errorMessage);
                            }
                        }
                        if (entityIdDto.getTypeCd() != null && !entityIdDto.getTypeCd().equalsIgnoreCase("LR")) {
                            newEntityIdDtoColl.add(entityIdDto);
                        }
                        if (identifier != null) {
                            identifierList.add(identifier);
                        }

                    }

                }

            }
            personContainer.setTheEntityIdDtoCollection(newEntityIdDtoColl);

        }catch (Exception ex) {
            String errorMessage = "Exception while creating hashcode for Provider entity IDs . ";
            logger.debug(ex.getMessage() + errorMessage);
            throw new DataProcessingException(errorMessage, ex);
        }
        return identifierList;

    }
    // getting Last name,First name for the providers
    private String getNameStringForProvider(PersonContainer personContainer) {
        String nameStr = null;
        if (personContainer.getThePersonNameDtoCollection() != null && personContainer.getThePersonNameDtoCollection().size() > 0) {
            Collection<PersonNameDto> personNameDtoColl = personContainer.getThePersonNameDtoCollection();
            for (PersonNameDto personNameDto : personNameDtoColl) {
                if (personNameDto.getNmUseCd() == null) {
                    String Message = "personNameDT.getNmUseCd() is null";
                    logger.debug(Message);
                }
                if (personNameDto.getNmUseCd() != null && personNameDto.getNmUseCd().equals(NEDSSConstant.LEGAL)) {
                    if (personNameDto.getLastNm() != null || personNameDto.getFirstNm() != null)
                        nameStr = personNameDto.getLastNm() + personNameDto.getFirstNm();
                }
            }
        }
        return nameStr;
    }

}
