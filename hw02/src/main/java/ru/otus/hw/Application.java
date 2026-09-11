package ru.otus.hw;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.otus.hw.config.ApplicationConfig;
import ru.otus.hw.service.TestRunnerService;

public class Application {

    public static void main(String[] args) {
        final var context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        final var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();
    }

}