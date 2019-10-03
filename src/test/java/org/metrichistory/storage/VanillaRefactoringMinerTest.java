package org.metrichistory.storage;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class VanillaRefactoringMinerTest {

    private static final String TEST_FILE = "src/test/resources/test-revisions.csv";

    @Test
    public void reduceList() {
        final VanillaRefactoringMiner reader = new VanillaRefactoringMiner();
        final List<String> expected = Arrays.asList("A", "B", "C");
        final List<String> list = Arrays.asList("A", "A", "B", "B", "C", "C");

        final List<String> result = reader.reduceCommits(list);

        assertEquals(expected, result);
    }

    @Test
    public void loadFile() throws IOException {
        final CommitReader commitReader = new VanillaRefactoringMiner();
        final List<String> revisions = commitReader.make(TEST_FILE);

        final List<String> expected = Arrays.asList(
                "f7d4f2835ec7dde25356d923ade0eb93f11cf1c9",
                "627b85abfd9c1f6c69b14f9d33fb292868fe6826",
                "347eb809e653d8fb601b9751f7b7971341e1f5ec");

        assertEquals(revisions, expected);
        assertNotSame(revisions, expected);
    }
}
