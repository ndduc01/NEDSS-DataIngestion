package gov.cdc.dataingestion.report.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "raw_message")
public class RawMessageEntity {

    @Id
    private String id;
    private String fileContent;


}
