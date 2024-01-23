package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.service.interfaces.IOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrganizationService implements IOrganizationService {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    public OrganizationService() {

    }

    public String processingOrganization() throws DataProcessingConsumerException {
        //TODO: Adding logic here
        try {
            return "processing org";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR");
        }

    }
}
