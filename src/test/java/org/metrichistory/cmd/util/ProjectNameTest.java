package org.metrichistory.cmd.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProjectNameTest {

    private ProjectName projectName;

    @Before
    public void setUp() throws Exception {
        projectName = new ProjectName();
    }

    @Test
    public void resolve_ShouldReturnTheLastFolderName_OnUnix() {
        projectName.resolve("/this/is/a/name");

        assertEquals("name", projectName.toString());
    }

    @Test
    public void resolve_ShouldReturnTheLastFolderName_OnWindows() {
        projectName.resolve("D:\\this\\is\\a\\name");

        assertEquals("name", projectName.toString());
    }

    @Test
    public void resolve_ShouldOverrideExistingNameByDefault() {
        final ProjectName projectName = new ProjectName("vertigo");
        projectName.resolve("/my/project/name/is/castor");

        assertEquals("castor", projectName.toString());
    }

    @Test
    public void resolve_ShouldOverrideExistingName_WhenSpecified() {
        final ProjectName projectName = new ProjectName("vertigo");
        projectName.resolve("/my/project/name/is/castor");

        assertEquals("castor", projectName.toString());
    }

    @Test
    public void resolve_ShouldKeepExistingName_WhenSpecified() {
        final ProjectName projectName = new ProjectName("vertigo");
        projectName.resolve("/my/project/name/is/castor", false);

        assertEquals("vertigo", projectName.toString());
    }

    @Test
    public void resolve_ShouldResolveName_WhenNameIsUndefinedAndNotOverride() {
        projectName.resolve("/my/project/name/is/castor", false);

        assertEquals("castor", projectName.toString());
    }

    @Test
    public void resolve_ShouldResolveName_WhenNameIsUndefinedAndOverriden() {
        projectName.resolve("/my/project/name/is/castor", true);

        assertEquals("castor", projectName.toString());
    }


    @Test(expected = IllegalArgumentException.class)
    public void resolve_ShouldThrowAnException_WhenParameterIsNull() {
        projectName.resolve(null);

        fail();
    }


    @Test(expected = IllegalArgumentException.class)
    public void resolve_ShouldThrowAnException_WhenParameterIsEmpty() {
        projectName.resolve("");

        fail();
    }
}
