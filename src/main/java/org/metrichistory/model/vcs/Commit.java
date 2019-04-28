package org.metrichistory.model.vcs;

import java.time.OffsetDateTime;

public class Commit extends Revision {

    private int commitSequence;

    /**
     * The tag corresponding the forthcoming release for the current commit.
     */
    private Tag nextRelease;

    /**
     * The tag corresponding to the previous release for the current commit.
     */
    private Tag previousRelease;

    public Commit(String id, OffsetDateTime date) {
        super(id, date);
    }

    @Override
    public boolean isTag() {
        return false;
    }

    protected void setNextRelease(Tag nextRelease) {
        if (nextRelease == null) {
            throw new IllegalArgumentException("next release should not be null");
        }
        if (this.nextRelease != null) {
            this.nextRelease.commits.remove(this);
        }
        this.nextRelease = nextRelease;
        this.nextRelease.commits.add(this);

    }

    protected void setPreviousRelease(Tag previousRelease) {
        this.previousRelease = previousRelease;
    }

    public Tag getNextRelease() {
        return nextRelease;
    }

    public Tag getPreviousRelease() {
        return previousRelease;
    }

    protected void setCommitSequence(int commitSequence) {
        this.commitSequence = commitSequence;
    }

    /**
     * Sequence of the commit towards the next release.
     * It starts from 1 denoting the first commit after the latest release
     */
    public int getCommitSequence() {
        return commitSequence;
    }

    /**
     * Number of commits till the next release (tag)
     * The next tag is assumed for a tag commit
     */
    public int getCommitsToNextRelease() {
        int nextReleaseCommitCount = nextRelease.getCommitCount();
        return nextReleaseCommitCount - commitSequence;
    }

    /**
     * Number of days since the latest release (tag)
     * A tag commit has postReleaseDays = 0
     */
/*    public int getPostReleaseDays() {
        if (previousRelease == null) {
            throw new IllegalStateException("Latest release not set");
        }
        long latestReleaseEpochDays = previousRelease.getDate().toLocalDate().toEpochDay();
        long commitEpochDays = date.toLocalDate().toEpochDay();
        long days = commitEpochDays - latestReleaseEpochDays;
        return (int) days;
    }*/

    /**
     * Number of days till the next release (tag)
     * The next tag is assumed for a tag commit
     */
    public int getDaysToNextRelease() {
        if (nextRelease == null){
            throw new IllegalStateException(("Next release is not available"));
        }
        long nextReleaseEpochDays = nextRelease.getDate().toLocalDate().toEpochDay();
        long commitEpochDays = date.toLocalDate().toEpochDay();
        long days = nextReleaseEpochDays - commitEpochDays;
        return (int) days;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        return buffer.append("Commit\t")
                .append(id + "\t")
                .append(nextRelease.getTagName() + "\t")
                .append(date)
                .toString();

    }

    public int getNextReleaseDuration() {
        return nextRelease.getDuration();
    }
}
