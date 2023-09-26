package gov.cdc.dataingestion.reportstatus.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

@Entity
@Table(name = "elr_record_status_id")
public class ReportStatusIdData {
    @Id
    @GenericGenerator(name = "generator", strategy = "guid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(name = "id" , columnDefinition="uniqueidentifier")
    private String id;

    @Column(name = "raw_message_id")
    private String rawMessageId;

    @Column(name = "nbs_interface_id")
    private Integer nbsInterfaceUid;

    @Transient
    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "updated_on")
    private Timestamp updatedOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRawMessageId() {
        return rawMessageId;
    }

    public void setRawMessageId(String rawMessageId) {
        this.rawMessageId = rawMessageId;
    }

    public Integer getNbsInterfaceUid() {
        return nbsInterfaceUid;
    }

    public void setNbsInterfaceUid(Integer nbsInterfaceUid) {
        this.nbsInterfaceUid = nbsInterfaceUid;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
