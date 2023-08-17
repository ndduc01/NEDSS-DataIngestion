package gov.cdc.dataingestion;

import gov.cdc.dataingestion.security.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;

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
    public static void main(final String[] args) throws IOException{
        SpringApplication.run(ServiceApplication.class, args);
    }
}
