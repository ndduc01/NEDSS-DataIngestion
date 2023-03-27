package gov.cdc.dataingestion.report.repository;

import gov.cdc.dataingestion.report.entity.RawMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawMessageRepository extends JpaRepository<RawMessageEntity, String> {


}
