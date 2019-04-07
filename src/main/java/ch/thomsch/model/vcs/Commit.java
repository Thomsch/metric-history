package ch.thomsch.model.vcs;

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

    @Override
    public boolean isTag(){
        return false;
    }

}
