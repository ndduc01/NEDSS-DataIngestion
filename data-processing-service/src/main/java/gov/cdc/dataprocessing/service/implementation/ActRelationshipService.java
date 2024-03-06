package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActRelationshipDT;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import gov.cdc.dataprocessing.service.interfaces.IActRelationshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class ActRelationshipService implements IActRelationshipService {
    private static final Logger logger = LoggerFactory.getLogger(ActRelationshipService.class);
    private final ActRelationshipRepository actRelationshipRepository;


    public ActRelationshipService(ActRelationshipRepository actRelationshipRepository) {
        this.actRelationshipRepository = actRelationshipRepository;
    }

    public Collection<ActRelationshipDT> loadActRelationshipBySrcIdAndTypeCode(Long uid, String type) {
        Collection<ActRelationshipDT> actRelationshipDTCollection = new ArrayList<>();
        var result = actRelationshipRepository.loadActRelationshipBySrcIdAndTypeCode(uid, type);
        if (result.isPresent()) {
            for(var item : result.get()) {
                var elem = new ActRelationshipDT(item);
                actRelationshipDTCollection.add(elem);
            }
        }

        return actRelationshipDTCollection;
    }
}
