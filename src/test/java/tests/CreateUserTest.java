package tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;
import utils.RandomDataGenerator;

import static org.hamcrest.Matchers.*;

public class CreateUserTest {
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
    @Step("Создание уникального пользователя")
    public void createUserSuccessfully() {
        Response response = UserSteps.createUser(email, password, name);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @Step("Создание уже существующего пользователя")
    public void createUserAlreadyExists() {
        // Создаём пользователя первый раз
        UserSteps.createUser(email, password, name);
        // Пытаемся создать снова
        Response response = UserSteps.createUser(email, password, name);
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Step("Создание пользователя без обязательного поля (email)")
    public void createUserWithoutEmail() {
        Response response = UserSteps.createUser("", password, name);
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}