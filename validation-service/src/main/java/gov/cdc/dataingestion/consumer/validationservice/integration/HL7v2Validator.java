package gov.cdc.dataingestion.consumer.validationservice.integration;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import gov.cdc.dataingestion.consumer.validationservice.integration.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.consumer.validationservice.model.MessageModel;
import gov.cdc.dataingestion.consumer.validationservice.model.enums.MessageType;

public class HL7v2Validator implements IHL7v2Validator {
    private HapiContext context;
    public HL7v2Validator(HapiContext context) {
        this.context = context;
    }

    public MessageModel  MessageValidation(String message) throws HL7Exception {
        String replaceSpecialCharacters;
        if (message.contains("\n")) {
            replaceSpecialCharacters = message.replaceAll("\n","\r");
        } else {
            replaceSpecialCharacters = message;
        }
        MessageModel model = new MessageModel();
        // Set validation
        context.setValidationContext(ValidationContextFactory.defaultValidation());
        PipeParser parser = context.getPipeParser();
        // if invalid HL7, exception will be thrown
        Message parsedMessage = parser.parse(replaceSpecialCharacters);

        model.setRawMessage(replaceSpecialCharacters);
        model.setMessageType(MessageType.HL7v2);
        model.setMessageVersion(parsedMessage.getVersion());

        return model;
    }
}
