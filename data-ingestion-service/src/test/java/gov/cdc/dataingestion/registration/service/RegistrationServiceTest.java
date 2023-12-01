package gov.cdc.dataingestion.registration.service;

import gov.cdc.dataingestion.registration.model.RegisterClient;
import gov.cdc.dataingestion.registration.repository.IClientRegisterRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrationServiceTest {
    @Mock
    private IClientRegisterRepository iClientRegisterRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @BeforeEach
    public void setUpEach() {
        MockitoAnnotations.openMocks(this);
        registrationService = new RegistrationService(passwordEncoder, iClientRegisterRepository);
    }

    @Test
    void createUserTestExistUser(){
        String username = "u";
        String password = "p";
        RegisterClient model = new RegisterClient();
        model.setId("id");
        model.setUsername("user");
        model.setPassword("pass");
        model.setRoles("tester");
        model.setUpdatedBy("test");
        model.setCreatedBy("test");
        model.setCreatedOn( new Timestamp(System.currentTimeMillis()));
        model.setUpdatedOn( new Timestamp(System.currentTimeMillis()));

        when(iClientRegisterRepository.findByUsername(username)).thenReturn(Optional.of(model));

        var result = registrationService.createUser(username, password);
        Assertions.assertFalse(result);
        verify(iClientRegisterRepository).findByUsername(username);
        Assertions.assertEquals("id", model.getId());
        Assertions.assertEquals("user", model.getUsername());
        Assertions.assertEquals("pass", model.getPassword());
        Assertions.assertEquals("tester", model.getRoles());
        Assertions.assertEquals("test", model.getCreatedBy());
        Assertions.assertEquals("test", model.getUpdatedBy());
        Assertions.assertNotNull(model.getCreatedOn());
        Assertions.assertNotNull(model.getUpdatedOn());
    }

    @Test
    void testCreateUserNotExistUserAdmin(){
        String username = "admin";
        String password = "password";

        when(iClientRegisterRepository.findByUsername(username)).thenReturn(Optional.empty());

        var result = registrationService.createUser(username, password);
        Assertions.assertTrue(result);

    }

    @Test
    void testCreateUserNotExistUserUser(){
        String username = "user";
        String password = "password";

        when(iClientRegisterRepository.findByUsername(username)).thenReturn(Optional.empty());

        var result = registrationService.createUser(username, password);
        Assertions.assertTrue(result);

    }
}