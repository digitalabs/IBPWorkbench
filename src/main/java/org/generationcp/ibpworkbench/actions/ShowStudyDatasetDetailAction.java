package org.generationcp.ibpworkbench.actions;

import java.util.Arrays;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDatasetForBreedingViewWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.Study;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

/**
 * 
 * @author Jeffrey Morales
 *
 */
@Configurable
public class ShowStudyDatasetDetailAction implements ItemClickListener {
    private static final long serialVersionUID = 1L;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private Table tblDataset;
    
    private Table tblFactors;
    
    private Table tblVariates;
    
    private SelectDatasetForBreedingViewWindow selectDatasetForBreedingViewWindow;

    public ShowStudyDatasetDetailAction(Table tblDataset, Table tblFactors, Table tblVariates, SelectDatasetForBreedingViewWindow selectDatasetForBreedingViewWindow) {
 
        this.tblDataset = tblDataset;
        this.tblFactors = tblFactors;
        this.tblVariates = tblVariates;
        this.selectDatasetForBreedingViewWindow = selectDatasetForBreedingViewWindow;
    }

    @Override
    public void itemClick(ItemClickEvent event) {

        Study study = (Study) event.getItemId();

        if (study == null) {
            return;
        }
        
        if (!study.equals(selectDatasetForBreedingViewWindow.getCurrentStudy())) {
            
            selectDatasetForBreedingViewWindow.setCurrentStudy(study);
            selectDatasetForBreedingViewWindow.setCurrentRepresentationId(null);
            selectDatasetForBreedingViewWindow.setCurrentDatasetName(null);
        }
        
        try {
            StudyDataManager studyDataManager = managerFactoryProvider.getManagerFactoryForProject(selectDatasetForBreedingViewWindow.getCurrentProject()).getStudyDataManager();
            List<Representation> datasetList = studyDataManager.getRepresentationByStudyID(study.getId());
            
            updateDatasetTable(datasetList, tblFactors, tblVariates);

        }
        catch (MiddlewareQueryException e) {
            showDatabaseError(event.getComponent().getWindow());
        }
    }
    
    private void updateDatasetTable(List<Representation> datasetList, Table tblFactors, Table tblVariates) {
        Object[] oldColumns = tblDataset.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
        
        BeanContainer<Integer, Representation> container = new BeanContainer<Integer, Representation>(Representation.class);
        container.setBeanIdProperty("id");
        tblDataset.setContainerDataSource(container);
        
        for (Representation representation : datasetList) {
            container.addBean(representation);
        }
        
        tblDataset.setContainerDataSource(container);
        
        tblDataset.setVisibleColumns(columns);
        
        tblDataset.addListener(new ShowDatasetVariablesDetailAction(tblDataset, tblFactors, tblVariates, selectDatasetForBreedingViewWindow));
    }
    
    
    private void showDatabaseError(Window window) {
        MessageNotifier.showError(window, 
                messageSource.getMessage(Message.DATABASE_ERROR), 
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
    }

}
