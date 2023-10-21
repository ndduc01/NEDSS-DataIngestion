package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import static org.apache.camel.builder.AdviceWith.adviceWith;
import static org.junit.jupiter.api.Assertions.*;

@UseAdviceWith
class SFTPRouteBuilderTest extends CamelTestSupport {
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

//        adviceWith(
//                route,
//                context,
//                new AdviceWithRouteBuilder() {
//                    @Override
//                    public void configure() throws Exception {
//                        weaveAddLast().to("mock:finishSftpRoute");
//                    }
//                });
        adviceWith(
                route,
                context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                       // weaveAddLast().to("mock:finishSftpRoute");
                        replaceFromWith("direct:start");
                    }
                });
        context.start();

        MockEndpoint mock = getMockEndpoint("mock:direct:start");
        mock.expectedMessageCount(1);

        template.sendBody("direct:start", "Team");

        mock.assertIsSatisfied();
    }
}