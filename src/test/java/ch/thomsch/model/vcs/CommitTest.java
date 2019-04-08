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
    private List<Tag> tagList;
    private List<Commit> commits;

    @Before
    public void setup(){
        helper = new RevisionHistoryHelper();
        helper.prepareData();
        tagList = helper.getTagList();
        commits = helper.getRevisionHistory();
    }

    @Test
    public void testFixtureSetup(){

        Tag projectStart = tagList.get(0);
        assertNotNull(projectStart.getNextTag());
        assertEquals(tagList.get(1), projectStart.getNextTag());
        assertEquals(projectStart, projectStart.getPreviousTag());

        Tag firstTag = tagList.get(1);
        assertNotNull(firstTag.getNextTag());
        assertNotNull(firstTag.getPreviousTag());

        Tag secondTag = tagList.get(2);
        assertNotNull(secondTag.getPreviousTag());
        assertNotNull(secondTag.getNextTag());
        assertEquals(firstTag, secondTag.getPreviousTag());
        assertEquals(tagList.get(3), secondTag.getNextTag());

        Tag thirdTag = tagList.get(3);
        assertEquals(secondTag, thirdTag.getPreviousTag());
        assertEquals(tagList.get(4), thirdTag.getNextTag());

        Tag masterTag = tagList.get(tagList.size() - 1);
        assertNotNull(masterTag.getPreviousTag());
        assertNull(masterTag.getNextTag());
        assertEquals(tagList.get(tagList.size() - 2), masterTag.getPreviousTag());

        Commit firstCommit = commits.get(0);
        assertNotNull(firstCommit.getLatestRelease());
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

    @Test
    public void testCommitInfo_toFirstRelease(){

        for(int i = 0; i < 3; i++){
            Commit commit = commits.get(i);
            assertEquals(i, commit.getPostReleaseSequence());
        }
        Commit firstReleaseCommit = commits.get(3);
        assertEquals(0, firstReleaseCommit.getPostReleaseSequence()); // sequence is reset
        assertEquals(0, commits.get(0).getPostReleaseDays());
    }


    @Test
    public void testCommitInfo_toSecondRelease(){

        for(int i = 4; i < 9; i++){
            Commit commit = commits.get(i);
            assertEquals(i - 3, commit.getPostReleaseSequence());
        }
        Commit secondReleaseCommit = commits.get(9);
        assertEquals(0, secondReleaseCommit.getPostReleaseSequence()); // sequence is reset

    }

    @Test
    public void testCommitInfo_afterLastRelease(){

        for(int i = 16; i < 21; i++){
            Commit commit = commits.get(i);
            assertEquals(i - 15, commit.getPostReleaseSequence());
        }
        Commit secondReleaseCommit = commits.get(21);
        assertEquals(6, secondReleaseCommit.getPostReleaseSequence());

    }

}