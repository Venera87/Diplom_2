package tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.OrderSteps;
import steps.UserSteps;
import utils.RandomDataGenerator;

import static org.hamcrest.Matchers.*;

public class OrderTest {

    private String email;
    private String password;
    private String name;
    private String accessToken;

    // Валидные ID ингредиентов из документации API
    private static final String[] VALID_INGREDIENTS = {
            "61c0c5a71d1f82001bdaaa6d", // булка
            "61c0c5a71d1f82001bdaaa70"  // начинка
    };

    @Before
    public void setUp() {
        email = RandomDataGenerator.generateEmail();
        password = RandomDataGenerator.generatePassword();
        name = RandomDataGenerator.generateName();
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
    @Step("Создание заказа с авторизацией и валидными ингредиентами")
    public void createOrderAuthorizedWithIngredients() {
        Response response = OrderSteps.createOrder(accessToken, VALID_INGREDIENTS);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Step("Создание заказа без авторизации (разрешено API)")
    public void createOrderWithoutAuthorization() {
        Response response = OrderSteps.createOrder("", VALID_INGREDIENTS);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Step("Создание заказа с авторизацией, но без ингредиентов")
    public void createOrderWithNoIngredients() {
        Response response = OrderSteps.createOrder(accessToken, new String[]{});

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Step("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientHash() {
        String[] invalidIngredients = {"invalid_id_123"};
        Response response = OrderSteps.createOrder(accessToken, invalidIngredients);

        // API может вернуть 500 или 400 — допускаем оба варианта
        response.then()
                .statusCode(anyOf(equalTo(500), equalTo(400)));
    }

    @Test
    @Step("Получение заказов авторизованным пользователем")
    public void getUserOrdersAuthorized() {
        // Создаём заказ, чтобы список не был пустым
        OrderSteps.createOrder(accessToken, VALID_INGREDIENTS);

        Response response = OrderSteps.getUserOrders(accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", not(empty()));
    }

    @Test
    @Step("Получение заказов неавторизованным пользователем")
    public void getUserOrdersUnauthorized() {
        Response response = OrderSteps.getUserOrders("");

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}