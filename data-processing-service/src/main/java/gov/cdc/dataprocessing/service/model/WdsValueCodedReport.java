package gov.cdc.dataprocessing.service.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WdsValueCodedReport {
    private String codeType;
    private String inputCode;
    private String wdsCode;
    private boolean matchedFound;
}
