package ch.thomsch;

import java.io.File;
import java.io.IOException;

import ch.thomsch.csv.Stores;
import ch.thomsch.model.ClassStore;

public final class MemoryPerformance {

    private static final File baseDir = new File("../data/raw");

    private MemoryPerformance() {
    }

    public static void main(String[] args) {
        try {
            System.out.println("Memory available " + bToMb(Runtime.getRuntime().maxMemory()) + "MBs.");
            System.out.println("Loading...");
            final long start = System.currentTimeMillis();
            final ClassStore store = Stores.loadClasses(baseDir.getPath() + '/' + "rxjava-raw.csv");
            final long diff = System.currentTimeMillis() - start;
            System.out.println("Loaded in " + diff / 1000 + " seconds.");

            System.gc();
            printMemoryUsage();
            printStoreStatistics(store);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printStoreStatistics(ClassStore store) {
        System.out.println("Revisions: " + store.revisions());
        System.out.println("Average classes/revision: " + averageInstances(store));
        System.out.println("Total instances: " + store.instances());
    }

    private static void printMemoryUsage() {
        final Runtime runtime = Runtime.getRuntime();
        final long usedMemoryInMb = bToMb(runtime.totalMemory() - runtime.freeMemory());
        final long totalMemoryInMb = bToMb(runtime.totalMemory());
        System.out.println(String.format("Memory used %s/%s MBs", usedMemoryInMb, totalMemoryInMb));
    }

    /**
     * Returns the average number of class per revision
     */
    private static double averageInstances(ClassStore store) {
        return store.instances() / (float) store.revisions();
    }

    /**
     * Converts Bytes to Megabytes.
     */
    private static long bToMb(long bytes) {
        return bytes / 1024 / 1024;
    }
}
