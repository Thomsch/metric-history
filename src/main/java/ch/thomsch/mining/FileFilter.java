package ch.thomsch.mining;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters file paths.
 * All credits goes to this implementation:
 * https://github.com/bzafiris/RefactoringMiner/commit/08ba7d54e64eb61a59208f67dadc352b87391b97
 *
 * @author Thomsch
 */
public class FileFilter {

    private static final FileFilter NO_FILTER = new FileFilter();

    private final List<String> exclusionPatterns = new ArrayList<>();

    /**
     * Creates a filter that only let through production code. (no tests or examples)
     *
     * @return a new instance of the filter
     */
    public static FileFilter production() {
        final FileFilter fileFilter = new FileFilter();
        fileFilter.addExclusionPattern("src/test");
        fileFilter.addExclusionPattern("javatests");
        fileFilter.addExclusionPattern("examples");
        return fileFilter;
    }

    public static FileFilter noFilter() {
        return NO_FILTER;
    }

    public void addExclusionPattern(String pattern) {
        exclusionPatterns.add(pattern);
    }

    /**
     * Checks if a path is conform to this filter.
     *
     * @param path the path
     * @return <code>true</code> if the path is conform
     */
    public boolean accept(String path) {
        if (exclusionPatterns.isEmpty()) {
            return true;
        }

        for (String pattern : exclusionPatterns) {
            if (path.contains(pattern)) {
                return false;
            }
        }
        return true;
    }
}
