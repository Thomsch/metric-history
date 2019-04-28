package org.metrichistory.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container for refactoring information.
 */
public class RefactoringDetail {

    private static final Logger logger = LoggerFactory.getLogger("RefactoringDetail");

    private final List<Detail> refactorings;

    public RefactoringDetail() {
        refactorings = new ArrayList<>();
    }

    /**
     * Returns all the classes related to this instance
     * @return the set of classes
     */
    public Set<String> getClasses() {
        final Set<String> result = new HashSet<>();

        for (Detail refactoring : refactorings) {
            result.addAll(refactoring.getClasses());
        }
        return result;
    }

    /**
     * Add a refactoring to this instance
     * @param refactoringType the type of the refactoring
     * @param description a description of the refactoring.
     */
    public void addRefactoring(String refactoringType, String description) {
        final Collection<String> classes = parseDetails(description);
        refactorings.add(new Detail(refactoringType, classes));
    }

    /**
     * Parses the description to find classes.
     * @param description the description
     * @return a list of classes found
     */
    private Collection<String> parseDetails(String description) {
        final Pattern pattern = Pattern.compile("(?i)(?:class\\s)?(([a-z|A-Z|\\d|_]+\\.)+[a-z|A-Z|\\d|_]+)");
        final Matcher matcher = pattern.matcher(description);

        final List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group(1));
        }

        if (result.isEmpty()){
            logger.warn("No class was extracted from this description: '" + description + '\'');
        }

        return result;
    }

    /**
     * Container for information related to a refactoring.
     */
    private class Detail {
        private final String type;
        private final Set<String> classLocations;

        Detail(String type, Collection<String> classes) {
            this.type = type;
            classLocations = new HashSet<>(classes);
        }

        Collection<String> getClasses() {
            return classLocations;
        }
    }
}
