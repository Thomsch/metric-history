package ch.thomsch.storage;

/**
 * Instantiate @{@link TradeoffOutput} depending on the parameters given.
 */
public final class OutputBuilder {
    private OutputBuilder() {
    }

    /**
     * Create a new instance of @{@link TradeoffOutput}.
     * @param outputFile if not null, will export to the given file as CSV.
     * @return the new instance.
     */
    public static TradeoffOutput create(String outputFile) {
        if(outputFile == null || outputFile.isEmpty()) {
            return new ConsoleOutput();
        } else {
            return new CsvOutput(outputFile);
        }
    }
}
