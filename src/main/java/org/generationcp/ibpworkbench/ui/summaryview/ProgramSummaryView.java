
package org.generationcp.ibpworkbench.ui.summaryview;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.generationcp.browser.study.containers.StudyDetailsQueryFactory;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class ProgramSummaryView extends VerticalLayout implements InitializingBean {

	public static final int COMPONENT_INDEX_OF_TABLES = 1;

	private static final String DESCRIPTION = "description";

	private static final String NAME = "name";

	private static final String CREATED_AT = "createdAt";

	private static final long serialVersionUID = 7007366368354834359L;

	private static final Logger LOG = LoggerFactory.getLogger(ProgramSummaryView.class);

	private static final String STUDY_NAME = "studyName";

	private static final String TITLE = "title";

	private static final String OBJECTIVE = "objective";

	private static final String START_DATE = "startDate";

	private static final String END_DATE = "endDate";

	private static final String PI_NAME = "piName";

	private static final String SITE_NAME = "siteName";

	private static final String STUDY_TYPE = "studyType";
	

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;
	
	@Autowired
	private WorkbenchDataManager workbenchDataManager;
	
	@Autowired
	private StudyDataManager studyDataManager;

	private Label header;
	private PopupView toolsPopup;
	private ToolsDropDown toolsDropDown;

	private PagedTable programActivitiesTable;
	private PagedTable programTrialsTable;
	private PagedTable programNurseriesTable;
	private PagedTable programStudiesTable;

	private int activityCount = 0;
	private int trialCount = 0;
	private int nurseryCount = 0;
	private int studiesCount = 0;
	private Button exportBtn;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initializeComponents();
		this.initializeActions();
		this.initializeData();
		this.initializeLayout();
	}

	void initializeLayout() {
		final HorizontalLayout headerArea = new HorizontalLayout();
		headerArea.setDebugId("headerArea");
		headerArea.setSizeUndefined();
		headerArea.setWidth("100%");

		final Embedded headerImg = new Embedded(null, new ThemeResource("images/recent-activity.png"));
		headerImg.setDebugId("headerImg");
		headerImg.setStyleName("header-img");

		final HorizontalLayout headerTitleWrap = new HorizontalLayout();
		headerTitleWrap.setDebugId("headerTitleWrap");
		headerTitleWrap.setSizeUndefined();
		headerTitleWrap.setSpacing(true);

		headerTitleWrap.addComponent(headerImg);
		headerTitleWrap.addComponent(this.header);

		headerArea.addComponent(headerTitleWrap);

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");
		buttonLayout.setSizeUndefined();

		buttonLayout.addComponent(this.exportBtn);
		buttonLayout.addComponent(this.toolsPopup);
		buttonLayout.setSpacing(true);

		headerArea.addComponent(buttonLayout);
		headerArea.setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
		headerArea.setComponentAlignment(headerTitleWrap, Alignment.BOTTOM_LEFT);
		headerArea.setMargin(true, false, false, false);

		// add Program Studies All as default table
		this.addComponent(headerArea);
		this.addComponent(this.programStudiesTable);
		this.updateHeaderAndTableControls(this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ALL), this.studiesCount, this.programStudiesTable);

		this.setWidth("100%");
		this.setSpacing(false);

	}

	void initializeComponents() {
		this.header = new Label(this.messageSource.getMessage(Message.ACTIVITIES));
		this.header.setDebugId("header");
		this.header.setStyleName(Bootstrap.Typography.H2.styleName());

		this.toolsDropDown =
				new ToolsDropDown(this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ACTIVITIES),
						this.messageSource.getMessage(Message.PROGRAM_SUMMARY_TRIALS),
						this.messageSource.getMessage(Message.PROGRAM_SUMMARY_NURSERY),
						this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ALL));

		this.toolsPopup = new PopupView(toolsDropDown);
		this.toolsPopup.setDebugId("toolsPopup");
		this.toolsPopup.setStyleName("btn-dropdown");
		this.toolsPopup.setHideOnMouseOut(false);
		
		this.exportBtn = new Button("<span class='glyphicon glyphicon-export' style='right: 4px'></span>EXPORT");
		this.exportBtn.setDebugId("exportBtn");
		this.exportBtn.setHtmlContentAllowed(true);

		this.programActivitiesTable = this.buildActivityTable();
		this.programTrialsTable = this.buildTrialSummaryTable();
		this.programNurseriesTable = this.buildNurserySummaryTable();
		this.programStudiesTable = this.buildProgramStudiesTable();

	}
	
	private void initializeActions(){
		this.toolsPopup.addListener(new PopupView.PopupVisibilityListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void popupVisibilityChange(PopupView.PopupVisibilityEvent event) {
				if (!event.isPopupVisible()) {
					int selection = toolsDropDown.getSelectedItem();

					if (selection >= 0) {
						ProgramSummaryView.this.removeComponent(ProgramSummaryView.this.getComponent(COMPONENT_INDEX_OF_TABLES));
					} else {
						return;
					}

					switch (selection) {
						case 0:
							ProgramSummaryView.this.addComponent(ProgramSummaryView.this.programActivitiesTable, COMPONENT_INDEX_OF_TABLES);
							ProgramSummaryView.this.updateHeaderAndTableControls(
									ProgramSummaryView.this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ACTIVITIES),
									ProgramSummaryView.this.activityCount, ProgramSummaryView.this.programActivitiesTable);
							break;
						case 1:
							ProgramSummaryView.this.addComponent(ProgramSummaryView.this.programTrialsTable, COMPONENT_INDEX_OF_TABLES);
							ProgramSummaryView.this.updateHeaderAndTableControls(
									ProgramSummaryView.this.messageSource.getMessage(Message.PROGRAM_SUMMARY_TRIALS), ProgramSummaryView.this.trialCount,
									ProgramSummaryView.this.programTrialsTable);
							break;
						case 2:
							ProgramSummaryView.this.addComponent(ProgramSummaryView.this.programNurseriesTable, COMPONENT_INDEX_OF_TABLES);
							ProgramSummaryView.this.updateHeaderAndTableControls(
									ProgramSummaryView.this.messageSource.getMessage(Message.PROGRAM_SUMMARY_NURSERY),
									ProgramSummaryView.this.nurseryCount, ProgramSummaryView.this.programNurseriesTable);
							break;
						case 3:
							ProgramSummaryView.this.addComponent(ProgramSummaryView.this.programStudiesTable, COMPONENT_INDEX_OF_TABLES);
							ProgramSummaryView.this.updateHeaderAndTableControls(
									ProgramSummaryView.this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ALL), ProgramSummaryView.this.studiesCount,
									ProgramSummaryView.this.programStudiesTable);
							break;
						default:
							break;
					}

				}
			}
		});
		
		this.exportBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				String tableName = ProgramSummaryView.this.header.getValue().toString().split("\\[")[0].trim();
				String programName = ProgramSummaryView.this.sessionData.getSelectedProject().getProjectName();

				ExcelExport export = new ExcelExport((Table) ProgramSummaryView.this.getComponent(1), tableName);
				export.setReportTitle(programName + " - " + tableName);
				export.setExportFileName((tableName + " " + programName + ".xls").replaceAll(" ", "_"));
				export.setDisplayTotals(false);

				ProgramSummaryView.LOG.info("Exporting " + tableName + ": " + export.getExportFileName() + ".xls will be downloaded in a moment.");
				export.export();

			}
		});

	}

	private PagedTable buildActivityTable() {
		final PagedTable activityTable = new PagedTableWithUpdatedControls();
		activityTable.setImmediate(true);

		BeanContainer<Integer, ProjectActivity> container = new BeanContainer<Integer, ProjectActivity>(ProjectActivity.class);
		container.setBeanIdProperty("projectActivityId");
		activityTable.setContainerDataSource(container);

		String[] columns = new String[] {CREATED_AT, NAME, DESCRIPTION};
		activityTable.setVisibleColumns(columns);
		activityTable.setWidth("100%");

		this.messageSource.setColumnHeader(activityTable, CREATED_AT, Message.DATE);
		this.messageSource.setColumnHeader(activityTable, NAME, Message.NAME);
		this.messageSource.setColumnHeader(activityTable, DESCRIPTION, Message.DESCRIPTION_HEADER);

		return activityTable;
	}

	private PagedTable buildTrialSummaryTable() {
		final PagedTable trialSummaryTable = new PagedTableWithUpdatedControls();
		trialSummaryTable.setImmediate(true);

		BeanContainer<Integer, StudyDetails> container = new BeanContainer<Integer, StudyDetails>(StudyDetails.class);
		container.setBeanIdProperty("id");
		trialSummaryTable.setContainerDataSource(container);

		String[] columns = this.getTrialOrNuseryTableColumns();
		trialSummaryTable.setVisibleColumns(columns);
		trialSummaryTable.setWidth("100%");

		this.messageSource.setColumnHeader(trialSummaryTable, columns[0], Message.NAME_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[1], Message.TITLE_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[2], Message.OBJECTIVE_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[3], Message.START_DATE_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[4], Message.END_DATE_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[5], Message.PI_NAME_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[6], Message.SITE_NAME_LABEL);

		return trialSummaryTable;
	}

	private PagedTable buildNurserySummaryTable() {
		final PagedTable nurserySummaryTable = new PagedTableWithUpdatedControls();
		nurserySummaryTable.setImmediate(true);

		BeanContainer<Integer, StudyDetails> container = new BeanContainer<Integer, StudyDetails>(StudyDetails.class);
		container.setBeanIdProperty("id");
		nurserySummaryTable.setContainerDataSource(container);

		String[] columns = this.getTrialOrNuseryTableColumns();
		nurserySummaryTable.setVisibleColumns(columns);
		nurserySummaryTable.setWidth("100%");

		this.messageSource.setColumnHeader(nurserySummaryTable, columns[0], Message.NAME_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[1], Message.TITLE_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[2], Message.OBJECTIVE_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[3], Message.START_DATE_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[4], Message.END_DATE_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[5], Message.PI_NAME_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[6], Message.SITE_NAME_LABEL);

		return nurserySummaryTable;
	}

	private PagedTable buildProgramStudiesTable() {
		final PagedTable allStudiesTable = new PagedTableWithUpdatedControls();
		allStudiesTable.setImmediate(true);

		BeanContainer<Integer, StudyDetails> container = new BeanContainer<Integer, StudyDetails>(StudyDetails.class);
		container.setBeanIdProperty("id");
		allStudiesTable.setContainerDataSource(container);

		String[] columns = this.getProgramStudiesTableColumns();
		allStudiesTable.setVisibleColumns(columns);
		allStudiesTable.setWidth("100%");

		this.messageSource.setColumnHeader(allStudiesTable, columns[0], Message.NAME_LABEL);
		this.messageSource.setColumnHeader(allStudiesTable, columns[1], Message.TITLE_LABEL);
		this.messageSource.setColumnHeader(allStudiesTable, columns[2], Message.OBJECTIVE_LABEL);
		this.messageSource.setColumnHeader(allStudiesTable, columns[3], Message.START_DATE_LABEL);
		this.messageSource.setColumnHeader(allStudiesTable, columns[4], Message.END_DATE_LABEL);
		this.messageSource.setColumnHeader(allStudiesTable, columns[5], Message.PI_NAME_LABEL);
		this.messageSource.setColumnHeader(allStudiesTable, columns[6], Message.SITE_NAME_LABEL);
		this.messageSource.setColumnHeader(allStudiesTable, columns[7], Message.STUDY_TYPE_LABEL);

		return allStudiesTable;
	}
	
	void initializeData(){
		long projectActivitiesCount;
		final Project project = this.sessionData.getSelectedProject();
		projectActivitiesCount = this.workbenchDataManager.countProjectActivitiesByProjectId(project.getProjectId());

		final List<ProjectActivity> activityList = this.workbenchDataManager.getProjectActivitiesByProjectId(project.getProjectId(),
						0, (int) projectActivitiesCount);
		this.populateActivityTable(activityList);
		
		final StudyDetailsQueryFactory trialFactory =
				new StudyDetailsQueryFactory(this.studyDataManager, StudyType.T, Arrays
						.asList(getTrialOrNuseryTableColumns()), project.getUniqueID());
		this.populateTrialSummaryTable(trialFactory);

		final StudyDetailsQueryFactory nurseryFactory =
				new StudyDetailsQueryFactory(studyDataManager, StudyType.N, Arrays
						.asList(this.getTrialOrNuseryTableColumns()), project.getUniqueID());
		this.populateNurserySummaryTable(nurseryFactory);

		StudyDetailsQueryFactory allStudiesTable =
				new StudyDetailsQueryFactory(studyDataManager, null, Arrays.asList(getProgramStudiesTableColumns()), project.getUniqueID());
		this.populateProgramStudiesTable(allStudiesTable);
	}

	void populateActivityTable(List<ProjectActivity> activityList) {
		BeanContainer<Integer, ProjectActivity> container = new BeanContainer<Integer, ProjectActivity>(ProjectActivity.class);
		container.setBeanIdProperty("projectActivityId");
		this.programActivitiesTable.setContainerDataSource(container);

		for (ProjectActivity activity : activityList) {
			container.addBean(activity);
		}

		this.activityCount = activityList.size();

		this.programActivitiesTable.setContainerDataSource(container);
	}

	void populateTrialSummaryTable(StudyDetailsQueryFactory factory) {
		LazyQueryContainer container = new LazyQueryContainer(factory, false, 10);
		String[] columns = this.getTrialOrNuseryTableColumns();

		for (String columnId : columns) {
			container.addContainerProperty(columnId, String.class, null);
		}

		container.getQueryView().getItem(0);

		this.trialCount = factory.getNumberOfItems();

		this.programTrialsTable.setContainerDataSource(container);
		this.programTrialsTable.setImmediate(true);
	}

	void populateNurserySummaryTable(StudyDetailsQueryFactory factory) {
		LazyQueryContainer container = new LazyQueryContainer(factory, false, 10);
		String[] columns = this.getTrialOrNuseryTableColumns();

		for (String columnId : columns) {
			container.addContainerProperty(columnId, String.class, null);
		}

		container.getQueryView().getItem(0);

		this.nurseryCount = factory.getNumberOfItems();

		this.programNurseriesTable.setContainerDataSource(container);
		this.programNurseriesTable.setImmediate(true);
	}

	void populateProgramStudiesTable(StudyDetailsQueryFactory factory) {
		LazyQueryContainer container = new LazyQueryContainer(factory, false, 10);
		String[] columns = this.getProgramStudiesTableColumns();

		for (String columnId : columns) {
			container.addContainerProperty(columnId, String.class, null);
		}

		container.getQueryView().getItem(0);

		this.studiesCount = factory.getNumberOfItems();

		this.programStudiesTable.setContainerDataSource(container);
		this.programStudiesTable.setImmediate(true);
	}

	private class ToolsDropDown implements PopupView.Content {

		private static final long serialVersionUID = -6646207856427647783L;
		private final Button[] choices;
		private Integer selectedItem = -1;
		private final VerticalLayout root = new VerticalLayout();

		public ToolsDropDown(String... selections) {
			this.root.setDebugId("rootToolsDropDown");

			this.choices = new Button[selections.length];

			for (int i = 0; i < selections.length; i++) {
				this.choices[i] = new Button(selections[i]);
				this.choices[i].setDebugId("choices[i]");
				this.choices[i].setStyleName(BaseTheme.BUTTON_LINK);
				this.choices[i].addListener(new ChoiceListener(i));
				this.choices[i].setWidth("100%");
				this.choices[i].setHeight("26px");
				this.root.addComponent(this.choices[i]);
			}

			this.root.setSizeUndefined();
			this.root.setWidth("200px");

		}

		@Override
		public String getMinimizedValueAsHTML() {
			return "<span class='glyphicon glyphicon-cog' style='right: 6px; top: 2px; font-size: 13px; font-weight: 300'></span>" 
					+ messageSource.getMessage(Message.ACTIONS);
		}

		@Override
		public Component getPopupComponent() {
			return this.root;
		}

		public Integer getSelectedItem() {
			int value = this.selectedItem;

			// reset the selected item
			this.selectedItem = -1;

			return value;
		}

		private class ChoiceListener implements Button.ClickListener {

			private static final long serialVersionUID = -1931124754200308585L;
			private final int choice;

			public ChoiceListener(int choice) {
				this.choice = choice;
			}

			@Override
			public void buttonClick(Button.ClickEvent event) {
				ToolsDropDown.this.selectedItem = this.choice;
				ProgramSummaryView.this.toolsPopup.setPopupVisible(false);
			}
		}
	}

	private void updateHeaderAndTableControls(String label, int count, PagedTable table) {
		if (this.getComponent(COMPONENT_INDEX_OF_TABLES).equals(table)) {

			if (count > 0) {
				this.header.setValue(label + " [" + count + "]");
			} else {
				this.header.setValue(label);
			}

			if (this.getComponentCount() > 2) {
				ProgramSummaryView.this.replaceComponent(this.getComponent(2), table.createControls());
			} else if (this.getComponentCount() == 2) {
				ProgramSummaryView.this.addComponent(table.createControls());
			}

		}

		table.setPageLength(10);
	}

	private String[] getTrialOrNuseryTableColumns() {
		return new String[] {ProgramSummaryView.STUDY_NAME, ProgramSummaryView.TITLE, ProgramSummaryView.OBJECTIVE, ProgramSummaryView.START_DATE,
				ProgramSummaryView.END_DATE, ProgramSummaryView.PI_NAME, ProgramSummaryView.SITE_NAME};
	}

	private String[] getProgramStudiesTableColumns() {
		return new String[] {ProgramSummaryView.STUDY_NAME, ProgramSummaryView.TITLE, ProgramSummaryView.OBJECTIVE, ProgramSummaryView.START_DATE,
				ProgramSummaryView.END_DATE, ProgramSummaryView.PI_NAME, ProgramSummaryView.SITE_NAME, ProgramSummaryView.STUDY_TYPE};
	}

	private class PagedTableWithUpdatedControls extends PagedTable {

		/**
		 *
		 */
		private static final long serialVersionUID = -3917644602215395122L;

		public PagedTableWithUpdatedControls() {
			this.setHeight("270px");
		}

		@Override
		public HorizontalLayout createControls() {
			HorizontalLayout controls = super.createControls();

			controls.setMargin(new MarginInfo(true, false, true, false));

			Iterator<Component> iterator = controls.getComponentIterator();

			while (iterator.hasNext()) {
				Component c = iterator.next();
				if (c instanceof HorizontalLayout) {
					Iterator<Component> iterator2 = ((HorizontalLayout) c).getComponentIterator();

					while (iterator2.hasNext()) {
						Component d = iterator2.next();

						if (d instanceof Button) {
							d.setStyleName("");
						}
						if (d instanceof TextField) {
							d.setWidth("30px");
						}

					}
				}
			}
			return controls;
		}

		@Override
		protected String formatPropertyValue(Object rowId, Object colId, Property property) {
			if (property.getType() == Date.class) {
				SimpleDateFormat sdf = DateUtil.getSimpleDateFormat(DateUtil.FRONTEND_TIMESTAMP_FORMAT);
				return property.getValue() == null ? "" : sdf.format((Date) property.getValue());
			}

			return super.formatPropertyValue(rowId, colId, property);
		}

	}
	
	// For test purposes only
	public Table getProgramStudiesTable() {
		return this.programStudiesTable;
	}
	
	// For test purposes only
	public Table getProgramActivitiesTable() {
		return this.programActivitiesTable;
	}
	
	// For test purposes only
	public Table getProgramTrialsTable() {
		return this.programTrialsTable;
	}
	
	// For test purposes only
	public Table getProgramNurseriesTable() {
		return this.programNurseriesTable;
	}
	

}