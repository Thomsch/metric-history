package ch.thomsch.versioncontrol;

import ch.thomsch.model.vcs.Commit;
import ch.thomsch.model.vcs.CommitFactory;
import ch.thomsch.model.vcs.NullTag;
import ch.thomsch.model.vcs.Tag;
import ch.thomsch.util.DateUtils;
import ch.thomsch.util.GitUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static ch.thomsch.util.DateUtils.*;

public class GitVcs implements VCS {

    private static final Logger logger = LoggerFactory.getLogger(GitVcs.class);

    private final Repository repository;
    private String saved;

    GitVcs(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void checkout(String revision) throws GitAPIException {
        final CheckoutCommand command = new Git(repository).checkout().setName(revision).setForce(true);
        command.call();
    }

    @Override
    public void clean() {
        try {
            final Status status = new Git(repository).status().call();

            if(status.getUntracked().size() > 0) {
                new Git(repository).clean().setCleanDirectories(true).call();
            }

            if(status.getUncommittedChanges().size() > 0 || status.getConflicting().size() > 0) {
                new Git(repository).reset().setMode(ResetCommand.ResetType.HARD).call();
            }

        } catch (NoWorkTreeException e) {
            logger.error("Cannot clean a bare working directory:", e);
        } catch (GitAPIException e) {
            unexpectedGitError(e);
        }
    }

    @Override
    public void saveVersion() {
        try {
            saved = repository.getBranch();

            if (saved == null) {
                logger.warn("No reference was saved!");
            }
        } catch (IOException e) {
            logger.error("An error occurred when trying to retrieve the current branch checked out", e);
        }
    }

    @Override
    public void restoreVersion() {
        if(saved == null) {
            logger.warn("No reference was saved. Ignoring.");
            return;
        }

        try {
            clean();
            checkout(saved);
        } catch (GitAPIException e) {
            unexpectedGitError(e);
        }
    }

    @Override
    public void getChangedFiles(String revision, Collection<File> beforeFiles, Collection<File> afterFiles)
            throws IOException {
        final Git git = new Git(repository);
        final ObjectReader reader = repository.newObjectReader();

        final ObjectId revisionId = repository.resolve(revision);

        try (RevWalk walk = new RevWalk(repository)) {
            final RevCommit commit = walk.parseCommit(revisionId);

            final CanonicalTreeParser oldTree = new CanonicalTreeParser();
            final CanonicalTreeParser newTree = new CanonicalTreeParser();
            newTree.reset(reader, commit.getTree());

            walk.markStart(commit.getParent(0));

            oldTree.reset(reader, commit.getParent(0).getTree());


            final List<DiffEntry> diffEntries = git.diff().setNewTree(newTree).setOldTree(oldTree).call();

            for (DiffEntry diffEntry : diffEntries) {
                if (diffEntry.getChangeType() != DiffEntry.ChangeType.ADD) {
                    beforeFiles.add(convertPathToFile(diffEntry.getOldPath()));
                }

                if (diffEntry.getChangeType() != DiffEntry.ChangeType.DELETE) {
                    afterFiles.add(convertPathToFile(diffEntry.getNewPath()));
                }
            }

        } catch (GitAPIException e) {
            logger.error("Unable to retrieve changed files for revision {}", revision, e);
        }
    }

    @Override
    public List<Tag> listReleases() {
        Git git = new Git(repository);
        List<Tag> tagList = new ArrayList<>();
        try {
            // project start pseudo-tag
            NullTag projectStart = new NullTag();
            tagList.add(projectStart);
            // find tag references
            List<Ref> tagRefs = git.tagList().call();

            Tag previousTag = projectStart;
            for(Ref ref: tagRefs){
                RevCommit commit = repository.parseCommit(ref.getPeeledObjectId());
                OffsetDateTime commitOffsetDateTime = offsetDateTimeOf(commit.getAuthorIdent().getWhen());
                // creates a tag domain object
                Tag tag = Tag.tag(ref.getPeeledObjectId().getName(), commitOffsetDateTime, ref.getName(), previousTag);
                previousTag = tag;
                tagList.add(tag);
            }

            // find and add the master ref
            Ref masterRef = repository.findRef(Tag.MASTER_REF);
            RevCommit lastCommit = repository.parseCommit(masterRef.getObjectId());
            OffsetDateTime commitDateTime = offsetDateTimeOf(lastCommit.getAuthorIdent().getWhen());
            Tag masterRefTag = Tag.masterRef(masterRef.getObjectId().getName(), commitDateTime, previousTag);
            tagList.add(masterRefTag);

            // establish next tag association to all but the last one
            for(int i = 0; i < tagList.size() - 1; i++){
                Tag tag = tagList.get(i);
                tag.setNextTag(tagList.get(i+1));
            }

        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return tagList;
    }

    @Override
    public List<Commit> listCommitsBetweenReleases(Tag fromTag, Tag toTag) {

        List<Commit> commits = new ArrayList<>();
        Git git = new Git(repository);
        LogCommand logCommand = git.log();
        try {
            ObjectId toTagId = repository.resolve(toTag.getId());
            if (fromTag.isNull()){
                logCommand.add(toTagId);
            } else {
                ObjectId fromTagId = repository.resolve(fromTag.getId());
                logCommand.addRange(fromTagId, toTagId);
            }
            logCommand.setRevFilter(GitUtils.noMergeFilter());
            Iterable<RevCommit> revCommitIterable = logCommand.call();

            List<RevCommit> revCommits = new ArrayList<>();
            for(RevCommit commit: revCommitIterable){
                revCommits.add(commit);
            }
            // process in commit creation order
            Collections.reverse(revCommits);
            CommitFactory commitFactory = CommitFactory.fromRelease(fromTag);
            for(RevCommit revCommit: revCommits){
                OffsetDateTime dateTime = offsetDateTimeOf(revCommit.getAuthorIdent().getWhen());
                Commit commit = commitFactory.nextCommit(revCommit.getName(), dateTime);
                commits.add(commit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return commits;
    }

    /**
     * Converts a {@link DiffEntry} path to the corresponding absolute {@link File}.
     *
     * @param path the path
     * @return a new instance of the file
     */
    private File convertPathToFile(String path) {
        final String concat = FilenameUtils.concat(getDirectory(), path);
        return new File(concat);
    }

    @Override
    public String getDirectory() {
        return FilenameUtils.normalize(repository.getDirectory().getParentFile().getAbsolutePath());
    }

    @Override
    public String getParent(String revision) throws IOException {
        final ObjectId revisionId = repository.resolve(revision);
        try(RevWalk walk = new RevWalk(repository)){
            final RevCommit commit = walk.parseCommit(revisionId);

            if(commit.getParentCount() == 0) {
                return null;
            }

            final RevCommit parentRevision = commit.getParent(0);
            return parentRevision.getName();
        }
    }

    @Override
    public void close() {
        repository.close();
    }

    private void unexpectedGitError(GitAPIException e) {
        logger.error("An unexpected error occurred in git:", e);
    }
}
