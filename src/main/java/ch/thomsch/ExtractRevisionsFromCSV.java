package ch.thomsch;

import java.util.List;


/**
 * @author TSC
 */
final class ExtractRevisionsFromCSV {
    private static final String REFACTORING_RESULTS = "src/main/resources/junit4-refactorings-master.csv";

    public static void main(String[] args) {
        final CommitReader commitReader = new RMinerReader();
        final List<String> refactorings = commitReader.load(REFACTORING_RESULTS);

        System.out.println(String.format("Found %s refactorings:", refactorings.size()));
        refactorings.forEach(System.out::println);
    }
}
