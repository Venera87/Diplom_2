package utils;

import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.UUID;

public class RandomDataGenerator {


    private static final Faker faker = new Faker(new Locale("ru"));

    public static String generateEmail() {
        String name = faker.name().firstName().toLowerCase();
        return name + "." + UUID.randomUUID().toString().substring(0, 8) + "@yandex.ru";
    }

    public static String generatePassword() {
        // Генерируем надёжный пароль: 10+ символов, буквы + цифры
        return faker.internet().password(10, 15);
    }

    public static String generateName() {
        return faker.name().firstName();
    }
}