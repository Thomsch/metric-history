package ch.thomsch.model.vcs;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalField;
import java.util.List;

import static org.junit.Assert.*;

public class CommitTest {

    RevisionHistoryHelper helper;

    @Before
    public void setup(){
        helper = new RevisionHistoryHelper();
        helper.prepareData();
    }

    @Test
    public void testFixtureSetup(){
        List<Tag> tagList = helper.getTagList();
        Tag firstTag = tagList.get(0);
        assertNotNull(firstTag.getNextTag());
        assertNull(firstTag.getPreviousTag());

        Tag secondTag = tagList.get(1);
        assertNotNull(secondTag.getPreviousTag());
        assertNotNull(secondTag.getNextTag());
        assertEquals(secondTag.getPreviousTag(), firstTag);
        assertEquals(secondTag.getNextTag(), tagList.get(2));

        Tag thirdTag = tagList.get(2);
        assertEquals(thirdTag.getPreviousTag(), secondTag);
        assertEquals(thirdTag.getNextTag(), tagList.get(3));

        Tag masterTag = tagList.get(tagList.size() - 1);
        assertNotNull(masterTag.getPreviousTag());
        assertNull(masterTag.getNextTag());
        assertEquals(masterTag.getPreviousTag(), tagList.get(tagList.size() - 2));

        List<Commit> commits = helper.getRevisionHistory();
        Commit firstCommit = commits.get(0);
        assertNull(firstCommit.getLatestRelease());
        assertEquals(firstCommit.getNextRelease(), firstTag);

        Commit firstTagCommit = commits.get(3);
        assertEquals(firstTagCommit.getLatestRelease(), firstTag);
        assertEquals(firstTagCommit.getNextRelease(), secondTag);
        assertEquals(firstTagCommit.getId(), firstTag.getId());

        Commit postFirstVersionCommit = commits.get(4);
        assertEquals(postFirstVersionCommit.getLatestRelease(), firstTag);
        assertEquals(postFirstVersionCommit.getNextRelease(), secondTag);

        Commit postSecondVersionCommit = commits.get(11);
        assertEquals(postSecondVersionCommit.getLatestRelease(), secondTag);
        assertEquals(postSecondVersionCommit.getNextRelease(), thirdTag);

    }
    
}