package org.generationcp.ibpworkbench.study.tree.listeners;

import java.util.Arrays;
import java.util.List;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.study.tree.StudyTree;
import org.generationcp.ibpworkbench.study.tree.StudyTypeFilterComponent;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Component;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Window;

public class StudyTreeExpandListenerTest {
	private static final String PROGRAM_UUID = "abcd-efg-12345";
	private static final Integer STUDY_ID = 100;
	private static final FolderReference FOLDER = new FolderReference(1, "Folder 1", "Folder 1 Description", PROGRAM_UUID);
	private static final StudyReference TRIAL = new StudyReference(100, "F1 Trial", "Trial Description", PROGRAM_UUID, StudyTypeDto.getTrialDto());
	private static final StudyReference NURSERY = new StudyReference(101, "F2 Nusery", "Nursery Description", PROGRAM_UUID, StudyTypeDto.getNurseryDto());
	private static final List<Reference> STUDY_REFERENCES = Arrays.asList(FOLDER, TRIAL, NURSERY);
	private static final StudyTypeDto TRIAL_FILTER = new StudyTypeDto(1, "Trial", "T");
	
	@Mock
	private StudyTree studyTree;
	
	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource;
	
	@Mock
	private ExpandEvent expandEvent;
	
	@Mock
	private Component component;
	
	@Mock
	private Window window;
	
	@InjectMocks
	private StudyTreeExpandListener expandListener;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.expandListener = new StudyTreeExpandListener(this.studyTree);
		this.expandListener.setContextUtil(this.contextUtil);
		this.expandListener.setMessageSource(this.messageSource);
		this.expandListener.setStudyDataManager(this.studyDataManager);
		
		Mockito.doReturn(STUDY_ID).when(this.expandEvent).getItemId();
		Mockito.doReturn(this.component).when(this.expandEvent).getComponent();
		Mockito.doReturn(this.window).when(this.component).getWindow();
		
		final Project project = ProjectTestDataInitializer.createProject();
		project.setUniqueID(PROGRAM_UUID);
		Mockito.doReturn(project).when(this.contextUtil).getProjectInContext();
		Mockito.doReturn(StudyTypeFilterComponent.ALL_OPTION).when(this.studyTree).getStudyTypeFilter();
		Mockito.doReturn(STUDY_REFERENCES).when(this.studyDataManager).getChildrenOfFolderByStudyType(ArgumentMatchers.anyInt(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());
		Mockito.doReturn(STUDY_REFERENCES).when(this.studyDataManager).getChildrenOfFolderByStudyType(ArgumentMatchers.anyInt(),
				ArgumentMatchers.anyString(), ArgumentMatchers.<Integer>isNull());
	}
	
	@Test
	public void testNodeExpandWhenRootNode() {
		Mockito.doReturn(StudyTree.STUDY_ROOT_NODE).when(this.expandEvent).getItemId();
		this.expandListener.nodeExpand(expandEvent);
		Mockito.verifyZeroInteractions(this.studyDataManager);
		Mockito.verifyZeroInteractions(this.contextUtil);
		Mockito.verifyZeroInteractions(this.messageSource);
		Mockito.verify(this.studyTree).selectItem(StudyTree.STUDY_ROOT_NODE);
	}
	
	@Test
	public void testNodeExpandWithMiddlewareException() {
		Mockito.doThrow(new MiddlewareQueryException("ERROR")).when(this.studyDataManager).getChildrenOfFolderByStudyType(ArgumentMatchers.anyInt(),
				ArgumentMatchers.anyString(), ArgumentMatchers.<Integer>isNull());
		this.expandListener.nodeExpand(expandEvent);
		Mockito.verify(this.studyTree, Mockito.never()).addItem(ArgumentMatchers.anyInt());
		Mockito.verify(this.studyTree, Mockito.never()).setItemCaption(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
		Mockito.verify(this.studyTree, Mockito.never()).setParent(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
		Mockito.verify(this.studyTree, Mockito.never()).setChildrenAllowed(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
		Mockito.verify(this.studyTree).selectItem(STUDY_ID);
	}
	
	@Test
	public void testNodeExpandWithNoFilterOnStudyType() {
		this.expandListener.nodeExpand(expandEvent);
		verifyItemWasAddedToTree(FOLDER.getId(), FOLDER.getName());
		verifyItemWasAddedToTree(TRIAL.getId(), TRIAL.getName());
		verifyItemWasAddedToTree(NURSERY.getId(), NURSERY.getName());
		Mockito.verify(this.studyDataManager).getChildrenOfFolderByStudyType(STUDY_ID, PROGRAM_UUID, null);
		Mockito.verify(this.studyTree).selectItem(STUDY_ID);	
	}
	
	@Test
	public void testNodeExpandWithFilterOnStudyType() {
		Mockito.doReturn(TRIAL_FILTER).when(this.studyTree).getStudyTypeFilter();
		Mockito.doReturn(TRIAL_FILTER).when(this.studyDataManager).getStudyTypeByLabel(ArgumentMatchers.anyString());
		
		this.expandListener.nodeExpand(expandEvent);
		verifyItemWasAddedToTree(FOLDER.getId(), FOLDER.getName());
		verifyItemWasAddedToTree(TRIAL.getId(), TRIAL.getName());
		verifyItemWasAddedToTree(NURSERY.getId(), NURSERY.getName());
		Mockito.verify(this.studyDataManager).getChildrenOfFolderByStudyType(STUDY_ID, PROGRAM_UUID, TRIAL_FILTER.getId());
		Mockito.verify(this.studyTree).selectItem(STUDY_ID);	
	}
	
	private void verifyItemWasAddedToTree(final Integer id, final String name) {
		Mockito.verify(this.studyTree).addItem(id);
		Mockito.verify(this.studyTree).setItemCaption(id, name);
		Mockito.verify(this.studyTree).setParent(id, STUDY_ID);
		Mockito.verify(this.studyTree).setChildrenAllowed(id, false);
	}

}
