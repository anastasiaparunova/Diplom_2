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

import java.util.HashMap;
import java.util.Map;

public class UserFieldsUpdateTests extends UserApiActions { //ПЛЮС ТЕСТ О ТОМ, ЧТО НЕЛЬЗЯ С ДУБЛИКАТОМ ПОЧТЫ

    private String randomEmail;
    private String randomPassword;
    private String randomName;
    private String token;
    Map<String, String> updates = new HashMap<>();
    private User user;
    private Response response;


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";

        Faker faker = new Faker();

        randomEmail = faker.internet().emailAddress();
        randomPassword = faker.internet().password();
        randomName = faker.name().name();

        user = new User(randomEmail, randomPassword, randomName);
        createUser(user);
        token = extractToken(user);
        updates.clear();
    }

    @Test
    @DisplayName("Should successfully update Email field for an authorized User")
    public void shouldUpdateEmailForAuthorizedUser() {

        updates.put("email", randomEmail + "_updated");

        response = updateUser(token, updates);

        checkResponseCode(response, 200);
        checkResponseMessage(response, "user.email", randomEmail + "_updated");
        checkResponseMessage(response, "user.name", randomName);
    }

    @Test
    @DisplayName("Should successfully update Name field for an authorized User")
    public void shouldUpdateNameForAuthorizedUser() {

        updates.put("name", randomName + "_updated");

        response = updateUser(token, updates);

        checkResponseCode(response, 200);
        checkResponseMessage(response, "user.email", randomEmail);
        checkResponseMessage(response, "user.name", randomName + "_updated");
    }

    @Test
    @DisplayName("Should successfully update Name and Email fields for an authorized User")
    public void shouldUpdateEmailAndNameForAuthorizedUser() {

        updates.put("name", randomName + "_updated");
        updates.put("email", randomEmail + "_updated");

        response = updateUser(token, updates);

        checkResponseCode(response, 200);
        checkResponseMessage(response, "user.email", randomEmail + "_updated");
        checkResponseMessage(response, "user.name", randomName + "_updated");
    }

    @Test
    @DisplayName("Should not update Email field if an email is already in use")
    public void shouldNotUpdateDuplicatedEmail() {

        User secondUser = new User (randomEmail + "_duplicated", randomPassword, randomName);
        createUser(secondUser);
        String secondUserToken = extractToken(secondUser);

        updates.put("email", randomEmail + "_duplicated");

        response = updateUser(token, updates);

        checkResponseCode(response, 403);
        checkResponseMessage(response, "message", "User with such email already exists");
        deleteUser(secondUserToken);
    }

    @Test
    @DisplayName("Should not update Email field for an unauthorized User")
    public void shouldNotUpdateEmailForUnauthorizedUser() {

        updates.put("email", randomEmail + "_updated");

        response = updateUserWithoutToken(updates);

        checkResponseCode(response, 401);
        checkResponseMessage(response, "message", "You should be authorised");

        user = new User(randomEmail, randomPassword);
        Response loginResponse = userLogin(user);

        checkResponseMessage(loginResponse, "user.email", randomEmail);
        checkResponseMessage(loginResponse, "user.name", randomName);


    }

    @Test
    @DisplayName("Should not update Name field for an unauthorized User")
    public void shouldNotUpdateNameForUnauthorizedUser() {

        updates.put("name", randomName + "_updated");

        response = updateUserWithoutToken(updates);

        checkResponseCode(response, 401);
        checkResponseMessage(response, "message", "You should be authorised");

        user = new User(randomEmail, randomPassword);
        Response loginResponse = userLogin(user);

        checkResponseMessage(loginResponse, "user.email", randomEmail);
        checkResponseMessage(loginResponse, "user.name", randomName);
    }

    @Test
    @DisplayName("Should not update Name and Email fields for unauthorized User")
    public void shouldNotUpdateEmailAndNameForUnauthorizedUser() {

        updates.put("name", randomName + "_updated");
        updates.put("email", randomEmail + "_updated");


        response = updateUserWithoutToken(updates);

        checkResponseCode(response, 401);
        checkResponseMessage(response, "message", "You should be authorised");

        user = new User(randomEmail, randomPassword);
        Response loginResponse = userLogin(user);

        checkResponseMessage(loginResponse, "user.email", randomEmail);
        checkResponseMessage(loginResponse, "user.name", randomName);
    }


    @After
    public void clearTestingData() {

        if (token != null) {
            deleteUser(token);
        }
    }
}
