package ch.thomsch;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TSC
 */
public class ModifiedRMinerReader extends RMinerReader {
    @Override
    public List<String> load(String filePath) {
        final List<String> modifiedResults = super.load(filePath);

        return modifiedResults.stream()
                .map(longCommit -> longCommit.split(" ")[1])
                .collect(Collectors.toList());
    }
}
