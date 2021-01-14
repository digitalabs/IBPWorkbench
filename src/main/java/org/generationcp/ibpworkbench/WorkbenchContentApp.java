package org.generationcp.ibpworkbench;

import com.vaadin.ui.Window;
import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.MultiSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.programadministration.OpenManageProgramPageAction;
import org.generationcp.ibpworkbench.ui.programadministration.ProgramAdministrationPanel;
import org.generationcp.ibpworkbench.ui.project.create.AddProgramView;
import org.generationcp.ibpworkbench.ui.project.create.CreateProjectPanel;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.annotation.Resource;

public class WorkbenchContentApp extends SpringContextApplication {

	private static final long serialVersionUID = -3098125752010885259L;

	@Resource
	private ContextUtil contextUtil;

	@Override
	public void close() {
		super.close();
	}

	@Override
	protected void initSpringApplication(final ConfigurableWebApplicationContext configurableWebApplicationContext) {
		this.setTheme("gcp-default");
		this.setMainWindow(new ContentWindow());
	}

	@Override
	public Window getWindow(final String name) {

		final Window w = super.getWindow(name);

		if (super.getWindow(name) == null) {
			if (ToolName.BREEDING_VIEW.getName().equals(name)) {
				final Project project = this.contextUtil.getProjectInContext();
				final SingleSiteAnalysisPanel singleSiteAnalysis = new SingleSiteAnalysisPanel(project);
				singleSiteAnalysis.setDebugId("singleSiteAnalysisPanel");
				final ContentWindow contentWindow = new ContentWindow();
				this.addWindow(contentWindow);
				contentWindow.showContent(singleSiteAnalysis);
				return contentWindow;
			} else if (ToolName.BV_GXE.getName().equals(name)) {
				final Project project = this.contextUtil.getProjectInContext();
				final MultiSiteAnalysisPanel gxeAnalysisPanel = new MultiSiteAnalysisPanel(project);
				gxeAnalysisPanel.setDebugId("gxeAnalysisPanel");
				final ContentWindow contentWindow = new ContentWindow();
				this.addWindow(contentWindow);
				contentWindow.showContent(gxeAnalysisPanel);
				return contentWindow;
			} else if (ToolName.MANAGE_PROGRAMS.getName().equals(name)) {
				final ProgramAdministrationPanel projectPanel = new ProgramAdministrationPanel();
				projectPanel.setDebugId("projectPanel");
				final ContentWindow contentWindow = new ContentWindow();
				this.addWindow(contentWindow);
				contentWindow.showContent(projectPanel);
				return contentWindow;
			} else if (ToolName.CREATE_PROGRAMS.getName().equals(name)) {
				final AddProgramView createProjectPanel = new AddProgramView();
				createProjectPanel.setDebugId("createProjectPanel");
				final ContentWindow contentWindow = new ContentWindow();
				this.addWindow(contentWindow);
				contentWindow.showContent(createProjectPanel);
				return contentWindow;
			}
		}

		return w;
	}

}

