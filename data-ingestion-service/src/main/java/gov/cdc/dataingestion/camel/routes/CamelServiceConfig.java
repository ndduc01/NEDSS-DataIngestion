package gov.cdc.dataingestion.camel.routes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelServiceConfig {
    @Bean
    public HL7FileProcessComponent hL7FileProcessComponent() {
        return new HL7FileProcessComponent();
    }
}
