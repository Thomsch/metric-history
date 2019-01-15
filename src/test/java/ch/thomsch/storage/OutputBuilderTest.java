package ch.thomsch.storage;

import org.junit.Test;

import static org.junit.Assert.*;

public class OutputBuilderTest {

    @Test
    public void create_ShouldInstantiateConsoleOutput_WhenNoFileIsGiven() {
        final TradeoffOutput output = OutputBuilder.create(null);

        assertNotNull(output);
        assertTrue(output instanceof ConsoleOutput);
    }

    @Test
    public void create_ShouldInstantiateConsoleOutput_WhenStringIsEmpty() {
        final TradeoffOutput output = OutputBuilder.create("");

        assertNotNull(output);
        assertTrue(output instanceof ConsoleOutput);
    }

    @Test
    public void create_ShouldInstantiateCsvOutput_WhenAFileIsGiven() {
        final TradeoffOutput output = OutputBuilder.create("some/file.csv");

        assertNotNull(output);
        assertTrue(output instanceof CsvOutput);
    }
}
