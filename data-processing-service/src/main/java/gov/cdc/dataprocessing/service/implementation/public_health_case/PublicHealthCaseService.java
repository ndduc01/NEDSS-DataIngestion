package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.utilities.component.public_health_case.PublicHealthCaseRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class PublicHealthCaseService implements IPublicHealthCaseService {
    private final EntityHelper entityHelper;
    private final PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
    public PublicHealthCaseService(EntityHelper entityHelper,
                                   PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil) {

        this.entityHelper = entityHelper;
        this.publicHealthCaseRepositoryUtil = publicHealthCaseRepositoryUtil;
    }

    public Long setPublicHealthCase(PublicHealthCaseContainer publicHealthCaseContainer) throws DataProcessingException {

        Long PubHealthCaseUid = -1L;

        try
        {

            PublicHealthCaseDto publicHealthCase = null;

            Collection<ActivityLocatorParticipationDto> alpDTCol = publicHealthCaseContainer.getTheActivityLocatorParticipationDTCollection();
            Collection<ActRelationshipDto> arDTCol = publicHealthCaseContainer.getTheActRelationshipDTCollection();
            Collection<ParticipationDto> pDTCol = publicHealthCaseContainer.getTheParticipationDTCollection();
            Collection<ActivityLocatorParticipationDto> col = null;
            Collection<ActRelationshipDto> colActRelationship = null;
            Collection<ParticipationDto> colParticipation = null;

            if (alpDTCol != null)
            {
                col = entityHelper.iterateALPDTActivityLocatorParticipation(alpDTCol);
                publicHealthCaseContainer.setTheActivityLocatorParticipationDTCollection(col);
            }

            if (arDTCol != null)
            {
                colActRelationship = entityHelper.iterateARDTActRelationship(arDTCol);
                publicHealthCaseContainer.setTheActRelationshipDTCollection(colActRelationship);
            }

            if (pDTCol != null)
            {
                colParticipation = entityHelper.iteratePDTForParticipation(pDTCol);
                publicHealthCaseContainer.setTheParticipationDTCollection(colParticipation);
            }

            if (publicHealthCaseContainer.isItNew())
            {
                publicHealthCaseRepositoryUtil.create(publicHealthCaseContainer);
                publicHealthCase =  publicHealthCaseContainer.getThePublicHealthCaseDto();
                PubHealthCaseUid = publicHealthCase.getPublicHealthCaseUid();
            }
            else
            {
                publicHealthCaseRepositoryUtil.update(publicHealthCaseContainer);
                PubHealthCaseUid = publicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid();
            }
        }
        catch (Exception e)
        {
           throw new DataProcessingException(e.getMessage(), e);
        }

        return PubHealthCaseUid;
    }
}
