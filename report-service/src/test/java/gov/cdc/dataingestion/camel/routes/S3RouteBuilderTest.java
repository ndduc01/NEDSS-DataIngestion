package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;

import static org.apache.camel.builder.AdviceWith.adviceWith;

class S3RouteBuilderTest extends CamelTestSupport {
    @MockBean
    private HL7FileProcessComponent hL7FileProcessComponent;

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new S3RouteBuilder();
    }

    @Test
    public void testMockEndpoints() throws Exception {
        RouteDefinition routeS3Main = context.getRouteDefinition("s3diRouteId");
        RouteDefinition routeS3UnzipFile = context.getRouteDefinition("s3UnzipFileRouteId");
        RouteDefinition routeS3ReadFromUnzipDir = context.getRouteDefinition("s3ReadFromUnzipDirRouteId");

        adviceWith(
                routeS3Main,
                context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:s3DIMainRoute");
                        weaveByToUri("bean:hL7FileProcessComponent*").replace().to("mock:s3DIMainResult");
                    }
                });
        adviceWith(
                routeS3UnzipFile,
                context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:s3UnzipFileRoute");
                        weaveByToUri("file:files/s3UnzipDownload").replace().to("mock:s3UnzipFileResult");
                    }
                });
        adviceWith(
                routeS3ReadFromUnzipDir,
                context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:s3ReadFromUnzipDirRoute");
                        weaveByToUri("bean:hL7FileProcessComponent*").replace().to("mock:s3ReadFromUnzippedFilesResult");
                    }
                });
        context.start();

        //For s3diRouteId
        MockEndpoint mock = getMockEndpoint("mock:s3DIMainResult");
        mock.expectedMessageCount(1);
        template.sendBody("direct:s3DIMainRoute", "HL7 Test message from S3 main Route");
        mock.assertIsSatisfied();
        //For s3UnzipFileRouteId
        MockEndpoint mockSftpUnzipFile = getMockEndpoint("mock:s3UnzipFileResult");
        mockSftpUnzipFile.expectedMinimumMessageCount(4);
        template.sendBody("direct:s3UnzipFileRoute", new File("src/test/resources/hl7testdata.zip"));
        mockSftpUnzipFile.assertIsSatisfied();
        ////For s3ReadFromUnzipDirRouteId
        MockEndpoint mockSftpReadFromUnzippedFiles = getMockEndpoint("mock:s3ReadFromUnzippedFilesResult");
        mockSftpReadFromUnzippedFiles.expectedMessageCount(1);
        template.sendBody("direct:s3ReadFromUnzipDirRoute", "HL7 Test message from S3 Route");
        mockSftpReadFromUnzippedFiles.assertIsSatisfied();
    }
}