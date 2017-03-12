package no.acntech.test.csv;

import java.util.HashMap;
import java.util.Set;

public class CsvRow extends HashMap<String, String> {

    public Set<String> getHeaders() {
        return this.keySet();
    }

    public String getString(String header) {
        return this.get(header);
    }

    public Integer getInteger(String header) {
        return Integer.parseInt(this.get(header));
    }

    public Long getLong(String header) {
        return Long.parseLong(this.get(header));
    }

    public Boolean getBoolean(String header) {
        return Boolean.parseBoolean(this.get(header));
    }
}
