package gov.cdc.dataprocessing.service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonId {
    public Long personId;
    public Long personParentId;
    public String localId;
}
