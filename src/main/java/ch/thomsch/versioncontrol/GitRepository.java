package ch.thomsch.versioncontrol;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author Thomsch
 */
public class GitRepository implements Repository {

    private static final Logger logger = LoggerFactory.getLogger(GitRepository.class);

    private final org.eclipse.jgit.lib.Repository repository;

    GitRepository(org.eclipse.jgit.lib.Repository repository) {
        this.repository = repository;
    }

    @Override
    public void checkout(String revision) throws GitAPIException {
        final CheckoutCommand command = new Git(repository).checkout().setName(revision);
        command.call();
    }

    @Override
    public String getParent(String revision) throws IOException {
        final ObjectId revisionId = repository.resolve(revision);
        final RevWalk walk = new RevWalk(repository);
        final RevCommit commit = walk.parseCommit(revisionId);

        if(commit.getParentCount() == 0) {
            return null;
        }

        final RevCommit parentRevision = commit.getParent(0);
        return parentRevision.getName();
    }

    @Override
    public void getChangedFiles(String revision, Collection<File> beforeFiles, Collection<File> afterFiles)
            throws IOException {
        final Git git = new Git(repository);
        final ObjectReader reader = repository.newObjectReader();

        final ObjectId revisionId = repository.resolve(revision);
        final RevWalk walk = new RevWalk(repository);
        final RevCommit commit = walk.parseCommit(revisionId);

        final CanonicalTreeParser oldTree = new CanonicalTreeParser();
        final CanonicalTreeParser newTree = new CanonicalTreeParser();
        newTree.reset(reader, commit.getTree());

        walk.markStart(commit.getParent(0));

        oldTree.reset(reader, commit.getParent(0).getTree());

        try {
            final List<DiffEntry> diffEntries = git.diff().setNewTree(newTree).setOldTree(oldTree).call();

            for (DiffEntry diffEntry : diffEntries) {
                if (diffEntry.getChangeType() != DiffEntry.ChangeType.ADD) {
                    beforeFiles.add(convertPathToFile(diffEntry.getOldPath()));
                }

                if (diffEntry.getChangeType() != DiffEntry.ChangeType.DELETE) {
                    afterFiles.add(convertPathToFile(diffEntry.getNewPath()));
                }
            }

        } catch (GitAPIException e) {
            logger.error("Unable to retrieve changed files for revision {}", revision, e);
        }
    }

    @Override
    public String getDirectory() {
        return FilenameUtils.normalize(repository.getDirectory().getParentFile().getAbsolutePath());
    }

    /**
     * Converts a {@link DiffEntry} path to the corresponding absolute {@link File}.
     *
     * @param path the path
     * @return a new instance of the file
     */
    private File convertPathToFile(String path) {
        final String concat = FilenameUtils.concat(getDirectory(), path);
        return new File(concat);
    }

    @Override
    public void close() throws Exception {
        checkout("master");
        repository.close();
    }

    /**
     * Creates a new instance for the repository located at <code>path</code>.
     * @param path the location of the repository
     * @return the instance
     * @throws IOException when there is no repository at the specified location.
     */
    public static GitRepository get(String path) throws IOException {
        final FileRepositoryBuilder builder = new FileRepositoryBuilder();
        final org.eclipse.jgit.lib.Repository repository = builder.setGitDir(new File(path, ".git")).setMustExist
                (true).build();

        return new GitRepository(repository);
    }
}
