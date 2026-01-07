package apitests;

import apiactions.OrderApiActions;
import apiactions.UserApiActions;
import com.github.javafaker.Faker;
import entities.Order;
import entities.User;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

public class OrderListTest extends OrderApiActions {

    private String randomEmail;
    private String randomPassword;
    private String randomName;
    private String token;
    private String[] ingredients;
    private User user;
    private Response response;
    UserApiActions userApiActions;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";

        Faker faker = new Faker();

        randomEmail = faker.internet().emailAddress();
        randomPassword = faker.internet().password();
        randomName = faker.name().name();

        userApiActions = new UserApiActions();
        user = new User(randomEmail, randomPassword, randomName);
        userApiActions.createUser(user);
        token = userApiActions.extractToken(user);
    }


    @Test
    @DisplayName("Should retrieve empty order list for authorized User")
    public void successForAuthorizedUserWithEmptyList() {
        response = getOrderList(token);

        checkResponseCode(response, 200);
        checkResponseMessage(response, "success", true);
        checkNumberOfOrders(response, 0);
    }


    @Test
    @DisplayName("Should retrieve order list for authorized User")
    public void successForAuthorizedUserWithExistingList() {

        String[] firstOrderIngredients = new String[]{
                "61c0c5a71d1f82001bdaaa77",
                "61c0c5a71d1f82001bdaaa78"};
        Order firstOrder = new Order(firstOrderIngredients);

        String[] secondOrderIngredients = new String[]{
                "61c0c5a71d1f82001bdaaa6c",
                "61c0c5a71d1f82001bdaaa73",
                "61c0c5a71d1f82001bdaaa70"
        };
        Order secondOrder = new Order(secondOrderIngredients);

        createOrder(firstOrder, token);
        createOrder(secondOrder, token);

        response = getOrderList(token);

        checkResponseCode(response, 200);
        checkResponseMessage(response, "success", true);
        checkNumberOfOrders(response, 2);
    }

    @Test
    @DisplayName("Should not retrieve order list for unauthorized User")

    public void failForUnauthorizedUser() {

        response = getOrderList(null);

        checkResponseCode(response, 401);
        checkResponseMessage(response, "success", false);
        checkResponseMessage(response, "message", "You should be authorised");
    }

}
