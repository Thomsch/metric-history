package ch.thomsch.model.vcs;

import java.time.OffsetDateTime;

public abstract class Revision {

    public Revision(String id, OffsetDateTime date) {
        this.id = id;
        this.date = date;
    }
    /**
     * The commit SHA-1
     */
    protected String id;
    /**
     * The author commit date
     */
    protected OffsetDateTime date;

    public abstract boolean isTag();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }
}
