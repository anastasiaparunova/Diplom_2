package apitests;

import apiactions.UserApiActions;
import com.github.javafaker.Faker;
import entities.User;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserLoginTests extends UserApiActions {

    private String randomEmail;
    private String randomPassword;
    private String randomName;
    private String token;


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";

        Faker faker = new Faker();

        randomEmail = faker.internet().emailAddress();
        randomPassword = faker.internet().password();
        randomName = faker.name().name();

        User user = new User(randomEmail, randomPassword, randomName);
        createUser(user);
        token = extractToken(user);
    }

    @Test
    @DisplayName("Should login successfully when all credentials are valid")
    public void shouldLoginSuccessfullyWithValidCredentials() {
        User user = new User(randomEmail, randomPassword);

        Response response = userLogin(user);

        checkResponseCode(response, 200);
        checkResponseMessage(response, "success",true);
    }

    @Test
    @DisplayName("Should return error when email is missing")
    public void shouldNotLoginWithEmptyEmail() {
        User user = new User("", randomPassword);

        Response response = userLogin(user);

        checkResponseCode(response, 401);
        checkResponseMessage(response, "message", "email or password are incorrect");
    }

    @Test
    @DisplayName("Should return error when password is missing")
    public void shouldNotLoginWithEmptyPassword() {
        User user = new User(randomEmail, "");

        Response response = userLogin(user);

        checkResponseCode(response, 401);
        checkResponseMessage(response,"message", "email or password are incorrect");
    }

    @Test
    @DisplayName("Should return error when email is invalid")
    public void shouldNotLoginWithInvalidEmail() {
        User user = new User(randomEmail + "_invalid", randomPassword);

        Response response = userLogin(user);

        checkResponseCode(response, 401);
        checkResponseMessage(response,"message", "email or password are incorrect");
    }

    @Test
    @DisplayName("Should return error when password is invalid")
    public void shouldNotLoginWithInvalidPassword() {
        User user = new User(randomEmail, randomPassword + "_invalid");

        Response response = userLogin(user);

        checkResponseCode(response, 401);
        checkResponseMessage(response, "message","email or password are incorrect");
    }

    @After
    public void clearTestingData() {
        if (token != null) {
           deleteUser(token);
        }
    }

}
