package gov.cdc.dataingestion.report.service;

import gov.cdc.dataingestion.report.entity.RawMessageEntity;

public interface RawMessageService {

    String save(RawMessageEntity entity);
}
