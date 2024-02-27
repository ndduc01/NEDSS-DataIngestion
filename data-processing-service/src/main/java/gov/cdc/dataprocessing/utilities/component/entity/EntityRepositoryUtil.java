package gov.cdc.dataprocessing.utilities.component.entity;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.classic_model.dto.PersonDT;
import gov.cdc.dataprocessing.repository.nbs.odse.EntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed.EntityODSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EntityRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(EntityRepositoryUtil.class);
    private final EntityRepository entityRepository;


    public EntityRepositoryUtil(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    public EntityODSE preparingEntityReposCallForPerson(PersonDT personDT, Long entityId, Object entityValue, String event) {
        EntityODSE entityODSE = null;
        if (entityValue.getClass().toString().equals("class java.lang.String")) {
            entityODSE = new EntityODSE();
            entityODSE.setEntityUid(entityId);
            entityODSE.setClassCd((String) entityValue);
            entityRepository.save(entityODSE);
        } else {
            if (entityValue.getClass().toString().equals("class java.sql.Timestamp")) {
                //TODO: Will get back to this
            }
            else {

            }
        }

        if (event.equals(NEDSSConstant.SELECT)) {

        }
        else if (event.equals(NEDSSConstant.SELECT_COUNT)) {

        }
        else {
            return entityODSE;
        }

        return entityODSE;
    }
}
