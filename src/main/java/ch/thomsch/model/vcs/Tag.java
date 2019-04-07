package ch.thomsch.model.vcs;

public class Tag extends Revision {

    public static final String MASTER = "master";

    /**
     * Tag name, for tagged revisions
     */
    private String tagName = "";

    /**
     * The sequence of the tag in the revision history
     */
    private int tagSequence = 0;

    /**
     * The tag that follows the current tag
     * It points to the latest commit of the master branch (name = master) for the last tag
     */
    private Tag nextTag;

    /**
     * The tag that precedes the current tag.
     * It has a null value for the first tag (FIXME)
     */
    private Tag previousTag;

    @Override
    public boolean isTag() {
        return true;
    }

    public int getTagSequence() {
        return tagSequence;
    }

}
