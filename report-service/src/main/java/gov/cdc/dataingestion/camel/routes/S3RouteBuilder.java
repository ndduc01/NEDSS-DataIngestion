package gov.cdc.dataingestion.camel.routes;

import com.amazonaws.services.s3.AmazonS3Client;
import org.apache.camel.FailedToStartRouteException;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.support.SimpleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Component
@ConditionalOnProperty(name = "di.camel.s3-endpoint.enabled", havingValue = "true")
public class S3RouteBuilder extends EndpointRouteBuilder {
    private static Logger logger = LoggerFactory.getLogger(S3RouteBuilder.class);

    @Autowired
    private HL7FileProcessComponent hL7FileProcessComponent;

    @Autowired
    AmazonS3 amazonS3Client;
//    @Autowired
//    private HL7FileProcessor hL7FileProcessor;
// mainObj=new Main();

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
        System.out.println("amazonS3Client:"+amazonS3Client);
       // S3Client s3Client= s3Client();
        //AmazonS3 s3Client= s3Client();
       // mainObj.bind("s3Client",s3Client);
//            from(aws2S3("di-manual-file-dropoff").delay(1000L)
//                    .useDefaultCredentialsProvider(true).deleteAfterRead(true)).routeId("s3diRouteId")
//                    .log("The file content from s3 is ${body}")
//                    .setHeader("msgType", constant("HL7"))
//                    .setHeader("validationActive", constant("false"))
//                    .process(hL7FileProcessor)
//                    .end();

//        from(aws2S3("di-manual-file-dropoff").delay(1000L)
//                .useDefaultCredentialsProvider(true).deleteAfterRead(true)).routeId("s3diRouteId")
//                .log("The file content from s3 is ${body}")
//                // .setHeader("msgType", constant("HL7"))
//                // .setHeader("validationActive", constant("false"))
//                .to("bean:hL7FileProcessComponent")
//                .end();
//        from(aws2S3("di-manual-file-dropoff?amazonS3Client=#s3Client").delay(1000L)
//                .deleteAfterRead(true)).routeId("s3diRouteId")
//                .log("The file content from s3 is ${body}")
//                // .setHeader("msgType", constant("HL7"))
//                // .setHeader("validationActive", constant("false"))
//               // .to("bean:hL7FileProcessComponent")
//                .end();
        from("aws2-s3://di-manual-file-dropoff?amazonS3Client=#amazonS3Client&deleteAfterRead=true")//&moveAfterRead=true&destinationBucket=myothercamelbucket&destinationBucketPrefix=RAW(pre-)&destinationBucketSuffix=RAW(-suff)")
                .routeId("s3diRouteId")//.onException(Exception.class).handled(true).end()
                .log("The file content from s3 is ${body}")
                .end();



    }

//    private S3Client getS3Client1(){
//        S3Client s3Client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsBucketAccessKey, awsBucketSecretKey)))
//                .region(Region.US_EAST_1).build();
//    }
}