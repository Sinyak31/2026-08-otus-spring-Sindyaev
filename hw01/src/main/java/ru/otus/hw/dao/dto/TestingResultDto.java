package ru.otus.hw.dao.dto;

import lombok.Data;

@Data
public class TestingResultDto {

    private StudentDto studentDto;

    private Integer numberPoints;

    public TestingResultDto(StudentDto studentDto, Integer numberPoints) {
        this.studentDto = studentDto;

        this.numberPoints = numberPoints;
    }

}
