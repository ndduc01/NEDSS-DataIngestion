package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface ILabProcessingService {
    String processingLabResult() throws DataProcessingConsumerException;
}
