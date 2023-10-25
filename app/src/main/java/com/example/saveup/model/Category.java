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
}