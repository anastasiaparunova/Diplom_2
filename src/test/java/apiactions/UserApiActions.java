package apiactions;

import com.google.gson.Gson;
import entities.TokenResponse;
import entities.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserApiActions extends ApiActions {

    static Gson gson = new Gson();

    @Step("Send POST request to /api/auth/register to register new User")
    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    @Step("Send POST request to /api/auth/login to login via a registered User")
    public Response userLogin(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/login");
    }

    @Step("Extract token from response")
    public String extractToken(User user) {

        Response response = userLogin(user);
        if (response.statusCode() != 200) {
            throw new AssertionError("Login failed");
        }
        String responseBody = response.getBody().asString();
        TokenResponse tokenResponse = gson.fromJson(responseBody, TokenResponse.class);

        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new AssertionError("Token is missing in the response");
        }

        String token = tokenResponse.getAccessToken();
        if (token.startsWith("Bearer ")) {
            token = token.substring("Bearer ".length());
        }
        return token;
    }

    @Step("Send PATCH request to /api/auth/user to update a User")
    public Response updateUser(String token, Map<String, String> updates) {
        String jsonBody = gson.toJson(updates);

        return given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Send PATCH request to /api/auth/user without token")
    public Response updateUserWithoutToken(Map<String, String> updates) {
        String jsonBody = gson.toJson(updates);

        return given()
                .header("Content-type", "application/json")
                .body(jsonBody)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Send DELETE request to /api/auth/user to delete a User")
    public void deleteUser(String token) {

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/auth/user");

    }

}