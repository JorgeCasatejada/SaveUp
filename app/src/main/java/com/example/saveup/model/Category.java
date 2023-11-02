package com.example.saveup.model;

public enum Category {
    OCIO, VIVIENDA, TRANSPORTE, EDUCACION, TECNOLOGIA, REGALOS, INVERSIONES, ALIMENTACION, ROPA, OTROS;

    public static String[] enumToStringArray() {
        Category[] enumValues = values();
        String[] stringArray = new String[enumValues.length];

        for (int i = 0; i < enumValues.length; i++) {
            stringArray[i] = enumValues[i].name();
        }

        return stringArray;
    }

    public static int getIndex(Category category) {
        Category[] enumValues = values();
        for (int i = 0; i < enumValues.length; i++) {
            if (category.name().equals(enumValues[i].name()))
                return i;
        }
        return 0;
    }
}