/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.dashboard.listener;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.ibpworkbench.util.SchemaVersionUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Configurable
public class LaunchProgramAction implements ItemClickListener, ClickListener {

	private static final long serialVersionUID = 5742093045098439073L;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private ContextUtil contextUtil;

	private static final Logger LOG = LoggerFactory.getLogger(LaunchProgramAction.class);

	private Project selectedProgram;

	private LaunchWorkbenchToolAction launchListManagerToolAction = new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER_MAIN);

	public LaunchProgramAction() {
		super();
	}

	public LaunchProgramAction(final Project selectedProgram) {
		this.selectedProgram = selectedProgram;
	}

	void openSelectedProgram(final Project project, final Window window) {
		try {
			final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);

			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(final TransactionStatus status) {

					// Sets selected program/project to context
					org.generationcp.commons.util.ContextUtil
							.setContextInfo(request, contextUtil.getCurrentWorkbenchUserId(), project.getProjectId(), null);

					// Warn if the selected program's crop is outdated
					final String minimumCropVersion = SchemaVersionUtil.getMinimumCropVersion();
					final String currentCropVersion = project.getCropType().getVersion();
					if (!SchemaVersionUtil.checkIfVersionIsSupported(currentCropVersion, minimumCropVersion)) {
						MessageNotifier.showWarning(window, "", LaunchProgramAction.this.messageSource
								.getMessage(Message.MINIMUM_CROP_VERSION_WARNING, currentCropVersion != null ?
										currentCropVersion :
										LaunchProgramAction.this.messageSource.getMessage(Message.NOT_AVAILABLE)));
					}

					LaunchProgramAction.this.updateProjectLastOpenedDate(project);

					// Set project name to header
					final WorkbenchMainView workbenchMainView = (WorkbenchMainView) window;
					workbenchMainView.addTitle(project.getProjectName());

					// update sidebar selection
					LaunchProgramAction.LOG.trace("selecting sidebar");
					if (null != WorkbenchSidebar.sidebarTreeMap.get("manage_list")) {
						workbenchMainView.getSidebar().selectItem(WorkbenchSidebar.sidebarTreeMap.get("manage_list"));
					}

					// page change to list manager, with parameter passed
					LaunchProgramAction.this.launchListManagerToolAction.onAppLaunch(window);

				}
			});
		} catch (final InternationalizableException e) {
			LaunchProgramAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(window, e.getCaption(), e.getDescription());
		} catch (final Exception e) {
			LaunchProgramAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(window, "", e.getLocalizedMessage());

		}
	}

	/**
	 * Updates last opened project for user in DB
	 *
	 * @param project : the program selected in dashboard
	 */
	void updateProjectLastOpenedDate(final Project project) {

		final ProjectUserInfoDAO projectUserInfoDao = this.workbenchDataManager.getProjectUserInfoDao();
		final ProjectUserInfo projectUserInfo =
				projectUserInfoDao.getByProjectIdAndUserId(project.getProjectId(), contextUtil.getCurrentWorkbenchUserId());
		if (projectUserInfo != null) {
			projectUserInfo.setLastOpenDate(new Date());
			this.workbenchDataManager.saveOrUpdateProjectUserInfo(projectUserInfo);
		}

		project.setLastOpenDate(new Date());
		this.workbenchDataManager.mergeProject(project);

	}

	@Override
	public void itemClick(final ItemClickEvent event) {
		final Project project = (Project) event.getItemId();
		this.openSelectedProgram(project, event.getComponent().getWindow());
	}

	@Override
	public void buttonClick(final ClickEvent event) {
		if (this.selectedProgram != null) {
			this.openSelectedProgram(this.selectedProgram, event.getComponent().getWindow());
		}

	}

	public void setTransactionManager(final PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setLaunchWorkbenchToolAction(final LaunchWorkbenchToolAction launchWorkbenchToolAction) {
		this.launchListManagerToolAction = launchWorkbenchToolAction;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}
}
