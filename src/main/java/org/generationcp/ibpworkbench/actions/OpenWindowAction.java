/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.ibpworkbench.actions;

import java.util.Arrays;
import java.util.Date;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.WorkflowConstants;
import org.generationcp.ibpworkbench.comp.common.ConfirmDialog;
import org.generationcp.ibpworkbench.comp.window.ChangePasswordWindow;
import org.generationcp.ibpworkbench.comp.window.GxeAnalysisWindow;
import org.generationcp.ibpworkbench.comp.window.ProjectMemberWindow;
import org.generationcp.ibpworkbench.comp.window.RestoreIBDBWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

@Configurable
public class OpenWindowAction implements WorkflowConstants, ClickListener, ActionListener {
    private static final long serialVersionUID = 1L;
    
    private final static Logger LOG = LoggerFactory.getLogger(OpenWindowAction.class);
    
    Project project;
    
    public static enum WindowEnum {
         GERMPLASM_BROWSER("germplasm_browser")
        ,STUDY_BROWSER("study_browser")
        ,GERMPLASM_LIST_BROWSER("germplasm_list_browser")
        ,GDMS("gdms")
        ,FIELDBOOK("fieldbook")
        ,OPTIMAS("optimas")
        ,BREEDING_MANAGER("breeding_manager")
        ,BREEDING_GXE("breeding_gxe")
        ,BREEDING_VIEW("breeding_view")
        ,MBDT("mbdt")
        ,MEMBER("member")
        ,BACKUP_IBDB("backup_ibdb")
        ,RESTORE_IBDB("restore_ibdb")
        ,CHANGE_PASSWORD("change_password")
        ;
        
        String windowName;
        
        WindowEnum(String windowName) {
            this.windowName = windowName;
        }
        
        public String getwindowName() {
            return windowName;
        }
        
        public static boolean isCorrectTool(String windowName) {
        	
        	for (WindowEnum winEnum : WindowEnum.values()) {
        		if (winEnum.getwindowName().equals(windowName)) {
        			return true;
        		}
        	}
        	
        	return false;

        	
        }
    }

    private WindowEnum windowEnum;
    
    private String toolConfiguration;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired
    private ToolUtil toolUtil;
    
    public OpenWindowAction() {
    }
    
    public OpenWindowAction(WindowEnum windowEnum) {
        this.windowEnum = windowEnum;
        this.toolConfiguration = WorkflowConstants.DEFAULT;
    }
    
    public OpenWindowAction(WindowEnum windowEnum, Project project) {
        this.windowEnum = windowEnum;
        this.project = project;
      
    }
    
    public OpenWindowAction(WindowEnum windowEnum, Project project, String toolConfiguration) {
        this.windowEnum = windowEnum;
        this.project = project;
        this.toolConfiguration = toolConfiguration;
    }
    
    

	@Override
    public void buttonClick(ClickEvent event) {
        
        Window window = event.getComponent().getWindow();
        
      //  launchTool(windowEnum.getwindowName(), window, true);
        launchWindow(window, windowEnum.getwindowName());
        
    }

    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);
    }

    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {

        String windowName = uriFragment.split("/")[1];
        
        if(WindowEnum.isCorrectTool(windowName)) {
        	launchWindow(window, windowName);
        } else {
            LOG.debug("Cannot launch window due to invalid window name: {}", windowName);
            MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR), 
                    messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC, Arrays.asList(windowName).toArray()));
        }
    }
    
    public void launchWindow(Window window, String windowName)
    {
    	Window mywindow = null;
    	Boolean windowLaunched = false;
    	if(WindowEnum.BREEDING_GXE.getwindowName().equals(windowName) )
    	{
    		mywindow = new GxeAnalysisWindow(this.project);
    		mywindow.setWidth("1000");
    		
    		window.addWindow(mywindow);
    		windowLaunched = true;
    	}
    	else if(WindowEnum.MEMBER.getwindowName().equals(windowName) )
    	{
    		mywindow = new ProjectMemberWindow(this.project);
    		mywindow.setWidth("700");
    		
    		
    		window.addWindow(mywindow);
    		windowLaunched = true;
    	}
    	
    	else if(WindowEnum.CHANGE_PASSWORD.getwindowName().equals(windowName) )
    	{
    		mywindow = new ChangePasswordWindow();
    		mywindow.setWidth("500");
    		mywindow.setHeight("200");
    		mywindow.center();
    		
    		window.addWindow(mywindow);
    		windowLaunched = true;
    	}
    	else if (WindowEnum.BACKUP_IBDB.getwindowName().equals(windowName))
    	{
    		//LOG.debug("Add Backup IBDB Window");
    		//window.addWindow(new BackupIBDBWindow(this.project));
    		ConfirmDialog.show(window,messageSource.getMessage(Message.BACKUP_IBDB_WINDOW_CAPTION),
    				messageSource.getMessage(Message.BACKUP_IBDB_WINDOW_DESC),
    				messageSource.getMessage(Message.YES),
    				messageSource.getMessage(Message.CANCEL),
    				new BackupIBDBSaveAction(this.project, window));
    		
    		// just a dummy window
    		mywindow = new Window(messageSource.getMessage(Message.BACKUP_IBDB_WINDOW_CAPTION));
    		
    		windowLaunched = true;
    	}
    	else if (WindowEnum.RESTORE_IBDB.getwindowName().equals(windowName))
    	{
    		//LOG.debug("Add Restore IBDB Window");
    		mywindow = new RestoreIBDBWindow(this.project);
    		window.addWindow(mywindow);
    		
    		windowLaunched = true;
    		
    	}
    	else
    	{
            LOG.debug("Cannot launch window due to invalid window name: {}", windowName);
            MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR), 
            messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC, Arrays.asList(windowName).toArray()));
        }
    	
    	// Add to Project Activity logs the launched windows
    	if (windowLaunched)
    	{
    		try {
                IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
                User user = app.getSessionData().getUserData();
                Project currentProject = app.getSessionData().getLastOpenedProject();

                ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, windowName, "Launched "+ mywindow.getCaption(), user, new Date());

                workbenchDataManager.addProjectActivity(projAct);

            } catch (MiddlewareQueryException e1) {
                MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                                          "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
                //return;
            }
    	}
    }
   
    
}
