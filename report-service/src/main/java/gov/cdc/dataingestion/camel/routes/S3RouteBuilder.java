package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.FailedToStartRouteException;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "di.camel.s3-endpoint.enabled", havingValue = "true")
public class S3RouteBuilder extends EndpointRouteBuilder {
    private static Logger logger = LoggerFactory.getLogger(S3RouteBuilder.class);

    @Autowired
    private HL7FileProcessComponent hL7FileProcessComponent;
//    @Autowired
//    private HL7FileProcessor hL7FileProcessor;

    @Override
    public void configure() throws Exception {
        logger.info("************ calling S3RouteBuilder *********");

        onException(FailedToStartRouteException.class)
                .log("FailedToStartRouteException s3RouteBuilder: ${exception}")
                .handled(true);
        onException(RuntimeCamelException.class).
                handled(true)
                .log("RuntimeCamelException s3RouteBuilder: ${exception}");
        System.out.println("************ calling S3RouteBuilder *********");
//            from(aws2S3("di-manual-file-dropoff").delay(1000L)
//                    .useDefaultCredentialsProvider(true).deleteAfterRead(true)).routeId("s3diRouteId")
//                    .log("The file content from s3 is ${body}")
//                    .setHeader("msgType", constant("HL7"))
//                    .setHeader("validationActive", constant("false"))
//                    .process(hL7FileProcessor)
//                    .end();

        from(aws2S3("di-manual-file-dropoff").delay(1000L)
                .useDefaultCredentialsProvider(true).deleteAfterRead(true)).routeId("s3diRouteId")
                .log("The file content from s3 is ${body}")
                // .setHeader("msgType", constant("HL7"))
                // .setHeader("validationActive", constant("false"))
                .to("bean:hL7FileProcessComponent")
                .end();
    }
//    @Bean
//    public HL7FileProcessComponent hL7FileProcessComponent() {
//        return new HL7FileProcessComponent();
//    }
}