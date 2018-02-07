package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.Button;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 10/28/13 Time: 11:07 AM To change this template use File | Settings | File Templates.
 */
@Configurable
public class UpdateProjectAction implements Button.ClickListener {

	private final UpdateProjectPanel projectPanel;
	private static final Logger LOG = LoggerFactory.getLogger(UpdateProjectAction.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ToolUtil toolUtil;

	@Autowired
	private ContextUtil contextUtil;

	private static final long serialVersionUID = 1L;

	public UpdateProjectAction(final UpdateProjectPanel projectPanel) {
		this.projectPanel = projectPanel;
	}

	@Override
	public void buttonClick(final Button.ClickEvent event) {

		if (this.projectPanel.validate()) {
			this.doUpdate();
		}

	}

	private void doUpdate() {

		final Project project = this.contextUtil.getProjectInContext();

		UpdateProjectAction.LOG.debug("doUpdate >");
		UpdateProjectAction.LOG.debug(String.format("Project: [%s]", project));

		if (this.projectPanel.validate()) {
			// It is important to store old project name before updating the project
			final String oldProjectName = this.projectPanel.getOldProjectName();
			
			// Update the project
			final Project updatedProject = this.projectPanel.getProjectBasicDetailsComponent().getProjectDetails();
			project.setProjectName(updatedProject.getProjectName());
			project.setStartDate(updatedProject.getStartDate());
			this.workbenchDataManager.saveOrUpdateProject(project);

			// Rename old workspace directory if found
			this.toolUtil.renameOldWorkspaceDirectory(oldProjectName, project);

			MessageNotifier.showMessage(this.projectPanel.getWindow(), "Program update is successful",
					String.format("%s is updated.", StringUtils.abbreviate(project.getProjectName(), 50)));

			this.contextUtil.logProgramActivity("Update Program", "Updated Program - " + project.getProjectName());

			if (IBPWorkbenchApplication.get().getMainWindow() instanceof WorkbenchMainView) {
				((WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow()).addTitle(project.getProjectName());
			}
		}
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setToolUtil(final ToolUtil toolUtil) {
		this.toolUtil = toolUtil;
	}

}
