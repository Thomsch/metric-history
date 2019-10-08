package org.metrichistory.versioncontrol;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public final class VcsBuilder {

    private VcsBuilder() {
    }

    /**
     * Creates an adapter for the version control system used by the project. Automatically calls {@link Vcs#saveVersion()}.
     * @param path the location of the VCS on the disk
     * @return a new instance of the adapter
     */
    public static Vcs create(String path) throws VcsNotFound {
        final FileRepositoryBuilder builder = new FileRepositoryBuilder();
        final Repository repository;
        try {
            repository = builder.setGitDir(new File(path, ".git")).setMustExist(true).build();
        } catch (IOException e) {
            throw new VcsNotFound();
        }

        final GitVcs vcs = new GitVcs(repository);
        vcs.saveVersion();
        return vcs;
    }
}
