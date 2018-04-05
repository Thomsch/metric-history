package ch.thomsch;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TSC
 */
public class RMinerReader implements CommitReader {

    @Override
    public List<String> load(String filePath) {
        final List<String> duplicatedRefactorings = loadAllCommits(filePath);
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

    private List<String> loadAllCommits(String filePath) {
        final List<String> result = new ArrayList<>();

        try (Reader in = new FileReader(filePath)) {
            final Iterable<CSVRecord> records = CSVFormat.RFC4180
                    .withDelimiter(';')
                    .withFirstRecordAsHeader()
                    .parse(in);

            records.forEach(record -> result.add(record.get(0)));

            return result;
        } catch (FileNotFoundException e) {
            System.err.println("Can't open file");
        } catch (IOException e) {
            System.err.println("Can't parse file");
        }
        return result;
    }
}
