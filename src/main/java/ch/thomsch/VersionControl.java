package ch.thomsch;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * @author TSC
 */
public interface VersionControl {
    void initializeRepository(String repositoryDirectory) throws IOException;

    void checkout(String revision) throws GitAPIException;

    void checkoutParent(String revision) throws IOException, GitAPIException;
}
