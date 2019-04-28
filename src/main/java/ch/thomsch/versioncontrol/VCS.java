package ch.thomsch.versioncontrol;

import ch.thomsch.model.vcs.Commit;
import ch.thomsch.model.vcs.Tag;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Represent a generic Version Control System.
 */
public interface VCS extends AutoCloseable {

    /**
     * Checkout the repository to the revision.
     *
     * @param revision the full SHA of the revision
     */
    void checkout(String revision) throws GitAPIException;

    /**
     * Discard any modification made to tracked or un-tracked files and reset any conflicts.
     */
    void clean();

    /**
     * Saves the current version of the repository. Erases last version saved if any.
     */
    void saveVersion();

    /**
     * Restore the version saved by {@link #saveVersion()}.
     */
    void restoreVersion();

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

    List<Tag> listSelectedReleases(List<String> tagList);

    List<Tag> listReleases();

    /**
     * List all commits between two releases
     * @param fromTag The starting release
     * @param toTag The next release
     * @return List of commits including the starting release commit and excluding the next release
     */
    List<Commit> listCommitsBetweenReleases(Tag fromTag, Tag toTag);

    /**
     * Return the location of this repository on the file system.
     */
    String getDirectory();

    /**
     * Return the parent/precedent revision for a revision
     *
     * @param revision the revision
     * @return the parent of the revision
     */
    String getParent(String revision) throws IOException;
}
