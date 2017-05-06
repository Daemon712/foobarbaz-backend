package ru.foobarbaz;

public class NumberUtils {
    /**
     * safe unboxing
     */
    public static int intOrDefault(Integer value, int defaultValue){
        return value == null ? defaultValue : value;
    }
}
