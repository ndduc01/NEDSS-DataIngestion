package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.service.interfaces.ILabProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LabProcessingService implements ILabProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(LabProcessingService.class);
    public LabProcessingService() {

    }
    public String processingLabResult() throws DataProcessingConsumerException {
        //TODO: Adding logic here
        try {
            return "processing lab result";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR");
        }

    }

}
