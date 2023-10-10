package gov.cdc.dataingestion.nbs.ecr.model.interview;

import gov.cdc.nedss.phdc.cda.POCDMT000040Component3;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class InterviewAnswer {
    POCDMT000040Component3 out;
    String oldQuestionId;
    int entryCounter;
}
