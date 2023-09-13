package com.openclassrooms.payMyBuddy.utils;

import java.util.UUID;

public class RandomAccountNumber {

    public static String createAccountNumber() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

}
