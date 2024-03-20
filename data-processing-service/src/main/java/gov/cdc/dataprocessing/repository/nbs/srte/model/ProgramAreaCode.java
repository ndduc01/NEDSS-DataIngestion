package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Program_area_code")
@Data
public class ProgramAreaCode {

    @Id
    @Column(name = "prog_area_cd", nullable = false)
    private String progAreaCd;

    @Column(name = "prog_area_desc_txt")
    private String progAreaDescTxt;

    @Column(name = "nbs_uid")
    private Integer nbsUid;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "code_set_nm")
    private String codeSetNm;

    @Column(name = "code_seq")
    private Integer codeSeq;

    // Constructors, getters, and setters are generated by Lombok
}