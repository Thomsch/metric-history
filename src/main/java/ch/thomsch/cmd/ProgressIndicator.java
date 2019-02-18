package ch.thomsch.cmd;

import java.io.PrintStream;
import java.util.Locale;

public class ProgressIndicator {

    private final double nTasks;
    private final PrintStream stream;
    private int count;
    private final double minInterval;
    private double nextInterval;
    private final double epsilon;

    /**
     * Create a new instance of {@link ProgressIndicator} outputting to the default system output.
     * @param tasks The total number of tasks.
     * @param interval interval between each progress report in percent
     */
    public ProgressIndicator(int tasks, int interval) {
        this(tasks, interval, System.out);
    }

    /**
     * Create a new instance of {@link ProgressIndicator} outputting to the default system output.
     * @param tasks The total number of tasks
     * @param interval interval between each progress report in percent
     * @param stream the stream where the report is written
     */
    public ProgressIndicator(int tasks, int interval, PrintStream stream) {
        nTasks = tasks;
        this.stream = stream;
        count = 0;
        minInterval = interval / 100.0;
        nextInterval = minInterval;
        epsilon = 0.001;
    }

    public void update() {
        count++;
        final double progress = count / nTasks;
        if (progress >= nextInterval + epsilon || progress >= nextInterval - epsilon) {
            stream.printf(Locale.ROOT, "%.2f%%\n", progress * 100);
            nextInterval += minInterval;
        }
    }
}
