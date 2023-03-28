package gov.cdc.dataingestion.report.it;

import gov.cdc.dataingestion.report.entity.RawMessageEntity;
import gov.cdc.dataingestion.report.service.impl.RawMessageServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = RawMessageServiceIT.DataSourceInitializer.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class RawMessageServiceIT {

    @Autowired
    private RawMessageServiceImpl rawMessageServiceImpl;

    @Container
    private static final PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:12.9-alpine");

    //@Container
    //private static final MSSQLServerContainer database = new MSSQLServerContainer()
      //      .acceptLicense();


    public static class DataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            database.start();
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
        entity.setFile("Content");

        String newEntityId = rawMessageServiceImpl.save(entity);
        RawMessageEntity entityRetrieved = rawMessageServiceImpl.findById(newEntityId);
        Assertions.assertThat(newEntityId).isEqualTo(entityRetrieved.getId());
    }


}
