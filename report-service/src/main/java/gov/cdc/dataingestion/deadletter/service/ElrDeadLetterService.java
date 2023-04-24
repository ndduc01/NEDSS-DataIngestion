package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.conversion.integration.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.conversion.repository.IHL7ToFHIRRepository;
import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.model.ElrDltStatus;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7DuplicateValidator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ElrDeadLetterService {
    private static final String CREATED_BY = "DeadLetterService";

    private final IElrDeadLetterRepository dltRepository;
    private final IRawELRRepository rawELRRepository;
    private final IValidatedELRRepository validatedELRRepository;
    private final IHL7ToFHIRRepository fhirRepository;
    private KafkaProducerService kafkaProducerService;

    @Value("${kafka.validation.topic}")
    private String validatedTopic = "";

    @Value("${kafka.fhir-conversion.topic}")
    private String convertedToFhirTopic = "";

    @Value("${kafka.xml-conversion.topic}")
    private String convertedToXmlTopic = "";

    @Value("${kafka.raw.topic}")
    private String rawTopic = "";

    public ElrDeadLetterService(
            IElrDeadLetterRepository dltRepository,
            IRawELRRepository rawELRRepository,
            IValidatedELRRepository validatedELRRepository,
            KafkaProducerService kafkaProducerService,
            IHL7ToFHIRRepository fhirRepository) {
        this.dltRepository = dltRepository;
        this.rawELRRepository = rawELRRepository;
        this.validatedELRRepository = validatedELRRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.fhirRepository = fhirRepository;
    }

    public List<ElrDeadLetterDto> getAllErrorDltRecord() {
        Optional<List<ElrDeadLetterModel>> deadLetterELRModels = dltRepository.findAllDltRecordByDltStatus(ElrDltStatus.ERROR.name(), Sort.by(Sort.Direction.DESC, "createdOn"));
        var dtoModels = convertModelToDto(deadLetterELRModels.get());
        return dtoModels;
    }

    public ElrDeadLetterDto getDltRecordById(String id) {
        Optional<ElrDeadLetterModel> model = dltRepository.findById(id);
        return new ElrDeadLetterDto(model.get());
    }

    public ElrDeadLetterDto updateAndReprocessingMessage(String id, String body) throws Exception {
        var existingRecord = getDltRecordById(id);
        existingRecord.setDltStatus(ElrDltStatus.REINJECTED.name());
        existingRecord.setDltOccurrence(existingRecord.getDltOccurrence() + 1);
        if(existingRecord.getErrorMessageSource().equalsIgnoreCase(rawTopic)) {
            var rawRecord = rawELRRepository.findById(existingRecord.getErrorMessageId());
            RawERLModel rawModel = rawRecord.get();
            rawModel.setPayload(body);
            rawELRRepository.save(rawModel);
            kafkaProducerService.sendMessageFromController(rawModel.getId(), rawTopic, rawModel.getType(), existingRecord.getDltOccurrence());
        } else if(existingRecord.getErrorMessageSource().equalsIgnoreCase(validatedTopic)) {

        } else if(existingRecord.getErrorMessageSource().equalsIgnoreCase(convertedToFhirTopic)) {

        } else if(existingRecord.getErrorMessageSource().equalsIgnoreCase(convertedToXmlTopic)) {

        } else {
            throw new Exception("Provided Error Source is not supported");
        }

        saveDltRecord(existingRecord);
        return existingRecord;
    }

    public ElrDeadLetterDto saveDltRecord(ElrDeadLetterDto model) {
        ElrDeadLetterModel modelForUpdate = dltRepository.save(convertDtoToModel(model));
        return model;
    }

    private List<ElrDeadLetterDto> convertModelToDto(List<ElrDeadLetterModel> models) {
        List<ElrDeadLetterDto>  dtlModels = new ArrayList<>() {};
        for(ElrDeadLetterModel model: models) {
            dtlModels.add(new ElrDeadLetterDto(model));
        }
        return dtlModels;
    }

    private ElrDeadLetterModel convertDtoToModel(ElrDeadLetterDto dtoModel) {
        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setId(dtoModel.getId());
        model.setErrorMessage(dtoModel.getErrorMessage());
        model.setErrorMessageId(dtoModel.getErrorMessageId());
        model.setErrorMessageSource(dtoModel.getErrorMessageSource());
        model.setErrorStackTrace(dtoModel.getErrorStackTrace());
        model.setDltOccurrence(dtoModel.getDltOccurrence());
        model.setDltStatus(dtoModel.getDltStatus());
        model.setCreatedOn(dtoModel.getCreatedOn());
        model.setUpdatedOn(dtoModel.getUpdatedOn());
        model.setCreatedBy(dtoModel.getCreatedBy());
        model.setUpdatedBy(dtoModel.getUpdatedBy());
        return model;
    }
}
