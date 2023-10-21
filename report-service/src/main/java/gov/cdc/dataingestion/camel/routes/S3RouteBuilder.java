package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class S3RouteBuilder extends EndpointRouteBuilder {
    private static Logger logger = LoggerFactory.getLogger(S3RouteBuilder.class);

    @Autowired
    private HL7FileProcessor hL7FileProcessor;

    @Override
    public void configure() throws Exception {
        logger.info("************ calling S3RouteBuilder *********");
        try {
            from(aws2S3("di-manual-file-dropoff").delay(1000L)
                    .useDefaultCredentialsProvider(true).deleteAfterRead(true)).routeId("s3RouteId")
                    .log("The file content from s3 is ${body}")
                    .setHeader("msgType", constant("HL7"))
                    .setHeader("validationActive", constant("false"))
                    .process(hL7FileProcessor)
                    .end();
        } catch (Exception e) {
            logger.error(e.getMessage());
            //throw e;
        }
    }
}