package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class HL7FileProcessComponent {
    private static Logger logger = LoggerFactory.getLogger(HL7FileProcessComponent.class);
    String msgType = "HL7";
    String validationActive = "false";
    @Autowired
    private RawELRService rawELRService;

    public String process(String body) throws Exception {
        String elrId="";
        try {
            System.out.println("*****Inside1 HL7FileProcessComponent*****");
            String hl7Str = body;

            logger.info("Message type:{}",msgType);
            logger.info("Validation Active:{}",validationActive);
            logger.info("HL7 Message:{}",hl7Str);

            RawERLDto rawERLDto = new RawERLDto();
            rawERLDto.setType(msgType);
            rawERLDto.setValidationActive(false);
            rawERLDto.setPayload(hl7Str);
            elrId= rawELRService.submission(rawERLDto);
        }
        catch(Exception e) {
            logger.error(e.getMessage());
        }
        return elrId;
    }
}