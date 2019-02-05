package ch.thomsch.versioncontrol;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public final class VcsBuilder {

    private VcsBuilder() {
    }

    /**
     * Creates an adapter for the version control system used by the project
     * @param path the location of the VCS on the disk
     * @return a new instance of the adapter
     */
    public static VCS create(String path) throws VcsNotFound {
        final FileRepositoryBuilder builder = new FileRepositoryBuilder();
        final Repository repository;
        try {
            repository = builder.setGitDir(new File(path, ".git")).setMustExist(true).build();
        } catch (IOException e) {
            throw new VcsNotFound();
        }

        return new GitVCS(repository);
    }
}
