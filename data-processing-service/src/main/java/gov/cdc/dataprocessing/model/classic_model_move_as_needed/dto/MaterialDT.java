package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.material.Material;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class MaterialDT extends AbstractVO {

    private Long materialUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String cd;
    private String cdDescTxt;
    private String description;
    private String effectiveDurationAmt;
    private String effectiveDurationUnitCd;
    private Timestamp effectiveFromTime;
    private Timestamp effectiveToTime;
    private String formCd;
    private String formDescTxt;
    private String handlingCd;
    private String handlingDescTxt;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String localId;
    private String nm;
    private String qty;
    private String qtyUnitCd;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String riskCd;
    private String riskDescTxt;
    private String statusCd;
    private Timestamp statusTime;
    private String userAffiliationTxt;
    private Integer versionCtrlNbr;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;
    private boolean itDirty = false;
    private boolean itNew = true;
    private boolean itDelete = false;

    public MaterialDT() {

    }

    public MaterialDT(Material material) {
        this.materialUid = material.getMaterialUid();
        this.addReasonCd = material.getAddReasonCd();
        this.addTime = material.getAddTime();
        this.addUserId = material.getAddUserId();
        this.cd = material.getCd();
        this.cdDescTxt = material.getCdDescTxt();
        this.description = material.getDescription();
        this.effectiveDurationAmt = material.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = material.getEffectiveDurationUnitCd();
        this.effectiveFromTime = material.getEffectiveFromTime();
        this.effectiveToTime = material.getEffectiveToTime();
        this.formCd = material.getFormCd();
        this.formDescTxt = material.getFormDescTxt();
        this.handlingCd = material.getHandlingCd();
        this.handlingDescTxt = material.getHandlingDescTxt();
        this.lastChgReasonCd = material.getLastChgReasonCd();
        this.lastChgTime = material.getLastChgTime();
        this.lastChgUserId = material.getLastChgUserId();
        this.localId = material.getLocalId();
        this.nm = material.getNm();
        this.qty = material.getQty();
        this.qtyUnitCd = material.getQtyUnitCd();
        this.recordStatusCd = material.getRecordStatusCd();
        this.recordStatusTime = material.getRecordStatusTime();
        this.riskCd = material.getRiskCd();
        this.riskDescTxt = material.getRiskDescTxt();
        this.statusCd = material.getStatusCd();
        this.statusTime = material.getStatusTime();
        this.userAffiliationTxt = material.getUserAffiliationTxt();
        this.versionCtrlNbr = material.getVersionCtrlNbr();
    }

}
