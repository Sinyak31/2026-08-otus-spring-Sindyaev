package ru.otus.hw.commands; // или ваш пакет

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;

@ShellComponent
public class DatabaseCheckCommands {

    private final JdbcTemplate jdbcTemplate;

    // Spring сам передаст сюда JdbcTemplate, так как он у вас настроен
    public DatabaseCheckCommands(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @ShellMethod("Показать все таблицы в базе данных")
    public void showTables() {
        // Запрос, который работает в H2 для получения списка таблиц
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'";

        List<String> tables = jdbcTemplate.queryForList(sql, String.class);

        if (tables.isEmpty()) {
            System.out.println("Таблиц не найдено.");
        } else {
            System.out.println("Найдены таблицы:");
            tables.forEach(System.out::println);
        }
    }

    @ShellMethod("Показать количество записей во всех таблицах")
    public void showCounts() {
        List<String> tables = List.of("AUTHORS", "GENRES", "BOOKS", "BOOKS_GENRES");
        for (String table : tables) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM " + table, Integer.class);
            System.out.printf("%-15s : %d%n", table, count);
        }
    }
}