package org.metrichistory.util;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import java.io.IOException;

public class GitUtils {

    public static RevFilter noMergeFilter(){
        return new RevFilter() {
            @Override
            public boolean include(RevWalk walker, RevCommit cmit) throws StopWalkException, MissingObjectException, IncorrectObjectTypeException, IOException {
                return cmit.getParentCount() <= 1;
            }

            @Override
            public RevFilter clone() {
                return null;
            }
        };
    }
}
