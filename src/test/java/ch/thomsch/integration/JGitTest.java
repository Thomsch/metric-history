package ch.thomsch.integration;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author TSC
 */
public class JGitTest {

    @Test
    public void retrieveParentCommit() throws IOException {
        final String revision = "1ad68a69e0a84ccbd4ad7ca39bdf1fd7626f0d92";
        final String expectedParent = "d0e9fcd31603881582da7a457f2c75802289af3c";

        final FileRepositoryBuilder builder = new FileRepositoryBuilder();
        final Repository repository = builder.setGitDir(new File("./", ".git")).build();
        final ObjectId revisionId = repository.resolve(revision);

        final RevWalk walk = new RevWalk(repository);
        final RevCommit commit = walk.parseCommit(revisionId);
        final RevCommit parent = commit.getParent(0);

        assertEquals(1, commit.getParentCount());
        assertEquals(expectedParent, parent.getId().getName());
    }
}
