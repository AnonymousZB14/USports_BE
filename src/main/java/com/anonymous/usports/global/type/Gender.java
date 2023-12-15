package com.anonymous.usports.global.type;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum Gender {

    MALE, FEMALE, BOTH;

    @JsonCreator
    public static Gender parsing(String inputValue) {
        return Stream.of(Gender.values())
                .filter(gender -> gender.toString().equals(inputValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
