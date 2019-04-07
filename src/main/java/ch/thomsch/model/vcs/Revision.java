package ch.thomsch.model.vcs;

import java.util.Date;

public abstract class Revision {
    /**
     * The commit SHA-1
     */
    protected String id;
    /**
     * The author commit date
     */
    protected Date date;

    public abstract boolean isTag();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
