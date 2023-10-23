package gov.cdc.dataingestion.registration.controller;

import gov.cdc.dataingestion.registration.service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RegisterController {
    private final RegistrationService registrationService;
    private String USERNAME_PWD_REQ_MSG="Username and/or password are required.";
    private String USERNAME_PWD_MIN_LENGTH_MSG="The username and password must be eight characters in length.";
    private String USER_CREATED_MSG="User Created Successfully.";
    private String USER_ALREADY_EXIST_MSG="User already exists.Please choose another.";
    public RegisterController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/registration")
    public String createUser(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        log.info("Inside registration controller...");
        System.out.println("username:>>"+username+"<< password:>>"+password+"<<");
        if(username.isEmpty() || password.isEmpty()) {
            log.error("Username and/or password are required.");
            return USERNAME_PWD_REQ_MSG;
        }
        if(username.trim().length()<8 || password.trim().length()<8){
            log.error("The username and password must be eight characters in length.");
            return USERNAME_PWD_MIN_LENGTH_MSG;
        }
        if(registrationService.createUser(username, password)) {
            log.debug("New User Created Successfully");
            return USER_CREATED_MSG;
        }
        log.error("Username already exists");
        return USER_ALREADY_EXIST_MSG;
    }
}