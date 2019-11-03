package com.sda.springmvc.example.controllers;

import com.sda.springmvc.example.entities.User;
import com.sda.springmvc.example.repositories.UserRepository;
import com.sda.springmvc.example.validation.AgeValidationService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestUserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AgeValidationService ageValidationService;

    @InjectMocks
    private RestUserController userController;

    @Before
    public void setUp(){
        RestAssuredMockMvc.standaloneSetup(userController);

        User user = new User();
        user.setId(100500L);

        doReturn(user).when(userRepository).save(any(User.class));
    }

    @Test
    public void should_fetch_all_users_from_user_repository(){
        given()
                .when()
                .get("/api/users")
                .then()
                .log().ifValidationFails()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void should_return_not_found_when_user_does_not_exist(){
        given()
                .when()
                .get("/api/users/1")
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.NOT_FOUND.value());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void should_reject_empty_json(){
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{}")
                .when()
                .post("/api/users")
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void should_send_valid_request_body_and_check_if_agevalidationservice_is_called(){
        postValidEntity()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void should_return_bad_request_when_age_is_not_valid(){
        doReturn(false).when(ageValidationService).isValid(any(User.class));

        postValidEntity()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(CoreMatchers.is(CoreMatchers.equalTo("{" +
                        "\"status\":400," +
                        "\"error\":\"Bad Request\"," +
                        "\"message\":\"Age is not valid\"" +
                        "}")));
    }

    @Test
    public void should_save_new_user_to_database(){
        doReturn(true).when(ageValidationService).isValid(any(User.class));

        postValidEntity()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value());

        verify(userRepository, times(1)).save(any(User.class));
    }

    private ValidatableMockMvcResponse postValidEntity() {
        final String validJson = "{" +
                "  \"name\": \"Josh Long\"," +
                "  \"email\": \"josh@long.com\"," +
                "  \"country\": \"US\"," +
                "  \"dateOfBirth\": \"2011-12-03\"" +
                "}";
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(validJson)
                .when()
                .post("/api/users")
                .then()
                .log().ifValidationFails();

    }

    @Test
    public void should_return_response_with_id(){
        doReturn(true).when(ageValidationService).isValid(any(User.class));
        final long id = 100500L;

        postValidEntity()
                .body("id", CoreMatchers.is(CoreMatchers.equalTo(100500)));
    }

}