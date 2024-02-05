package gov.cdc.dataprocessing.model.classic_model.dt;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
public class EDXActivityDetailLogDT extends AbstractVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long edxActivityLogUid;
    private String recordId;
    private String recordType;
    private String recordName;
    private String logType;
    private String comment;
    private String logTypeHtml;
    private String commentHtml;

    private boolean itNew;
    private boolean itDelete;
    private boolean itDirty;
    private Long lastChgUserId;
    private Timestamp lastChgTime;
    private Long addUserId;
    private Timestamp addTime;
    private Integer publishVersionNbr;

}
