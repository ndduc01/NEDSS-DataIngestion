package gov.cdc.dataingestion.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Report details.
 */
@Data
@AllArgsConstructor
public class ReportDetails {

    /**
     * Report data.
     */
    private String data;
}
