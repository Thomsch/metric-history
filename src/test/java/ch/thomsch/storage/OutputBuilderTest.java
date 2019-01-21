package ch.thomsch.storage;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OutputBuilderTest {

    @Test
    public void create_ShouldInstantiateConsoleOutput_WhenNoFileIsGiven() {
        final StoreOutput output = OutputBuilder.create(null);

        assertNotNull(output);
        assertTrue(output instanceof ConsoleOutput);
    }

    @Test
    public void create_ShouldInstantiateConsoleOutput_WhenStringIsEmpty() {
        final StoreOutput output = OutputBuilder.create("");

        assertNotNull(output);
        assertTrue(output instanceof ConsoleOutput);
    }

    @Test
    public void create_ShouldInstantiateCsvOutput_WhenAFileIsGiven() {
        final StoreOutput output = OutputBuilder.create("some/file.csv");

        assertNotNull(output);
        assertTrue(output instanceof CsvOutput);
    }
}
