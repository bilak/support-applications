package com.github.bilak.zipcodes.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel reader which transforms sheet to specified POJO.
 */
public class ExcelReader {

    private static final String UNKNOWN_CELL_TYPE = "Unknown cell type: [%s]";

    private ExcelReader() {
        // utility class
    }

    /**
     * Converts {@code file} to a specified {@code <T>} with mapper.
     *
     * @param <T> the type of the converted row
     * @param mapper a function which maps a Row into a Pojo
     * @param hasHeader true - the header of the excel sheet is going to be ignored.
     * @param tab the number of the tab in the excel sheet
     * @param file file to be converted
     * @return a Stream of mapped POJOs.
     */
    public static <T> Stream<T> load(final Function<Row, T> mapper, final boolean hasHeader, final int tab, final File file) {
        int skipCount = 0;
        if (hasHeader) {
            skipCount = 1;
        }
        try (final InputStream inp = new BufferedInputStream(new FileInputStream(file));
             final XSSFWorkbook wb = new XSSFWorkbook(inp)) {

            final XSSFSheet sheet = wb.getSheetAt(tab);
            final Stream<Row> stream = StreamSupport.stream(sheet.spliterator(), false);
            return stream.skip(skipCount).map(mapper);
        } catch (final IOException ex) {
            throw new IllegalStateException(String.format("Problems processing file: [%s]", file.getName()), ex);
        }
    }

    public static <T> Stream<T> load(final Function<Row, T> mapper, final int tab, final File file) {
        return load(mapper, true, tab, file);
    }

    public static <T> Stream<T> load(final Function<Row, T> mapper, final File file) {
        return load(mapper, true, 0, file);
    }

    /**
     * Assumes the Cell content is numeric and converts that into a Java double
     *
     * @param cell HSF specific data container
     * @return Content converted into double or 0 if blank
     */
    public static double asDouble(final Cell cell) {
        if (cell == null) {
            return 0;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case BLANK:
                return 0;
            case FORMULA:
                return cell.getNumericCellValue();
            default:
                throw new IllegalArgumentException(String.format(UNKNOWN_CELL_TYPE, cell.getCellType()));
        }
    }

    /**
     * Assumes the Cell content is text and converts it into a java.lang.String
     *
     * @param cell HSF specific datacontainer
     * @return Content converted into String or "" if blank
     */
    public static String asString(final Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case BLANK:
                return "";
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (final IllegalStateException ise) {
                    return String.valueOf(Math.round(cell.getNumericCellValue()));
                }
            default:
                throw new IllegalArgumentException(String.format(UNKNOWN_CELL_TYPE, cell.getCellType()));
        }
    }

    /**
     * Assumes the Cell content is numeric and converts it into a java.lang.Long
     *
     * @param cell HSF specific datacontainer
     * @return Content converted into Long or 0 if blank
     */
    public static long asLong(final Cell cell) {
        if (cell == null) {
            return 0L;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return Math.round(cell.getNumericCellValue());
            case BLANK:
                return 0L;
            case FORMULA:
                return Math.round(cell.getNumericCellValue());
            default:
                throw new IllegalArgumentException(String.format(UNKNOWN_CELL_TYPE, cell.getCellType()));
        }
    }

}
