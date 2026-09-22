package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

import org.springframework.context.i18n.LocaleContextHolder;

@ShellComponent
@RequiredArgsConstructor
public class ApplicationCommands {

    private final TestRunnerService testRunnerService;

    private final MessageSource messageSource;

    @ShellMethod(key = {"help", "h"})
    public String help() {
        return messageSource.getMessage("shell.help", null, LocaleContextHolder.getLocale());
    }

    @ShellMethod(key = {"test", "t"})
    public String runTest() {
        testRunnerService.run();
        return messageSource.getMessage("shell.test.completed", null, LocaleContextHolder.getLocale());
    }
}
