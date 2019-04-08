package ch.thomsch.model.vcs;

import java.time.OffsetDateTime;

public class Commit extends Revision {

    /**
     * Sequence of the commit after the latest release (tag)
     * A tag commit has postReleaseSequence = 0
     */
    protected int postReleaseSequence;
    /**
     * Number of days since the latest release (tag)
     * A tag commit has postReleaseDays = 0
     */
    protected int postReleaseDays;

    /**
     * Number of commits till the next release (tag)
     * The next tag is assumed for a tag commit
     */
    protected int commitsToNextRelease;

    /**
     * Number of days till the next release (tag)
     * The next tag is assumed for a tag commit
     */
    protected int daysToNextRelease;

    /**
     * The tag corresponding the forthcoming release for the current commit.
     */
    protected Tag nextRelease;

    /**
     * The tag corresponding to the latest release for the current commit.
     * The latestRelease of a tag commit is the current tag.
     *
     * The first commits of the project have a null value in this field
     */
    protected Tag latestRelease;

    public Commit(String id, OffsetDateTime date) {
        super(id, date);
    }

    @Override
    public boolean isTag(){
        return false;
    }

    public void setNextRelease(Tag nextRelease) {
        this.nextRelease = nextRelease;
    }

    public void setLatestRelease(Tag latestRelease) {
        this.latestRelease = latestRelease;
    }

    public Tag getNextRelease() {
        return nextRelease;
    }

    public Tag getLatestRelease() {
        return latestRelease;
    }

    public int getPostReleaseSequence() {
        return postReleaseSequence;
    }

    public void setPostReleaseSequence(int postReleaseSequence) {
        this.postReleaseSequence = postReleaseSequence;
    }

    public int getPostReleaseDays() {
        if (latestRelease == null){
            throw new IllegalStateException("Latest release not set");
        }
        long latestReleaseEpochDays = latestRelease.getDate().toLocalDate().toEpochDay();
        long commitEpochDays = date.toLocalDate().toEpochDay();
        long days = commitEpochDays - latestReleaseEpochDays;
        return (int)days;
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
