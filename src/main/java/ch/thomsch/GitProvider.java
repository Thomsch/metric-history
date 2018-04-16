package ch.thomsch;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

/**
 * @author TSC
 */
public class GitProvider {

    private Repository repository;

    public void initializeRepository(String repositoryDirectory) throws IOException {
        final FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(new File(repositoryDirectory, ".git")).build();
    }

    /**
     * Checkout the repository to the revision.
     * @param revision the full SHA of the revision
     */
    public void checkout(String revision) throws GitAPIException {
        verifyRepositoryInitialization();

        final CheckoutCommand command = new Git(repository).checkout().setName(revision);
        command.call();
    }

    public void checkoutParent(String revision) throws IOException, GitAPIException {
        verifyRepositoryInitialization();

        final ObjectId revisionId = repository.resolve(revision);

        final RevWalk walk = new RevWalk(repository);
        final RevCommit commit = walk.parseCommit(revisionId);
        final RevCommit parentRevision = commit.getParent(0);

        checkout(parentRevision.getId().getName());
    }

    private void verifyRepositoryInitialization() {
        if(repository == null) {
            throw new IllegalStateException("Repository hasn't been initialized");
        }
    }
}
