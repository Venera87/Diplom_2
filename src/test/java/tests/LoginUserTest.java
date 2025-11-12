package tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;
import utils.RandomDataGenerator;

import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {

    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    public void setUp() {
        email = RandomDataGenerator.generateEmail();
        password = RandomDataGenerator.generatePassword();
        name = RandomDataGenerator.generateName();
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            UserSteps.deleteUser(accessToken);
        }
    }

    @Test
    @Step("Логин под существующим пользователем")
    public void loginExistingUser() {
        // Создаём пользователя
        UserSteps.createUser(email, password, name);
        // Выполняем логин
        Response response = UserSteps.login(email, password);
        accessToken = response.jsonPath().getString("accessToken");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", equalTo(accessToken));
    }

    @Test
    @Step("Логин с неверным логином и паролем")
    public void loginWithInvalidCredentials() {
        Response response = UserSteps.login("invalid@example.com", "wrongpassword");

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}