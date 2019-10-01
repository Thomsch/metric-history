package org.metrichistory.analyzer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.metrichistory.model.FormatException;
import org.metrichistory.storage.DirectoryCreationException;
import org.metrichistory.storage.DiskUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class SourceMeterConverter {

    private static final Logger logger = LoggerFactory.getLogger(SourceMeterConverter.class);

    /**
     * Converts sourcemeter results to the RAW format.
     * @param inputPath The folder containing the results from an execution of a sourcemeter analysis.
     * @param output The file or folder where to write the results.
     * @throws FormatException when the <code>inputPath</code> isnt a valid sourcemeter path.
     * @throws DirectoryCreationException when a directory cannot be created.
     * @throws IOException when an read or write error occurred.
     */
    public static void convert(String inputPath, String output) throws FormatException, DirectoryCreationException, IOException {
        final SourceMeterConverter converter = new SourceMeterConverter();

        logger.info("Scanning {}", inputPath);
        final String[] folders = converter.getRevisionFolders(inputPath);

        if(DiskUtils.isFile(output)) {
            try (CSVPrinter printer = converter.getPrinter(new File(output))) {
                logger.info("Saving contents in {}", output);
                converter.convertProject(folders, printer);
            }
        } else {
            final File outputDirectory = DiskUtils.createDirectory(output);
            logger.info("Saving contents to {}", outputDirectory);
            for (String folder : folders) {
                final File classResults = converter.getClassResultsFile(folder);
                final String revision = FilenameUtils.getBaseName(folder);
                try (CSVPrinter printer = converter.getPrinter(new File(outputDirectory, revision + ".csv"))) {
                    converter.convertClassResult(classResults, revision, printer);
                }
            }
        }
    }

    /**
     * Converts the SourceMeter results for classes and prints them to the printer.
     *
     * @param revisionFolders the folders containing the results
     * @param printer         the instance responsible to write the results
     */
    void convertProject(String[] revisionFolders, CSVPrinter printer) throws IOException {
        for (String folder : revisionFolders) {
            final File classResults = getClassResultsFile(folder);
            convertClassResult(classResults, FilenameUtils.getBaseName(folder), printer);
        }
    }

    /**
     * Returns the printer to export the results.
     * If the printer cannot be created, returns null.
     *
     * @param outputFile the path of the file where the results will be print.
     * @return a new instance of {@link CSVPrinter}
     */
    CSVPrinter getPrinter(File outputFile) throws IOException {
        final BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
        return new CSVPrinter(out, getOutputFormat());
    }

    /**
     * Returns the absolute path of each folder containing the results for a revision of a project.
     *
     * @param project the root directory of the project
     * @return the absolute paths of the revision folders
     * @throws FormatException if the project is not using SourceMeter format
     */
    String[] getRevisionFolders(String project) throws FormatException {
        final File file = new File(project, "java");

        if (!file.exists()) {
            throw new FormatException("Does not recognized Source Meter directory results");
        }

        final String[] list = file.list(DirectoryFileFilter.INSTANCE);

        if(list == null) {
            return new String[0];
        }

        for (int i = 0; i < list.length; i++) {
            list[i] = new File(file, list[i]).getAbsolutePath();
        }
        return list;
    }

    /**
     * Converts SourceMeter's results for classes in a common format.
     *
     * @param classFile the class file
     * @param revision the current revision of the file
     * @param printer the printer where the results will be written
     * @throws IOException           if the file cannot be read
     * @throws FileNotFoundException if the file cannot be found
     */
    void convertClassResult(File classFile, String revision, CSVPrinter printer) throws IOException {
        try (CSVParser records = getParser(classFile)) {
            for (CSVRecord record : records) {
                printer.printRecord(formatClassValues(revision, record));
            }
        }
    }

    /**
     * Returns a  CSV parser for SourceMeter files.
     *
     * @param classFile the file to parse
     * @return the parser
     * @throws IOException if an I/O error occurs or the file does not exists
     */
    private CSVParser getParser(File classFile) throws IOException {
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new FileReader(classFile));
    }

    private CSVFormat getOutputFormat() {
        return CSVFormat.DEFAULT
                .withRecordSeparator('\n')
                .withHeader(getHeader())
                .withDelimiter(';');
    }

    private String[] getHeader() {
        return new String[]{"revision", "class",
                "LCOM5", "NL", "NLE", "WMC", "CBO", "CBOI", "NII", "NOI", "RFC", "AD",
                "CD", "CLOC", "DLOC", "PDA", "PUA", "TCD", "TCLOC", "DIT", "NOA", "NOC",
                "NOD", "NOP", "LLOC", "LOC", "NA", "NG", "NLA", "NLG", "NLM", "NLPA",
                "NLPM", "NLS", "NM", "NOS", "NPA", "NPM", "NS", "TLLOC", "TLOC", "TNA",
                "TNG", "TNLA", "TNLG", "TNLM", "TNLPA", "TNLPM", "TNLS", "TNM", "TNOS", "TNPA",
                "TNPM", "TNS"};
    }

    private Object[] formatClassValues(String revision, CSVRecord sourceMeterRecord) {
        final ArrayList<Object> result = new ArrayList<>();
        result.add(revision);
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
        final ArrayList<Object> result = new ArrayList<>(to - from + 1);

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
     * @throws FileNotFoundException if the class result file doesn't exist in the folder
     */
    private File getClassResultsFile(String folder) throws FileNotFoundException {
        final Collection<File> files = FileUtils.listFiles(new File(folder), new SuffixFileFilter("-Class.csv"), null);

        if (files.size() > 1) {
            throw new IllegalArgumentException("There is more than one class result file in directory " + folder);
        } else if (!files.iterator().hasNext()) {
            throw new FileNotFoundException("There is no class result file in " + folder);
        } else {
            return files.iterator().next();
        }
    }
}
