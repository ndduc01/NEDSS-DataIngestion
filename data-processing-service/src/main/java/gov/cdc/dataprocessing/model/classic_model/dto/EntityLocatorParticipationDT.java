package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.EntityLocatorParticipation;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class EntityLocatorParticipationDT extends AbstractVO {

    private Long locatorUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private Timestamp asOfDate;
    private String cd;
    private String cdDescTxt;
    private String classCd;
    private String durationAmt;
    private String durationUnitCd;
    private Timestamp fromTime;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String locatorDescTxt;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String statusCd;
    private Timestamp statusTime;
    private Timestamp toTime;
    private String useCd;
    private String userAffiliationTxt;
    private String validTimeTxt;
    private Long entityUid;
    private PostalLocatorDT thePostalLocatorDT;
    private PhysicalLocatorDT thePhysicalLocatorDT;
    private TeleLocatorDT theTeleLocatorDT;
    private Integer versionCtrlNbr;

    public EntityLocatorParticipationDT() {

    }

    public EntityLocatorParticipationDT(EntityLocatorParticipation entityLocatorParticipation) {
        this.entityUid = entityLocatorParticipation.getEntityUid();
        this.locatorUid = entityLocatorParticipation.getLocatorUid();
        this.addReasonCd = entityLocatorParticipation.getAddReasonCd();
        this.addTime = entityLocatorParticipation.getAddTime();
        this.addUserId = entityLocatorParticipation.getAddUserId();
        this.cd = entityLocatorParticipation.getCd();
        this.cdDescTxt = entityLocatorParticipation.getCdDescTxt();
        this.classCd = entityLocatorParticipation.getClassCd();
        this.durationAmt = entityLocatorParticipation.getDurationAmt();
        this.durationUnitCd = entityLocatorParticipation.getDurationUnitCd();
        this.fromTime = entityLocatorParticipation.getFromTime();
        this.lastChgReasonCd = entityLocatorParticipation.getLastChgReasonCd();
        this.lastChgTime = entityLocatorParticipation.getLastChgTime();
        this.lastChgUserId = entityLocatorParticipation.getLastChgUserId();
        this.locatorDescTxt = entityLocatorParticipation.getLocatorDescTxt();
        this.recordStatusCd = entityLocatorParticipation.getRecordStatusCd();
        this.recordStatusTime = entityLocatorParticipation.getRecordStatusTime();
        this.statusCd = entityLocatorParticipation.getStatusCd();
        this.statusTime = entityLocatorParticipation.getStatusTime();
        this.toTime = entityLocatorParticipation.getToTime();
        this.useCd = entityLocatorParticipation.getUseCd();
        this.userAffiliationTxt = entityLocatorParticipation.getUserAffiliationTxt();
        this.validTimeTxt = entityLocatorParticipation.getValidTimeTxt();
        this.versionCtrlNbr = entityLocatorParticipation.getVersionCtrlNbr();
        this.asOfDate = entityLocatorParticipation.getAsOfDate();
    }

}
