package ru.otus.hw.dao.dto;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class StudentDto {

    private String name;

    private String lastName;

    public StudentDto(String name, String surname) {
        this.name = name;
        this.lastName = surname;
    }

}
