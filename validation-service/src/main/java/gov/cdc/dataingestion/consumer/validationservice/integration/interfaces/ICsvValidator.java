package gov.cdc.dataingestion.consumer.validationservice.integration.interfaces;

import gov.cdc.dataingestion.consumer.validationservice.model.MessageModel;

import java.io.Reader;
import java.util.List;

public interface ICsvValidator {
    MessageModel ValidateCSVAgainstCVSSchema(String message) throws Exception;
    List<List<String>> ReadLineByLine(Reader reader) throws Exception;
}
