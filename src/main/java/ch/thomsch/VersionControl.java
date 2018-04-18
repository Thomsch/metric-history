package ch.thomsch;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * @author TSC
 */
public interface VersionControl {

    /**
     * Define the repository that's going to be used.
     *
     * @param repositoryDirectory Path to the repository
     * @throws IOException when the repository cannot be located
     */
    void initializeRepository(String repositoryDirectory) throws IOException;

    /**
     * Checkout the repository to the revision.
     * @param revision the full SHA of the revision
     */
    void checkout(String revision) throws GitAPIException;

    /**
     * Checkout the parent of the given revision.
     * @param revision the full SHA of the revision
     */
    void checkoutParent(String revision) throws IOException, GitAPIException;

    /**
     * Fills <code>beforeFiles</code> and <code>afterFiles</code> for a revision.
     *
     * @param revision    the revision
     * @param beforeFiles a list of changed files existing before the revision
     * @param afterFiles  a list of changed files existing after the revision
     */
    void getChangedFiles(
            String revision,
            Collection<File> beforeFiles,
            Collection<File> afterFiles) throws IOException;
}
