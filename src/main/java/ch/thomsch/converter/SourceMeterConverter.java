package ch.thomsch.converter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

import ch.thomsch.Format;

/**
 * @author Thomsch
 */
public class SourceMeterConverter {

    private static final Logger logger = LoggerFactory.getLogger(SourceMeterConverter.class);

    /**
     * Convert results in the SourceMeter format to the raw format.
     *
     * @param inputPath  the path of the folder containing SourceMeter's results
     * @param outputPath the path of the file that will contain the results
     */
    public static void convert(String inputPath, String outputPath) {
        SourceMeterConverter converter = new SourceMeterConverter();

        File classResults = converter.getClassResultsFile(inputPath);

        try {
            converter.convertClassResult(classResults, outputPath);
        } catch (FileNotFoundException e) {
            logger.error("{} does not exists", classResults.getAbsolutePath(), e);
        } catch (IOException e) {
            logger.error("Cannot read or write {}", classResults.getAbsolutePath(), e);
        }
    }

    /**
     * Converts SourceMeter's results for classes in a common format.
     *
     * @param classFile  the class file
     * @param outputPath the path where the result is stored
     * @throws IOException           if the file cannot be read
     * @throws FileNotFoundException if the file cannot be found
     */
    public void convertClassResult(File classFile, String outputPath) throws IOException {
        Reader in = null;
        BufferedWriter out = null;
        try {
            in = new FileReader(classFile);
            out = new BufferedWriter(new FileWriter(outputPath));

            CSVPrinter printer = new CSVPrinter(out, getOutputFormat());

            final Iterable<CSVRecord> records = CSVFormat.RFC4180
                    .withFirstRecordAsHeader()
                    .parse(in);

            for (CSVRecord record : records) {
                printer.printRecord(formatClassValues(record));
                printer.flush();
            }

        } finally {
            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    private CSVFormat getOutputFormat() {
        return CSVFormat.DEFAULT
                .withHeader(getHeader())
                .withDelimiter(Format.DELIMITER);
    }

    private String[] getHeader() {
        return new String[]{"class",
                "LCOM5", "NL", "NLE", "WMC", "CBO", "CBOI", "NII", "NOI", "RFC", "AD",
                "CD", "CLOC", "DLOC", "PDA", "PUA", "TCD", "TCLOC", "DIT", "NOA", "NOC",
                "NOD", "NOP", "LLOC", "LOC", "NA", "NG", "NLA", "NLG", "NLM", "NLPA",
                "NLPM", "NLS", "NM", "NOS", "NPA", "NPM", "NS", "TLLOC", "TLOC", "TNA",
                "TNG", "TNLA", "TNLG", "TNLM", "TNLPA", "TNLPM", "TNLS", "TNM", "TNOS", "TNPA",
                "TNPM", "TNS"};
    }

    private Object[] formatClassValues(CSVRecord sourceMeterRecord) {
        ArrayList<Object> result = new ArrayList<>();
        result.add(sourceMeterRecord.get(2));
        result.addAll(getRecordSlice(sourceMeterRecord, 10, 61));
        return result.toArray();
    }

    /**
     * Returns a slice of a record from slices <code>from/code> to <code>to</code>.
     *
     * @param sourceMeterRecord the record
     * @param from              included
     * @param to                included
     * @return the slice
     */
    private Collection<Object> getRecordSlice(CSVRecord sourceMeterRecord, int from, int to) {
        ArrayList<Object> result = new ArrayList<>(to - from + 1);

        for (int i = from; i <= to; i++) {
            result.add(sourceMeterRecord.get(i));
        }

        return result;
    }

    /**
     * Finds the class results file in a folder
     *
     * @param folder the path of the folder
     * @return the file containing the class results
     * @throws IllegalArgumentException if there is more than one result file for classes in the directory
     */
    private File getClassResultsFile(String folder) {
        final Collection<File> files = FileUtils.listFiles(new File(folder), new SuffixFileFilter("-Class.csv"), null);

        if (files.size() > 1) {
            throw new IllegalArgumentException("There is more than one class result file in directory " + folder);
        }
        return files.iterator().next();
    }
}
