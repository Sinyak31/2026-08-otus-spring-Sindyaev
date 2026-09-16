package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.LocalizedIOServiceImpl;
import ru.otus.hw.service.LocalizedMessagesService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис тестирования")
class TestServiceImplTest {

    private static final Student STUDENT = new Student("Ivan", "Petrov");

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

    private DummyIOService dummyIoService;
    private LocalizedIOServiceImpl localizedIoService;
    private DummyQuestionDao questionDao;
    private TestServiceImpl testService;
    private LocalizedMessagesService localizedMessagesService;

    @BeforeEach
    void setUp() {
        localizedMessagesService = new DummyLocalizedMessagesService();
        dummyIoService = new DummyIOService();
        localizedIoService = new LocalizedIOServiceImpl(localizedMessagesService, dummyIoService);
        questionDao = new DummyQuestionDao();
        testService = new TestServiceImpl(localizedIoService, questionDao);
    }

    @Test
    @DisplayName("возвращает результат с правильным студентом")
    void shouldReturnResultWithCorrectStudent() {
        questionDao.setQuestions(questions);
        dummyIoService.setAnswers(2, 1);

        TestResult result = testService.executeTestFor(STUDENT);

        assertThat(result.getStudent()).isSameAs(STUDENT);
    }

    @Test
    @DisplayName("печатает приветственное сообщение перед тестом")
    void shouldPrintWelcomeMessageBeforeTest() {
        questionDao.setQuestions(questions);
        dummyIoService.setAnswers(2, 1);

        testService.executeTestFor(STUDENT);

        assertThat(dummyIoService.getMessages())
                .anyMatch(msg -> msg.contains("Please answer the questions below"));
    }

    @Test
    @DisplayName("печатает текст всех вопросов")
    void shouldPrintAllQuestionTexts() {
        questionDao.setQuestions(questions);
        dummyIoService.setAnswers(2, 1);

        testService.executeTestFor(STUDENT);

        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("1) " + QUESTION_1.text()));
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("2) " + QUESTION_2.text()));
    }

    @Test
    @DisplayName("печатает варианты ответов для каждого вопроса")
    void shouldPrintAllAnswerOptionsForEachQuestion() {
        questionDao.setQuestions(questions);
        dummyIoService.setAnswers(2, 1);

        testService.executeTestFor(STUDENT);

        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("1. " + QUESTION_1.answers().get(0).text()));
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("2. " + QUESTION_1.answers().get(1).text()));
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("3. " + QUESTION_1.answers().get(2).text()));
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("1. " + QUESTION_2.answers().get(0).text()));
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("2. " + QUESTION_2.answers().get(1).text()));
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("3. " + QUESTION_2.answers().get(2).text()));
    }

    @Test
    @DisplayName("считает правильные ответы, если студент ответил верно")
    void shouldCountCorrectAnswers() {
        questionDao.setQuestions(questions);
        dummyIoService.setAnswers(2, 1);

        TestResult result = testService.executeTestFor(STUDENT);

        assertThat(result.getRightAnswersCount()).isEqualTo(2);
        assertThat(result.getAnsweredQuestions()).hasSize(2);
        assertThat(result.getAnsweredQuestions().get(0)).isEqualTo(QUESTION_1);
        assertThat(result.getAnsweredQuestions().get(1)).isEqualTo(QUESTION_2);
    }

    @Test
    @DisplayName("обрабатывает тест с одним вопросом")
    void shouldHandleTestWithSingleQuestion() {
        questionDao.setQuestions(List.of(QUESTION_1));
        dummyIoService.setAnswers(2);

        TestResult result = testService.executeTestFor(STUDENT);

        assertThat(result.getAnsweredQuestions()).hasSize(1);
        assertThat(result.getRightAnswersCount()).isEqualTo(1);
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("1) " + QUESTION_1.text()));
    }

    @Test
    @DisplayName("обрабатывает тест без вопросов")
    void shouldHandleTestWithNoQuestions() {
        questionDao.setQuestions(List.of());
        dummyIoService.setAnswers();

        TestResult result = testService.executeTestFor(STUDENT);

        assertThat(result.getAnsweredQuestions()).isEmpty();
        assertThat(result.getRightAnswersCount()).isZero();
        assertThat(dummyIoService.getMessages())
                .anyMatch(msg -> msg.contains("Please answer the questions below"));
    }

    @Test
    @DisplayName("печатает пустую строку в начале теста")
    void shouldPrintEmptyLineAtStart() {
        questionDao.setQuestions(questions);
        dummyIoService.setAnswers(2, 1);

        testService.executeTestFor(STUDENT);

        assertThat(dummyIoService.getMessages()).contains("");
    }

    @Test
    @DisplayName("обрабатывает вопросы с разным числом ответов")
    void shouldHandleQuestionsWithDifferentAnswerCounts() {
        final Question questionWithTwoAnswers = new Question(
                "True or False?",
                List.of(
                        new Answer("True", true),
                        new Answer("False", false)
                )
        );
        questionDao.setQuestions(List.of(QUESTION_1, questionWithTwoAnswers));
        dummyIoService.setAnswers(2, 1);

        TestResult result = testService.executeTestFor(STUDENT);

        assertThat(result.getAnsweredQuestions()).hasSize(2);
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("1. " + QUESTION_1.answers().get(0).text()));
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("2. " + QUESTION_1.answers().get(1).text()));
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("3. " + QUESTION_1.answers().get(2).text()));
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("1. " + questionWithTwoAnswers.answers().get(0).text()));
        assertThat(dummyIoService.getMessages()).anyMatch(msg -> msg.contains("2. " + questionWithTwoAnswers.answers().get(1).text()));
    }

    static class DummyLocalizedMessagesService implements LocalizedMessagesService {

        @Override
        public String getMessage(String code, Object... args) {
            return String.format(code, args);
        }
    }

    static class DummyIOService implements IOService {

        private final List<String> messages = new ArrayList<>();

        private final Deque<Integer> answers = new ArrayDeque<>();

        void setAnswers(int... values) {
            answers.clear();
            for (int v : values) {
                answers.add(v);
            }
        }

        List<String> getMessages() {
            return messages;
        }

        @Override
        public void printLine(String s) {
            messages.add(s);
        }

        @Override
        public void printFormattedLine(String s, Object... args) {
            messages.add(String.format(s, args));
        }

        @Override
        public int readInt() {
            return answers.removeFirst();
        }

        @Override
        public String readString() {
            throw new UnsupportedOperationException("readString not used in TestServiceImpl");
        }

        @Override
        public String readStringWithPrompt(String prompt) {
            throw new UnsupportedOperationException("readStringWithPrompt not used in TestServiceImpl");
        }

        @Override
        public int readIntForRange(int min, int max, String errorMessage) {
            throw new UnsupportedOperationException("readIntForRange not used in TestServiceImpl");
        }

        @Override
        public int readIntForRangeWithPrompt(int min, int max, String prompt, String errorMessage) {
            throw new UnsupportedOperationException("readIntForRangeWithPrompt not used in TestServiceImpl");
        }
    }

    static class DummyQuestionDao implements QuestionDao {

        private List<Question> questions = List.of();

        void setQuestions(List<Question> questions) {
            this.questions = questions;
        }

        @Override
        public List<Question> findAll() {
            return questions;
        }
    }
}