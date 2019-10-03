package org.metrichistory.storage;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleCommitReader implements CommitReader {
    private static final Logger logger = LoggerFactory.getLogger(SimpleCommitReader.class);

    @Override
    public List<String> make(String filePath) throws FileNotFoundException, IOException {
        final List<String> revisions = loadAllLines(filePath);
        return revisions;
    }

    List<String> loadAllLines(String filePath) throws FileNotFoundException, IOException {
        final Set<String> result = new HashSet<>();
        try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
            in.lines().forEach(result::add);
        }
        return new ArrayList<>(result);
    }

    char getSeparator() {
        return ';';
    }

    String getRevision(CSVRecord record) {
        return record.get(0);
    }
}
