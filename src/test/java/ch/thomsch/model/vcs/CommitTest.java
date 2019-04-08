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
        assertEquals(0, projectStart.getTagSequence());
        assertNotNull(projectStart.getNextTag());
        assertEquals(tagList.get(1), projectStart.getNextTag());
        assertEquals(projectStart, projectStart.getPreviousTag());

        Tag firstTag = tagList.get(1);
        assertEquals(1, firstTag.getTagSequence());
        assertNotNull(firstTag.getNextTag());
        assertNotNull(firstTag.getPreviousTag());

        Tag secondTag = tagList.get(2);
        assertEquals(2, secondTag.getTagSequence());
        assertNotNull(secondTag.getPreviousTag());
        assertNotNull(secondTag.getNextTag());
        assertEquals(firstTag, secondTag.getPreviousTag());
        assertEquals(tagList.get(3), secondTag.getNextTag());

        Tag thirdTag = tagList.get(3);
        assertEquals(3, thirdTag.getTagSequence());
        assertEquals(secondTag, thirdTag.getPreviousTag());
        assertEquals(tagList.get(4), thirdTag.getNextTag());

        Tag masterTag = tagList.get(tagList.size() - 1);
        assertEquals(4, masterTag.getTagSequence());
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
    public void testCommitInfo_startToV1(){

        for(int i = 0; i < 3; i++){
            Commit commit = commits.get(i);
            assertEquals(i, commit.getPostReleaseSequence());
        }

        assertEquals(0, commits.get(0).getPostReleaseDays());
        assertEquals(3, commits.get(0).getCommitsToNextRelease());
        assertEquals(52, commits.get(0).getDaysToNextRelease());

        assertEquals(35, commits.get(1).getPostReleaseDays());
        assertEquals(2, commits.get(1).getCommitsToNextRelease());
        assertEquals(17, commits.get(1).getDaysToNextRelease());

        assertEquals(49, commits.get(2).getPostReleaseDays());
        assertEquals(1, commits.get(2).getCommitsToNextRelease());
        assertEquals(3, commits.get(2).getDaysToNextRelease());

        Commit tagV1Commit = commits.get(3);
        assertEquals(0, tagV1Commit.getPostReleaseSequence()); // sequence is reset
        assertEquals(0, tagV1Commit.getPostReleaseDays());
        assertEquals(6, tagV1Commit.getCommitsToNextRelease());
        assertEquals(45, tagV1Commit.getDaysToNextRelease());
    }


    @Test
    public void testCommitInfo_v1ToV2(){

        for(int i = 4; i < 9; i++){
            Commit commit = commits.get(i);
            assertEquals(i - 3, commit.getPostReleaseSequence());
        }

        assertEquals(0, commits.get(4).getPostReleaseDays());
        assertEquals(5, commits.get(4).getCommitsToNextRelease());
        assertEquals(45, commits.get(4).getDaysToNextRelease());

        assertEquals(7, commits.get(5).getPostReleaseDays());
        assertEquals(4, commits.get(5).getCommitsToNextRelease());
        assertEquals(38, commits.get(5).getDaysToNextRelease());

        assertEquals(11, commits.get(6).getPostReleaseDays());
        assertEquals(3, commits.get(6).getCommitsToNextRelease());
        assertEquals(34, commits.get(6).getDaysToNextRelease());

        Commit tagV2Commit = commits.get(9);
        assertEquals(0, tagV2Commit.getPostReleaseSequence()); // sequence is reset
        assertEquals(0, tagV2Commit.getPostReleaseDays());
        assertEquals(6, tagV2Commit.getCommitsToNextRelease());
    }

    @Test
    public void testCommitInfo_afterLastRelease(){

        Commit tagV3Commit = commits.get(15);
        assertEquals(0, tagV3Commit.getPostReleaseSequence());
        assertEquals(0, tagV3Commit.getPostReleaseDays());
        assertEquals(6, tagV3Commit.getCommitsToNextRelease());
        assertEquals(120, tagV3Commit.getDaysToNextRelease());

        for(int i = 16; i < 21; i++){
            Commit commit = commits.get(i);
            assertEquals(i - 15, commit.getPostReleaseSequence());
        }

        assertEquals(7, commits.get(16).getPostReleaseDays());
        assertEquals(5, commits.get(16).getCommitsToNextRelease());
        assertEquals(113, commits.get(16).getDaysToNextRelease());

        assertEquals(47, commits.get(17).getPostReleaseDays());
        assertEquals(4, commits.get(17).getCommitsToNextRelease());
        assertEquals(73, commits.get(17).getDaysToNextRelease());

        Commit lastCommit = commits.get(21);
        assertEquals(0, lastCommit.getPostReleaseSequence());
        // the next release requires at least 1 more commit (not yet available)
        assertEquals(1, lastCommit.getCommitsToNextRelease());

        try {
            assertEquals(53, lastCommit.getDaysToNextRelease());
            fail();
        } catch (IllegalStateException e){

        }
    }

}