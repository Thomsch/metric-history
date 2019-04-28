package org.metrichistory.model.vcs;

import java.time.OffsetDateTime;

public class CommitFactory {

    private Tag startingRelease;
    private int commitSequence = 1;

    public CommitFactory(Tag tag) {
        this.startingRelease = tag;
    }

    /**
     * @param startingRelease
     * @return
     */
    public static CommitFactory towardsRelease(Tag startingRelease) {
        return new CommitFactory(startingRelease);
    }

    /**
     * Create the next commit starting from the first commit
     * till the commit that is tagged as a release
     * @param commitId
     * @param commitDate
     * @return
     */
    public Commit nextCommit(String commitId, OffsetDateTime commitDate) {

        if (commitId == null || commitDate == null) {
            return null;
        }

        Commit commit = new Commit(commitId, commitDate);

        // project start
        if (startingRelease.isNull() && commitSequence == 0) {
            // update release date from first commit
            startingRelease.setDate(commitDate);
        }

        commit.setPreviousRelease(startingRelease.getPreviousTag());
        commit.setNextRelease(startingRelease);
        commit.setCommitSequence(commitSequence++);
        return commit;
    }
}
