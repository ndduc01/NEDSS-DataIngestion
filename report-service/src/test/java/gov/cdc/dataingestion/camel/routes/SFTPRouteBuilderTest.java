package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.test.spring.junit5.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.*;


class SFTPRouteBuilderTest  extends CamelTestSupport {

//    @Mock
//    private HL7FileProcessor hl7FileProcessor;
//    @MockBean
//HL7FileProcessComponent hL7FileProcessComponent;
    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new SFTPRouteBuilder();
    }

    @Test
    public void testMockEndpoints() throws Exception {
        RouteDefinition route = context.getRouteDefinition("sftpRouteId");

        adviceWith(
                route,
                context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:start1");
                        weaveByToUri("bean:hL7FileProcessComponent*").replace().to("mock:end2");
                        //weaveAddLast().to("mock:end2");

                    }
                });
        //context.start();
        //context.getRoute("direct:start1").setProcessor(hl7FileProcessor);
        context.start();

        MockEndpoint mock = getMockEndpoint("mock:end2");
        mock.expectedMessageCount(1);

        template.sendBody("direct:start1", "Unit testing for bean");

        mock.assertIsSatisfied();
    }
}