package ru.otus.hw.service;

import ru.otus.hw.dao.dto.StudentDto;
import ru.otus.hw.dao.dto.TestingResultDto;
import ru.otus.hw.domain.Answer;

import java.util.List;
import java.util.Objects;

public class TestResultServiceImpl implements TestResultService {

    @Override
    public TestingResultDto calculateResult(StudentDto student, List<Answer> studentAnswers) {
        if (Objects.isNull(student) || Objects.isNull(studentAnswers)) {
            throw new RuntimeException("Произошла ошибка во время тестирования повторите попытку позже");
        }

        final var correctAnswers = studentAnswers.stream().filter(Answer::isCorrect).toList();
        return new TestingResultDto(student, correctAnswers.size());
    }

}