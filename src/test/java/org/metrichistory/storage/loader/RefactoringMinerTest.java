package org.metrichistory.storage.loader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RefactoringMinerTest {

    private RefactoringMiner reader;

    @BeforeEach
    public void setUp() {
        reader = new RefactoringMiner();
    }

    @Test
    public void testLoad() {
        final List<String> revisions = reader.make("src/test/resources/z-refactorings.csv");

        final List<String> expected = Arrays.asList(
                "d4bce13a443cf12da40a77c16c1e591f4f985b47",
                "9a5c33b16d07d62651ea80552e8782974c96bb8a",
                "0bb0526b70870d57cbac9fcc8c4a7346a4ce5879");

        assertEquals(revisions, expected);
        assertNotSame(revisions, expected);
    }

    @Test
    public void make_ShouldThrowException_WhenFileContainsQuoteImmediatelyAfterSeparator() {
        assertThrows(IllegalStateException.class, () -> reader.make("src/test/resources/invalid-refactorings.csv"));
    }
}
