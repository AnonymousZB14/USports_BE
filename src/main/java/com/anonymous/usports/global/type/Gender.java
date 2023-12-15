package com.anonymous.usports.global.type;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

    MALE("남성"),
    FEMALE("여성"),
    BOTH("성별 무관");

    private final String description;
    @JsonCreator
    public static Gender parsing(String inputValue) {
        return Stream.of(Gender.values())
                .filter(gender -> gender.toString().equals(inputValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
