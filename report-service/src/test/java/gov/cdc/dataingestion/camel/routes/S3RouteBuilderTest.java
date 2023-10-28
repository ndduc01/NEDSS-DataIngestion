package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

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
        RouteDefinition route = context.getRouteDefinition("s3diRouteId");
        adviceWith(
                route,
                context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:fromS3Route");
                        weaveByToUri("bean:hL7FileProcessComponent*").replace().to("mock:results3");
                    }
                });
        context.start();

        MockEndpoint mock = getMockEndpoint("mock:results3");
        mock.expectedMessageCount(1);
        template.sendBody("direct:fromS3Route", "HL7 Test message from S3 Route");
        mock.assertIsSatisfied();
    }
}