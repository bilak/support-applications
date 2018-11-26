package com.github.bilak.zipcodes.slovak.util;

import static com.github.bilak.zipcodes.slovak.util.ExcelReader.asString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import lombok.Builder;
import lombok.Data;

/**
 * Test suite for {@link ExcelReader}.
 *
 * @author Lukáš Vasek
 */
public class ExcelReaderTest {

    final ClassPathResource excelFile = new ClassPathResource("exceltest.xlsx");

    final Function<Row, Person> rowPersonFunction = row -> {
        int i = 0;
        return Person.builder()
                .name(asString(row.getCell(i++)))
                .surname(asString(row.getCell(i++)))
                .age(asString(row.getCell(i)))
                .build();
    };

    @Test
    public void testHeaderIsIgnored() throws IOException {
        final Stream<Person> personStream = ExcelReader.load(rowPersonFunction, excelFile.getFile());
        assertThat("header should not be present", personStream.count(), is(3L));
    }

    @Test
    public void testHeaderIsNotIgnored() throws IOException {
        final Stream<Person> personStream = ExcelReader.load(rowPersonFunction, false, 0, excelFile.getFile());
        assertThat("header should be present", personStream.count(), is(4L));
    }

    @Data
    @Builder
    static class Person {

        private String name;
        private String surname;
        private String age;
    }
}