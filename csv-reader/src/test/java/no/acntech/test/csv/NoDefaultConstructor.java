package no.acntech.test.csv;

public class NoDefaultConstructor {

    private String field;

    public NoDefaultConstructor(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
