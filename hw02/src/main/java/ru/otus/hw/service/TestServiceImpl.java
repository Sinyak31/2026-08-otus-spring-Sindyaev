package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        final var questions = questionDao.findAll();
        final var testResult = new TestResult(student);
        testingProcess(testResult, questions);

        return testResult;
    }

    private void testingProcess(TestResult testResult, List<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            ioService.printFormattedLine("%d%s %s%n", i + 1, ")", questions.get(i).text());
            final var answers = questions.get(i).answers();
            for (int j = 0; j < answers.size(); j++) {
                final var answer = answers.get(j);
                ioService.printFormattedLine("%d. %s%n", j + 1, answer.text());
            }
            final var answerNumber = ioService.readInt();
            if (answers.get(answerNumber - 1).isCorrect()) {
                testResult.applyAnswer(questions.get(i), true);
            }
        }
    }

}