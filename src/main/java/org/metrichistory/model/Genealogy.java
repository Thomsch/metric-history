package org.metrichistory.model;

import org.metrichistory.versioncontrol.VCS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Contains the revisions and their respective parents.
 */
public class Genealogy {
    private static final Logger logger = LoggerFactory.getLogger(Genealogy.class);

    private final VCS vcs;

    private final Map<String, String> model; // The key is the revision, the value is its first parent.
    private final Set<String> ignored;

    public Genealogy(VCS vcs) {
        this.vcs = vcs;

        model = new LinkedHashMap<>();
        ignored = new HashSet<>();
    }

    /**
     * Build the genealogy for the given revisions.
     * @param revisions the revisions to add to the genealogy
     */
    public void addRevisions(List<String> revisions) {
        Objects.requireNonNull(revisions);
        logger.info("Retrieving parents of {} versions", revisions.size());

        for (String revision : revisions) {
            try {
                final String parent = vcs.getParent(revision);

                if (parent == null) {
                    ignored.add(revision);
                } else {
                    model.put(revision, parent);
                }
            } catch (IOException e) {
                logger.error("I/O error prevented retrieval {}'s parent", revision);
            }
        }

        if(ignored.size() > 0) {
            logger.info("{} version{} were ignored because they have no parents", ignored.size(), ignored.size() > 1 ? "s" : "");
        }
    }

    /**
     * Determines if during the importation of the revision, some were without a parent.
     * @return <code>true</code> if some revision are missing a parent. <code>false</code> otherwise.
     */
    public boolean hasIgnoredRevisions() {
        return ignored.size() != 0;
    }

    /**
     * Returns the revisions that do not have a parent.
     */
    public List<String> getIgnoredRevisions() {
        return new ArrayList<>(ignored);
    }

    /**
     * Returns the genealogical map. Every revision in the map has a parent.
     * @return an unmodifiable view of the genealogy
     */
    public Map<Object, Object> getMap() {
        return Collections.unmodifiableMap(model);
    }

    /**
     * Returns a list of unique revisions present in the genealogy.
     * @return a list containing the unique revisions of the genealogy.
     */
    public List<String> getUniqueRevisions() {
        final Set<String> uniques = new HashSet<>();
        uniques.addAll(model.keySet());
        uniques.addAll(model.values());
        return new ArrayList<>(uniques);
    }
}
