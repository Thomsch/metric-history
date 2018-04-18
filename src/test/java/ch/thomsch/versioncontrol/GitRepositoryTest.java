package ch.thomsch.versioncontrol;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author TSC
 */
public class GitRepositoryTest {
    @Test
    @Ignore
    public void changedFiles() throws IOException {
        final Repository repository = GitRepository.get(".");

        final Collection<File> beforeFiles = new ArrayList<>();
        final Collection<File> afterFiles = new ArrayList<>();

        repository.getChangedFiles("1ed6ac86d344fb17f8016f0362efa46f4d8d1eb3", beforeFiles, afterFiles);

        System.out.println("Files to examine before");
        System.out.println(Arrays.toString(beforeFiles.toArray()));

        System.out.println("Files to examine after");
        System.out.println(Arrays.toString(afterFiles.toArray()));
    }
}
