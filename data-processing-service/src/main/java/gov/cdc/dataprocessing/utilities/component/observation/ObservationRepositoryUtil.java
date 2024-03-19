package gov.cdc.dataprocessing.utilities.component.observation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.observation.*;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.implementation.other.OdseIdGeneratorService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class ObservationRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(ObservationRepositoryUtil.class);

    private final ObservationRepository observationRepository;
    private final ObservationReasonRepository observationReasonRepository;
    private final ActIdRepository actIdRepository;
    private final ObservationInterpRepository observationInterpRepository;
    private final ObsValueCodedRepository obsValueCodedRepository;
    private final ObsValueTxtRepository obsValueTxtRepository;
    private final ObsValueDateRepository obsValueDateRepository;
    private final ObsValueNumericRepository obsValueNumericRepository;
    private final ActLocatorParticipationRepository actLocatorParticipationRepository;
    private final ActRelationshipRepository actRelationshipRepository;
    private final ParticipationRepository participationRepository;
    private final EntityHelper entityHelper;
    private final OdseIdGeneratorService odseIdGeneratorService;

    private final ActRepository actRepository;

    public ObservationRepositoryUtil(ObservationRepository observationRepository,
                                     ObservationReasonRepository observationReasonRepository,
                                     ActIdRepository actIdRepository,
                                     ObservationInterpRepository observationInterpRepository,
                                     ObsValueCodedRepository obsValueCodedRepository,
                                     ObsValueTxtRepository obsValueTxtRepository,
                                     ObsValueDateRepository obsValueDateRepository,
                                     ObsValueNumericRepository obsValueNumericRepository,
                                     ActLocatorParticipationRepository actLocatorParticipationRepository,
                                     ActRelationshipRepository actRelationshipRepository,
                                     ParticipationRepository participationRepository,
                                     EntityHelper entityHelper,
                                     OdseIdGeneratorService odseIdGeneratorService, ActRepository actRepository) {
        this.observationRepository = observationRepository;
        this.observationReasonRepository = observationReasonRepository;
        this.actIdRepository = actIdRepository;
        this.observationInterpRepository = observationInterpRepository;
        this.obsValueCodedRepository = obsValueCodedRepository;
        this.obsValueTxtRepository = obsValueTxtRepository;
        this.obsValueDateRepository = obsValueDateRepository;
        this.obsValueNumericRepository = obsValueNumericRepository;
        this.actLocatorParticipationRepository = actLocatorParticipationRepository;
        this.actRelationshipRepository = actRelationshipRepository;
        this.participationRepository = participationRepository;
        this.entityHelper = entityHelper;
        this.odseIdGeneratorService = odseIdGeneratorService;
        this.actRepository = actRepository;
    }


    public ObservationContainer loadObject(long obUID) throws DataProcessingException
    {
        ObservationContainer obVO;
        try{
            obVO = new ObservationContainer();

            /**
             *  Selects ObservationDto object
             */

            ObservationDto obDT = selectObservation(obUID);
            obVO.setTheObservationDto(obDT);
            /**
             * Selects ObservationReasonDto List
             */

            Collection<ObservationReasonDto> obReasonColl = selectObservationReasons(obUID);
            obVO.setTheObservationReasonDtoCollection(obReasonColl);

            /**
             * Selects ActityIdDT collection
             */

            Collection<ActIdDto> idColl = selectActivityIDs(obUID);
            obVO.setTheActIdDtoCollection(idColl);

            /**
             * Selects ObservationInterpDto collection
             */

            Collection<ObservationInterpDto> obInterpColl = selectObservationInterps(obUID);
            obVO.setTheObservationInterpDtoCollection(obInterpColl);

            /**
             * Selects ObsValueCodedDto collection
             */

            Collection<ObsValueCodedDto> obsValueCodedColl = selectObsValueCodeds(obUID);
            obVO.setTheObsValueCodedDtoCollection(obsValueCodedColl);

            /**
             * Selects ObsValueTxtDto collection
             */

            Collection<ObsValueTxtDto> obsValueTxtColl = selectObsValueTxts(obUID);
            obVO.setTheObsValueTxtDtoCollection(obsValueTxtColl);

            /**
             * Selects ObsValueDateDto collection
             */

            Collection<ObsValueDateDto> obsValueDateColl = selectObsValueDates(obUID);
            obVO.setTheObsValueDateDtoCollection(obsValueDateColl);

            /**
             * Selects ObsValueNumericDto collection
             */

            Collection<ObsValueNumericDto> obsValueNumericColl = selectObsValueNumerics(obUID);
            obVO.setTheObsValueNumericDtoCollection(obsValueNumericColl);

            /**
             * Selects ActivityLocatorParticipationDto collection
             */

            Collection<ActivityLocatorParticipationDto> activityLocatorParticipationColl = selectActivityLocatorParticipations(obUID);
            obVO.setTheActivityLocatorParticipationDtoCollection(activityLocatorParticipationColl);

            //Selects ActRelationshiopDTcollection
            Collection<ActRelationshipDto> actColl = selectActRelationshipDTCollection(obUID);
            obVO.setTheActRelationshipDtoCollection(actColl);

            //SelectsParticipationDTCollection
            Collection<ParticipationDto> parColl = selectParticipationDTCollection(obUID);
            obVO.setTheParticipationDtoCollection(parColl);

            obVO.setItNew(false);
            obVO.setItDirty(false);
            return obVO;
        }catch(Exception ex){
            throw new DataProcessingException(ex.toString());
        }
    }

    @Transactional
    public Long saveObservation(ObservationContainer observationContainer) throws DataProcessingException {
        Long observationUid = -1L;


        try {
            Observation observation = null;

            Collection<ActivityLocatorParticipationDto> alpDTCol = observationContainer.getTheActivityLocatorParticipationDtoCollection();
            Collection<ActRelationshipDto> arDTCol = observationContainer.getTheActRelationshipDtoCollection();
            Collection<ParticipationDto> pDTCol = observationContainer.getTheParticipationDtoCollection();
            Collection<ActRelationshipDto> colAct = null;
            Collection<ParticipationDto> colParticipation = null;
            Collection<ActivityLocatorParticipationDto> colActLocatorParticipation = null;


            if (alpDTCol != null)
            {
                colActLocatorParticipation = entityHelper.iterateActivityParticipation(alpDTCol);
                observationContainer.setTheActivityLocatorParticipationDtoCollection(colActLocatorParticipation);
            }

            if (arDTCol != null)
            {
                colAct = entityHelper.iterateActRelationship(arDTCol);
                observationContainer.setTheActRelationshipDtoCollection(colAct);
            }

            if (pDTCol != null)
            {
                colParticipation = entityHelper.iteratePDTForParticipation(pDTCol);
                observationContainer.setTheParticipationDtoCollection(colParticipation);
            }

            if (observationContainer.isItNew())
            {
                //observation = home.create(observationContainer);
                var obsUid =  createNewObservation(observationContainer);
                observationUid = obsUid;
            }
            else
            {
                if (observationContainer.getTheObservationDto() != null) // make sure it is not null
                {
                    updateObservation(observationContainer);
                    observationUid = observationContainer.getTheObservationDto().getObservationUid();
                }
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        return observationUid;

    }

    @Transactional
    public Long createNewObservation(ObservationContainer observationContainer) throws DataProcessingException {
        try {
            Long obsId = saveNewObservation(observationContainer.getTheObservationDto());
            observationContainer.getTheObservationDto().setItNew(false);
            observationContainer.getTheObservationDto().setItDirty(false);

            addObservationReasons(obsId, observationContainer.getTheObservationReasonDtoCollection());
            addActivityId(obsId, observationContainer.getTheActIdDtoCollection(), false);
            addObservationInterps(obsId, observationContainer.getTheObservationInterpDtoCollection());
            addObsValueCoded(obsId, observationContainer.getTheObsValueCodedDtoCollection());
            addObsValueTxts(obsId, observationContainer.getTheObsValueTxtDtoCollection());
            addObsValueDates(obsId, observationContainer.getTheObsValueDateDtoCollection());
            addObsValueNumeric(obsId, observationContainer.getTheObsValueNumericDtoCollection());
            addActivityLocatorParticipations(obsId, observationContainer.getTheActivityLocatorParticipationDtoCollection());
            return obsId;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    @Transactional
    public Long updateObservation(ObservationContainer observationContainer) throws DataProcessingException {
        Long uid = -1L;
        if (observationContainer.getTheObservationDto().getObservationUid() == null) {
            uid = saveNewObservation(observationContainer.getTheObservationDto());
            observationContainer.getTheObservationDto().setItNew(false);
            observationContainer.getTheObservationDto().setItDirty(false);
        } else {
            uid = saveObservation(observationContainer.getTheObservationDto());
            observationContainer.getTheObservationDto().setItNew(false);
            observationContainer.getTheObservationDto().setItDirty(false);
        }

        updateObservationReason(uid, observationContainer.getTheObservationReasonDtoCollection());
        addActivityId(uid, observationContainer.getTheActIdDtoCollection(), true);
        updateObservationInterps(uid, observationContainer.getTheObservationInterpDtoCollection());
        updateObsValueCoded(uid, observationContainer.getTheObsValueCodedDtoCollection());
        updateObsValueTxts(uid, observationContainer.getTheObsValueTxtDtoCollection());
        updateObsValueDates(uid, observationContainer.getTheObsValueDateDtoCollection());
        updateObsValueNumerics(uid, observationContainer.getTheObsValueNumericDtoCollection());
        addActivityLocatorParticipations(uid, observationContainer.getTheActivityLocatorParticipationDtoCollection());
        return uid;
    }

    private ObservationDto selectObservation(long obUID) throws  DataProcessingException {
        try {
            // QUERY OBS
            var result = observationRepository.findById(obUID);
            if (result.isPresent()) {
                ObservationDto item = new ObservationDto(result.get());
                item.setItNew(false);
                item.setItDirty(false);
                return  item;
            } else {
                throw new DataProcessingException("NO OBS FOUND");
            }

        } catch (Exception ex) {
            throw new DataProcessingException(ex.toString());
        }

    }

    private Collection<ObservationReasonDto> selectObservationReasons(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObservationReason> observationReasons = observationReasonRepository.findRecordsById(aUID);
            Collection<ObservationReasonDto> dtCollection = new ArrayList<>();
            for (var observationReason : observationReasons) {
                ObservationReasonDto dt = new ObservationReasonDto(observationReason);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ActIdDto> selectActivityIDs(long aUID) throws  DataProcessingException
    {
        try
        {
            var result  = actIdRepository.findRecordsById(aUID);
            Collection<ActIdDto> dtCollection = new ArrayList<>();
            if (result.isPresent()) {
                Collection<ActId> col = result.get();
                for (var item : col) {
                    ActIdDto dt = new ActIdDto(item);
                    dt.setItNew(false);
                    dt.setItDirty(false);
                    dtCollection.add(dt);
                }
            }

            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }

    }

    private Collection<ObservationInterpDto> selectObservationInterps(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObservationInterp> col = observationInterpRepository.findRecordsById(aUID);
            Collection<ObservationInterpDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObservationInterpDto dt = new ObservationInterpDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ObsValueCodedDto> selectObsValueCodeds(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueCoded> col = obsValueCodedRepository.findRecordsById(aUID);
            Collection<ObsValueCodedDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueCodedDto dt = new ObsValueCodedDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ObsValueTxtDto> selectObsValueTxts(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueTxt> col = obsValueTxtRepository.findRecordsById(aUID);
            Collection<ObsValueTxtDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueTxtDto dt = new ObsValueTxtDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ObsValueDateDto> selectObsValueDates(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueDate> col = obsValueDateRepository.findRecordsById(aUID);
            Collection<ObsValueDateDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueDateDto dt = new ObsValueDateDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ObsValueNumericDto> selectObsValueNumerics(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ObsValueNumeric> col = obsValueNumericRepository.findRecordsById(aUID);
            Collection<ObsValueNumericDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ObsValueNumericDto dt = new ObsValueNumericDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ActivityLocatorParticipationDto> selectActivityLocatorParticipations(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ActLocatorParticipation> col = actLocatorParticipationRepository.findRecordsById(aUID);
            Collection<ActivityLocatorParticipationDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ActivityLocatorParticipationDto dt = new ActivityLocatorParticipationDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ActRelationshipDto> selectActRelationshipDTCollection(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<ActRelationship> col = actRelationshipRepository.findRecordsById(aUID);
            Collection<ActRelationshipDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ActRelationshipDto dt = new ActRelationshipDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Collection<ParticipationDto> selectParticipationDTCollection(long aUID) throws DataProcessingException
    {
        try
        {
            Collection<Participation> col = participationRepository.findRecordsById(aUID);
            Collection<ParticipationDto> dtCollection = new ArrayList<>();
            for (var item : col) {
                ParticipationDto dt = new ParticipationDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.toString());
        }
    }

    private Long saveNewObservation(ObservationDto observationDto) throws DataProcessingException {
        try {
            var uid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.OBSERVATION);

            Act act = new Act();
            act.setActUid(uid.getSeedValueNbr());
            act.setClassCode(NEDSSConstant.OBSERVATION_CLASS_CODE);
            act.setMoodCode(NEDSSConstant.EVENT_MOOD_CODE);

            actRepository.save(act);

            //TODO EVALUATE
            // Local uid
            Observation observation = new Observation(observationDto);
            observation.setVersionCtrlNbr(1);
            observation.setLocalId(uid.getUidPrefixCd() + uid.getSeedValueNbr() + uid.getUidSuffixCd());
            observation.setObservationUid(uid.getSeedValueNbr());

            observationRepository.save(observation);
            return uid.getSeedValueNbr();
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private Long saveObservation(ObservationDto observationDto) {
        Observation observation = new Observation(observationDto);
        observationRepository.save(observation);
        return observation.getObservationUid();
    }

    // private void insertObservationReasons(ObservationContainer obVO) throws  NEDSSSystemException
    private void  addObservationReasons(Long obsUid, Collection<ObservationReasonDto> observationReasonDtoCollection) throws DataProcessingException {
        try {
            if (observationReasonDtoCollection != null) {
                ArrayList<ObservationReasonDto> arr = new ArrayList<>(observationReasonDtoCollection);
                for(var item: arr) {
                    item.setObservationUid(obsUid);
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    saveObservationReason(item);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void saveObservationReason(ObservationReasonDto item) {
        var data = new ObservationReason(item);
        observationReasonRepository.save(data);
    }

    private void updateObservationReason(Long obsUid, Collection<ObservationReasonDto> observationReasonDtoCollection) throws DataProcessingException {
        try {
            ArrayList<ObservationReasonDto> arr = new ArrayList<>(observationReasonDtoCollection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObservationReason(item);
                } else {
                    observationReasonRepository.delete(new ObservationReason(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void addActivityId(Long obsUid, Collection<ActIdDto> actIdDtoCollection, boolean updateApplied) throws DataProcessingException {
        if (actIdDtoCollection != null) {
            int maxSegId = 0;
            if (!updateApplied) {
                var res = actIdRepository.findRecordsById(obsUid);
                if (res.isPresent()) {
                    var existingAct = new ArrayList<>(res.get());
                    if (!existingAct.isEmpty()) {
                        maxSegId = existingAct.stream().mapToInt(ActId::getActIdSeq).max().orElseThrow(() -> new DataProcessingException("List is empty"));
                    }
                }
            }

            ArrayList<ActIdDto> arr = new ArrayList<>(actIdDtoCollection);
            for(var item: arr) {
                item.setItNew(false);
                item.setItDirty(false);
                item.setItDelete(false);
                item.setActUid(obsUid);
                if (!updateApplied) {
                    item.setActIdSeq(++maxSegId);
                }
                var reason = new ActId(item);
                actIdRepository.save(reason);
            }
        }


    }

    private void addObservationInterps(Long obsUid, Collection<ObservationInterpDto> observationInterpDtoCollection) throws DataProcessingException {
        try {
            if (observationInterpDtoCollection != null) {
                ArrayList<ObservationInterpDto> arr = new ArrayList<>(observationInterpDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setObservationUid(obsUid);
                    saveObservationInterp(item);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void saveObservationInterp(ObservationInterpDto item) {
        var reason = new ObservationInterp(item);
        observationInterpRepository.save(reason);
    }

    private void updateObservationInterps(Long obsUid, Collection<ObservationInterpDto> collection) throws DataProcessingException {
        try {
            ArrayList<ObservationInterpDto> arr = new ArrayList<>(collection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObservationInterp(item);
                } else {
                    observationInterpRepository.delete(new ObservationInterp(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void addObsValueCoded(Long obsUid, Collection<ObsValueCodedDto> obsValueCodedDtoCollection) throws DataProcessingException {
        try {
            if (obsValueCodedDtoCollection != null) {
                ArrayList<ObsValueCodedDto> arr = new ArrayList<>(obsValueCodedDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setObservationUid(obsUid);
                    saveObsValueCoded(item);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void saveObsValueCoded(ObsValueCodedDto item) {
        var reason = new ObsValueCoded(item);
        obsValueCodedRepository.save(reason);
    }

    private void updateObsValueCoded(Long obsUid, Collection<ObsValueCodedDto> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueCodedDto> arr = new ArrayList<>(collection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObsValueCoded(item);

                } else {
                    obsValueCodedRepository.delete(new ObsValueCoded(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void addObsValueTxts(Long obsUid, Collection<ObsValueTxtDto> obsValueTxtDtoCollection) throws DataProcessingException {
        try {
            if (obsValueTxtDtoCollection != null)  {
                ArrayList<ObsValueTxtDto> arr = new ArrayList<>(obsValueTxtDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setObservationUid(obsUid);
                    saveObsValueTxt(item);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }


    }

    private void saveObsValueTxt(ObsValueTxtDto item) {
        var reason = new ObsValueTxt(item);
        obsValueTxtRepository.save(reason);
    }

    private void updateObsValueTxts(Long obsUid, Collection<ObsValueTxtDto> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueTxtDto> arr = new ArrayList<>(collection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObsValueTxt(item);

                } else {
                    obsValueTxtRepository.delete(new ObsValueTxt(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void addObsValueDates(Long obsUid, Collection<ObsValueDateDto> obsValueDateDtoCollection) throws DataProcessingException {
        try {
            if (obsValueDateDtoCollection != null) {
                ArrayList<ObsValueDateDto> arr = new ArrayList<>(obsValueDateDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setObservationUid(obsUid);
                    saveObsValueDate(item);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }


    }

    private void saveObsValueDate(ObsValueDateDto item) {
        var reason = new ObsValueDate(item);
        obsValueDateRepository.save(reason);
    }

    private void updateObsValueDates(Long obsUid, Collection<ObsValueDateDto> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueDateDto> arr = new ArrayList<>(collection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObsValueDate(item);

                } else {
                    obsValueDateRepository.delete(new ObsValueDate(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void addObsValueNumeric(Long obsUid, Collection<ObsValueNumericDto> obsValueNumericDtoCollection) throws DataProcessingException {
        try {
            if (obsValueNumericDtoCollection != null) {
                ArrayList<ObsValueNumericDto> arr = new ArrayList<>(obsValueNumericDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setObservationUid(obsUid);
                    saveObsValueNumeric(item);
                }
            }
        } catch (Exception e) {
            throw  new DataProcessingException(e.getMessage(), e);
        }


    }

    private void saveObsValueNumeric(ObsValueNumericDto item)  {
        var reason = new ObsValueNumeric(item);
        obsValueNumericRepository.save(reason);
    }

    private void updateObsValueNumerics(Long obsUid, Collection<ObsValueNumericDto> collection) throws DataProcessingException {
        try {
            ArrayList<ObsValueNumericDto> arr = new ArrayList<>(collection);
            for(var item: arr) {
                if (!item.isItDelete()) {
                    item.setObservationUid(obsUid);
                    saveObsValueNumeric(item);

                } else {
                    obsValueNumericRepository.delete(new ObsValueNumeric(item));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void addActivityLocatorParticipations(Long obsUid, Collection<ActivityLocatorParticipationDto> activityLocatorParticipationDtoCollection) throws DataProcessingException {
        try {
            if (activityLocatorParticipationDtoCollection != null) {
                ArrayList<ActivityLocatorParticipationDto> arr = new ArrayList<>(activityLocatorParticipationDtoCollection);
                for(var item: arr) {
                    item.setItNew(false);
                    item.setItDirty(false);
                    item.setItDelete(false);
                    item.setActUid(obsUid);
                    var reason = new ActLocatorParticipation(item);
                    actLocatorParticipationRepository.save(reason);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


}
