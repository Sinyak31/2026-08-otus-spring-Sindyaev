package ru.otus.hw.service;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.dao.dto.StudentDto;
import ru.otus.hw.dao.dto.TestingResultDto;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TestServiceImplTest {

    private final TestServiceImplTest.DummyIOService ioService = new TestServiceImplTest.DummyIOService();
    private final TestServiceImplTest.DummyQuestionDao questionDao = new TestServiceImplTest.DummyQuestionDao();
    private final TestServiceImplTest.DummyStudentService studentService = new TestServiceImplTest.DummyStudentService();
    private final TestServiceImplTest.DummyTestResultService testResultService = new TestServiceImplTest.DummyTestResultService();

    private TestServiceImpl testService;

    private static final StudentDto STUDENT = new StudentDto("Ivan", "Petrov");

    private static final Question QUESTION_1 = new Question(
            "What is 2 + 2?",
            List.of(
                    new Answer("3", false),
                    new Answer("4", true),
                    new Answer("5", false)
            )
    );

    private static final Question QUESTION_2 = new Question(
            "What is the capital of Russia?",
            List.of(
                    new Answer("Moscow", true),
                    new Answer("Saint Petersburg", false),
                    new Answer("Kazan", false)
            )
    );

    private final List<Question> questions = List.of(QUESTION_1, QUESTION_2);

    @BeforeEach
    void setUp() {
        testService = new TestServiceImpl(
                ioService, questionDao, studentService, testResultService);
    }

    @Test
    void shouldExecuteTestSuccessfully() {
        // given
        studentService.setStudent(STUDENT);
        questionDao.setQuestions(questions);

        final TestingResultDto expectedResult = new TestingResultDto(STUDENT, 100);
        testResultService.setExpectedResult(expectedResult);

        String input = "2\n1\n";
        setSystemIn(input);

        // when
        testService.executeTest();

        // then
        assertThat(studentService.getEnteredStudent()).isSameAs(STUDENT);
        assertThat(testResultService.getLastStudent()).isSameAs(STUDENT);
        assertThat(testResultService.getLastAnswers()).hasSize(2);
    }

    @Test
    void shouldPrintWelcomeMessageBeforeTest() {
        // given
        studentService.setStudent(STUDENT);
        questionDao.setQuestions(questions);
        testResultService.setExpectedResult(new TestingResultDto(STUDENT, 100));
        setSystemIn("2\n1\n");

        // when
        testService.executeTest();

        // then
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("Please answer the questions below"));
    }

    @Test
    void shouldPrintAllQuestionTexts() {
        // given
        studentService.setStudent(STUDENT);
        questionDao.setQuestions(questions);
        testResultService.setExpectedResult(new TestingResultDto(STUDENT, 100));
        setSystemIn("2\n1\n");

        // when
        testService.executeTest();

        // then
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("1) " + QUESTION_1.text()));
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("2) " + QUESTION_2.text()));
    }

    @Test
    void shouldPrintAllAnswerOptionsForEachQuestion() {
        // given
        studentService.setStudent(STUDENT);
        questionDao.setQuestions(questions);
        testResultService.setExpectedResult(new TestingResultDto(STUDENT, 100));
        setSystemIn("2\n1\n");

        // when
        testService.executeTest();

        // then
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("1. " + QUESTION_1.answers().get(0).text()));
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("2. " + QUESTION_1.answers().get(1).text()));
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("3. " + QUESTION_1.answers().get(2).text()));
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("1. " + QUESTION_2.answers().get(0).text()));
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("2. " + QUESTION_2.answers().get(1).text()));
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("3. " + QUESTION_2.answers().get(2).text()));
    }

    @Test
    void shouldPassCorrectAnswersToTestResultService() {
        // given
        studentService.setStudent(STUDENT);
        questionDao.setQuestions(questions);
        testResultService.setExpectedResult(new TestingResultDto(STUDENT, 100));
        // Input: answer index 2 (correct for Q1), answer index 1 (correct for Q2)
        setSystemIn("2\n1\n");

        // when
        testService.executeTest();

        // then
        List<Answer> answers = testResultService.getLastAnswers();
        assertThat(answers).hasSize(2);
        assertThat(answers.get(0)).isEqualTo(QUESTION_1.answers().get(1));
        assertThat(answers.get(1)).isEqualTo(QUESTION_2.answers().get(0));
    }

    @Test
    @DisplayName("должен вывести результат тестирования с правильными данными студента")
    void shouldPrintTestResultWithCorrectStudentData() {
        // given
        studentService.setStudent(STUDENT);
        questionDao.setQuestions(questions);
        testResultService.setExpectedResult(new TestingResultDto(STUDENT, 80));
        setSystemIn("2\n1\n");

        // when
        testService.executeTest();

        // then
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("Petrov") && msg.contains("80 баллов"));
    }

    @Test
    @DisplayName("должен обработать тест с одним вопросом")
    void shouldHandleTestWithSingleQuestion() {
        // given
        studentService.setStudent(STUDENT);
        questionDao.setQuestions(List.of(QUESTION_1));
        testResultService.setExpectedResult(new TestingResultDto(STUDENT, 50));
        setSystemIn("2\n");

        // when
        testService.executeTest();

        // then
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("1) " + QUESTION_1.text()));
        assertThat(testResultService.getLastAnswers()).hasSize(1);
    }

    @Test
    @DisplayName("должен обработать тест без вопросов")
    void shouldHandleTestWithNoQuestions() {
        // given
        studentService.setStudent(STUDENT);
        questionDao.setQuestions(List.of());
        testResultService.setExpectedResult(new TestingResultDto(STUDENT, 0));
        setSystemIn("");

        // when
        testService.executeTest();

        // then
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("Please answer the questions below"));
        assertThat(testResultService.getLastStudent()).isSameAs(STUDENT);
    }

    @Test
    @DisplayName("должен вывести пустую строку после каждого ответа")
    void shouldPrintEmptyLineAfterEachAnswer() {
        // given
        studentService.setStudent(STUDENT);
        questionDao.setQuestions(questions);
        testResultService.setExpectedResult(new TestingResultDto(STUDENT, 100));
        setSystemIn("2\n1\n");

        // when
        testService.executeTest();

        // then
        long emptyLinesCount = ioService.getMessages().stream()
                .filter(String::isEmpty)
                .count();
        assertThat(emptyLinesCount).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldHandleQuestionsWithDifferentAnswerCounts() {
        // given
        final Question questionWithTwoAnswers = new Question(
                "True or False?",
                List.of(
                        new Answer("True", true),
                        new Answer("False", false)
                )
        );
        studentService.setStudent(STUDENT);
        questionDao.setQuestions(List.of(QUESTION_1, questionWithTwoAnswers));
        testResultService.setExpectedResult(new TestingResultDto(STUDENT, 75));
        setSystemIn("2\n1\n");

        // when
        testService.executeTest();

        // then
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("1. " + QUESTION_1.answers().get(0).text()));
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("2. " + QUESTION_1.answers().get(1).text()));
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("3. " + QUESTION_1.answers().get(2).text()));
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("1. " + questionWithTwoAnswers.answers().get(0).text()));
        assertThat(ioService.getMessages()).anyMatch(msg -> msg.contains("2. " + questionWithTwoAnswers.answers().get(1).text()));
    }


    private void setSystemIn(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    @Getter
    static class DummyIOService implements IOService {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void printLine(String s) {
            messages.add(s);
        }

        @Override
        public void printFormattedLine(String s, Object... args) {
            messages.add(String.format(s, args));
        }

    }

    @Setter
    static class DummyStudentService implements StudentService {
        private StudentDto student;

        public StudentDto getEnteredStudent() {
            return student;
        }

        @Override
        public StudentDto inputDataStudent() {
            return student;
        }
    }

    @Setter
    static class DummyQuestionDao implements QuestionDao {

        private List<Question> questions;

        @Override
        public List<Question> findAll() {
            return questions;
        }
    }

    @Setter
    @Getter
    static class DummyTestResultService implements TestResultService {

        private TestingResultDto expectedResult;
        private StudentDto lastStudent;
        private List<Answer> lastAnswers;

        @Override
        public TestingResultDto calculateResult(StudentDto studentDto, List<Answer> studentAnswers) {
            this.lastStudent = studentDto;
            this.lastAnswers = studentAnswers;
            return expectedResult;
        }
    }

}
