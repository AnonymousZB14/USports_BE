package com.anonymous.usports.global.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomCreator {

    private static String[] PASSWORD_UPPER_CHARACTERS = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"
            , "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private static String[] PASSWORD_LOWER_CHARACTERS = new String[]{
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m"
            , "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };

    private static String[] PASSWORD_NUMBERS = new String[]{
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    private static String[] PASSWORD_SPECIAL_SYMBOLS = new String[] {
            "@", "$", "!", "%", "*", "?", "&"
    };

    public static int createNumber() {
        int number = 0;
        number = (int)(Math.random() * (9000)) + 100000;
        return number;
    }

    public static String createPassword(){
        Random random = new Random();
        String newTempPassword = "";

        for (int i = 0; i < 4; i ++) {
            String upper = PASSWORD_UPPER_CHARACTERS[random.nextInt(26)];
            newTempPassword += upper;
        }

        for (int i = 0; i < 4; i ++) {
            String lower = PASSWORD_LOWER_CHARACTERS[random.nextInt(26)];
            newTempPassword += lower;
        }

        for (int i = 0; i < 4; i ++) {
            String num = PASSWORD_NUMBERS[random.nextInt(10)];
            newTempPassword += num;
        }

        for (int i = 0; i < 2; i ++) {
            String symbol = PASSWORD_SPECIAL_SYMBOLS[random.nextInt(6)];
            newTempPassword += symbol;
        }

        String[] splitPass = newTempPassword.split("");
        List<String> passList = Arrays.asList(splitPass);

        Collections.shuffle(passList);

        return String.join("", passList);
    }
}
