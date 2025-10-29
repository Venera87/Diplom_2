package tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;
import utils.RandomDataGenerator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UpdateUserTest {

    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    public void setUp() {
        email = RandomDataGenerator.generateEmail();
        password = RandomDataGenerator.generatePassword();
        name = RandomDataGenerator.generateName();
        // Создаём пользователя и логинимся для получения токена
        UserSteps.createUser(email, password, name);
        Response loginResponse = UserSteps.login(email, password);
        accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            UserSteps.deleteUser(accessToken);
        }
    }

    @Test
    @Step("Изменение данных пользователя с авторизацией")
    public void updateUserWithAuthorization() {
        String newName = "UpdatedName";
        Response response = UserSteps.updateUser(accessToken, newName, null, null);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(newName));
    }

    @Test
    @Step("Изменение данных пользователя без авторизации")
    public void updateUserWithoutAuthorization() {
        String newName = "HackerName";
        Response response = UserSteps.updateUser("", newName, null, null);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}