package ch.thomsch.example;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import ch.thomsch.converter.SourceMeterConverter;

import static org.junit.Assert.assertTrue;

/**
 * @author Thomsch
 */
public class SourceMeterConverterTest {

    private SourceMeterConverter converter;

    @Before
    public void setUp() {
        converter = new SourceMeterConverter();
    }

    @Test
    public void test() throws IOException {
        File actual = new File("src/test/resources/conversion/classes.csv");
        File reference = new File("src/test/resources/conversion/ref.csv");

        converter.convertClassResult(new File("src/test/resources/conversion/source-meter-classes.csv"), actual
                .getPath());

        assertTrue(FileUtils.contentEquals(reference, actual));
        actual.deleteOnExit();
    }
}
