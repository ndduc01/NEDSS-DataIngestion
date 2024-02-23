package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.EdxPatientMatchDT;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;

public interface IPatientMatchingService {
    EdxPatientMatchDT getMatchingPatient(PersonVO personVO) throws DataProcessingException;
    boolean getMultipleMatchFound();
}