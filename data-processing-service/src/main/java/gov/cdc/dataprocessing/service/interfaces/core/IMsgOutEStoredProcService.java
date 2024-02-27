package gov.cdc.dataprocessing.service.interfaces.core;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;

public interface IMsgOutEStoredProcService {
    void callUpdateSpecimenCollDateSP(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException;
}