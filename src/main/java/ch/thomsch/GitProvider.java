package ch.thomsch;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
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
 * @author TSC
 */
public class GitProvider implements VersionControl {

    private static final Logger logger = LoggerFactory.getLogger(GitProvider.class);

    private Repository repository;

    @Override
    public void initializeRepository(String repositoryDirectory) throws IOException {
        final FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(new File(repositoryDirectory, ".git")).setMustExist(true).build();
    }

    @Override
    public void checkout(String revision) throws GitAPIException {
        verifyRepositoryInitialization();

        final CheckoutCommand command = new Git(repository).checkout().setName(revision);
        command.call();
    }

    @Override
    public void checkoutParent(String revision) throws IOException, GitAPIException {
        verifyRepositoryInitialization();

        final ObjectId revisionId = repository.resolve(revision);


        final RevWalk walk = new RevWalk(repository);

        final RevCommit commit = walk.parseCommit(revisionId);
        final RevCommit parentRevision = commit.getParent(0);

        checkout(parentRevision.getId().getName());
    }

    @Override
    public void getChangedFiles(String revision, Collection<File> beforeFiles, Collection<File> afterFiles)
            throws IOException {
        verifyRepositoryInitialization();
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

    /**
     * Converts a {@link DiffEntry} path to the corresponding absolute {@link File}.
     *
     * @param path the path
     * @return a new instance of the file
     */
    private File convertPathToFile(String path) {
        return new File(FilenameUtils.concat(getFolder(), path));
    }

    /**
     * Returns the folder of the repository on the filesystem.
     * There is no trailing "/".
     *
     * @return the absolute path of the repository
     */
    private String getFolder() {
        verifyRepositoryInitialization();

        try {
            return repository.getDirectory().getParentFile().getCanonicalPath();
        } catch (IOException e) {
            return repository.getDirectory().getParentFile().getAbsolutePath();
        }
    }

    private void verifyRepositoryInitialization() {
        if(repository == null) {
            throw new IllegalStateException("Repository hasn't been initialized");
        }
    }
}
