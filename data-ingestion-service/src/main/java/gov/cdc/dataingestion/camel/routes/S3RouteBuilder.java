package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.FailedToStartRouteException;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipSplitter;
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

    @Override
    public void configure() throws Exception {
        logger.info("************ calling S3RouteBuilder *********");

        try {
            from(aws2S3("di-manual-file-dropoff").delay(1000L)
                    .useDefaultCredentialsProvider(true).deleteAfterRead(true)).routeId("s3diRouteId")
                    .log("S3 file name:${header.CamelAwsS3Key}")
                    //.log("S3 header:${headers}")
                    .choice()
                    .when(simple("${header.CamelAwsS3Key} endsWith '.zip'"))
                    .log(" when .zip condition...The file name: ${header.CamelAwsS3Key}")
                    .to("file:files/s3download")
                    .otherwise()
                    .log(" Otherwise condition for txt files ...The file ${header.CamelAwsS3Key}")
                    .to("bean:hL7FileProcessComponent")
                    .end();
            // Unzip the downloaded file
            log.debug("Calling s3UnzipFileRouteId");
            from("file:files/s3download")
                    .routeId("s3UnzipFileRouteId")
                    //.log("file type: ${headers}")
                    .split(new ZipSplitter()).streaming()
                    .to("file:files/s3UnzipDownload")
                    .end();

            //Process the files from unzipped folder
            log.debug("Calling s3downloadUnzip");
            from("file:files/s3UnzipDownload?includeExt=txt")
                    .routeId("s3ReadFromUnzipDirRouteId")
                    .log(" Read from unzipped files folder ...The file ${file:name}")
                    .to("bean:hL7FileProcessComponent")
                    .end();
        } catch (FailedToStartRouteException fex) {
            fex.printStackTrace();
        } catch (RuntimeCamelException rex) {
            rex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("Exception in s3route builder:" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}