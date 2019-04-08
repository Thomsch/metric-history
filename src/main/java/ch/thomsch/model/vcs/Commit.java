package ch.thomsch.model.vcs;

import java.time.OffsetDateTime;

public class Commit extends Revision {

    private int postReleaseSequence;

    /**
     * The tag corresponding the forthcoming release for the current commit.
     */
    private Tag nextRelease;

    /**
     * The tag corresponding to the latest release for the current commit.
     * The latestRelease of a tag commit is the current tag.
     * <p>
     * The first commits of the project have a null value in this field
     */
    private Tag latestRelease;

    public Commit(String id, OffsetDateTime date) {
        super(id, date);
    }

    @Override
    public boolean isTag() {
        return false;
    }

    protected void setNextRelease(Tag nextRelease) {
        this.nextRelease = nextRelease;
    }

    protected void setLatestRelease(Tag latestRelease) {
        if (latestRelease == null) {
            throw new IllegalArgumentException("latest release should not be null");
        }
        if (this.latestRelease != null) {
            this.latestRelease.postReleaseCommits.remove(this);
        }
        this.latestRelease = latestRelease;
        this.latestRelease.postReleaseCommits.add(this);
    }

    public Tag getNextRelease() {
        return nextRelease;
    }

    public Tag getLatestRelease() {
        return latestRelease;
    }

    protected void setPostReleaseSequence(int postReleaseSequence) {
        this.postReleaseSequence = postReleaseSequence;
    }

    /**
     * Sequence of the commit after the latest release (tag)
     * A tag commit has postReleaseSequence = 0
     */
    public int getPostReleaseSequence() {
        return postReleaseSequence;
    }

    /**
     * Number of commits till the next release (tag)
     * The next tag is assumed for a tag commit
     */
    public int getCommitsToNextRelease() {
        int postReleaseCommits = latestRelease.getPostReleaseCommitCount();
        return postReleaseCommits - postReleaseSequence;
    }

    /**
     * Number of days since the latest release (tag)
     * A tag commit has postReleaseDays = 0
     */
    public int getPostReleaseDays() {
        if (latestRelease == null) {
            throw new IllegalStateException("Latest release not set");
        }
        long latestReleaseEpochDays = latestRelease.getDate().toLocalDate().toEpochDay();
        long commitEpochDays = date.toLocalDate().toEpochDay();
        long days = commitEpochDays - latestReleaseEpochDays;
        return (int) days;
    }

    /**
     * Number of days till the next release (tag)
     * The next tag is assumed for a tag commit
     */
    public int getDaysToNextRelease() {
        return -1;
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
}
