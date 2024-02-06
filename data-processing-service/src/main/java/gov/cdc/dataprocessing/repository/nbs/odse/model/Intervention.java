package gov.cdc.dataprocessing.repository.nbs.odse.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "Intervention")
public class Intervention  {

    @Id
    @Column(name = "intervention_uid")
    private Long interventionUid;

    @Column(name = "activity_duration_amt")
    private String activityDurationAmt;

    @Column(name = "activity_duration_unit_cd")
    private String activityDurationUnitCd;

    @Column(name = "activity_from_time")
    private Timestamp activityFromTime;

    @Column(name = "activity_to_time")
    private Timestamp activityToTime;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cd")
    private String cd;

    @Column(name = "cd_desc_txt")
    private String cdDescTxt;

    @Column(name = "cd_system_cd")
    private String cdSystemCd;

    @Column(name = "cd_system_desc_txt")
    private String cdSystemDescTxt;

    @Column(name = "class_cd")
    private String classCd;

    @Column(name = "confidentiality_cd")
    private String confidentialityCd;

    @Column(name = "confidentiality_desc_txt")
    private String confidentialityDescTxt;

    @Column(name = "effective_duration_amt")
    private String effectiveDurationAmt;

    @Column(name = "effective_duration_unit_cd")
    private String effectiveDurationUnitCd;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "jurisdiction_cd")
    private String jurisdictionCd;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "local_id")
    private String localId;

    @Column(name = "method_cd")
    private String methodCd;

    @Column(name = "method_desc_txt")
    private String methodDescTxt;

    @Column(name = "prog_area_cd")
    private String progAreaCd;

    @Column(name = "priority_cd")
    private String priorityCd;

    @Column(name = "priority_desc_txt")
    private String priorityDescTxt;

    @Column(name = "qty_amt")
    private String qtyAmt;

    @Column(name = "qty_unit_cd")
    private String qtyUnitCd;

    @Column(name = "reason_cd")
    private String reasonCd;

    @Column(name = "reason_desc_txt")
    private String reasonDescTxt;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "repeat_nbr")
    private Integer repeatNbr;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "target_site_cd")
    private String targetSiteCd;

    @Column(name = "target_site_desc_txt")
    private String targetSiteDescTxt;

    @Column(name = "txt")
    private String txt;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "program_jurisdiction_oid")
    private Long programJurisdictionOid;

    @Column(name = "shared_ind")
    private String sharedInd;

    @Column(name = "version_ctrl_nbr")
    private Integer versionCtrlNbr;

    @Column(name = "material_cd")
    private String materialCd;

    @Column(name = "age_at_vacc")
    private Integer ageAtVacc;

    @Column(name = "age_at_vacc_unit_cd")
    private String ageAtVaccUnitCd;

    @Column(name = "vacc_mfgr_cd")
    private String vaccMfgrCd;

    @Column(name = "material_lot_nm")
    private String materialLotNm;

    @Column(name = "material_expiration_time")
    private Timestamp materialExpirationTime;

    @Column(name = "vacc_dose_nbr")
    private Integer vaccDoseNbr;

    @Column(name = "vacc_info_source_cd")
    private String vaccInfoSourceCd;

    @Column(name = "electronic_ind")
    private String electronicInd;

    // Constructors, getters, and setters
}
