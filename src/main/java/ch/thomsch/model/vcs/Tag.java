package ch.thomsch.model.vcs;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tag extends Revision {

    public static final String MASTER_REF = "refs/heads/master";

    public static final String TAG_REF_PREFIX = "refs/tags/";
    /**
     * Tag name, for tagged revisions (just the label, not the full reference)
     */
    protected String tagName = "";

    protected String tagRef;

    /**
     * The sequence of the tag in the revision history
     */
    protected int tagSequence = 0;

    /**
     * The tag that follows the current tag
     * It points to the latest commit of the master branch (name = master) for the last tag
     */
    protected Tag nextTag;

    /**
     * The tag that precedes the current tag.
     * It has a null value for the first tag (FIXME)
     */
    protected Tag previousTag;

    protected List<Commit> postReleaseCommits = new ArrayList<>();

    public Tag(String id, OffsetDateTime date, String tagName, int sequence) {
        super(id, date);
        this.tagRef = TAG_REF_PREFIX + tagName;
        this.tagSequence = sequence;
        this.tagName = tagName;
    }

    public boolean isMasterRef(){
        return tagRef.equals(MASTER_REF);
    }

    /**
     * Commit count starting from the current release and extending (without including)
     * the next release
     * @return
     */
    public int getPostReleaseCommitCount(){
        return postReleaseCommits.size();
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

    public static Tag masterRef(String commitId, OffsetDateTime commitDate, Tag previousTag){
        Tag tag = new Tag(commitId, commitDate, "master", previousTag.tagSequence + 1);
        tag.setPreviousTag(previousTag);
        return tag;
    }

    public static Tag tag(String commitId, OffsetDateTime commitDate, String tagName, Tag previousTag){
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

    public boolean isNull(){
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagRef);
    }
}
