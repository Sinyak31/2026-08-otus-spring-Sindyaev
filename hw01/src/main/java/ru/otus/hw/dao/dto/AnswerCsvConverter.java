package ru.otus.hw.dao.dto;

import com.opencsv.bean.AbstractCsvConverter;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import ru.otus.hw.domain.Answer;

public class AnswerCsvConverter extends AbstractCsvConverter {

    @Override
    public Answer convertToRead(String value) throws CsvDataTypeMismatchException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String[] parts = value.split("%");

        if (parts.length != 2) {
            throw new CsvDataTypeMismatchException(
                    "Invalid answer format: '" + value +
                            "'. Expected format: 'answerText%true' or 'answerText%false'"
            );
        }

        String answerText = parts[0].trim();
        boolean isCorrect = Boolean.parseBoolean(parts[1].trim());

        return new Answer(answerText, isCorrect);
    }
}
