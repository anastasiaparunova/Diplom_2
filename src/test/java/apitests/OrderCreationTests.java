package apitests;

import apiactions.OrderApiActions;
import apiactions.UserApiActions;
import com.github.javafaker.Faker;
import entities.Order;
import entities.User;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class OrderCreationTests extends OrderApiActions {

    private String randomEmail;
    private String randomPassword;
    private String randomName;
    private String token;
    private String[] ingredients;
    private User user;
    private Response response;
    private Order order;
    UserApiActions userApiActions;


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";

        userApiActions = new UserApiActions();

        Faker faker = new Faker();

        randomEmail = faker.internet().emailAddress();
        randomPassword = faker.internet().password();
        randomName = faker.name().name();

        user = new User(randomEmail, randomPassword, randomName);
        userApiActions.createUser(user);
        token = userApiActions.extractToken(user);

    }

    @Test
    @DisplayName("Should create order for authorized User")
    public void successForAuthorizedUser() {

        ingredients = new String[]{"61c0c5a71d1f82001bdaaa77", "61c0c5a71d1f82001bdaaa78"};
        order = new Order(ingredients);
        response = createOrder(order, token);

        checkResponseCode(response, 200);
        checkResponseMessage(response, "success", true);
        checkResponseMessage(response, "order.number");
    }

    @Test
    @DisplayName("Should not create an order without ingredients for authorized User")
    public void failWithoutIngredientsForAuthorizedUser() {

        ingredients = new String[]{};
        order = new Order(ingredients);
        response = createOrder(order, token);

        checkResponseCode(response, 400);
        checkResponseMessage(response, "success", false);
        checkResponseMessage(response, "message", "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Should not create an order with invalid ingredients for authorized User")
    public void failWithInvalidIngredientForAuthorizedUser() {

        ingredients = new String[]{"invalid_ingredient"};
        order = new Order(ingredients);
        response = createOrder(order, token);

        checkResponseCode(response, 500);
    }

    @Test
    @DisplayName("Should not create order for unauthorized User")
// В документации указано, что создавать заказы могут только авторизованные пользователи.
// Однако не описано, какое именно поведение должно быть в случае ошибки при попытке создания заказа без авторизации.
// В данном тесте я предположила, что сервер должен вернуть ошибку 401 Unauthorized и success: false.
    public void failForUnauthorizedUser() {

        ingredients = new String[]{"61c0c5a71d1f82001bdaaa77", "61c0c5a71d1f82001bdaaa78"};
        order = new Order(ingredients);
        response = createOrder(order, null);

        checkResponseCode(response, 401);
        checkResponseMessage(response, "success", false);
    }

    @Test
    @DisplayName("Should not create an order without ingredients for unauthorized User")
    public void failWithoutIngredientsForUnauthorizedUser() {

        ingredients = new String[]{};
        order = new Order(ingredients);
        response = createOrder(order, null);

        checkResponseCode(response, 400);
        checkResponseMessage(response, "success", false);
        checkResponseMessage(response, "message", "Ingredient ids must be provided");
    }


    @Test
    @DisplayName("Should not create an order with invalid ingredients for unauthorized User")
    public void failWithInvalidIngredientForUnauthorizedUser() {
        ingredients = new String[]{"invalid_ingredient"};
        order = new Order(ingredients);
        response = createOrder(order, null);

        checkResponseCode(response, 500);
    }

    @After
    public void clearTestingData() {
        if (token != null) {
            userApiActions.deleteUser(token);
        }
    }
}


