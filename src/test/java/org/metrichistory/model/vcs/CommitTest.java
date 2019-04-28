package org.metrichistory.model.vcs;

import org.junit.Before;
import org.junit.Test;

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
        assertNotNull(firstCommit.getPreviousRelease());
        assertEquals(firstCommit.getNextRelease(), firstTag);

        Commit firstTagCommit = commits.get(3);
        assertEquals(firstTagCommit.getPreviousRelease(), projectStart);
        assertEquals(firstTagCommit.getNextRelease(), firstTag);
        assertEquals(firstTagCommit.getId(), firstTag.getId());

        Commit postFirstVersionCommit = commits.get(4);
        assertEquals(postFirstVersionCommit.getPreviousRelease(), firstTag);
        assertEquals(postFirstVersionCommit.getNextRelease(), secondTag);

        Commit postSecondVersionCommit = commits.get(11);
        assertEquals(postSecondVersionCommit.getPreviousRelease(), secondTag);
        assertEquals(postSecondVersionCommit.getNextRelease(), thirdTag);

    }

    @Test
    public void testCommitInfo_startToV1(){

        for(int i = 0; i < 3; i++){
            Commit commit = commits.get(i);
            assertEquals(i + 1, commit.getCommitSequence());
        }

        assertEquals(3, commits.get(0).getCommitsToNextRelease());
        assertEquals(52, commits.get(0).getDaysToNextRelease());

        assertEquals(2, commits.get(1).getCommitsToNextRelease());
        assertEquals(17, commits.get(1).getDaysToNextRelease());

        assertEquals(1, commits.get(2).getCommitsToNextRelease());
        assertEquals(3, commits.get(2).getDaysToNextRelease());

        Commit tagV1Commit = commits.get(3);
        assertEquals(4, tagV1Commit.getCommitSequence());
        assertEquals(0, tagV1Commit.getCommitsToNextRelease());
        assertEquals(0, tagV1Commit.getDaysToNextRelease());
    }


    @Test
    public void testCommitInfo_v1ToV2(){

        for(int i = 4; i < 9; i++){
            Commit commit = commits.get(i);
            assertEquals(i - 3, commit.getCommitSequence());
        }


        assertEquals(5, commits.get(4).getCommitsToNextRelease());
        assertEquals(45, commits.get(4).getDaysToNextRelease());


        assertEquals(4, commits.get(5).getCommitsToNextRelease());
        assertEquals(38, commits.get(5).getDaysToNextRelease());


        assertEquals(3, commits.get(6).getCommitsToNextRelease());
        assertEquals(34, commits.get(6).getDaysToNextRelease());

        Commit tagV2Commit = commits.get(9);
        assertEquals(6, tagV2Commit.getCommitSequence());
        assertEquals(0, tagV2Commit.getCommitsToNextRelease());
    }

    @Test
    public void testCommitInfo_afterLastRelease(){

        Commit tagV3Commit = commits.get(15);
        assertEquals(6, tagV3Commit.getCommitSequence());
        assertEquals(0, tagV3Commit.getCommitsToNextRelease());
        assertEquals(0, tagV3Commit.getDaysToNextRelease());

        for(int i = 16; i < 21; i++){
            Commit commit = commits.get(i);
            assertEquals(i - 15, commit.getCommitSequence());
        }

        assertEquals(5, commits.get(16).getCommitsToNextRelease());
        assertEquals(113, commits.get(16).getDaysToNextRelease());

        assertEquals(4, commits.get(17).getCommitsToNextRelease());
        assertEquals(73, commits.get(17).getDaysToNextRelease());

        Commit lastCommit = commits.get(21);
        assertEquals(6, lastCommit.getCommitSequence());
        // the next release requires at least 1 more commit (not yet available)
        assertEquals(0, lastCommit.getCommitsToNextRelease());
        assertEquals(0, lastCommit.getDaysToNextRelease());

    }

}
