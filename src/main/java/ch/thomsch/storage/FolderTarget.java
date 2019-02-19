package ch.thomsch.storage;

import java.io.File;

import ch.thomsch.model.MeasureStore;

/**
 * Export measures to a folder.
 */
public class FolderTarget extends SaveTarget {

    private final File folder;

    FolderTarget(File folder) {
        this.folder = folder;
    }

    @Override
    public void export(MeasureStore measureStore) {
        if(measureStore.hasNoVersion()) {
            return;
        }

        if(!measureStore.hasSingleVersion()) {
            throw new RuntimeException(String.format("There is more than one version to export: %d", measureStore.versions().size()));
        }
        final String version = (String) measureStore.versions().toArray()[0];

        final FileTarget file = new FileTarget(new File(folder, version + ".csv"));
        file.export(measureStore);
    }
}
