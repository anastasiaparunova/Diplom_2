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

public class UserCreationTests extends UserApiActions {


    private String randomEmail;
    private String randomPassword;
    private String randomName;
    private Response response;
    private String token;
    User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";

        Faker faker = new Faker();

        randomEmail = faker.internet().emailAddress();
        randomPassword = faker.internet().password();
        randomName = faker.name().name();
    }

    @Test
    @DisplayName("Should successfully create a unique User")
    public void shouldCreateUser() {
        user = new User(randomEmail, randomPassword, randomName);
        response = createUser(user);

        checkResponseCode(response, 200);
        checkResponseMessage(response,"success", true);
    }

    @Test
    @DisplayName("Should not allow creating a User with an existing login data")
    public void shouldNotCreateUserWithSameData() {
        user = new User(randomEmail, randomPassword, randomName);
        createUser(user);
        response = createUser(user);

        checkResponseCode(response, 403);
        checkResponseMessage(response, "message","User already exists");
    }

    @Test
    @DisplayName("Should not allow creating a User with an empty email")
    public void shouldNotCreateUserWithEmptyLogin() {
        user = new User("", randomPassword, randomName);
        response = createUser(user);

        checkResponseCode(response, 403);
        checkResponseMessage(response, "message", "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Should not allow creating a User with an empty password")
    public void shouldNotCreateUserWithEmptyPassword() {
        user = new User(randomEmail, "", randomName);
        response = createUser(user);

        checkResponseCode(response, 403);
        checkResponseMessage(response, "message", "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Should not allow creating a User with an empty name")
    public void shouldNotCreateUserWithEmptyName() {
        User user = new User(randomEmail, randomPassword, "");
        response = createUser(user);

        checkResponseCode(response, 403);
        checkResponseMessage(response, "message","Email, password and name are required fields");
    }

    @Test
    @DisplayName("Should not allow creating a User with an empty email, password, name")
    public void shouldNotCreateUserWithEmptyLoginData() {
        User user = new User("", "", "");
        response = createUser(user);

        checkResponseCode(response, 403);
        checkResponseMessage(response, "message","Email, password and name are required fields");
    }


    @After
public void clearTestingData() {
        if (response != null && response.statusCode() == 200) {
            token = extractToken(user);
            deleteUser(token);
        }
    }
    }
