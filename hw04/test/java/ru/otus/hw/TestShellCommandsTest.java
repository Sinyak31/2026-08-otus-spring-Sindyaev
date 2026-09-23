package ru.otus.hw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.shell.Shell;
import ru.otus.hw.service.TestRunnerService;
import ru.otus.hw.shell.ApplicationCommands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Тесты shell-команд")
@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
class TestShellCommandsTest {

    @Autowired
    private ApplicationCommands applicationCommands;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private Shell shell;

    @MockBean
    private TestRunnerService testRunnerService;

    @Test
    @DisplayName("команда help возвращает локализованную строку")
    void helpShouldReturnLocalizedMessage() {
        String result = applicationCommands.help();

        assertThat(result).isNotBlank();
        assertThat(result).contains("help");
        assertThat(result).contains("test");
        assertThat(result).contains("exit");
    }

    @Test
    @DisplayName("команда test вызывает TestRunnerService.run()")
    void testCommandShouldCallTestRunnerServiceRun() {
        applicationCommands.runTest();

        verify(testRunnerService, times(1)).run();
    }

    @Test
    @DisplayName("команда test возвращает локализованное сообщение о завершении")
    void testCommandShouldReturnLocalizedCompletionMessage() {
        String result = applicationCommands.runTest();

        assertThat(result).isNotBlank();
    }

    @Test
    @DisplayName("сообщения help локализуются корректно для английского языка")
    void helpShouldBeLocalizedForEnglish() {
        String result = applicationCommands.help();

        assertThat(result).contains("help");
        assertThat(result).contains("test");
    }
}
