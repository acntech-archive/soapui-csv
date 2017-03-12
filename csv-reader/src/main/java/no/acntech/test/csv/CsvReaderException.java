package no.acntech.test.csv;

public class CsvReaderException extends RuntimeException {

    public CsvReaderException() {
    }

    public CsvReaderException(String message) {
        super(message);
    }

    public CsvReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvReaderException(Throwable cause) {
        super(cause);
    }
}
