package ch.thomsch.storage.loader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class VanillaRefactoringMiner implements CommitReader {
    private static final Logger logger = LoggerFactory.getLogger(VanillaRefactoringMiner.class);

    @Override
    public List<String> make(String filePath) {
        final List<String> duplicatedRefactorings = loadAllLines(filePath);
        return reduceCommits(duplicatedRefactorings);
    }

    List<String> reduceCommits(List<String> commits) {
        final List<String> results = new ArrayList<>();
        String lastCommit = "";
        for (String commit : commits) {
            if (!commit.equals(lastCommit)) {
                results.add(commit);
                lastCommit = commit;
            }
        }
        return results;
    }

    List<String> loadAllLines(String filePath) {
        final List<String> result = new ArrayList<>();

        try (Reader in = new FileReader(filePath)) {
            final Iterable<CSVRecord> records = CSVFormat.RFC4180
                    .withDelimiter(getSeparator())
                    .withFirstRecordAsHeader()
                    .parse(in);

            records.forEach(record -> result.add(getRevision(record)));

            return result;
        } catch (FileNotFoundException e) {
            logger.error("Can't open file:", e);
        } catch (IOException e) {
            logger.error("Can't parse file:", e);
        }
        return result;
    }

    char getSeparator() {
        return ';';
    }

    String getRevision(CSVRecord record) {
        return record.get(0);
    }
}
