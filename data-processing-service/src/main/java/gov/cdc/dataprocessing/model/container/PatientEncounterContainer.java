package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.PatientEncounterDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
public class PatientEncounterContainer  extends BaseContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    public PatientEncounterDto thePatientEncounterDT = new PatientEncounterDto();
    public Collection<Object> theActivityLocatorParticipationDTCollection;
    public Collection<Object> theActIdDTCollection;
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theActRelationshipDTCollection;
}
