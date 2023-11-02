/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package gov.cdc.dataingestion.hl7.helper;

import ca.uhn.hl7v2.DefaultHapiContext;
import gov.cdc.dataingestion.hl7.helper.integration.FhirConverter;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiFhirException;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.hl7.helper.integration.HL7Parser;
import gov.cdc.dataingestion.hl7.helper.integration.interfaces.IFhirConverter;
import gov.cdc.dataingestion.hl7.helper.integration.interfaces.IHL7Parser;
import gov.cdc.dataingestion.hl7.helper.model.FhirConvertedMessage;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;

public class HL7Helper {

    private IHL7Parser parser = new HL7Parser(new DefaultHapiContext());
    private IFhirConverter fhirConverter = new FhirConverter(new HL7ToFHIRConverter());

    private static HL7Helper instance = new HL7Helper();

    /**
     * HL7 string validator, replacing "\n" by "\r"
     * */
    public String hl7StringValidator(String message) throws DiHL7Exception {
        return parser.hl7MessageStringValidation(message);
    }

    /**
     * Parser to be updated
     * */
    public HL7ParsedMessage hl7StringParser(String message) throws DiHL7Exception {
        message = parser.processFhsMessage(message);
        return parser.hl7StringParser(message);
    }

    public ca.uhn.hl7v2.model.v231.message.ORU_R01 hl7StringParser231(String message) throws DiHL7Exception {
        return parser.hl7v231StringParser(message);
    }

    public HL7ParsedMessage convert231To251(String message) throws DiHL7Exception {
        return parser.convert231To251(message, null);
    }

    /**
     * Convert HL7 message into fhir
     * */
    public FhirConvertedMessage convertHl7ToFhir(String message) throws DiFhirException {
        return fhirConverter.HL7ToFHIRConversion(message);
    }

    public String hl7Validation(String message) throws DiHL7Exception{
        message = parser.processFhsMessage(message);
        return parser.hl7ORUValidation(message);
    }

    public String processFhsMessage(String message)  {
        return parser.processFhsMessage(message);
    }


}
