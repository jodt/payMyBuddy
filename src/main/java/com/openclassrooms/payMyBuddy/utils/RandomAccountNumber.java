package com.openclassrooms.payMyBuddy.utils;

import java.util.UUID;

public class RandomAccountNumber {

    /**
     * This method generate a unique id as a string
     *
     * @return a unique id as a string
     */
    public static String createAccountNumber() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

}
