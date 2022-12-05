package dev.radon.naruto_universe.util;

import java.security.SecureRandom;

public class HelperMethods {
    private static final SecureRandom random = new SecureRandom();

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass){
        int x = random.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
    }
}
