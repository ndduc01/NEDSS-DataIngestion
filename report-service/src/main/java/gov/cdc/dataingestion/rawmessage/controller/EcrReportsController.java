package     gov.cdc.dataingestion.rawmessage.controller;

import      io.swagger.v3.oas.annotations.Operation;
import      io.swagger.v3.oas.annotations.tags.Tag;
import      lombok.RequiredArgsConstructor;

import      org.slf4j.Logger;
import      org.slf4j.LoggerFactory;
import      org.springframework.beans.factory.annotation.Autowired;
import      org.springframework.http.MediaType;
import      org.springframework.http.ResponseEntity;
import      org.springframework.web.bind.annotation.RestController;
import      org.springframework.web.bind.annotation.RequestMapping;
import      org.springframework.web.bind.annotation.PostMapping;
import      org.springframework.web.bind.annotation.RequestBody;
import      org.springframework.web.bind.annotation.RequestHeader;

import 	    gov.cdc.dataingestion.nbs.services.EcrServiceProvider;

@Tag(name = "ECR Reports", description = "ECR reports API")

@RestController
@RequestMapping("/api/ecrreport")
@RequiredArgsConstructor
public class EcrReportsController {
    private static Logger log = LoggerFactory.getLogger(EcrReportsController.class);

    @Autowired
    private EcrServiceProvider ecrServiceProvider;

    @Operation(
            summary = "Submit phdc xml message",
            description = "Submit phdc xml message with msgType header",
            tags = { "dataingestion", "ecr" })
    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> process(@RequestBody final String msgPayload, @RequestHeader("msgType") String msgType) {
        String msgId = ecrServiceProvider.saveEcrXmlMessage(msgType, msgPayload);
        return ResponseEntity.ok(msgId);
    }
}
