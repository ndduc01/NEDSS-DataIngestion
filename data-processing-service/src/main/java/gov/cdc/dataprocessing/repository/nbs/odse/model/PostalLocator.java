package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.classic_model.dto.PostalLocatorDT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;


@Entity
@Table(name = "Postal_locator", schema = "dbo")
@Data
public class PostalLocator {

    @Id
    @Column(name = "postal_locator_uid")
    private Long postalLocatorUid;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "census_block_cd")
    private String censusBlockCd;

    @Column(name = "census_minor_civil_division_cd")
    private String censusMinorCivilDivisionCd;

    @Column(name = "census_track_cd")
    private String censusTrackCd;

    @Column(name = "city_cd")
    private String cityCd;

    @Column(name = "city_desc_txt")
    private String cityDescTxt;

    @Column(name = "cntry_cd")
    private String cntryCd;

    @Column(name = "cntry_desc_txt")
    private String cntryDescTxt;

    @Column(name = "cnty_cd")
    private String cntyCd;

    @Column(name = "cnty_desc_txt")
    private String cntyDescTxt;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "MSA_congress_district_cd")
    private String msaCongressDistrictCd;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "region_district_cd")
    private String regionDistrictCd;

    @Column(name = "state_cd")
    private String stateCd;

    @Column(name = "street_addr1")
    private String streetAddr1;

    @Column(name = "street_addr2")
    private String streetAddr2;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "zip_cd")
    private String zipCd;

    @Column(name = "geocode_match_ind")
    private String geocodeMatchInd;

    @Column(name = "within_city_limits_ind")
    private String withinCityLimitsInd;

    @Column(name = "census_tract")
    private String censusTract;

    public PostalLocator(PostalLocatorDT postalLocatorDT) {
        this.postalLocatorUid = postalLocatorDT.getPostalLocatorUid();
        this.addReasonCd = postalLocatorDT.getAddReasonCd();
        this.addTime = postalLocatorDT.getAddTime();
        this.addUserId = postalLocatorDT.getAddUserId();
        this.censusBlockCd = postalLocatorDT.getCensusBlockCd();
        this.censusMinorCivilDivisionCd = postalLocatorDT.getCensusMinorCivilDivisionCd();
        this.censusTrackCd = postalLocatorDT.getCensusTrackCd();
        this.cityCd = postalLocatorDT.getCityCd();
        this.cityDescTxt = postalLocatorDT.getCityDescTxt();
        this.cntryCd = postalLocatorDT.getCntryCd();
        this.cntryDescTxt = postalLocatorDT.getCntryDescTxt();
        this.cntyCd = postalLocatorDT.getCntyCd();
        this.cntyDescTxt = postalLocatorDT.getCntyDescTxt();
        this.lastChgReasonCd = postalLocatorDT.getLastChgReasonCd();
        this.lastChgTime = postalLocatorDT.getLastChgTime();
        this.lastChgUserId = postalLocatorDT.getLastChgUserId();
        this.msaCongressDistrictCd = postalLocatorDT.getMSACongressDistrictCd();
        this.recordStatusCd = postalLocatorDT.getRecordStatusCd();
        this.recordStatusTime = postalLocatorDT.getRecordStatusTime();
        this.regionDistrictCd = postalLocatorDT.getRegionDistrictCd();
        this.stateCd = postalLocatorDT.getStateCd();
        this.streetAddr1 = postalLocatorDT.getStreetAddr1();
        this.streetAddr2 = postalLocatorDT.getStreetAddr2();
        this.userAffiliationTxt = postalLocatorDT.getUserAffiliationTxt();
        this.zipCd = postalLocatorDT.getZipCd();
        this.geocodeMatchInd = postalLocatorDT.getGeocodeMatchInd();
        this.withinCityLimitsInd = postalLocatorDT.getWithinCityLimitsInd();
        this.censusTract = postalLocatorDT.getCensusTract();
    }

    public PostalLocator() {
        // Default constructor
    }

}