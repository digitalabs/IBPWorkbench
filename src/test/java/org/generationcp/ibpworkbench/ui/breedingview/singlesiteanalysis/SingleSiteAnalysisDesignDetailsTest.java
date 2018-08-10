package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Property;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

public class SingleSiteAnalysisDesignDetailsTest {
	
	private static final int DATASET_ID = 3;
	private static final int STUDY_ID = 1;
	private static final String DEFAULT_REPLICATES = "REPLICATES";
	private static final String ROW_FACTOR_LABEL = "Specify Row Factor";
	private static final String COLUMN_FACTOR_LABEL = "Specify Column Factor";
	private static final String BLOCK_NO = "BLOCK_NO";
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private SingleSiteAnalysisDetailsPanel ssaDetailsPanel;
	
	@InjectMocks
	private SingleSiteAnalysisDesignDetails ssaDesignDetails;
	
	private BreedingViewInput input;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.input = new BreedingViewInput();
		this.input.setStudyId(STUDY_ID);
		this.input.setDatasetId(DATASET_ID);
		Mockito.doReturn(this.input).when(ssaDetailsPanel).getBreedingViewInput();
		Mockito.doReturn(this.createTestFactors()).when(this.ssaDetailsPanel).getFactorsInDataset();
		
		Mockito.doReturn(COLUMN_FACTOR_LABEL).when(this.messageSource).getMessage(Message.BV_SPECIFY_COLUMN_FACTOR);
		Mockito.doReturn(ROW_FACTOR_LABEL).when(this.messageSource).getMessage(Message.BV_SPECIFY_ROW_FACTOR);
		this.ssaDesignDetails = new SingleSiteAnalysisDesignDetails(this.ssaDetailsPanel);
		this.ssaDesignDetails.setMessageSource(messageSource);
		this.ssaDesignDetails.setStudyDataManager(studyDataManager);
		this.ssaDesignDetails.instantiateComponents();
		this.ssaDesignDetails.initializeValues();
	}
	
	@Test
	public void testInitialization() {
		final ComboBox selDesignType = this.ssaDesignDetails.getSelDesignType();
		Assert.assertEquals(5, selDesignType.getItemIds().size());
		final Iterator<?> designTypeIterator = selDesignType.getItemIds().iterator();
		Assert.assertEquals(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName(), designTypeIterator.next());
		Assert.assertEquals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName(), designTypeIterator.next());
		Assert.assertEquals(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName(), designTypeIterator.next());
		Assert.assertEquals(DesignType.P_REP_DESIGN.getName(), designTypeIterator.next());
		Assert.assertEquals(DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName(), designTypeIterator.next());
		
		this.ssaDesignDetails.addListeners();
		Assert.assertNotNull(selDesignType.getListeners(Property.ValueChangeEvent.class));
	}
	
	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableNonLatin() {

		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()));

		this.ssaDesignDetails.displayDesignElementsBasedOnDesignTypeOfTheStudy();
		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaDesignDetails.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getLblReplicates()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getSelReplicates()));

		final boolean spatialVariablesRequired = false;
		this.verifyRowAndColumnFactorsArePresent(components, spatialVariablesRequired);
		
		Assert.assertEquals(this.ssaDesignDetails.getSelDesignType().getValue(), DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());

		if ((!this.ssaDesignDetails.getSelReplicates().isEnabled() || this.ssaDesignDetails.getSelReplicates().getItemIds().isEmpty()) && !this.ssaDesignDetails
				.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaDesignDetails.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaDesignDetails.getSelBlocks().getItemIds()) {
				Assert.assertEquals(SingleSiteAnalysisDesignDetailsTest.DEFAULT_REPLICATES,
					this.ssaDesignDetails.getSelReplicates().getItemCaption(itemId));
			}
		}
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableLatin() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()));

		this.ssaDesignDetails.displayDesignElementsBasedOnDesignTypeOfTheStudy();
		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaDesignDetails.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getLblReplicates()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getSelReplicates()));

		final boolean spatialVariablesRequired = false;
		this.verifyRowAndColumnFactorsArePresent(components, spatialVariablesRequired);
		
		Assert.assertEquals(this.ssaDesignDetails.getSelDesignType().getValue(), DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());

		if ((!this.ssaDesignDetails.getSelReplicates().isEnabled() || this.ssaDesignDetails.getSelReplicates().getItemIds().isEmpty()) && !this.ssaDesignDetails
				.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaDesignDetails.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaDesignDetails.getSelBlocks().getItemIds()) {
				Assert.assertEquals(SingleSiteAnalysisDesignDetailsTest.DEFAULT_REPLICATES,
					this.ssaDesignDetails.getSelReplicates().getItemCaption(itemId));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignLatin() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()));

		this.ssaDesignDetails.displayDesignElementsBasedOnDesignTypeOfTheStudy();
		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertFalse(components.contains(this.ssaDesignDetails.getLblBlocks()));
		Assert.assertFalse(components.contains(this.ssaDesignDetails.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getLblReplicates()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getSelReplicates()));

		final boolean spatialVariablesRequired = true;
		this.verifyRowAndColumnFactorsArePresent(components, spatialVariablesRequired);
		
		Assert.assertEquals(this.ssaDesignDetails.getSelDesignType().getValue(), DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());

		if ((!this.ssaDesignDetails.getSelReplicates().isEnabled() || this.ssaDesignDetails.getSelReplicates().getItemIds().isEmpty()) && !this.ssaDesignDetails
				.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaDesignDetails.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaDesignDetails.getSelBlocks().getItemIds()) {
				Assert.assertEquals(SingleSiteAnalysisDesignDetailsTest.DEFAULT_REPLICATES,
					this.ssaDesignDetails.getSelReplicates().getItemCaption(itemId));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignNonLatin() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()));

		this.ssaDesignDetails.displayDesignElementsBasedOnDesignTypeOfTheStudy();
		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertFalse(components.contains(this.ssaDesignDetails.getLblBlocks()));
		Assert.assertFalse(components.contains(this.ssaDesignDetails.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getLblReplicates()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getSelReplicates()));

		final boolean spatialVariablesRequired = true;
		this.verifyRowAndColumnFactorsArePresent(components, spatialVariablesRequired);
		
		Assert.assertEquals(this.ssaDesignDetails.getSelDesignType().getValue(), DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());

		if ((!this.ssaDesignDetails.getSelReplicates().isEnabled() || this.ssaDesignDetails.getSelReplicates().getItemIds().isEmpty()) && !this.ssaDesignDetails
				.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaDesignDetails.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaDesignDetails.getSelBlocks().getItemIds()) {
				Assert.assertEquals(SingleSiteAnalysisDesignDetailsTest.DEFAULT_REPLICATES,
					this.ssaDesignDetails.getSelReplicates().getItemCaption(itemId));
			}
		}
	}

	@Test
	public void testDesignTypeRandomizedBlockDesign() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()));

		this.ssaDesignDetails.displayDesignElementsBasedOnDesignTypeOfTheStudy();
		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertFalse(components.contains(this.ssaDesignDetails.getLblBlocks()));
		Assert.assertFalse(components.contains(this.ssaDesignDetails.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getLblReplicates()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getSelReplicates()));

		final boolean spatialVariablesRequired = false;
		this.verifyRowAndColumnFactorsArePresent(components, spatialVariablesRequired);
		
		Assert.assertEquals(this.ssaDesignDetails.getSelDesignType().getValue(), DesignType.RANDOMIZED_BLOCK_DESIGN.getName());

		if ((!this.ssaDesignDetails.getSelReplicates().isEnabled() || this.ssaDesignDetails.getSelReplicates().getItemIds().isEmpty()) && !this.ssaDesignDetails
				.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaDesignDetails.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaDesignDetails.getSelBlocks().getItemIds()) {
				Assert.assertEquals(SingleSiteAnalysisDesignDetailsTest.DEFAULT_REPLICATES,
					this.ssaDesignDetails.getSelReplicates().getItemCaption(itemId));
			}
		}
	}
	
	@Test
	public void testDesignTypeAugmentedDesign() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.AUGMENTED_RANDOMIZED_BLOCK.getId()));

		this.ssaDesignDetails.displayDesignElementsBasedOnDesignTypeOfTheStudy();
		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaDesignDetails.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getSelBlocks()));
		Assert.assertFalse(components.contains(this.ssaDesignDetails.getLblReplicates()));
		Assert.assertFalse(components.contains(this.ssaDesignDetails.getSelReplicates()));

		final boolean spatialVariablesRequired = false;
		this.verifyRowAndColumnFactorsArePresent(components, spatialVariablesRequired);
		
		Assert.assertEquals(this.ssaDesignDetails.getSelDesignType().getValue(), DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName());
		Assert.assertNull("Replicates factor is not needed in Augmented design, so replicates should be unselected (null)",
				this.ssaDesignDetails.getSelReplicates().getValue());
	}
	
	@Test
	public void testDesignTypePRepDesign() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.AUGMENTED_RANDOMIZED_BLOCK.getId()));

		this.ssaDesignDetails.displayPRepDesignElements();
		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaDesignDetails.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getSelBlocks()));
		Assert.assertFalse(components.contains(this.ssaDesignDetails.getLblReplicates()));
		Assert.assertFalse(components.contains(this.ssaDesignDetails.getSelReplicates()));

		final boolean spatialVariablesRequired = false;
		this.verifyRowAndColumnFactorsArePresent(components, spatialVariablesRequired);
		Assert.assertNull("Replicates factor is not needed in P-rep design, so replicates should be unselected (null)",
				this.ssaDesignDetails.getSelReplicates().getValue());
	}

	@Test
	public void testDesignTypeInvalid() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(null);

		this.ssaDesignDetails.displayDesignElementsBasedOnDesignTypeOfTheStudy();
		Assert.assertNull(this.ssaDesignDetails.getSelDesignType().getValue());
		Assert.assertTrue(this.ssaDesignDetails.getDesignDetailsContainer().getComponentCount() == 0);
	}

	@Test
	public void testPopulateChoicesForReplicates() {
		this.ssaDesignDetails.populateChoicesForReplicates();
		final ComboBox selReplicates = this.ssaDesignDetails.getSelReplicates();
		Assert.assertEquals("Dropdown should have 1 factor", 1, selReplicates.getItemIds().size());
		Assert.assertNotNull(selReplicates.getItem("REP_NO"));
	}

	@Test
	public void testPopulateChoicesForBlocks() {
		this.ssaDesignDetails.populateChoicesForBlocks();
		final ComboBox blockSelect = this.ssaDesignDetails.getSelBlocks();
		Assert.assertEquals("Dropdown should have 1 factor", 1, blockSelect.getItemIds().size());
		Assert.assertNotNull(blockSelect.getItem("BLOCK_NO"));
	}

	@Test
	public void testPopulateChoicesForRowFactor() {
		this.ssaDesignDetails.populateChoicesForRowFactor();
		final ComboBox rowSelect = this.ssaDesignDetails.getSelRowFactor();
		Assert.assertEquals("Dropdown should have 1 factor", 1, rowSelect.getItemIds().size());
		Assert.assertNotNull(rowSelect.getItem("ROW_NO"));
	}

	@Test
	public void testPopulateChoicesForColumnFactor() {
		this.ssaDesignDetails.populateChoicesForColumnFactor();
		final ComboBox columnSelect = this.ssaDesignDetails.getSelColumnFactor();
		Assert.assertEquals("Dropdown should have 1 factor", 1, columnSelect.getItemIds().size());
		Assert.assertNotNull(columnSelect.getItem("COLUMN_NO"));
	}

	@Test
	public void testSubstituteMissingReplicatesWithBlocksNoReplicatesFactor() {
		this.ssaDesignDetails.getSelBlocks().addItem(SingleSiteAnalysisDesignDetailsTest.BLOCK_NO);
		this.ssaDesignDetails.getSelReplicates().removeAllItems();

		this.ssaDesignDetails.substituteMissingReplicatesWithBlocks();

		Assert.assertEquals("The value of Replicates Factor Select Field should be the same as the Block factor",
				SingleSiteAnalysisDesignDetailsTest.BLOCK_NO, this.ssaDesignDetails.getSelReplicates().getValue());
		Assert.assertEquals("If block factor is used as a substitute for replicates, then the item caption should be \""
						+ SingleSiteAnalysisDetailsPanel.REPLICATES + "\"", SingleSiteAnalysisDetailsPanel.REPLICATES,
				this.ssaDesignDetails.getSelReplicates().getItemCaption(this.ssaDesignDetails.getSelReplicates().getValue()));
	}
	
	@Test
	public void testReset(){
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()));
		this.ssaDesignDetails.getSelDesignType().setValue(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN);
		this.ssaDesignDetails.getSelReplicates().select(null);
		this.ssaDesignDetails.getSelColumnFactor().select(null);
		this.ssaDesignDetails.getSelRowFactor().select(null);
		
		this.ssaDesignDetails.reset();
		Assert.assertEquals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName(), this.ssaDesignDetails.getSelDesignTypeValue());
		Assert.assertEquals("REP_NO", this.ssaDesignDetails.getSelReplicatesValue());
		Assert.assertEquals("BLOCK_NO", this.ssaDesignDetails.getSelBlocksValue());
		Assert.assertEquals("ROW_NO", this.ssaDesignDetails.getSelRowFactorValue());
		Assert.assertEquals("COLUMN_NO", this.ssaDesignDetails.getSelColumnFactorValue());
	}


	private void verifyRowAndColumnFactorsArePresent(final List<Component> components, final boolean spatialVariablesRequired) {
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getLblSpecifyColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getSelColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getLblSpecifyRowFactor()));
		Assert.assertTrue(components.contains(this.ssaDesignDetails.getSelRowFactor()));
		if (spatialVariablesRequired) {
			Assert.assertEquals(COLUMN_FACTOR_LABEL + SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR, this.ssaDesignDetails.getLblSpecifyColumnFactor().getValue().toString());
			Assert.assertEquals(ROW_FACTOR_LABEL + SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR, this.ssaDesignDetails.getLblSpecifyRowFactor().getValue().toString());
		} else {
			Assert.assertEquals(COLUMN_FACTOR_LABEL, this.ssaDesignDetails.getLblSpecifyColumnFactor().getValue().toString());
			Assert.assertEquals(ROW_FACTOR_LABEL, this.ssaDesignDetails.getLblSpecifyRowFactor().getValue().toString());
		}
	}
	
	private List<Component> getComponentsListFromGridLayout() {
		
		final GridLayout gLayout = (GridLayout) this.ssaDesignDetails.getDesignDetailsContainer().getComponentIterator().next();
		final Iterator<Component> componentsIterator = gLayout.getComponentIterator();
		final List<Component> components = new ArrayList<>();
		while (componentsIterator.hasNext()) {
			final Component component = componentsIterator.next();
			components.add(component);
		}

		return components;
	}
	
	private List<DMSVariableType> createTestFactors() {
		final List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		int rank = 1;
		final StandardVariable entryNoVariable = new StandardVariable();
		entryNoVariable.setId(TermId.ENTRY_NO.getId());
		entryNoVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryNoVariable.setProperty(new Term(1, "GERMPLASM ENTRY", "GERMPLASM ENTRY"));
		factors.add(new DMSVariableType(TermId.ENTRY_NO.name(), TermId.ENTRY_NO.name(), entryNoVariable, rank++));

		final StandardVariable gidVariable = new StandardVariable();
		gidVariable.setId(TermId.GID.getId());
		gidVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		gidVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType(TermId.GID.name(), TermId.ENTRY_NO.name(), gidVariable, rank++));

		final StandardVariable desigVariable = new StandardVariable();
		desigVariable.setId(TermId.DESIG.getId());
		desigVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		desigVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType("DESIGNATION", "DESIGNATION", desigVariable, rank++));

		final StandardVariable entryTypeVariable = new StandardVariable();
		entryTypeVariable.setId(TermId.ENTRY_TYPE.getId());
		entryTypeVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryTypeVariable.setProperty(new Term(1, TermId.ENTRY_TYPE.name(), TermId.ENTRY_TYPE.name()));
		factors.add(new DMSVariableType(TermId.ENTRY_TYPE.name(), TermId.ENTRY_TYPE.name(), entryTypeVariable, rank++));

		final StandardVariable plotIdVariable = new StandardVariable();
		plotIdVariable.setId(TermId.PLOT_ID.getId());
		plotIdVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		plotIdVariable.setProperty(new Term(1, TermId.PLOT_ID.name(), TermId.PLOT_ID.name()));
		factors.add(new DMSVariableType(TermId.PLOT_ID.name(), TermId.PLOT_ID.name(), plotIdVariable, rank++));

		final StandardVariable repVariable = new StandardVariable();
		repVariable.setId(TermId.REP_NO.getId());
		repVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		repVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.REPLICATION_FACTOR, "REP_NO"));
		factors.add(new DMSVariableType(TermId.REP_NO.name(), TermId.REP_NO.name(), repVariable, rank++));

		final StandardVariable blockVariable = new StandardVariable();
		blockVariable.setId(TermId.BLOCK_NO.getId());
		blockVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		blockVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.BLOCKING_FACTOR, "BLOCK_NO"));
		factors.add(new DMSVariableType(TermId.BLOCK_NO.name(), TermId.BLOCK_NO.name(), blockVariable, rank++));

		final StandardVariable rowVariable = new StandardVariable();
		rowVariable.setId(TermId.ROW.getId());
		rowVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		rowVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.ROW_FACTOR, "ROW_NO"));
		factors.add(new DMSVariableType("ROW_NO", "ROW_NO", rowVariable, rank++));

		final StandardVariable columnVariable = new StandardVariable();
		columnVariable.setId(TermId.COLUMN_NO.getId());
		columnVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		columnVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.COLUMN_FACTOR, "COL_NO"));
		factors.add(new DMSVariableType(TermId.COLUMN_NO.name(), TermId.COLUMN_NO.name(), columnVariable, rank++));

		return factors;
	}


}