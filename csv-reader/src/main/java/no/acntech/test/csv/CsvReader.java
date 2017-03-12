package no.acntech.test.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvReader {

    public static List<File> findCsvFiles(File csvRootDir) {
        if (csvRootDir == null) {
            throw new IllegalArgumentException("CSV files root directory is null");
        }
        List<File> fileList = new ArrayList<>();
        findCsvFiles(csvRootDir.toPath(), fileList);
        return fileList;
    }

    private static void findCsvFiles(Path rootDir, List<File> fileList) {
        if (rootDir == null) {
            throw new IllegalArgumentException("CSV files root directory is null");
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDir)) {
            stream.forEach(path -> {
                File file = path.toFile();
                if (file.isFile()) {
                    fileList.add(file);
                } else if (file.isDirectory()) {
                    findCsvFiles(path, fileList);
                }
            });
        } catch (IOException e) {
            throw new CsvReaderException("Unable to read from directory", e);
        }
    }

    public static List<CsvRow> readAsCsvRows(File csvFile) {
        return readAsCsvRows(csvFile, CSVFormat.DEFAULT.withHeader());
    }

    private static List<CsvRow> readAsCsvRows(File csvFile, CSVFormat csvFormat) {
        if (csvFile == null) {
            throw new IllegalArgumentException("CSV input file is null");
        }
        try (Reader reader = new FileReader(csvFile); CSVParser parser = new CSVParser(reader, csvFormat)) {
            Map<String, Integer> headerMap = parser.getHeaderMap();
            if (headerMap == null) {
                throw new CsvReaderException("Header row is missing in CSV file");
            }

            return parser.getRecords().stream().map(csvRecord -> parseCsvRow(headerMap, csvRecord)).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            throw new CsvReaderException("Could not find CSV file", e);
        } catch (IOException e) {
            throw new CsvReaderException("Could not parse CSV file", e);
        }
    }

    private static CsvRow parseCsvRow(Map<String, Integer> headerMap, CSVRecord csvRecord) {
        CsvRow csvRow = new CsvRow();
        headerMap.keySet().forEach(csvHeader -> csvRow.put(csvHeader, csvRecord.get(csvHeader)));
        return csvRow;
    }

    public static <T> List<T> readAsObjects(File csvFile, Class<T> valueObjectClass) {
        return readAsObjects(csvFile, valueObjectClass, CSVFormat.DEFAULT.withHeader());
    }

    private static <T> List<T> readAsObjects(File csvFile, Class<T> valueObjectClass, CSVFormat csvFormat) {
        if (csvFile == null) {
            throw new IllegalArgumentException("CSV input file is null");
        }
        if (valueObjectClass == null) {
            throw new IllegalArgumentException("CSV record class is null");
        }
        try (Reader reader = new FileReader(csvFile); CSVParser parser = new CSVParser(reader, csvFormat)) {
            Map<String, Integer> headerMap = parser.getHeaderMap();
            if (headerMap == null) {
                throw new CsvReaderException("Header row is missing in CSV file");
            }

            return parser.getRecords().stream().map(csvRecord -> parseObject(headerMap, csvRecord, valueObjectClass)).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            throw new CsvReaderException("Could not find CSV file", e);
        } catch (IOException e) {
            throw new CsvReaderException("Could not parse CSV file", e);
        }
    }

    private static <T> T parseObject(Map<String, Integer> headerMap, CSVRecord csvRecord, Class<T> valueObjectClass) {
        try {
            T valueObject = valueObjectClass.newInstance();

            headerMap.keySet().forEach(csvHeader -> {
                String csvValue = csvRecord.get(csvHeader);
                setValue(valueObject, csvHeader, csvValue, valueObjectClass);
            });

            return valueObject;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CsvReaderException(String.format("Could not create new instance of class %s", valueObjectClass.getName()), e);
        }
    }

    private static <T> void setValue(T valueObject, String csvHeader, String csvValue, Class<T> valueObjectClass) {
        try {
            Field field = valueObjectClass.getDeclaredField(csvHeader);
            field.setAccessible(Boolean.TRUE);
            Class<?> fieldClass = field.getType();

            if (fieldClass == String.class) {
                field.set(valueObject, csvValue);
            } else if (fieldClass == Integer.class) {
                field.set(valueObject, Integer.parseInt(csvValue));
            } else if (fieldClass == Long.class) {
                field.set(valueObject, Long.parseLong(csvValue));
            } else if (fieldClass == Boolean.class) {
                field.set(valueObject, Boolean.parseBoolean(csvValue));
            }
        } catch (NoSuchFieldException e) {
            throw new CsvReaderException(String.format("Could not find field %s in class %s", csvHeader, valueObjectClass.getName()), e);
        } catch (IllegalAccessException e) {
            throw new CsvReaderException(String.format("Could not set value for field %s in class %s", csvHeader, valueObjectClass.getName()), e);
        }
    }
}
