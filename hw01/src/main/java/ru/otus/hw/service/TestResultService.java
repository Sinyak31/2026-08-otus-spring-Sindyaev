package ru.otus.hw.service;

import ru.otus.hw.dao.dto.StudentDto;
import ru.otus.hw.dao.dto.TestingResultDto;
import ru.otus.hw.domain.Answer;

import java.util.List;

public interface TestResultService {

    TestingResultDto calculateResult(StudentDto studentDto , List<Answer> student);

}
