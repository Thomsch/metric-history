package ch.thomsch.model.vcs;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to create domain objects for revision history tests
 */
public class RevisionHistoryHelper {

    private List<Commit> revisionHistory;

    private List<Tag> tagList;

    private CommitFactory commitFactory;
    /**
     * Sample data from retrofit project
     */
    public void prepareData(){


        tagList = new ArrayList<>();
        Tag projectStart = projectStart();
        Tag v1 = tag("76322c21acadaac608267f5e00e9af6a1e8a6797 2010-10-28T17:06:14-07:00", "0.1");
        Tag v2 = tag("d86f495fb4d907885fe9b4e9ba06532dd0aaabe9 2010-12-12T14:14:45-06:00", "0.2");
        Tag v3 = tag("288c470840037fc5cd65ff51cd4b445d6d7643b6 2011-02-21T14:37:42-06:00", "0.3");
        masterRef("5d6650a97d9c0f24526f4aeac2ae494bc601785e 2018-11-18T09:57:46-05:00");

        // git log 0.1 --pretty="%H %aI %d"
        revisionHistory = new ArrayList<>();
        commitFactory = CommitFactory.fromRelease(projectStart);
        commit("17886a10eecccada75e736cb2ffb30b8b8a58b55 2010-09-06T17:30:14-05:00", tagList.get(0));
        commit("0404ce4a2ef46e4ed9c5f06da6ebf862cc52253d 2010-10-11T14:00:17-05:00", tagList.get(0));
        commit("84665377d800695590be837655b6c8c9c94cf7f1 2010-10-25T15:57:57-07:00", tagList.get(0));

        commitFactory = CommitFactory.fromRelease(v1);
        commit("76322c21acadaac608267f5e00e9af6a1e8a6797 2010-10-28T17:06:14-07:00", tagList.get(0)); // tag v0.1 commit (3)
        commit("11da2563aaf50c42c2f175012ce86291a2b818dd 2010-10-28T17:07:54-07:00", tagList.get(1));
        commit("44e798e2834d2c2d42b8310f1838169b6310411f 2010-11-04T15:42:13-05:00", tagList.get(1));
        commit("e14a77a40ec26bc08788eb414b2ea36a25443a05 2010-11-08T18:10:19-06:00", tagList.get(1));
        commit("57a57d2c10f1bc33e2bdc0f5579201dfbd44153c 2010-11-23T17:09:12-06:00", tagList.get(1));
        commit("0edb2af09e289c4bbb2877e9c0c5168d4872fc4e 2010-12-07T10:07:07-06:00", tagList.get(1));

        commitFactory = CommitFactory.fromRelease(v2);
        commit("d86f495fb4d907885fe9b4e9ba06532dd0aaabe9 2010-12-12T14:14:45-06:00", tagList.get(1)); // tag v0.2 commit (9)
        commit("9259e3add43cb442859f3e3c9cbe2286920620e3 2010-12-12T14:14:45-06:00", tagList.get(2)); // 10
        commit("cd2285b4f34ba73bfd20fcb82bfc557bf58e6aa1 2010-12-12T17:06:26-06:00", tagList.get(2));
        commit("ab2cf4a5cfccf0c02fc0f907e3866a8b02d20af9 2011-01-20T10:09:21-08:00", tagList.get(2));
        commit("97ad14dcca125d3fff1fca7f4aaa14d2a92219a6 2011-01-20T11:30:58-08:00", tagList.get(2));
        commit("fe0d2bbf14e2c07fd0c4b5c506dfb997a0d917e8 2011-01-20T23:55:16-06:00", tagList.get(2));

        commitFactory = CommitFactory.fromRelease(v3);
        commit("288c470840037fc5cd65ff51cd4b445d6d7643b6 2011-02-21T14:37:42-06:00", tagList.get(2)); // tag v0.3 commit (15)
        commit("4a048cc03f5a851b64d9726098df49b5fa4130c6 2018-07-28T17:19:28-04:00", tagList.get(3)); // 16
        commit("c3633423b4a52566446bc6d540904f1f5f715205 2018-09-06T10:33:22-04:00", tagList.get(3));
        commit("a8e37897b3326e7748622a5867e775003bb6e15e 2018-09-22T14:40:47-04:00", tagList.get(3));
        commit("ebc0128add0d416a846ff661888910d16751dd34 2018-11-16T15:38:34-05:00", tagList.get(3));
        commit("bc620698b4e684c7991b2b26be8171803246a61f 2018-11-16T17:28:11-05:00", tagList.get(3));
        commit("5d6650a97d9c0f24526f4aeac2ae494bc601785e 2018-11-18T09:57:46-05:00", tagList.get(3)); // origin/master (21)

    }

    public List<Commit> getRevisionHistory() {
        return revisionHistory;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public static OffsetDateTime time(String isoFormattedTime){
        return OffsetDateTime.parse(isoFormattedTime);
    }

    public Tag projectStart(){
        NullTag nullTag = new NullTag();
        tagList.add(nullTag);
        return nullTag;
    }

    public Tag tag(String commitIdIsoDate, String tagName){
        String[] parts= commitIdIsoDate.split(" ");
        Tag previousTag = tagList.get(tagList.size() - 1);
        Tag tag = Tag.tag(parts[0], time(parts[1]), tagName, previousTag);
        previousTag.setNextTag(tag);
        tagList.add(tag);
        return tag;
    }

    public Tag masterRef(String commitIdIsoDate){
        String[] parts= commitIdIsoDate.split(" ");
        Tag previousTag = tagList.get(tagList.size() - 1);
        Tag tag = Tag.masterRef(parts[0], time(parts[1]), previousTag);
        previousTag.setNextTag(tag);
        tagList.add(tag);
        return tag;
    }

    public Commit commit(String commitIdIsoDate, Tag tag){
        String[] parts= commitIdIsoDate.split(" ");
        Commit c = commitFactory.nextCommit(parts[0], time(parts[1]));
        revisionHistory.add(c);
        return c;
    }

}
