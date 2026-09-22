package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.exceptions.QuestionReadException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CsvQuestionDaoTest {

    private static final String TEST_FILE = "questions-test.csv";

    private TestFileNameProvider fileNameProvider;

    private CsvQuestionDao questionDao;

    @BeforeEach
    void setUp() {
        fileNameProvider = mock(TestFileNameProvider.class);
        questionDao = new CsvQuestionDao(fileNameProvider);
    }

    @Test
    @DisplayName("читает вопросы из CSV файла")
    void shouldReadQuestionsFromFile() {
        //GIVEN
        when(fileNameProvider.getTestFileName()).thenReturn(TEST_FILE);

        //WHEN
       final var questions = questionDao.findAll();

        //THEN
        assertThat(questions).hasSize(2);

        final var first = questions.get(0);
        assertThat(first.text()).isEqualTo("Что такое Spring?");
        assertThat(first.answers()).hasSize(3);
        assertThat(first.answers().get(0).text()).isEqualTo("контейнер");
        assertThat(first.answers().get(0).isCorrect()).isTrue();
        assertThat(first.answers().get(1).isCorrect()).isFalse();

        final var second = questions.get(1);
        assertThat(second.text()).isEqualTo("Что такое бин?");
    }

    @Test
    @DisplayName("бросает QuestionReadException, если файл не найден")
    void shouldThrowWhenFileNotFound() {
        //GIVEN
        when(fileNameProvider.getTestFileName()).thenReturn("no-such-file.csv");

        //WHEN-THEN
        assertThatThrownBy(() -> questionDao.findAll())
                .isInstanceOf(QuestionReadException.class);
    }

}
