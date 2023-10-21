package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import lombok.NoArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class HL7FileProcessor implements Processor {
    private static Logger logger = LoggerFactory.getLogger(HL7FileProcessor.class);

    @Autowired
    private RawELRService rawELRService;

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            String msgType = (String) exchange.getIn().getHeader("msgType");
            String validationActive = (String) exchange.getIn().getHeader("validationActive");
            String hl7Str = exchange.getIn().getBody(String.class);

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
