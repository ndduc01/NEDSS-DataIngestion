package gov.cdc.dataingestion.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *  Report service application.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "gov.cdc.dataingestion.report.repository")
@EntityScan(basePackages = { "gov.cdc.dataingestion.report.entity" })
public class ReportServiceApplication {
    /**
     * Main method for spring boot application.
     * @param args
     */
    public static void main(final String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
    }

}
