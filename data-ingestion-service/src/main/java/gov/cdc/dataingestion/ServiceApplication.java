package gov.cdc.dataingestion;

import gov.cdc.dataingestion.security.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 *  Report service application.
 */
@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class ServiceApplication {
    /**
     * Main method for spring boot application.
     * @param args
     */
    public static void main(final String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}