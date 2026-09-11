package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.dto.StudentDto;

import java.util.Scanner;

@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final IOService ioService;

    @Override
    public StudentDto inputDataStudent() {
        ioService.printFormattedLine("%s", "Введите ваше имя");
        final var scanner = new Scanner(System.in);
        final var name = scanner.nextLine();
        ioService.printFormattedLine("%s", "Введите вашу фамилию");
        final var surname = scanner.nextLine();
        return new StudentDto(name, surname);
    }

}