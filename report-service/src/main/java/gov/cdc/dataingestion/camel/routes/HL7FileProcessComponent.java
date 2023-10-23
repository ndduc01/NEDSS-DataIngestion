package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class HL7FileProcessComponent {
    private static Logger logger = LoggerFactory.getLogger(HL7FileProcessComponent.class);

    @Autowired
    private RawELRService rawELRService;


    public void process(String body) throws Exception {
        try {
//            String msgType = (String) exchange.getIn().getHeader("msgType");
//            String validationActive = (String) exchange.getIn().getHeader("validationActive");
//            String hl7Str = exchange.getIn().getBody(String.class);
            System.out.println("*****Inside HL7FileProcessComponent*****");
            String msgType = "HL7";
            String validationActive = "false";//(String) exchange.getIn().getHeader("validationActive");
            String hl7Str = body;

            logger.info("Message type:{}",msgType);
            logger.info("Validation Active:{}",validationActive);
            logger.info("HL7 Message:{}",hl7Str);

            RawERLDto rawERLDto = new RawERLDto();
            rawERLDto.setType(msgType);
            rawERLDto.setPayload(hl7Str);
            if (validationActive != null && validationActive.equalsIgnoreCase("true")) {
                rawERLDto.setValidationActive(true);
            }
            rawELRService.submission(rawERLDto);
        }
        catch(Exception e) {
            logger.error(e.getMessage());
        }
    }
}
