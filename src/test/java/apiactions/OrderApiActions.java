package apiactions;

import entities.Order;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

public class OrderApiActions extends ApiActions {

    @Step("Send POST request to /api/orders to create an Order")
    public Response createOrder(Order order, String token) {

        RequestSpecification request = given()
                .header("Content-type", "application/json")
                .body(order);

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return request.post("/api/orders");
    }

    @Step("Send GET request to /api/orders to retrieve Order List")
    public Response getOrderList(String token) {

        RequestSpecification request = given();

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return request.get("/api/orders");
    }

    @Step("Check the orders array in the response")
    public void checkNumberOfOrders(Response response, int expectedCount) {
        response.then().body("orders", hasSize(expectedCount));
    }
}