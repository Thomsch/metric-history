package ch.thomsch.model.vcs;

import java.time.OffsetDateTime;
import java.util.Objects;

public class Tag extends Revision {

    public static final String MASTER_REF = "refs/heads/master";

    public static final String TAG_REF_PREFIX = "refs/tags/";
    /**
     * Tag name, for tagged revisions (just the label, not the full reference)
     */
    private String tagName = "";

    private String tagRef;
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

    public Tag(String id, OffsetDateTime date, String tagName, int sequence) {
        super(id, date);
        this.tagRef = TAG_REF_PREFIX + tagName;
        this.tagSequence = sequence;
        this.tagName = tagName;
    }

    public boolean isMasterRef(){
        return tagRef.equals(MASTER_REF);
    }

    @Override
    public boolean isTag() {
        return true;
    }

    public int getTagSequence() {
        return tagSequence;
    }

    public Tag getNextTag() {
        return nextTag;
    }

    public void setNextTag(Tag nextTag) {
        this.nextTag = nextTag;
    }

    public Tag getPreviousTag() {
        return previousTag;
    }

    public void setPreviousTag(Tag previousTag) {
        this.previousTag = previousTag;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public static Tag firstTag(String commitId, OffsetDateTime commitDate, String tagName){
        Tag tag = new Tag(commitId, commitDate, tagName, 1);
        // FIXME: use null object
        tag.setPreviousTag(null);
        return tag;
    }

    public static Tag masterRef(String commitId, OffsetDateTime commitDate, Tag previousTag){
        Tag tag = new Tag(commitId, commitDate, "master", previousTag.tagSequence + 1);
        tag.setPreviousTag(previousTag);
        return tag;
    }

    public static Tag intermediateTag(String commitId, OffsetDateTime commitDate, String tagName, Tag previousTag){
        Tag tag = new Tag(commitId, commitDate, tagName, previousTag.tagSequence + 1);
        tag.setPreviousTag(previousTag);
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return tagRef.equals(tag.tagRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagRef);
    }
}
