package ru.otus.hw.dao;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CsvQuestionDao implements QuestionDao {

    private static final char SEPARATOR = ';';

    private static final char QUOTE_CHAR = '"';

    private final TestFileNameProvider fileNameProvider;


    @Override
    public List<Question> findAll() {
        try {
            return readQuestions();
        } catch (Exception e) {
            throw new QuestionReadException("Failed to read questions", e);
        }
    }

    private List<Question> readQuestions() throws Exception {

        var resource = new ClassPathResource(fileNameProvider.getTestFileName());

        try (var inputStream = resource.getInputStream();
             var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            var parser = new CSVParserBuilder()
                    .withSeparator(SEPARATOR)
                    .withQuoteChar(QUOTE_CHAR)
                    .build();
            var csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(parser)
                    .withSkipLines(1)
                    .build();
            var csvToBean = new CsvToBeanBuilder<QuestionDto>(csvReader)
                    .withType(QuestionDto.class)
                    .build();
            var dtos = csvToBean.parse();
            var questions = new ArrayList<Question>();
            for (var dto : dtos) {
                questions.add(dto.toDomainObject());
            }
            return questions;
        }
    }

}