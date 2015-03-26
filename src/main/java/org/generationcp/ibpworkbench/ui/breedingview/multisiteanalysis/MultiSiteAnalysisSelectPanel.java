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

package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.util.DatasetUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Aldrin Batac
 *
 */
@Configurable
public class MultiSiteAnalysisSelectPanel extends VerticalLayout implements InitializingBean, 
			InternationalizableComponent, IBPWorkbenchLayout {

    private static final String TESTEDIN = "testedin";
	private static final String DESCRIPTION_COLUMN = "description";
	private static final String DESCRIPTION = "Description";
	private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MultiSiteAnalysisSelectPanel.class);
    
    private Table factors;
    private Table variates;
    private Property.ValueChangeListener selectAllListener;
    private CheckBox chkVariatesSelectAll;
    
    private Boolean refreshing = false;
    
    private Label lblEnvironmentFactorHeader;
    private Label lblEnvironmentFactorDescription;
    private Label lblGenotypesFactorDescription;
    private Label lblEnvironmentGroupsHeader;
    private Label lblEnvironmentGroupsDescription;
    private Label lblEnvironmentGroupsSpecify;
    private Label lblReviewSelectedDataset;
    private Label lblFactorTableHeader;
    private Label lblFactorTableDescription;
    private Label lblVariateTableHeader;
    private Label lblVariateTableDescription;
    
    private Label lblStudyTreeDetailTitle;
    
    private VerticalLayout generalLayout;
    
    private HorizontalLayout specifyEnvironmentFactorLayout;
    private HorizontalLayout specifyGenotypesFactorLayout;
    private HorizontalLayout specifyEnvironmentGroupsLayout;
    
    private VerticalLayout datasetVariablesDetailLayout;
    
    private Project currentProject;

    private Study currentStudy;
    
    private Integer currentRepresentationId;
    
    private Integer currentDataSetId;
    
    private String currentDatasetName;

    private Button btnCancel;
    private Button btnNext;
    private Component buttonArea;
    private Select selectSpecifyEnvironment;
    private Select selectSpecifyGenotypes;
    private Select selectSpecifyEnvironmentGroups;
    
    private Map<String, Boolean> variatesCheckboxState;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private MultiSiteAnalysisPanel gxeAnalysisComponentPanel;
    
	private StudyDataManager studyDataManager;
    
    private ManagerFactory managerFactory;
    
    private List<String> environmentNames = new ArrayList<String>();
    private TrialEnvironments trialEnvironments = null;

    public MultiSiteAnalysisSelectPanel(StudyDataManager studyDataManager,Project currentProject, Study study, MultiSiteAnalysisPanel gxeAnalysisComponentPanel) {
    	this.studyDataManager = studyDataManager;
        this.currentProject = currentProject;
        this.currentStudy = study;
        this.setGxeAnalysisComponentPanel(gxeAnalysisComponentPanel);
        
        setWidth("100%");
        
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        messageSource.setCaption(btnCancel, Message.RESET);
        messageSource.setCaption(btnNext, Message.NEXT);
        messageSource.setValue(lblEnvironmentFactorHeader, Message.GXE_ENVIRONMENT_FACTOR_HEADER);
        messageSource.setValue(lblEnvironmentFactorDescription, Message.GXE_ENVIRONMENT_FACTOR_DESCRIPTION);
        messageSource.setValue(lblGenotypesFactorDescription, Message.GXE_GENOTYPES_FACTOR_DESCRIPTION);
        messageSource.setValue(lblEnvironmentGroupsHeader, Message.GXE_ENVIRONMENT_GROUPS_HEADER);
        messageSource.setValue(lblEnvironmentGroupsDescription, Message.GXE_ENVIRONMENT_GROUPS_DESCRIPTION);
        messageSource.setValue(lblEnvironmentGroupsSpecify, Message.GXE_ENVIRONMENT_GROUPS_SPECIFY);
        messageSource.setValue(lblReviewSelectedDataset, Message.GXE_REVIEW_SELECTED_DATASET);
        messageSource.setValue(lblFactorTableDescription, Message.GXE_FACTOR_TABLE_DESCRIPTION);
        messageSource.setValue(lblVariateTableDescription, Message.GXE_TRAIT_TABLE_DESCRIPTION);
    }
    
	@Override
	public void instantiateComponents() {
		managerFactory = managerFactoryProvider.getManagerFactoryForProject(currentProject);

    	setVariatesCheckboxState(new HashMap<String, Boolean>());
    	
    	lblEnvironmentFactorHeader = new Label();
    	lblEnvironmentFactorHeader.setStyleName(Bootstrap.Typography.H2.styleName());
    	
    	lblEnvironmentFactorDescription = new Label();
    	lblGenotypesFactorDescription = new Label();
    	
    	lblEnvironmentGroupsHeader = new Label();
    	lblEnvironmentGroupsHeader.setStyleName(Bootstrap.Typography.H2.styleName());
    	
    	lblEnvironmentGroupsDescription = new Label();
    	
    	lblEnvironmentGroupsSpecify = new Label();
    	
    	lblReviewSelectedDataset = new Label();
    	lblReviewSelectedDataset.setStyleName(Bootstrap.Typography.H2.styleName());
    	
    	lblFactorTableHeader = new Label("<span class='bms-factors' style='position: relative; top:-2px; color: #39B54A; "
    			+ "font-size: 22px; font-weight: bold;'></span><b>&nbsp;FACTORS</b>",Label.CONTENT_XHTML);
    	lblFactorTableHeader.setStyleName(Bootstrap.Typography.H3.styleName());
    	
    	lblFactorTableDescription = new Label();
    	
    	lblVariateTableHeader = new Label("<span class='bms-variates' style='position: relative; top:-2px; color: #B8D433; font-size: 22px; font-weight: bold;'></span><b>&nbsp;TRAITS</b>",Label.CONTENT_XHTML);
    	lblVariateTableHeader.setStyleName(Bootstrap.Typography.H3.styleName());
    	
    	lblVariateTableDescription = new Label();
                
        lblStudyTreeDetailTitle = new Label();
        lblStudyTreeDetailTitle.setStyleName(Bootstrap.Typography.H1.styleName());

        factors = initializeFactorsTable();
        factors.setImmediate(true);
        initializeVariatesTable();
        variates.setImmediate(true);
        
        chkVariatesSelectAll = new CheckBox();
        chkVariatesSelectAll.setImmediate(true);
        chkVariatesSelectAll.setCaption("Select All");
        
        selectSpecifyEnvironment = new Select();
        selectSpecifyEnvironment.setSizeFull();
        selectSpecifyEnvironment.setImmediate(true);
                
        selectSpecifyGenotypes = new Select();
        selectSpecifyGenotypes.setSizeFull();
        
        selectSpecifyEnvironmentGroups = new Select();
        selectSpecifyEnvironmentGroups.setSizeFull();
        
        populateFactorsVariatesByDataSetId(currentStudy, factors, variates);
        
        //initialize buttons
        btnCancel = new Button();
        btnNext = new Button();
        btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
	}

	@Override
	public void initializeValues() {

		for (Iterator<?> i = selectSpecifyEnvironment.getItemIds().iterator(); i.hasNext();){
        	selectSpecifyEnvironment.select(i.next());
        	break;
        }
        
        for (Iterator<?> i = selectSpecifyGenotypes.getItemIds().iterator(); i.hasNext();){
        	selectSpecifyGenotypes.select(i.next());
        	break;
        }
       
        Object item = "None";
        selectSpecifyEnvironmentGroups.addItem(item);
        selectSpecifyEnvironmentGroups.select(item);
        
        environmentNames.clear();
        try {
			trialEnvironments = getStudyDataManager().getTrialEnvironmentsInDataset(getCurrentDataSetId());
			for (Variable var : trialEnvironments.getVariablesByLocalName(selectSpecifyEnvironment.getValue().toString())){
				if (var.getValue() != null && !"".equals(var.getValue())) {
                    environmentNames.add(var.getValue());
                }
			}
        } catch (MiddlewareQueryException e) {
			LOG.error("Error getting trial environments" + e);
		}
	}

	@Override
	public void addListeners() {
		selectSpecifyEnvironment.addListener(new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				
				try{
					factors.removeAllItems();
					variates.removeAllItems();
					environmentNames.clear();
					
					if (selectSpecifyEnvironment.getValue() == null) {
                        return;
                    }
					
				} catch(Exception e) {
					LOG.error("Error changing values for environment factor", e);
				}
				
				 try {
						trialEnvironments = getStudyDataManager().getTrialEnvironmentsInDataset(getCurrentDataSetId());
						for (Variable var : trialEnvironments.getVariablesByLocalName(selectSpecifyEnvironment.getValue().toString())){
							if (var.getValue() != null && !"".equals(var.getValue())) {
                                environmentNames.add(var.getValue());
                            }
						}
			        } catch (MiddlewareQueryException e) {
						LOG.error("Error getting trial environments", e);
					}
			
				populateFactorsVariatesByDataSetId(currentStudy, factors, variates);
				
				managerFactory.close();
				
			}
		});

        selectAllListener = new Property.ValueChangeListener(){

			private static final long serialVersionUID = -6750267436054378894L;

			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean val = (Boolean) event.getProperty().getValue();
				BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) variates.getContainerDataSource();
				for (Object itemId : container.getItemIds()){
					container.getItem(itemId).getBean().setActive(val);
				}
				for (Entry<String, Boolean> entry : variatesCheckboxState.entrySet()){
					variatesCheckboxState.put(entry.getKey(), val);
				}
				
				refreshing = true;
				variates.refreshRowCache();
				refreshing = false;
			}
        	
        };
        chkVariatesSelectAll.addListener(selectAllListener);
        
		btnCancel.setImmediate(true);
        btnCancel.addListener(new Button.ClickListener() {
	
			private static final long serialVersionUID = 4719456133687409089L;

			@Override
			public void buttonClick(ClickEvent event) {
				selectSpecifyEnvironment.select((Object) null);
				selectSpecifyEnvironment.select(selectSpecifyEnvironment.getItemIds().iterator().next());
				selectSpecifyGenotypes.select(selectSpecifyGenotypes.getItemIds().iterator().next());
				selectSpecifyEnvironmentGroups.select((Object) "Analyze All");
				chkVariatesSelectAll.setValue(false);
				
				
			}
		});
        btnNext.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8377610125826448065L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (selectSpecifyEnvironment.getValue() != null 
						&& selectSpecifyGenotypes.getValue() != null ){
					getGxeAnalysisComponentPanel().generateTabContent(currentStudy, 
							selectSpecifyEnvironment.getValue().toString(), 
							selectSpecifyGenotypes.getValue().toString(), 
							selectSpecifyEnvironmentGroups.getValue().toString(), 
							variatesCheckboxState, MultiSiteAnalysisSelectPanel.this);
				}
				
			}
		});
	}

	@Override
	public void layoutComponents() {
        
		//Sub-Layouts
        specifyEnvironmentFactorLayout = new HorizontalLayout();
        specifyEnvironmentFactorLayout.setSpacing(true);
        specifyGenotypesFactorLayout = new HorizontalLayout();
        specifyGenotypesFactorLayout.setSpacing(true);
        specifyEnvironmentGroupsLayout = new HorizontalLayout();
        specifyEnvironmentGroupsLayout.setSpacing(true);
        datasetVariablesDetailLayout = new VerticalLayout();

    	specifyEnvironmentFactorLayout.addComponent(lblEnvironmentFactorDescription);
    	specifyEnvironmentFactorLayout.addComponent(selectSpecifyEnvironment);
    
    	specifyGenotypesFactorLayout.addComponent(lblGenotypesFactorDescription);
    	specifyGenotypesFactorLayout.addComponent(selectSpecifyGenotypes);
    
    	specifyEnvironmentGroupsLayout.addComponent(lblEnvironmentGroupsSpecify);
    	specifyEnvironmentGroupsLayout.addComponent(selectSpecifyEnvironmentGroups);
    
    	buttonArea = layoutButtonArea();
    	
    	//Main Layout
    	generalLayout = new VerticalLayout();
        generalLayout.setSpacing(true);
        generalLayout.setMargin(true);
        
    	generalLayout.addComponent(lblEnvironmentFactorHeader);
    	generalLayout.addComponent(specifyEnvironmentFactorLayout);
    	generalLayout.addComponent(specifyGenotypesFactorLayout);
    	
    	generalLayout.addComponent(lblEnvironmentGroupsDescription);
    	generalLayout.addComponent(specifyEnvironmentGroupsLayout);	
    	
	    generalLayout.addComponent(lblReviewSelectedDataset);
	    generalLayout.addComponent(lblFactorTableHeader);
	    generalLayout.addComponent(lblFactorTableDescription);
	    generalLayout.addComponent(factors);
	    generalLayout.addComponent(lblVariateTableHeader);
	    generalLayout.addComponent(lblVariateTableDescription);
	    generalLayout.addComponent(variates);
	    generalLayout.addComponent(chkVariatesSelectAll);
	    
	    generalLayout.addComponent(datasetVariablesDetailLayout);
	    generalLayout.addComponent(buttonArea);
	    
	    generalLayout.setComponentAlignment(buttonArea, Alignment.TOP_LEFT);
	    
	    addComponent(generalLayout);
	}
    
    protected Table initializeFactorsTable() {
        
        final Table tblFactors = new Table();
        tblFactors.setImmediate(true);
        tblFactors.setWidth("100%");
        tblFactors.setHeight("170px");
        
        BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
        container.setBeanIdProperty("id");
        tblFactors.setContainerDataSource(container);
        
        String[] columns = new String[] {"name", DESCRIPTION_COLUMN};
        String[] columnHeaders = new String[] {"Name", DESCRIPTION};
        tblFactors.setVisibleColumns(columns);
        tblFactors.setColumnHeaders(columnHeaders);
        
        
        tblFactors.setItemDescriptionGenerator(new ItemDescriptionGenerator() {                             

			private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public String generateDescription(Component source, Object itemId, Object propertyId) {
        	    	 BeanContainer<Integer, FactorModel> container = (BeanContainer<Integer, FactorModel>) tblFactors.getContainerDataSource();
        	    	 FactorModel fm = container.getItem(itemId).getBean();
        	    	 
        	    	 StringBuilder sb = new StringBuilder();
        	    	 sb.append(String.format("<span class=\"gcp-table-header-bold\">%s</span><br>", fm.getName()));
        	    	 sb.append(String.format("<span>Property:</span> %s<br>", fm.getTrname()));
        	    	 sb.append(String.format("<span>Scale:</span> %s<br>", fm.getScname()));
        	    	 sb.append(String.format("<span>Method:</span> %s<br>", fm.getTmname()));
        	    	 sb.append(String.format("<span>Data Type:</span> %s", fm.getDataType()));
        	                                                                        
        	         return sb.toString();
        	     }
        	});
        
        return tblFactors;
    }
    
    protected void initializeVariatesTable() {
        
        variates = new Table();
        variates.setImmediate(true);
        variates.setWidth("100%");
        variates.setHeight("100%");
        variates.setColumnExpandRatio("", 0.5f);
        variates.setColumnExpandRatio("name", 1);
        variates.setColumnExpandRatio(DESCRIPTION_COLUMN, 4);
        variates.setColumnExpandRatio(TESTEDIN, 1);
        
        variates.addGeneratedColumn(TESTEDIN, new Table.ColumnGenerator(){

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				
					BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) source.getContainerDataSource();
					VariateModel vm = container.getItem(itemId).getBean();
					
					int testedIn = getTestedIn(selectSpecifyEnvironment.getValue().toString(), environmentNames, vm.getVariableId(), getCurrentDataSetId(), trialEnvironments);
					if (testedIn > 3){
						vm.setActive(true);
					}
					
					return String.format("%s of %s", testedIn, environmentNames.size());
					
			}});
        
        variates.addGeneratedColumn("", new Table.ColumnGenerator(){

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				
				BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) variates.getContainerDataSource();
				final VariateModel vm = container.getItem(itemId).getBean();
				
				final CheckBox checkBox = new CheckBox();
				checkBox.setImmediate(true);
				checkBox.setVisible(true);
				checkBox.addListener(new Property.ValueChangeListener() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(final ValueChangeEvent event) {
						Boolean val = (Boolean) event.getProperty()
								.getValue();
						getVariatesCheckboxState().put(vm.getName(), val);
						vm.setActive(val);
						
						if (!val){
							chkVariatesSelectAll.removeListener(selectAllListener);
							chkVariatesSelectAll.setValue(val);
							chkVariatesSelectAll.addListener(selectAllListener);
						}
					
					}
				});
				
				if (!refreshing){
					int testedIn = getTestedIn(selectSpecifyEnvironment.getValue().toString(), environmentNames, vm.getVariableId(), getCurrentDataSetId(), trialEnvironments);
					if (testedIn > 3){
						vm.setActive(true);
					}else{
						vm.setActive(false);
					}
				}

				if (vm.getActive()) {
					checkBox.setValue(true);
					getVariatesCheckboxState().put(vm.getName(), true);
				} else {
					checkBox.setValue(false);
					getVariatesCheckboxState().put(vm.getName(), false);
				}
				
				return checkBox;
				
			}
        	
        });
        
        variates.setItemDescriptionGenerator(new ItemDescriptionGenerator() {                             

			private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public String generateDescription(Component source, Object itemId, Object propertyId) {
        	    	 BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) variates.getContainerDataSource();
        	    	 VariateModel vm = container.getItem(itemId).getBean();
        	    	 
        	    	 StringBuilder sb = new StringBuilder();
        	    	 sb.append(String.format("<span class=\"gcp-table-header-bold\">%s</span><br>", vm.getDisplayName()));
        	    	 sb.append(String.format("<span>Property:</span> %s<br>", vm.getTrname()));
        	    	 sb.append(String.format("<span>Scale:</span> %s<br>", vm.getScname()));
        	    	 sb.append(String.format("<span>Method:</span> %s<br>", vm.getTmname()));
        	    	 sb.append(String.format("<span>Data Type:</span> %s", vm.getDatatype()));
        	                                                                        
        	         return sb.toString();
        	     }
        	});
		
        BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
        container.setBeanIdProperty("id");
        variates.setContainerDataSource(container);
        
        String[] columns = new String[] {"", "displayName", DESCRIPTION_COLUMN,TESTEDIN};
        String[] columnHeaders = new String[] {"<span class='glyphicon glyphicon-ok'></span>","Name", DESCRIPTION,"Tested In"};
        variates.setVisibleColumns(columns);
        variates.setColumnHeaders(columnHeaders);
        variates.setColumnWidth("", 18);

    }
   
    
    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSizeFull();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);

        Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);
        spacer.setSizeFull();
        
        buttonLayout.addComponent(spacer);
        buttonLayout.setExpandRatio(spacer,1.0F);
        buttonLayout.addComponent(btnCancel);
        buttonLayout.addComponent(btnNext);

        return buttonLayout;
    }
    
    public void populateFactorsVariatesByDataSetId(Study study, Table factors, Table variates) {

        try {
        	DataSet ds = DatasetUtil.getMeansDataSet(studyDataManager, study.getId());
            DataSet trialDs = DatasetUtil.getTrialDataSet(studyDataManager,study.getId());
        	
            if (ds==null || trialDs==null) {
                return;
            }
            
            List<FactorModel> factorList = new ArrayList<FactorModel>();
            List<VariateModel> variateList = new ArrayList<VariateModel>();
            
            populateEnvironmentDropdown(trialDs);
			
			populateGenotypeDropdown(ds, factorList);
            
            populateTraitGroup(ds, variateList);
           
            this.setCurrentDatasetName(ds.getName());
            this.setCurrentDataSetId(ds.getId());
            
            updateFactorsTable(factorList, factors);
            updateVariatesTable(variateList, factors, variates);

        } catch (MiddlewareQueryException e) {
            LOG.error("Error getting dataset(s) for MSA screen", e);
        }
    }

	protected void populateTraitGroup(DataSet ds, List<VariateModel> variateList) {
		for (VariableType variate : ds.getVariableTypes().getVariates().getVariableTypes()){
			
			VariateModel vm = new VariateModel();
			vm.setId(variate.getRank());
			vm.setVariableId(variate.getId());
			vm.setName(variate.getLocalName());
			vm.setDisplayName(variate.getLocalName().replace("_Means", ""));
			vm.setScname(variate.getStandardVariable().getScale().getName());
			vm.setScaleid(variate.getStandardVariable().getScale().getId());
			vm.setTmname(variate.getStandardVariable().getMethod().getName());
			vm.setTmethid(variate.getStandardVariable().getMethod().getId());
			vm.setTrname(variate.getStandardVariable().getProperty().getName());
			vm.setTraitid(variate.getStandardVariable().getProperty().getId());
			vm.setDescription(variate.getLocalDescription());
			vm.setDatatype(variate.getStandardVariable().getDataType().getName());
			if (!"error estimate".equalsIgnoreCase(variate.getStandardVariable().getMethod().getName()) 
					&& !variate.getStandardVariable().getMethod().getName().equalsIgnoreCase("error estimate (" + variate.getLocalName().replace("_UnitErrors", "") + ")") 
					&& !"ls blups".equalsIgnoreCase(variate.getStandardVariable().getMethod().getName())){
				vm.setActive(false);
				variateList.add(vm);
			}
			
		}
	}

	protected void populateGenotypeDropdown(DataSet ds, List<FactorModel> factorList) {
		for (VariableType factor : ds.getVariableTypes().getFactors().getVariableTypes()){
			
			FactorModel fm = new FactorModel();
			fm.setId(factor.getRank());
			fm.setName(factor.getLocalName());
			fm.setScname(factor.getStandardVariable().getScale().getName());
			fm.setScaleid(factor.getStandardVariable().getScale().getId());
			fm.setTmname(factor.getStandardVariable().getMethod().getName());
			fm.setTmethid(factor.getStandardVariable().getMethod().getId());
			fm.setTrname(factor.getStandardVariable().getProperty().getName());
			fm.setDescription(factor.getLocalDescription());
			fm.setTraitid(factor.getStandardVariable().getProperty().getId());
			fm.setDataType(factor.getStandardVariable().getDataType().getName());
			
			if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM && factor.getId() != TermId.ENTRY_TYPE.getId()){
				factorList.add(fm);
				getSelectSpecifyGenotypes().addItem(fm.getName());
			}
		}
	}

	protected void populateEnvironmentDropdown(DataSet trialDs) {
		for (VariableType factor : trialDs.getVariableTypes().getFactors().getVariableTypes()){
			
			if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
					&& factor.getStandardVariable().getStoredIn().getId() != TermId.TRIAL_INSTANCE_STORAGE.getId()){
				selectSpecifyEnvironmentGroups.addItem(factor.getLocalName());
			}
			
			// only TRIAL_ENVIRONMENT_INFO_STORAGE(1020) TRIAL_INSTANCE_STORAGE(1021) factors in selectEnv dropdown
			if (factor.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()
					|| factor.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()){
		    	getSelectSpecifyEnvironment().addItem(factor.getLocalName());
			}
		}
	}
    
    

	private void updateFactorsTable(List<FactorModel> factorList, Table factors){
	   Object[] oldColumns = factors.getVisibleColumns();
       String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
       
       BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
       container.setBeanIdProperty("id");
       factors.setContainerDataSource(container);
       
       for (FactorModel f : factorList ){
    	   container.addBean(f);
       }
       
       factors.setContainerDataSource(container);
       
       factors.setVisibleColumns(columns);
    }
    
    
    private void updateVariatesTable(List<VariateModel> variateList,Table factors, Table variates){
 	   	BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
        container.setBeanIdProperty("id");
        this.variates.setContainerDataSource(container);
        
        for (VariateModel v : variateList ){
     	   container.addBean(v);
        }
        
        this.variates.setContainerDataSource(container);
        
        this.variates.setVisibleColumns(new String[]{ "","displayName", DESCRIPTION_COLUMN,TESTEDIN});
        this.variates.setColumnHeaders(new String[]{ "<span class='glyphicon glyphicon-ok'></span>", "Name", DESCRIPTION, "Tested In"});
    }
	
	private int getTestedIn(String envFactorName, List<String> environmentNames , Integer variableId , Integer meansDataSetId ,TrialEnvironments trialEnvironments){
		int counter = 0;
		
		for (String environmentName : environmentNames){
			try{
				TrialEnvironment te = trialEnvironments.findOnlyOneByLocalName(envFactorName, environmentName);
				if (te!=null){
					long count = studyDataManager.countStocks(
							meansDataSetId
						,te.getId()
						,variableId
							);
					if (count > 0) {
                        counter++;
                    }
				}
			} catch (Exception e) {
				LOG.error("Error counting stocks", e);
			}
			
		
		}
		
		return counter;
	}
	
	//SETTERS AND GETTERS
	public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public Study getCurrentStudy() {
        return currentStudy;
    }

    public void setCurrentStudy(Study currentStudy) {
        this.currentStudy = currentStudy;
    }

    public Integer getCurrentRepresentationId() {
        return currentRepresentationId;
    }

    public void setCurrentRepresentationId(Integer currentRepresentationId) {
        this.currentRepresentationId = currentRepresentationId;
    }
    
    public Integer getCurrentDataSetId() {
        return currentDataSetId;
    }

    public void setCurrentDataSetId(Integer currentDataSetId) {
        this.currentDataSetId = currentDataSetId;
    }

    public String getCurrentDatasetName() {
        return currentDatasetName;
    }

    public void setCurrentDatasetName(String currentDatasetName) {
        this.currentDatasetName = currentDatasetName;
    }

	public MultiSiteAnalysisPanel getGxeAnalysisComponentPanel() {
		return gxeAnalysisComponentPanel;
	}

	public void setGxeAnalysisComponentPanel(MultiSiteAnalysisPanel gxeAnalysisComponentPanel) {
		this.gxeAnalysisComponentPanel = gxeAnalysisComponentPanel;
	}
	
    public StudyDataManager getStudyDataManager() {
    	if (this.studyDataManager == null) {
            this.studyDataManager = managerFactory.getNewStudyDataManager();
        }
		return this.studyDataManager;
	}

	public Map<String, Boolean> getVariatesCheckboxState() {
		return variatesCheckboxState;
	}

	public void setVariatesCheckboxState(Map<String, Boolean> variatesCheckboxState) {
		this.variatesCheckboxState = variatesCheckboxState;
	}
	
	@Override
	public Object getData(){
		return this.getCurrentStudy();
		
	}

	public Select getSelectSpecifyEnvironment() {
		return selectSpecifyEnvironment;
	}

	public Select getSelectSpecifyGenotypes() {
		return selectSpecifyGenotypes;
	}

}
