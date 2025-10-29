package steps;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    static {
        RestAssured.baseURI = BASE_URL;
    }

    @Step("Создание заказа")
    public static Response createOrder(String token, String[] ingredients) {
        Map<String, String[]> body = new HashMap<>();
        body.put("ingredients", ingredients);

        RequestSpecification request = given()
                .header("Content-Type", "application/json");

        if (!token.isEmpty()) {
            request.header("Authorization", token);
        }

        return request
                .body(body)
                .when()
                .post("/orders");
    }

    @Step("Получение заказов пользователя")
    public static Response getUserOrders(String token) {
        RequestSpecification request = given();

        if (!token.isEmpty()) {
            request.header("Authorization", token);
        }

        return request
                .when()
                .get("/orders");
    }
}