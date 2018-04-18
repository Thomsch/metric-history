package ch.thomsch.versioncontrol;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Represents a repository from a version control system.
 *
 * @author TSC
 */
public interface Repository extends AutoCloseable {

    /**
     * Checkout the repository to the revision.
     *
     * @param revision the full SHA of the revision
     */
    void checkout(String revision) throws GitAPIException;

    /**
     * Checkout the parent of the given revision.
     *
     * @param revision the full SHA of the revision
     */
    void checkoutParent(String revision) throws IOException, GitAPIException;

    /**
     * Retrieve the files changed for a revision and puts them in <code>beforeFiles</code> or <code>afterFiles</code>.
     *
     * @param revision    the revision
     * @param beforeFiles a list of changed files existing before the revision
     * @param afterFiles  a list of changed files existing after the revision
     */
    void getChangedFiles(
            String revision,
            Collection<File> beforeFiles,
            Collection<File> afterFiles) throws IOException;

    /**
     * Return the location of this repository on the file system.
     */
    String getDirectory();
}
