package ch.thomsch.loader;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Thomsch
 */
public class ZafeirisRefactoringMiner extends VanillaRefactoringMiner {

    private static final Logger logger = LoggerFactory.getLogger(ZafeirisRefactoringMiner.class);

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
