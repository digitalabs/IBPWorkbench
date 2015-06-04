
package org.generationcp.ibpworkbench.ui.breedingview;

import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public class SelectStudyDialogForBreedingViewUpload extends SelectStudyDialog {

	private static final long serialVersionUID = 1L;

	public SelectStudyDialogForBreedingViewUpload(Window parentWindow, Component source, StudyDataManagerImpl studyDataManager,
			Project project) {
		super(parentWindow, source, studyDataManager, project);
	}

	@Override
	public void updateLabels() {
		this.messageSource.setCaption(this, Message.BV_SELECT_STUDY_FOR_UPLOAD);
		this.messageSource.setValue(this.lblStudyTreeDetailDescription, Message.BV_SELECT_STUDY_FOR_UPLOAD_DESCRIPTION);
	}

	@Override
	protected void openStudy(Reference r) {
		SingleSiteAnalysisPanel ssaPanel = (SingleSiteAnalysisPanel) this.source;
		FileUploadBreedingViewOutputWindow dialog =
				new FileUploadBreedingViewOutputWindow(this.parentWindow, r.getId(), ssaPanel.getCurrentProject(), null);
		this.parentWindow.getWindow().addWindow(dialog);
		this.parentWindow.getWindow().removeWindow(this);
	}

}
