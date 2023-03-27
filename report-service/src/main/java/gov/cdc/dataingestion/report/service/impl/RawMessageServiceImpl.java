package gov.cdc.dataingestion.report.service.impl;

import gov.cdc.dataingestion.report.entity.RawMessageEntity;
import gov.cdc.dataingestion.report.repository.RawMessageRepository;
import gov.cdc.dataingestion.report.service.RawMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RawMessageServiceImpl {

    private RawMessageRepository rawMessageRepository;

    public RawMessageServiceImpl(RawMessageRepository rawMessageRepository) {
        this.rawMessageRepository = rawMessageRepository;
    }

    public String save(RawMessageEntity entity) {
        RawMessageEntity createdEntity = rawMessageRepository.save(entity);
        return createdEntity.getId();
    }

    public RawMessageEntity findById(String id) {
        return rawMessageRepository.findById(id).get();
    }
}
