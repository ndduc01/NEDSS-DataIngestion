package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxEntityMatch;
import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxPatientMatch;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.matching.EdxEntityMatchRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.matching.EdxPatientMatchRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.EdxPatientMatchStoredProcRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EdxPatientMatchRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(EdxPatientMatchRepositoryUtil.class);
    private final EdxPatientMatchRepository edxPatientMatchRepository;
    private final EdxEntityMatchRepository edxEntityMatchRepository;
    private final EdxPatientMatchStoredProcRepository edxPatientMatchStoreProcRepository;


    public EdxPatientMatchRepositoryUtil(EdxPatientMatchRepository edxPatientMatchRepository,
                                         EdxEntityMatchRepository edxEntityMatchRepository,
                                         EdxPatientMatchStoredProcRepository edxPatientMatchStoreProcRepository) {
        this.edxPatientMatchRepository = edxPatientMatchRepository;
        this.edxEntityMatchRepository = edxEntityMatchRepository;
        this.edxPatientMatchStoreProcRepository = edxPatientMatchStoreProcRepository;
    }

    @Transactional
    public EdxPatientMatchDto getEdxPatientMatchOnMatchString(String typeCd, String matchString) throws DataProcessingException {
        if (typeCd == null || matchString == null) {
            return new EdxPatientMatchDto();
        }
        try {
            return edxPatientMatchStoreProcRepository.getEdxPatientMatch(typeCd, matchString);

        } catch (Exception ex) {
            logger.error("Exception in EdxPatientMatchDAO.getEdxPatientMatchOnMatchString for typeCd=" + typeCd + " match string=" + matchString + ": ERROR = " + ex);
            throw new DataProcessingException(ex.toString(), ex);
        }
    }

    @Transactional
    public EdxEntityMatchDto getEdxEntityMatchOnMatchString(String typeCd, String matchString) throws DataProcessingException {
        if (typeCd == null || matchString == null) {
            return new EdxEntityMatchDto();
        }
        try {
            return edxPatientMatchStoreProcRepository.getEdxEntityMatch(typeCd, matchString);

        } catch (Exception ex) {
            logger.error("Exception in EdxPatientMatchDAO.getEdxPatientMatchOnMatchString for typeCd=" + typeCd + " match string=" + matchString + ": ERROR = " + ex);
            throw new DataProcessingException(ex.toString(), ex);
        }
    }


    @Transactional
    public void saveEdxEntityMatch(EdxEntityMatchDto edxEntityMatchDto) {
        EdxEntityMatch model = new EdxEntityMatch(edxEntityMatchDto);
        edxEntityMatchRepository.save(model);
    }


    public EdxPatientMatch setEdxPatientMatchDT(EdxPatientMatchDto edxPatientMatchDto) {
        EdxPatientMatch edxPatientMatch = new EdxPatientMatch(edxPatientMatchDto);
        edxPatientMatchRepository.save(edxPatientMatch);
        return edxPatientMatch;
    }

    public void deleteEdxPatientMatchDTColl(Long patientUid) {
        edxPatientMatchRepository.deleteByPatientUidAndMatchStringNotLike(patientUid);
    }
}
