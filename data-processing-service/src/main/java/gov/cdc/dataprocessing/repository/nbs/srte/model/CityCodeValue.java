package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "City_code_value")
public class CityCodeValue {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "assigning_authority_cd")
    private String assigningAuthorityCd;

    @Column(name = "assigning_authority_desc_txt")
    private String assigningAuthorityDescTxt;

    @Column(name = "code_desc_txt")
    private String codeDescTxt;

    @Column(name = "code_short_desc_txt")
    private String codeShortDescTxt;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "excluded_txt")
    private String excludedTxt;

    @Column(name = "indent_level_nbr")
    private Integer indentLevelNbr;

    @Column(name = "is_modifiable_ind")
    private String isModifiableInd;

    @Column(name = "parent_is_cd")
    private String parentIsCd;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "code_set_nm")
    private String codeSetNm;

    @Column(name = "seq_num")
    private Integer seqNum;

    @Column(name = "nbs_uid")
    private Integer nbsUid;

    @Column(name = "source_concept_id")
    private String sourceConceptId;
}
