package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.service.interfaces.IProgramAreaJurisdictionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProgramAreaJurisdictionService implements IProgramAreaJurisdictionService {
    private static final Logger logger = LoggerFactory.getLogger(ProgramAreaJurisdictionService.class);
    public ProgramAreaJurisdictionService() {

    }

    public String processingProgramArea() throws DataProcessingConsumerException {
        //TODO: Adding logic here
        try {
            return "processing program area";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR");
        }

    }

    public String processingJurisdiction() throws DataProcessingConsumerException {
        //TODO: Adding logic here
        try {
            return "processing jurisdiction";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR");
        }

    }
}
