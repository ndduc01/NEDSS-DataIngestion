package gov.cdc.dataprocessing.repository.nbs.odse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "Postal_locator", schema = "dbo")
@Data
public class PostalLocator {

    @Id
    @Column(name = "postal_locator_uid", nullable = false)
    private BigInteger postalLocatorUid;

    @Column(name = "add_reason_cd", length = 80)
    private String addReasonCd;

    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "add_user_id")
    private BigInteger addUserId;

    @Column(name = "census_block_cd", length = 20)
    private String censusBlockCd;

    @Column(name = "census_minor_civil_division_cd", length = 20)
    private String censusMinorCivilDivisionCd;

    @Column(name = "census_track_cd", length = 20)
    private String censusTrackCd;

    @Column(name = "city_cd", length = 20)
    private String cityCd;

    @Column(name = "city_desc_txt", length = 100)
    private String cityDescTxt;

    @Column(name = "cntry_cd", length = 20)
    private String cntryCd;

    @Column(name = "cntry_desc_txt", length = 100)
    private String cntryDescTxt;

    @Column(name = "cnty_cd", length = 20)
    private String cntyCd;

    @Column(name = "cnty_desc_txt", length = 100)
    private String cntyDescTxt;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Date lastChgTime;

    @Column(name = "last_chg_user_id")
    private BigInteger lastChgUserId;

    @Column(name = "MSA_congress_district_cd", length = 20)
    private String MSACongressDistrictCd;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Date recordStatusTime;

    @Column(name = "region_district_cd", length = 20)
    private String regionDistrictCd;

    @Column(name = "state_cd", length = 20)
    private String stateCd;

    @Column(name = "street_addr1", length = 100)
    private String streetAddr1;

    @Column(name = "street_addr2", length = 100)
    private String streetAddr2;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationTxt;

    @Column(name = "zip_cd", length = 20)
    private String zipCd;

    @Column(name = "geocode_match_ind", length = 1)
    private String geocodeMatchInd;

    @Column(name = "within_city_limits_ind", length = 3)
    private String withinCityLimitsInd;

    @Column(name = "census_tract", length = 10)
    private String censusTract;

    // Add getters and setters as needed
}