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

package org.generationcp.browser.study;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.browser.study.listeners.StudySelectedTabChangeListener;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class StudyAccordionMenu extends Accordion implements InitializingBean, InternationalizableComponent {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(StudyAccordionMenu.class);
    private static final long serialVersionUID = -1409312205229461614L;
    
    private static final String STUDY_VARIATES = "Study Variates";
    private static final String STUDY_FACTORS = "Study Factors";
    private static final String STUDY_EFFECTS = "Study Effects";
    
    private int studyId;
    private VerticalLayout layoutVariate;
    private VerticalLayout layoutFactor;
    private VerticalLayout layoutEffect;

    private StudyDataManagerImpl studyDataManager;
    private StudyDetailComponent studyDetailComponent;

    private boolean fromUrl;                //this is true if this component is created by accessing the Study Details page directly from the URL
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
	private boolean h2hCall;

    public StudyAccordionMenu(int studyId, StudyDetailComponent studyDetailComponent,
            StudyDataManagerImpl studyDataManager, boolean fromUrl,boolean h2hCall) {
        this.studyId = studyId;
        this.studyDataManager = studyDataManager;
        this.studyDetailComponent = studyDetailComponent;
        this.fromUrl = fromUrl;
        this.h2hCall=h2hCall;
    }

    public void selectedTabChangeAction() throws InternationalizableException{
        Component selected = this.getSelectedTab();
        Tab tab = this.getTab(selected);
        if (tab.getComponent() instanceof VerticalLayout) {
            if (((VerticalLayout) tab.getComponent()).getData().equals(STUDY_FACTORS)) {
                if (layoutFactor.getComponentCount() == 0) {
                    layoutFactor.addComponent(new StudyFactorComponent(studyDataManager, studyId));
                    layoutFactor.setMargin(true);
                    layoutFactor.setSpacing(true);
                }
            } else if (((VerticalLayout) tab.getComponent()).getData().equals(STUDY_VARIATES)) {
                if (layoutVariate.getComponentCount() == 0) {
                    layoutVariate.addComponent(new StudyVariateComponent(studyDataManager, studyId));
                    layoutVariate.setMargin(true);
                    layoutVariate.setSpacing(true);
                }
            } else if (((VerticalLayout) tab.getComponent()).getData().equals(STUDY_EFFECTS)) {
                if (layoutEffect.getComponentCount() == 0) {
                    layoutEffect.addComponent(new StudyEffectComponent(studyDataManager, studyId, this, fromUrl,h2hCall));
                }
            }
        }
    }
    
    @Override
    public void afterPropertiesSet() {
        this.setSizeFull();

        layoutVariate = new VerticalLayout();
        layoutVariate.setData(STUDY_VARIATES);
        layoutVariate.setMargin(true);
        
        layoutFactor = new VerticalLayout();
        layoutFactor.setData(STUDY_FACTORS);
        layoutFactor.setMargin(true);
        
        layoutEffect = new VerticalLayout();
        layoutEffect.setData(STUDY_EFFECTS);
        layoutEffect.setMargin(true);
        
        this.addTab(studyDetailComponent, messageSource.getMessage(Message.STUDY_DETAILS_TEXT)); // "Study Details"
        this.addTab(layoutFactor, messageSource.getMessage(Message.FACTORS_TEXT)); // "Factors"
        this.addTab(layoutVariate, messageSource.getMessage(Message.VARIATES_TEXT)); // "Variates"
        this.addTab(layoutEffect, messageSource.getMessage(Message.DATASETS_TEXT)); // "Datasets"

        this.addListener(new StudySelectedTabChangeListener(this));        
    }
    
    @Override
    public void attach() {
        super.attach();
        updateLabels();
    }

    @Override
    public void updateLabels() {
        // does nothing
    }

}
