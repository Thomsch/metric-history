package org.metrichistory.cmd.util;

import org.metrichistory.storage.DiskUtils;

import java.io.File;
import java.util.function.Supplier;

/**
 * Deduces the name of a project using the root folder's name.
 */
public class ProjectNameResolver implements Supplier<String> {
    private final String projectPath;

    public ProjectNameResolver(String projectPath) {
        this.projectPath = projectPath;
    }

    @Override
    public String get() {
        if(projectPath == null || projectPath.isEmpty()) {
            resolveFailed(projectPath);
        }

        final String normalizedPath = DiskUtils.normalizePath(projectPath);
        final String result = new File(normalizedPath).getName();

        if(result.isEmpty()) {
            resolveFailed(normalizedPath);
        }

        return result;
    }

    private void resolveFailed(String path) {
        throw new IllegalArgumentException(String.format("The project's name cannot be resolved from %s.", path));
    }
}
