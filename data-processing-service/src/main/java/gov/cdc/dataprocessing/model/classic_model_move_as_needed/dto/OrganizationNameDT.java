package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationNameDT extends AbstractVO {
    private Long organizationUid;
    private Integer organizationNameSeq;
    private String nmTxt;
    private String nmUseCd;
    private String recordStatusCd;
    private String defaultNmInd;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;
    private boolean itDirty = false;
    private boolean itNew = true;
    private boolean itDelete = false;

    public OrganizationNameDT(){
    }
    public OrganizationNameDT(OrganizationName organizationName){
        this.organizationUid=organizationName.getOrganizationUid();
        this.organizationNameSeq=organizationName.getOrganizationNameSeq();
        this.nmTxt=organizationName.getNameText();
        this.nmUseCd=organizationName.getNameUseCode();
        this.recordStatusCd=organizationName.getRecordStatusCode();
        this.defaultNmInd=organizationName.getDefaultNameIndicator();
    }
}
