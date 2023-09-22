package gov.cdc.dataingestion.nbs.services;

import	gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import 	gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;

import 	lombok.AllArgsConstructor;
import 	org.springframework.beans.factory.annotation.Autowired;
import 	org.springframework.stereotype.Service;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import  java.time.ZonedDateTime;
import  java.time.ZoneOffset;
import	java.util.Calendar;
import	java.util.TimeZone;
import	java.util.UUID;

import	java.sql.Timestamp;

@Service
@AllArgsConstructor
public class EcrServiceProvider {
	private static Logger log = LoggerFactory.getLogger(EcrServiceProvider.class);

	private static String IMPEXP_CD = "I";
	private static String STATUS_UNPROCESSED = "ORIG_QUEUED";
	private static String SYSTEM_NAME_NBS = "NEDSS-Modernization";
	private static String DOCUMENT_TYPE_CODE = "PHC236";
	private static String FILLER_ORDER_NBR = "HL7EntityIdentifier";
	private static String LAB_CLIA = "HL7UniversalID";
	private static String ORDER_TEST_CODE = "HL7AlternateIdentifier";

    @Autowired
    private NbsInterfaceRepository nbsInterfaceRepo;
    
    public String saveEcrXmlMessage(String msgType, String xmlMsg) {
		NbsInterfaceModel item = new NbsInterfaceModel();
		String msgId = UUID.randomUUID().toString();

		String xmlPayload = xmlMsg + " <!-- raw_message_id = " + msgId + " -->";

		log.info("{} : ECR xml being persisted to NBS Legacy database", msgId);

		item.setPayload(xmlPayload);
		item.setImpExpIndCd(IMPEXP_CD);
		item.setRecordStatusCd(STATUS_UNPROCESSED);

		Timestamp recordTimestamp = new Timestamp(getGmtTimestamp());

		item.setRecordStatusTime(recordTimestamp);
		item.setAddTime(recordTimestamp);

		item.setSystemNm(SYSTEM_NAME_NBS);
		item.setDocTypeCd(DOCUMENT_TYPE_CODE);
		item.setOriginalPayload(xmlMsg);
		item.setOriginalDocTypeCd(null);
		//item.setFillerOrderNbr(FILLER_ORDER_NBR);
		//item.setLabClia(LAB_CLIA);
		item.setSpecimenCollDate(null);
		//item.setOrderTestCode(ORDER_TEST_CODE);
		item.setObservationUid(null);

    	nbsInterfaceRepo.save(item);
		log.info("{} : Persisted ECR xml to nbs database", msgId);

    	return msgId;
    }

	private long getGmtTimestamp() {
		ZonedDateTime currentDate = ZonedDateTime.now( ZoneOffset.UTC );
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.set(Calendar.HOUR, currentDate.getHour());
		cal.set(Calendar.MINUTE, currentDate.getMinute());
		cal.set(Calendar.SECOND, currentDate.getSecond());
		return cal.getTimeInMillis();
	}
}