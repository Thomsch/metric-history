package ch.thomsch.model.vcs;

import java.time.OffsetDateTime;

public class CommitFactory {

    private Tag startingRelease;
    private int commitSequence = 0;

    public CommitFactory(Tag tag) {
        this.startingRelease = tag;
    }

    /**
     * @param startingRelease
     * @return
     */
    public static CommitFactory fromRelease(Tag startingRelease) {
        return new CommitFactory(startingRelease);
    }

    /**
     * Create a commit that is either the startingRelease or follows it
     *
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

        commit.setLatestRelease(startingRelease);
        commit.setNextRelease(startingRelease.getNextTag());
        commit.setPostReleaseSequence(commitSequence++);
        return commit;
    }
}
