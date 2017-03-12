package no.acntech.test.csv;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class CsvReaderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testThatNullFileThrowsException() throws Exception {
        exception.expect(IllegalArgumentException.class);

        CsvReader.readAsObjects(null, null);
    }

    @Test
    public void testThatNullValueObjectClassThrowsException() throws Exception {
        exception.expect(IllegalArgumentException.class);

        CsvReader.readAsObjects(new File(""), null);
    }

    @Test
    public void testThatNoFileThrowsException() throws Exception {
        exception.expect(CsvReaderException.class);
        exception.expectCause(instanceOf(FileNotFoundException.class));

        CsvReader.readAsObjects(new File(""), ValueObject.class);
    }

    @Test
    public void testThatInterfaceAsValueObjectThrowsException() throws Exception {
        exception.expect(CsvReaderException.class);
        exception.expectCause(instanceOf(InstantiationException.class));

        File file = new File(getClass().getResource("/WithSomeValues.csv").toURI());
        CsvReader.readAsObjects(file, Interface.class);
    }

    @Test
    public void testThatValueObjectWithNoDefaultConstructorThrowsException() throws Exception {
        exception.expect(CsvReaderException.class);
        exception.expectCause(instanceOf(InstantiationException.class));

        File file = new File(getClass().getResource("/WithSomeValues.csv").toURI());
        CsvReader.readAsObjects(file, NoDefaultConstructor.class);
    }

    @Test
    public void testThatGenericClassAsValueObjectThrowsException() throws Exception {
        exception.expect(CsvReaderException.class);
        exception.expectCause(instanceOf(IllegalAccessException.class));

        File file = new File(getClass().getResource("/WithSomeValues.csv").toURI());
        CsvReader.readAsObjects(file, Class.class);
    }

    @Test
    public void testThatWrongHeaderNamesThrowsException() throws Exception {
        exception.expect(CsvReaderException.class);
        exception.expectCause(instanceOf(NoSuchFieldException.class));

        File file = new File(getClass().getResource("/WithWrongHeaders.csv").toURI());
        CsvReader.readAsObjects(file, ValueObject.class);
    }

    @Test
    public void testThatEmptyFileGivesEmptyList() throws Exception {
        File file = new File(getClass().getResource("/Empty.csv").toURI());
        List<ValueObject> objects = CsvReader.readAsObjects(file, ValueObject.class);

        assertThat(objects, notNullValue());
        assertThat(objects.size(), is(0));
    }

    @Test
    public void testThatFileWithSomeValuesIsParsedCorrectly() throws Exception {
        File file = new File(getClass().getResource("/WithSomeValues.csv").toURI());
        List<ValueObject> objects = CsvReader.readAsObjects(file, ValueObject.class);

        assertThat(objects, notNullValue());
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).getStringField(), is("Row1"));
        assertThat(objects.get(0).getIntegerField(), nullValue());
        assertThat(objects.get(0).getLongField(), is(1L));
        assertThat(objects.get(0).getBooleanField(), nullValue());
    }

    @Test
    public void testThatFileWithAllValuesIsParsedCorrectly() throws Exception {
        File file = new File(getClass().getResource("/WithAllValues.csv").toURI());
        List<ValueObject> objects = CsvReader.readAsObjects(file, ValueObject.class);

        assertThat(objects, notNullValue());
        assertThat(objects.size(), is(3));
        assertThat(objects.get(0).getStringField(), is("Row1"));
        assertThat(objects.get(0).getIntegerField(), is(1));
        assertThat(objects.get(0).getLongField(), is(1L));
        assertThat(objects.get(0).getBooleanField(), is(true));
        assertThat(objects.get(1).getStringField(), is("Row2"));
        assertThat(objects.get(1).getIntegerField(), is(2));
        assertThat(objects.get(1).getLongField(), is(2L));
        assertThat(objects.get(1).getBooleanField(), is(false));
        assertThat(objects.get(2).getStringField(), is("Row3"));
        assertThat(objects.get(2).getIntegerField(), is(3));
        assertThat(objects.get(2).getLongField(), is(3L));
        assertThat(objects.get(2).getBooleanField(), is(true));
    }
}