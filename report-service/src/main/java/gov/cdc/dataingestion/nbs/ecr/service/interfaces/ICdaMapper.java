package gov.cdc.dataingestion.nbs.ecr.service.interfaces;

import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Service;

public interface ICdaMapper {
    String tranformSelectedEcrToCDAXml(EcrSelectedRecord input) throws XmlException;
}
