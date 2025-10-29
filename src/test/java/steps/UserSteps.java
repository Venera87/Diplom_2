package steps;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserSteps {

    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    static {
        RestAssured.baseURI = BASE_URL;
    }

    @Step("Создание пользователя")
    public static Response createUser(String email, String password, String name) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("name", name);

        return given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/auth/register");
    }

    @Step("Логин пользователя")
    public static Response login(String email, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        return given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/auth/login");
    }

    @Step("Обновление данных пользователя")
    public static Response updateUser(String token, String name, String email, String password) {
        Map<String, String> body = new HashMap<>();
        if (name != null) body.put("name", name);
        if (email != null) body.put("email", email);
        if (password != null) body.put("password", password);

        RequestSpecification request = given()
                .header("Content-Type", "application/json");

        if (!token.isEmpty()) {
            request.header("Authorization", token);
        }

        return request
                .body(body)
                .when()
                .patch("/auth/user");
    }

    @Step("Удаление пользователя")
    public static void deleteUser(String token) {
        given()
                .header("Authorization", token)
                .when()
                .delete("/auth/user");
    }
}