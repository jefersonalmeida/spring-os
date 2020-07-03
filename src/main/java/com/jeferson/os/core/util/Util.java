package com.jeferson.os.core.util;

// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Random;

public class Util {

    public static final String UUID_PATTERN = "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}$";

    public static String randomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 72;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String toCamelCase(String variableName) {
        if(variableName.isEmpty()) return null;

        while (variableName.contains("_")) {
            variableName = variableName.toLowerCase().replaceFirst("_[a-z]",
                    String.valueOf(
                            Character.toUpperCase(
                                    variableName.charAt(variableName.indexOf("_") + 1)
                            )
                    )
            );
        }
        return variableName;
    }

    public static String encoder(String password) {
        if (password.isEmpty()) return null;

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
