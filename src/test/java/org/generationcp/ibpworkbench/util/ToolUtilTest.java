
package org.generationcp.ibpworkbench.util;

import java.io.File;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/12/2015 Time: 11:10 AM
 */

@RunWith(MockitoJUnitRunner.class)
public class ToolUtilTest {

	public static final String DUMMY_INSTALLATION_PATH = "C:/BMS_TEST";
	public static final String DUMMY_TOOL_NAME = "DummyTool";
	public static final String DUMMY_TOOL_TITLE = "DummyTitle";
	public static final String DUMMY_NATIVE_TOOL_PATH = "c:/Breeding Management System/tools/dummyTool/dummyTool.exe";
	public static final String DUMMY_NON_NATIVE_TOOL_PATH = "http://localhost:8081/Fieldbook/someTool";

	public static final String DUMMY_SQL_USERNAME = "username";
	public static final String DUMMY_SQL_PASSWORD = "password";

	public static final String DUMMY_WORKBENCH_DB_NAME = "workbench";

	public static final String ANY_STRING = "any";

	public static final long DUMMY_PROJECT_ID = 1;
	public static final String DUMMY_PROJECT_NAME = "Maize Tutorial Program";

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@InjectMocks
	private ToolUtil toolUtil;
	
	private String currentRandomDirectory;

	@Before
	public void setUp() throws Exception {
		this.toolUtil.setWorkbenchInstallationDirectory(ToolUtilTest.DUMMY_INSTALLATION_PATH);
		
		this.currentRandomDirectory = ToolUtilTest.DUMMY_INSTALLATION_PATH  + (int) (Math.random() * 1000);
		final WorkbenchSetting setting = new WorkbenchSetting();
		setting.setInstallationDirectory(this.currentRandomDirectory);
		Mockito.doReturn(setting).when(this.workbenchDataManager).getWorkbenchSetting();
	}

	@Test
	public void testComputeToolPath() {
		Tool tool = this.constructDummyNativeTool();
		String path = this.toolUtil.getComputedToolPath(tool);

		Assert.assertNotNull(path);
		Assert.assertTrue("ToolUtil unable to provide the correct tool path", path.contains(ToolUtilTest.DUMMY_INSTALLATION_PATH));
	}

	@Test
	public void testComputeToolPathNonNative() {
		Tool tool = new Tool(ToolUtilTest.DUMMY_TOOL_NAME, ToolUtilTest.DUMMY_TOOL_TITLE, ToolUtilTest.DUMMY_NON_NATIVE_TOOL_PATH);

		String path = this.toolUtil.getComputedToolPath(tool);

		Assert.assertNotNull(path);
		Assert.assertFalse("ToolUtil unable to provide the correct tool path", path.contains(ToolUtilTest.DUMMY_INSTALLATION_PATH));
		Assert.assertEquals("ToolUtil should not modify this tool path", tool.getPath(), path);
	}
	
	@Test
	public void testCreateWorkspaceDirectoriesForProject() {
		final Project project = this.createTestProject();
		this.toolUtil.createWorkspaceDirectoriesForProject(project);
		
		final File projectWorkspaceDirectory = new File(this.currentRandomDirectory + File.separator + ToolUtil.WORKSPACE_DIR, DUMMY_PROJECT_NAME);
		Assert.assertTrue(projectWorkspaceDirectory.exists());
		// Check that only "breeding_view" directory is under program
		Assert.assertEquals(1, projectWorkspaceDirectory.list().length);
		final File breedingViewDirectory = new File(projectWorkspaceDirectory, ToolEnum.BREEDING_VIEW.getToolName());
		Assert.assertTrue(breedingViewDirectory.exists());
		final File bvInputDirectory = new File(breedingViewDirectory, ToolUtil.INPUT);
		Assert.assertTrue(bvInputDirectory.exists());
		final File bvOutputDirectory = new File(breedingViewDirectory, ToolUtil.OUTPUT);
		Assert.assertTrue(bvOutputDirectory.exists());
		
		// Delete test installation directory and its contents as part of cleanup
		final File testInstallationDirectory = new File(this.currentRandomDirectory);
		this.recursiveFileDelete(testInstallationDirectory);
	}
	
	@Test
	public void testCreateWorkspaceDirectoriesForProjectWhenDirectoryAlreadyExists() {
		// Already create project directory. Test method should not continue with creating sub-contents
		final File projectWorkspaceDirectory = new File(this.currentRandomDirectory + File.separator + ToolUtil.WORKSPACE_DIR, DUMMY_PROJECT_NAME);
		projectWorkspaceDirectory.mkdirs();
		final Project project = this.createTestProject();
		this.toolUtil.createWorkspaceDirectoriesForProject(project);
		
		Assert.assertTrue(projectWorkspaceDirectory.exists());
		// Check that "breeding_view" directory and sub-folders will not be created anymore
		final File breedingViewDirectory = new File(projectWorkspaceDirectory, ToolEnum.BREEDING_VIEW.getToolName());
		Assert.assertFalse(breedingViewDirectory.exists());
		final File bvInputDirectory = new File(breedingViewDirectory, ToolUtil.INPUT);
		Assert.assertFalse(bvInputDirectory.exists());
		final File bvOutputDirectory = new File(breedingViewDirectory, ToolUtil.OUTPUT);
		Assert.assertFalse(bvOutputDirectory.exists());
		
		// Delete test installation directory and its contents as part of cleanup
		final File testInstallationDirectory = new File(this.currentRandomDirectory);
		this.recursiveFileDelete(testInstallationDirectory);
	}
	
	private void recursiveFileDelete(File file) {
        //to end the recursive loop
        if (!file.exists())
            return;
         
        //if directory, go inside and call recursively
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                //call recursively
                recursiveFileDelete(f);
            }
        }
        //call delete to delete files and empty directory
        file.delete();
    }

	private Project createTestProject() {
		final Project project = new Project();
		project.setProjectId(DUMMY_PROJECT_ID);
		project.setProjectName(DUMMY_PROJECT_NAME);
		return project;
	}

	protected Tool constructDummyNativeTool() {
		return new Tool(ToolName.GDMS.name(), ToolUtilTest.DUMMY_TOOL_TITLE, ToolUtilTest.DUMMY_NATIVE_TOOL_PATH);
	}
}
