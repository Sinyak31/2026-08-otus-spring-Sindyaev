package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    private final StudentService studentService;

    private final TestResultService testResultService;

    @Override
    public void executeTest() {
        final var student = studentService.inputDataStudent();

        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        final var questions = questionDao.findAll();
        final var answersCurrentStudent = testingProcess(questions);
        final var result = testResultService.calculateResult(student, answersCurrentStudent);

        ioService.printFormattedLine("Результат тестирования студента: %s %s %n %d баллов",
                result.getStudentDto().getLastName(), result.getStudentDto().getLastName(), result.getNumberPoints());
    }

    private List<Answer> testingProcess(List<Question> questions) {
        final var scanner = new Scanner(System.in);
        List<Answer> answersCurrentStudent = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            ioService.printFormattedLine("%d%s %s%n", i + 1, ")", questions.get(i).text());
            final var answers = questions.get(i).answers();
            for (int j = 0; j < answers.size(); j++) {
                final var answer = answers.get(j);
                ioService.printFormattedLine("%d. %s%n", j + 1, answer.text());
            }
            final var answerNumber = scanner.nextInt();
            answersCurrentStudent.add(questions.get(i).answers().get(answerNumber - 1));
            ioService.printLine("");
        }
        return answersCurrentStudent;
    }

}