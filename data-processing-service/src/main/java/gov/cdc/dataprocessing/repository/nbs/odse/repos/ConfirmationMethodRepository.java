package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.ClinicalDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.model.ConfirmationMethod;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ConfirmationMethodRepository extends JpaRepository<ConfirmationMethod, Long> {
    @Query("SELECT data FROM ConfirmationMethod data WHERE data.publicHealthCaseUid = :uid")
    Optional<Collection<ConfirmationMethod>> findRecordsByPhcUid(@Param("uid") Long phcUid);
}
