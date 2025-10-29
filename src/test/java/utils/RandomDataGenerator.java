package utils;

import java.util.UUID;

public class RandomDataGenerator {

    public static String generateEmail() {
        return "user+" + System.currentTimeMillis() + "@example.com";
    }

    public static String generatePassword() {
        return "Pass" + System.currentTimeMillis();
    }

    public static String generateName() {
        return "User" + System.currentTimeMillis();
    }
}