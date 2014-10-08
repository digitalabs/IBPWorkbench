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

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;
import org.generationcp.browser.study.containers.StudyDataIndexContainer;
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
public class StudyFactorComponent extends Table implements InitializingBean, InternationalizableComponent{

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(StudyFactorComponent.class);
    private static final long serialVersionUID = 1053068118831514119L;

    private static final String NAME = "NAME";
    private static final String DESC = "DESCRIPTION";
    private static final String PROP = "PROPERTY";
    private static final String SCA = "SCALE";
    private static final String METH = "METHOD";
    private static final String DTYPE = "DATATYPE";
    private static final String VALUE = "VALUE";

    private int studyId;

    private StudyDataManagerImpl studyDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public StudyFactorComponent(StudyDataManagerImpl studyDataManager, int studyId) {
        this.studyDataManager = studyDataManager;
        this.studyId = studyId;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        StudyDataIndexContainer dataIndexContainer = new StudyDataIndexContainer(studyDataManager, studyId);
        IndexedContainer dataStudyFactor;

        dataStudyFactor = dataIndexContainer.getStudyFactor();

        this.setContainerDataSource(dataStudyFactor);

        setSelectable(true);
        setMultiSelect(false);
        setImmediate(true); // react at once when something is
        setWidth("100%");
        setHeight("330px");
        setColumnReorderingAllowed(true);
        setColumnCollapsingAllowed(true);
        setColumnHeaders(new String[] { NAME, DESC, PROP, SCA, METH, DTYPE, VALUE });
    }

    @Override
    public void attach() {
        super.attach();
        updateLabels();
    }

    @Override
    public void updateLabels() {
        messageSource.setColumnHeader(this, "NAME", Message.NAME_HEADER);
        messageSource.setColumnHeader(this, "DESCRIPTION", Message.DESCRIPTION_HEADER);
        messageSource.setColumnHeader(this, "PROPERTY", Message.PROPERTY_HEADER);
        messageSource.setColumnHeader(this, "SCALE", Message.SCALE_HEADER);
        messageSource.setColumnHeader(this, "METHOD", Message.METHOD_HEADER);
        messageSource.setColumnHeader(this, "DATATYPE", Message.DATATYPE_HEADER);
        messageSource.setColumnHeader(this, "VALUE", Message.VALUE_HEADER);
    }

}
