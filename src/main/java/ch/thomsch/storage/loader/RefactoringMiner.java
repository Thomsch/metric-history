package ch.thomsch.storage.loader;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RefactoringMiner extends VanillaRefactoringMiner {

    private static final Logger logger = LoggerFactory.getLogger(RefactoringMiner.class);

    @Override
    public List<String> make(String filePath) {
        return loadAllLines(filePath);
    }

    @Override
    char getSeparator() {
        return '^';
    }

    @Override
    String getRevision(CSVRecord record) {
        return record.get(2);
    }
}
