package ch.thomsch.storage;

import java.util.HashMap;

import ch.thomsch.model.Metrics;

/**
 * Prints the results on the standard output.
 */
public class ConsoleOutput implements TradeoffOutput {

    @Override
    public void export(HashMap<String, Metrics> results, String ... whitelist) {
        final int[] indices = Stores.getIndices(whitelist);

        System.out.println("Trade-offs:");
        results.forEach((revision, metrics) -> System.out.println(String.format("%s: %s", revision, metrics.hasTradeOff(indices))));
    }
}
