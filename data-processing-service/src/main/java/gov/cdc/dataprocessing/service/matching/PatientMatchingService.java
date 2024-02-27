package gov.cdc.dataprocessing.service.matching;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.service.core.CheckingValueService;
import gov.cdc.dataprocessing.service.interfaces.IPatientMatchingService;
import gov.cdc.dataprocessing.service.matching.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.model.PersonId;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PatientMatchingService extends PatientMatchingBaseService implements IPatientMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(PatientMatchingService.class);
    private boolean multipleMatchFound = false;

    public PatientMatchingService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CheckingValueService checkingValueService) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, checkingValueService);
    }

    @Transactional
    public EdxPatientMatchDT getMatchingPatient(PersonVO personVO) throws DataProcessingException {
        Long patientUid = personVO.getThePersonDT().getPersonUid();
        String cd = personVO.getThePersonDT().getCd();
        String patientRole = personVO.getRole();
        EdxPatientMatchDT edxPatientFoundDT;
        EdxPatientMatchDT edxPatientMatchFoundDT = null;
        PersonId patientPersonUid;
        boolean matchFound = false;

        boolean newPatientCreationApplied = false;

        if (patientRole == null || patientRole.isEmpty() || patientRole.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_ROLE_CD)) {
            EdxPatientMatchDT localIdHashCode;
            String localId;
            int localIdhshCd = 0;
            localId = getLocalId(personVO);
            if (localId != null) {
                localId = localId.toUpperCase();
                localIdhshCd = localId.hashCode();
            }
            //NOTE: Matching Start here
            try {
                // Try to get the matching with the match string
                //	(was hash code but hash code had dups on rare occasions)
                edxPatientMatchFoundDT = getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(cd, localId);
                if (edxPatientMatchFoundDT.isMultipleMatch()){
                    multipleMatchFound = true;
                    matchFound = false;
                }
                else if (edxPatientMatchFoundDT != null && edxPatientMatchFoundDT.getPatientUid() != null) {
                    matchFound = true;
                }
            } catch (Exception ex) {
                logger.error("Error in geting the  matching Patient");
                throw new DataProcessingException("Error in geting the  matching Patient" + ex.getMessage(), ex);
            }

            if (localId != null) {
                localIdHashCode = new EdxPatientMatchDT();
                localIdHashCode.setTypeCd(NEDSSConstant.PAT);
                localIdHashCode.setMatchString(localId);
                localIdHashCode.setMatchStringHashCode((long) localIdhshCd);
            }

            // NOTE: Matching by Identifier
            if (!matchFound) {
                String IdentifierStr = null;
                int identifierStrhshCd = 0;

                List identifierStrList = getIdentifier(personVO);
                if (identifierStrList != null && !identifierStrList.isEmpty()) {
                    for (int k = 0; k < identifierStrList.size(); k++) {
                        IdentifierStr = (String) identifierStrList.get(k);
                        if (IdentifierStr != null) {
                            IdentifierStr = IdentifierStr.toUpperCase();
                            identifierStrhshCd = IdentifierStr.hashCode();
                        }

                        if (IdentifierStr != null) {
                            edxPatientFoundDT = new EdxPatientMatchDT();
                            edxPatientFoundDT.setTypeCd(NEDSSConstant.PAT);
                            edxPatientFoundDT.setMatchString(IdentifierStr);
                            edxPatientFoundDT.setMatchStringHashCode((long) identifierStrhshCd);
                            // Try to get the matching with the hash code
                            edxPatientMatchFoundDT = getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(cd, IdentifierStr);

                            if (edxPatientMatchFoundDT.isMultipleMatch()){
                                matchFound = false;
                                multipleMatchFound = true;
                            } else if (edxPatientMatchFoundDT.getPatientUid() == null || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT.getPatientUid() <= 0)) {
                                matchFound = false;
                            } else {
                                matchFound = true;
                                break;
                            }
                        }
                    }
                }
            }

            // NOTE: Matching with last name ,first name ,date of birth and current sex
            if (!matchFound) {
                String namesdobcursexStr = null;
                int namesdobcursexStrhshCd = 0;
                namesdobcursexStr = getLNmFnmDobCurSexStr(personVO);
                if (namesdobcursexStr != null) {
                    namesdobcursexStr = namesdobcursexStr.toUpperCase();
                    namesdobcursexStrhshCd = namesdobcursexStr.hashCode();
                    try {
                        if (namesdobcursexStr != null) {
                            edxPatientFoundDT = new EdxPatientMatchDT();
                            edxPatientFoundDT.setPatientUid(patientUid);
                            edxPatientFoundDT.setTypeCd(NEDSSConstant.PAT);
                            edxPatientFoundDT.setMatchString(namesdobcursexStr);
                            edxPatientFoundDT.setMatchStringHashCode((long) namesdobcursexStrhshCd);
                        }
                        edxPatientMatchFoundDT = getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(cd, namesdobcursexStr);
                        if (edxPatientMatchFoundDT.isMultipleMatch()){
                            multipleMatchFound = true;
                            matchFound = false;
                        } else if (edxPatientMatchFoundDT.getPatientUid() == null || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT.getPatientUid() <= 0)) {
                            matchFound = false;
                        } else {
                            matchFound = true;
                        }
                    } catch (Exception ex) {
                        logger.error("Error in geting the  matching Patient");
                        throw new DataProcessingException("Error in geting the  matching Patient" + ex.getMessage(), ex);
                    }
                }
            }

            // NOTE: Decision, Match Not Found, Start Person Creation
            if (!matchFound) {
                if (personVO.getTheEntityIdDTCollection() != null) {
                    //SORTING out existing EntityId
                    Collection<EntityIdDT> newEntityIdDTColl = new ArrayList<>();
                    Iterator<EntityIdDT> iter = personVO.getTheEntityIdDTCollection().iterator();
                    while (iter.hasNext()) {
                        EntityIdDT entityIdDT = iter.next();
                        if (entityIdDT.getTypeCd() != null && !entityIdDT.getTypeCd().equalsIgnoreCase("LR")) {
                            newEntityIdDTColl.add(entityIdDT);
                        }
                    }
                    personVO.setTheEntityIdDTCollection(newEntityIdDTColl);
                }
                try {
                    // NOTE: IF new patient then create
                    // IF existing patient, then query find it, then Get Parent Patient ID
                    if (personVO.getThePersonDT().getCd().equals(NEDSSConstant.PAT)) { // Patient
                        patientPersonUid = setAndCreateNewPerson(personVO);
                        personVO.getThePersonDT().setPersonParentUid(patientPersonUid.getPersonParentId());
                        personVO.getThePersonDT().setLocalId(patientPersonUid.getLocalId());
                        personVO.getThePersonDT().setPersonUid(patientPersonUid.getPersonId());
                        newPatientCreationApplied = true;
                    }
                } catch (Exception e) {
                    logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
                    throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
                }
                personVO.setPatientMatchedFound(false);
            }
            else {
                personVO.setPatientMatchedFound(true);
            }

            //NOTE: In this flow, if new patient, revision record is still get inserted
            //NOTE: if existing pateint, revision also insrted
            try {

                /**
                 * NOTE:
                 * Regarding New or Existing Patient
                 * This logic will do Patient Hash update and do Patient Revision update
                 * */

                /**
                 * 2.0 NOTE: if new patient flow, skip revision
                 * otherwise: go to update existing patient
                 * */


                if (!newPatientCreationApplied && personVO.getPatientMatchedFound()) {
                    personVO.getThePersonDT().setPersonParentUid(edxPatientMatchFoundDT.getPatientUid());
                    patientPersonUid = updateExistingPerson(personVO, NEDSSConstant.PAT_CR, personVO.getThePersonDT().getPersonParentUid());

                    personVO.getThePersonDT().setPersonParentUid(patientPersonUid.getPersonParentId());
                    personVO.getThePersonDT().setLocalId(patientPersonUid.getLocalId());
                    personVO.getThePersonDT().setPersonUid(patientPersonUid.getPersonId());
                }
                else if (newPatientCreationApplied) {
                    setPersonHashCdPatient(personVO);
                }
            } catch (Exception e) {
                logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage());
                throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
            }

        }
        return edxPatientMatchFoundDT;
    }

    public boolean getMultipleMatchFound() {
        return multipleMatchFound;
    }







}
