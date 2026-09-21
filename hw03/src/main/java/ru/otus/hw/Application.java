package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.hw.service.TestRunnerService;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        final var context = SpringApplication.run(Application.class, args);
        final var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();
    }

}