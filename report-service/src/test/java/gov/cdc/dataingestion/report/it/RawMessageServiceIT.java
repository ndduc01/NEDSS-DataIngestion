package gov.cdc.dataingestion.report.it;

import gov.cdc.dataingestion.report.entity.RawMessageEntity;
import gov.cdc.dataingestion.report.service.impl.RawMessageServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;


@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = RawMessageServiceIT.DataSourceInitializer.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class RawMessageServiceIT {

    private static final Logger log = LoggerFactory.getLogger(RawMessageServiceIT.class);

    @Autowired
    private RawMessageServiceImpl rawMessageServiceImpl;

    //@Container
    //private static final PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:12.9-alpine");

    private static final DockerImageName taggedImageName = DockerImageName.parse("mcr.microsoft.com/azure-sql-edge")
            .withTag("latest")
      .asCompatibleSubstituteFor("mcr.microsoft.com/mssql/server");
    @Container
    private static final MSSQLServerContainer database = new MSSQLServerContainer<>(taggedImageName)
            .withTmpFs(Collections.singletonMap("/testtmpfs", "rw"))
            .withReuse(true)
            .acceptLicense();


    public static class DataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            database.start();
            System.out.println("Started::" + database.isRunning());
            System.out.println("postgres:" + database.getJdbcUrl());
            System.out.println("MSSQLServer:" + database.getJdbcUrl());

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.test.database.replace=none",
                    "spring.datasource.url=" + database.getJdbcUrl(),
                    "spring.datasource.username=" + database.getUsername(),
                    "spring.datasource.password=" + database.getPassword()
            );
        }
    }


    @Test
    public void saveRawMessage(){

        RawMessageEntity entity = new RawMessageEntity();
        entity.setId("Test1");
        entity.setFileContent("Content");

        String newEntityId = rawMessageServiceImpl.save(entity);
        RawMessageEntity entityRetrieved = rawMessageServiceImpl.findById(newEntityId);
        Assertions.assertThat(newEntityId).isEqualTo(entityRetrieved.getId());
    }


}
