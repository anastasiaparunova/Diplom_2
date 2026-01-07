package apiactions;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Assert;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class ApiActions {

    @Step("Compare a response code to an expected one")
    public void checkResponseCode(Response response, int expectedCode) {
        response.then().statusCode(expectedCode);

    }


    @Step("Compare a response message to an expected one")
    public void checkResponseMessage(Response response, String fieldPath, Object expectedValue) {
        try {
            response.then().body(fieldPath, equalTo(expectedValue));
        } catch (Exception e) {
            Assert.fail("Failed to extract field from the response");
        }
    }

    @Step("Check that field exists in the response")
    public void checkResponseMessage(Response response, String fieldPath) {
        try {
            String responseBody = response.getBody().asString();
            System.out.println(responseBody);
            response.then().body(fieldPath, notNullValue());
        } catch (Exception e) {
            Assert.fail("Field '" + fieldPath + "' is missing or null in the response");
        }
    }


}