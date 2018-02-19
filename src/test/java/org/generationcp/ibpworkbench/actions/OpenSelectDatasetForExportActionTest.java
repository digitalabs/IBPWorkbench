/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.breedingview.xml.ProjectType;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ContentWindow;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.StudyUtil;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;

public class OpenSelectDatasetForExportActionTest {

	private static final String STUDY_NAME = "TRIAL 88";

	private static final String BV_VERSION = "1.7";

	private static final int STUDY_ID = 4526;

	private OpenSelectDatasetForExportAction openSelectDatasetForExportAction;
	
	@Mock
	private SingleSiteAnalysisPanel singleSiteAnalysisPanel;
	
	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private InstallationDirectoryUtil installationDirectoryUtil;
	
	@Mock
	private ClickEvent clickEvent;
	
	@Mock
	private Component component;
	
	@Mock
	private ContentWindow window;
	
	@Mock
	private VariableTypeList summaryVariables;
	
	@Mock
	private VariableTypeList trialVariables;
	
	@Mock
	private VariableTypeList factors;
	
	@Mock
	private List<DMSVariableType> factorVariableTypes;
	
	@Mock
	private List<DMSVariableType> trialVariableTypes;
	
	@Mock
	private DataSet summaryDataset;
	
	@Mock
	private DataSet trialDataset;
	
	@Mock
	private Study study;
	
	@Captor
	private ArgumentCaptor<Component> componentCaptor;
	
	private StudyUtil studyUtil;
	private Project project;
	private Tool bvTool;

	public static final String DATASET_NAME = "TEST\\ /:*?'\"<>|[]{},.?~`!@#$%^&()-=_+-PLOTDATA";
	public static final String SANITIZED_DATASET_NAME = "TEST_ _-_-PLOTDATA";
	public static final Integer DATASET_ID = 99;
	public static final String INPUT_DIRECTORY = "workspace/input";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.project = ProjectTestDataInitializer.createProject();

		this.studyUtil = StudyUtil.getInstance();
		this.openSelectDatasetForExportAction = new OpenSelectDatasetForExportAction(this.singleSiteAnalysisPanel);
		this.openSelectDatasetForExportAction.setWorkbenchDataManager(this.workbenchDataManager);
		this.openSelectDatasetForExportAction.setInstallationDirectoryUtil(this.installationDirectoryUtil);
		this.openSelectDatasetForExportAction.setStudyDataManager(this.studyDataManager);
		
		Mockito.doReturn(STUDY_ID).when(this.study).getId();
		Mockito.doReturn(STUDY_NAME).when(this.study).getName();
		Mockito.doReturn(this.project).when(this.singleSiteAnalysisPanel).getCurrentProject();
		this.openSelectDatasetForExportAction.setProject(this.project);
		Mockito.doReturn(this.study).when(this.singleSiteAnalysisPanel).getCurrentStudy();
		this.openSelectDatasetForExportAction.setStudy(this.study);
		Mockito.doReturn(DATASET_ID).when(this.singleSiteAnalysisPanel).getCurrentDataSetId();
		this.openSelectDatasetForExportAction.setDataSetId(DATASET_ID);
		Mockito.doReturn(DATASET_NAME).when(this.singleSiteAnalysisPanel).getCurrentDatasetName();
		this.openSelectDatasetForExportAction.setDatasetName(DATASET_NAME);
		
		Mockito.doReturn(this.summaryDataset).when(this.studyDataManager).getDataSet(DATASET_ID);
		Mockito.doReturn(this.summaryVariables).when(this.summaryDataset).getVariableTypes();
		Mockito.doReturn(this.factors).when(this.summaryVariables).getFactors();
		Mockito.doReturn(this.factorVariableTypes).when(this.factors).getVariableTypes();
		Mockito.doReturn(Arrays.asList(this.trialDataset)).when(this.studyDataManager).getDataSetsByType(Matchers.eq(STUDY_ID), Matchers.any(DataSetType.class));
		Mockito.doReturn(this.trialVariables).when(this.trialDataset).getVariableTypes();
		Mockito.doReturn(this.trialVariableTypes).when(this.trialVariables).getVariableTypes();
		
		
		this.bvTool = new Tool();
		this.bvTool.setVersion(BV_VERSION);
		Mockito.doReturn(this.bvTool).when(this.workbenchDataManager).getToolWithName(Matchers.anyString());
		Mockito.doReturn(INPUT_DIRECTORY).when(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);
		Mockito.doReturn(this.component).when(this.clickEvent).getComponent();
		Mockito.doReturn(this.window).when(this.component).getWindow();
	}

	@Test
	public void testCheckIfNumericCategoricalVarAreIncluded() {
		final List<VariateModel> variateList = new ArrayList<VariateModel>();
		final Map<String, Boolean> variatesCheckboxState = new HashMap<String, Boolean>();
		this.createVariateListWithStateTestData(variateList, variatesCheckboxState);
		Assert.assertTrue("Numerical categorical variates if selected can be included",
				this.openSelectDatasetForExportAction.checkIfNumericCategoricalVarAreIncluded(variateList, variatesCheckboxState));
	}

	@Test
	public void testCheckIfNonNumericVarAreIncluded() {
		final List<VariateModel> variateList = new ArrayList<VariateModel>();
		final Map<String, Boolean> variatesCheckboxState = new HashMap<String, Boolean>();
		this.createVariateListWithStateTestData(variateList, variatesCheckboxState);
		Assert.assertFalse("Non-numeric variates cannot be included",
				this.openSelectDatasetForExportAction.checkIfNonNumericVarAreIncluded(variateList, variatesCheckboxState));
	}

	@Test
	public void testPopulateAnalysisName() {

		final BreedingViewInput breedingViewInput = new BreedingViewInput();
		this.openSelectDatasetForExportAction.populateAnalysisName(breedingViewInput, DATASET_NAME);
		Assert.assertTrue(breedingViewInput.getBreedingViewAnalysisName().contains("SSA analysis of " + SANITIZED_DATASET_NAME + "  (run at "));
	}

	@Test
	public void testPopulateProjectNameAndFilePaths() {

		final BreedingViewInput breedingViewInput = new BreedingViewInput();
		this.openSelectDatasetForExportAction.populateProjectNameAndFilePaths(breedingViewInput, this.project, INPUT_DIRECTORY);

		Assert.assertEquals(this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME, breedingViewInput.getBreedingViewProjectName());
		Assert.assertEquals(INPUT_DIRECTORY + File.separator + this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME + ".xml", breedingViewInput.getDestXMLFilePath());
		Assert.assertEquals(INPUT_DIRECTORY + File.separator + this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME + ".csv", breedingViewInput.getSourceXLSFilePath());
	}
	
	@Test
	public void testButtonClick() {
		this.openSelectDatasetForExportAction.buttonClick(clickEvent);
		
		Mockito.verify(this.workbenchDataManager).getToolWithName(ToolName.BREEDING_VIEW.getName());
		Mockito.verify(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);
		Mockito.verify(this.studyDataManager).getDataSetsByType(Matchers.eq(STUDY_ID), Matchers.any(DataSetType.class));
		
		Mockito.verify(this.window).showContent(this.componentCaptor.capture());
		Assert.assertTrue(this.componentCaptor.getValue() instanceof SingleSiteAnalysisDetailsPanel);
		final SingleSiteAnalysisDetailsPanel ssaDetailsPanel = (SingleSiteAnalysisDetailsPanel) this.componentCaptor.getValue();
		Assert.assertEquals(this.bvTool, ssaDetailsPanel.getTool());
		Assert.assertEquals(this.project, ssaDetailsPanel.getProject());
		Assert.assertEquals(this.singleSiteAnalysisPanel, ssaDetailsPanel.getSelectDatasetForBreedingViewPanel());
		Assert.assertEquals(this.factorVariableTypes, ssaDetailsPanel.getFactorsInDataset());
		Assert.assertEquals(this.trialVariableTypes, ssaDetailsPanel.getTrialVariablesInDataset());
		
		final BreedingViewInput bvInput = ssaDetailsPanel.getBreedingViewInput();
		Assert.assertEquals(this.project, bvInput.getProject());
		Assert.assertEquals(STUDY_ID, bvInput.getStudyId().intValue());
		Assert.assertEquals(DATASET_ID, bvInput.getDatasetId());
		Assert.assertEquals(DATASET_NAME, bvInput.getDatasetName());
		Assert.assertEquals(STUDY_NAME, bvInput.getDatasetSource());
		Assert.assertEquals(BV_VERSION, bvInput.getVersion());
		Assert.assertEquals(ProjectType.FIELD_TRIAL.getName(), bvInput.getProjectType());
		Assert.assertEquals(0, bvInput.getOutputDatasetId().intValue());
		Assert.assertEquals(INPUT_DIRECTORY + File.separator + this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME + ".xml", bvInput.getDestXMLFilePath());
		Assert.assertEquals(INPUT_DIRECTORY + File.separator + this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME + ".csv", bvInput.getSourceXLSFilePath());
		Assert.assertEquals(INPUT_DIRECTORY + File.separator + this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME + ".csv", bvInput.getSourceXLSFilePath());
		Assert.assertTrue(bvInput.getBreedingViewAnalysisName().contains("SSA analysis of " + SANITIZED_DATASET_NAME + "  (run at "));

	}

	private void createVariateListWithStateTestData(final List<VariateModel> variateList, final Map<String, Boolean> variatesCheckboxState) {
		final VariableTypeList variates = this.studyUtil.createVariateVarsTestData();
		for (final DMSVariableType variate : variates.getVariates().getVariableTypes()) {
			final VariateModel vm = new SingleSiteAnalysisPanel(this.project).transformVariableTypeToVariateModel(variate);
			variateList.add(vm);
			variatesCheckboxState.put(vm.getName(), vm.getActive());
		}
	}

}
