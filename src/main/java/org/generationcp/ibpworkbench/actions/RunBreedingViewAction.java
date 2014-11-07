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

import com.google.common.base.Strings;
import com.mysql.jdbc.StringUtils;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import org.generationcp.commons.breedingview.xml.*;
import org.generationcp.commons.util.Util;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.util.*;
import org.generationcp.ibpworkbench.util.tomcat.TomcatUtil;
import org.generationcp.ibpworkbench.util.tomcat.WebAppStatusInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author Jeffrey Morales
 *
 */
@Configurable
public class RunBreedingViewAction implements ClickListener {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(RunBreedingViewAction.class);
    
    private SingleSiteAnalysisDetailsPanel source;
    
    private Project project;

    public static final String WEB_SERVICE_URL_PROPERTY = "bv.web.url";

    @Autowired
    private Properties workbenchProperties;
    
    @Autowired
    private ToolUtil toolUtil;
    
    @Autowired
    private TomcatUtil tomcatUtil;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public RunBreedingViewAction(SingleSiteAnalysisDetailsPanel selectDetailsForBreedingViewWindow, Project project) {
        this.source = selectDetailsForBreedingViewWindow;
        this.project = project;
    }
    
    @Override
    public void buttonClick(final ClickEvent event) {
        
        BreedingViewInput breedingViewInput = this.source.getBreedingViewInput();
        
        breedingViewInput.setSelectedEnvironments(source.getSelectedEnvironments());
        
        String analysisProjectName = (String) this.source.getTxtAnalysisName().getValue();
        if(StringUtils.isNullOrEmpty(analysisProjectName)){
            MessageNotifier.showError(event.getComponent().getWindow(), "Please enter an Analysis Name.", "");
            return;
        } else{

        	breedingViewInput.setBreedingViewAnalysisName(analysisProjectName);
        }
        
        String envFactor = (String) this.source.getSelEnvFactor().getValue();
        
        if (StringUtils.isNullOrEmpty(envFactor)){
        	MessageNotifier.showError(event.getComponent().getWindow(), "Please select an environment factor.", "");
        	return;
        }
        
        
        if(!StringUtils.isNullOrEmpty(envFactor)){
            Environment environment = new Environment();
            environment.setName(envFactor.trim());
            
            if(breedingViewInput.getSelectedEnvironments().isEmpty()){
            	MessageNotifier.showError(event.getComponent().getWindow(), "Please select environment for analysis.", "");
                return;
            } else{
               
                breedingViewInput.setEnvironment(environment);
                
            }
        } else{
            breedingViewInput.setEnvironment(null);
        }
                
        String designType = (String) this.source.getSelDesignType().getValue();
        if(StringUtils.isNullOrEmpty(designType)){
        	MessageNotifier.showError(event.getComponent().getWindow(), "Please specify design type.", "");
            return;
        } else{
            breedingViewInput.setDesignType(designType);
        }
        
        String replicates = (String) this.source.getSelReplicates().getValue();
        if(StringUtils.isNullOrEmpty(replicates)){
            if(designType.equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName()) && this.source.getSelReplicates().isEnabled()){
            	MessageNotifier.showError(event.getComponent().getWindow(), "Please specify replicates factor.", "");
                return;
            } else{
            	Replicates reps = new Replicates();
                reps.setName("_REPLICATES_");
                breedingViewInput.setReplicates(reps);
            }
        } else{
            Replicates reps = new Replicates();
            reps.setName(replicates.trim());
            breedingViewInput.setReplicates(reps);
            
            if(designType.equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName())){
                breedingViewInput.setDesignType(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
            } else if(designType.equals(DesignType.ROW_COLUMN_DESIGN.getName())){
                breedingViewInput.setDesignType(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());
            }
        }
        
        String blocksName = (String) this.source.getSelBlocks().getValue();
        if(StringUtils.isNullOrEmpty(blocksName)){
            if(designType.equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName())){
                MessageNotifier.showError(event.getComponent().getWindow(), "Please specify incomplete block factor.", "");
                return;
            } else{
                breedingViewInput.setBlocks(null);
            }
        } else{
            Blocks blocks = new Blocks();
            blocks.setName(blocksName.trim());
            breedingViewInput.setBlocks(blocks);
        }
        
        String columnName = (String) this.source.getSelColumnFactor().getValue();
        
        if (designType.equals(DesignType.ROW_COLUMN_DESIGN.getName())){
            if(StringUtils.isNullOrEmpty(columnName)){

                MessageNotifier.showError(event.getComponent().getWindow(), "Please specify column factor.", "");
                return;
            } else{
                Columns columns = new Columns();
                columns.setName(columnName.trim());
                breedingViewInput.setColumns(columns);
            }
        }
        
        String rowName = (String) this.source.getSelRowFactor().getValue();
        
        if (designType.equals(DesignType.ROW_COLUMN_DESIGN.getName())){
            if(StringUtils.isNullOrEmpty(rowName)){
                MessageNotifier.showError(event.getComponent().getWindow(), "Please specify row factor.", "");
                return;
            } else{
                Rows rows = new Rows();
                rows.setName(rowName.trim());
                breedingViewInput.setRows(rows);
            }
        }
            	
        String genotypesName = (String) this.source.getSelGenotypes().getValue();
        if(StringUtils.isNullOrEmpty(genotypesName)){
            MessageNotifier.showError(event.getComponent().getWindow(), "Please specify Genotypes factor.", "");
            return;
        } else{
           
            String entryName  = "";
            String plotName = "";
			try {
				entryName = source.getManagerFactory().getNewStudyDataManager().getLocalNameByStandardVariableId(breedingViewInput.getDatasetId(), TermId.ENTRY_NO.getId());
				plotName = source.getManagerFactory().getNewStudyDataManager().getLocalNameByStandardVariableId(breedingViewInput.getDatasetId(), TermId.PLOT_NO.getId());
				if (Strings.isNullOrEmpty(plotName)){
					plotName = source.getManagerFactory().getNewStudyDataManager().getLocalNameByStandardVariableId(breedingViewInput.getDatasetId(), TermId.PLOT_NNO.getId());
				}
			} catch (ConfigException e) {
				LOG.error("ERROR: ", e);
			} catch (MiddlewareQueryException e) {
				LOG.error("ERROR: ", e);
			} 
			
			Genotypes genotypes = new Genotypes();
	        genotypes.setName(genotypesName.trim());
            genotypes.setEntry(entryName);
            breedingViewInput.setGenotypes(genotypes);
            
            if (!Strings.isNullOrEmpty(plotName)){
	            Plot plot = new Plot();
	            plot.setName(plotName);
	            breedingViewInput.setPlot(plot);
            }
            
        }
        
        
        DatasetExporter datasetExporter = new DatasetExporter(source.getManagerFactory().getNewStudyDataManager(), null, breedingViewInput.getDatasetId());
        
        try {
			
        	List<String> selectedEnvironments = new ArrayList<String>();
        	for (SeaEnvironmentModel m : breedingViewInput.getSelectedEnvironments()){
        		selectedEnvironments.add(m.getTrialno());
        	}
        	
        	datasetExporter.exportToCSVForBreedingView(breedingViewInput.getSourceXLSFilePath(), (String) this.source.getSelEnvFactor().getValue(), selectedEnvironments, breedingViewInput);
        	
        } catch (DatasetExporterException e1) {
        	LOG.error("ERROR: ", e1);
		}
       
		launchBV(event);
      	    
       
    }   
    
    private void launchBV(ClickEvent event){
    	
    	 BreedingViewXMLWriter breedingViewXMLWriter;
         BreedingViewInput breedingViewInput = this.source.getBreedingViewInput();
    	 
         try {
             // when launching BreedingView, update the web service tool first
             Tool webServiceTool = new Tool();
             webServiceTool.setToolName("ibpwebservice");
             webServiceTool.setPath(workbenchProperties.getProperty(WEB_SERVICE_URL_PROPERTY));
             webServiceTool.setToolType(ToolType.WEB);
             updateToolConfiguration(event.getButton().getWindow(), webServiceTool);
             
             // write the XML input for breeding view
             breedingViewXMLWriter = new BreedingViewXMLWriter(breedingViewInput);
             breedingViewXMLWriter.writeProjectXML();
             
             // launch breeding view
             File absoluteToolFile = new File(this.source.getTool().getPath()).getAbsoluteFile();
             
             LOG.info(breedingViewInput.toString());
             LOG.info(absoluteToolFile.getAbsolutePath() + " -project=\"" +  breedingViewInput.getDestXMLFilePath() + "\"");
             
             ProcessBuilder pb = new ProcessBuilder(absoluteToolFile.getAbsolutePath(), "-project=", breedingViewInput.getDestXMLFilePath());
             pb.start();
             
         } catch (BreedingViewXMLWriterException e) {
             LOG.debug("Cannot write Breeding View input XML", e);
             
             MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
         } catch (IOException e) {
             LOG.debug("Cannot write Breeding View input XML", e);
             
             MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
         }
        
        source.getManagerFactory().close();
    }
    
    private boolean updateToolConfiguration(Window window, Tool tool) {
        Project currentProject = project;
        
        String url = tool.getPath();
        
        // update the configuration of the tool
        boolean changedConfig = false;
        try {
            changedConfig = toolUtil.updateToolConfigurationForProject(tool, currentProject);
        } catch (IOException e1) {
        	LOG.error("ERROR: ", e1);
            MessageNotifier.showError(window, "Cannot update configuration for tool: " + tool.getToolName(),
                                      "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return false;
        } catch (MiddlewareQueryException e) {
        	LOG.error("ERROR: ", e);
            MessageNotifier.showError(window, "Cannot update configuration for tool: " + tool.getToolName(),
                                      "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return false;
        }
        
        boolean webTool = Util.isOneOf(tool.getToolType(), ToolType.WEB_WITH_LOGIN, ToolType.WEB);
        
        WebAppStatusInfo statusInfo = null;
        String contextPath = null;
        String localWarPath = null;
        try {
            statusInfo = tomcatUtil.getWebAppStatus();
            if (webTool) {
                contextPath = TomcatUtil.getContextPathFromUrl(url);
                localWarPath = TomcatUtil.getLocalWarPathFromUrl(url);
                
                
            }
        } catch (Exception e1) {
        	LOG.error("ERROR: ", e1);
            MessageNotifier.showError(window, "Cannot get webapp status.",
                                      "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return false;
        }
        
        if (webTool) {
            try {
                boolean deployed = statusInfo.isDeployed(contextPath);
                boolean running = statusInfo.isRunning(contextPath);
                
                if (changedConfig || !running) {
                    if (!deployed) {
                        // deploy the webapp
                        tomcatUtil.deployLocalWar(contextPath, localWarPath);
                    } else if (running) {
                        // reload the webapp
                        tomcatUtil.reloadWebApp(contextPath);
                    } else {
                        // start the webapp
                        tomcatUtil.startWebApp(contextPath);
                    }
                }
            } catch (Exception e) {
            	LOG.error("ERROR: ", e);
                MessageNotifier.showError(window, "Cannot load tool: " + tool.getToolName(),
                                          "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
                return false;
            }
        }
        
        return true;
    }
}
