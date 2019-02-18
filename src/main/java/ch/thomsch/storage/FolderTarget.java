package ch.thomsch.storage;

import java.io.File;

import ch.thomsch.model.ClassStore;

/**
 * Export measures to a folder.
 */
public class FolderTarget extends SaveTarget {

    private final File folder;

    FolderTarget(File folder) {
        this.folder = folder;
    }

    @Override
    public void export(ClassStore classStore) {
        if(classStore.hasNoVersion()) {
            return;
        }

        if(!classStore.hasSingleVersion()) {
            throw new RuntimeException(String.format("There is more than one version to export: %d", classStore.getVersions().size()));
        }
        final String version = (String) classStore.getVersions().toArray()[0];

        final FileTarget file = new FileTarget(new File(folder, version + ".csv"));
        file.export(classStore);
    }
}
