package ch.thomsch.model.vcs;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tag extends Revision {

    public static String masterBranchRef = "refs/heads/master";

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
     */
    protected Tag previousTag;

    /**
     * The part of revision history that contributed to the current release.
     * It excludes commits from previous releases.
     *
     * Thus, if current release is v2 and previous release is v1, then the field
     * contains the result of <code>git log v1..v2 --no-merges</code>
     */
    protected List<Commit> commits = new ArrayList<>();

    public Tag(String id, OffsetDateTime date, String tagName, int sequence) {
        super(id, date);
        if (tagName.contains(TAG_REF_PREFIX)){
            tagRef = tagName;
            tagName = tagName.replace(TAG_REF_PREFIX, "");
        } else {
            this.tagRef = tagName.equals("master") ? masterBranchRef : TAG_REF_PREFIX + tagName;
        }
        this.tagSequence = sequence;
        this.tagName = tagName;
    }

    public boolean isMasterRef(){
        return tagRef.equals(masterBranchRef);
    }

    /**
     * Commit count
     * @return
     */
    public int getCommitCount(){
        return commits.size();
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

    protected void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagRef() {
        return tagRef;
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

    /**
     * Set master branch name (default master)
     * @param masterBranchName
     */
    public static void setMasterBranch(String masterBranchName) {
        Tag.masterBranchRef = "refs/heads/" + masterBranchName;
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

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[Tag ");
        buffer.append(tagName);
        buffer.append(" ref: ");
        buffer.append(tagRef);
        buffer.append(" commitId: ");
        buffer.append(id);
        buffer.append(" date: ");
        buffer.append(date != null ? date : "-");
        buffer.append(" previous: ");
        buffer.append(getPreviousTag().isNull() ? "-" : previousTag.getTagName());
        buffer.append(" next: ");
        buffer.append(nextTag != null ? nextTag.getTagName() : "-");
        return buffer.toString();
    }
}
