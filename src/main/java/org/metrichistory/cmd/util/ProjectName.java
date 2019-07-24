package org.metrichistory.cmd.util;

import org.metrichistory.storage.DiskUtils;

import java.io.File;

/**
 * Extract the name of a project from various sources.
 */
public class ProjectName {

    private String name;

    public ProjectName(String name) {
        this.name = name;
    }

    public ProjectName() {
        this("");
    }

    /**
     * Resolve the name of the project from the repository's path.
     * @param repositoryPath Repository's path
     * @throws IllegalArgumentException if the path doesn't contain the project's name.
     */
    public void resolve(String repositoryPath) {
        resolve(repositoryPath, true);
    }

    /**
     * Resolve the name of the project from the repository's path.
     * @param repositoryPath Repository's path
     * @param override Overrides the current project's name.
     * @throws IllegalArgumentException if the path doesn't contain the project's name.
     */
    public void resolve(String repositoryPath, boolean override) {
        if(exists(name) && !override){
            return;
        }

        if(repositoryPath == null || repositoryPath.isEmpty()) {
            resolveFailed(repositoryPath);
        }

        repositoryPath = DiskUtils.normalizePath(repositoryPath);
        final String result = new File(repositoryPath).getName();

        if(result.isEmpty()) {
            resolveFailed(repositoryPath);
        }

        name = result;
    }

    private boolean exists(String name) {
        return name != null && !name.isEmpty();
    }

    @Override
    public String toString() {
        return name;
    }

    private void resolveFailed(String repositoryPath) {
        throw new IllegalArgumentException(String.format("The project's name cannot be resolved from %s.",
                repositoryPath));
    }
}
