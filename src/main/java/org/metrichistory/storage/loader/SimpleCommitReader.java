package org.metrichistory.storage.loader;

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
import java.util.function.Consumer;

public class SimpleCommitReader implements CommitReader {
    private static final Logger logger = LoggerFactory.getLogger(SimpleCommitReader.class);

    @Override
    public List<String> make(String filePath) {
        final List<String> revisions = loadAllLines(filePath);
        return revisions;
    }

    List<String> loadAllLines(String filePath) {
        final Set<String> result = new HashSet<>();
        try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
            in.lines().forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    result.add(s);
                }
            });
        } catch (FileNotFoundException e) {
            logger.error("Can't open file:", e);
        } catch (IOException e) {
            logger.error("Can't parse file:", e);
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
