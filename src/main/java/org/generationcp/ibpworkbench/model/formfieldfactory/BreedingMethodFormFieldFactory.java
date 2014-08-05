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
package org.generationcp.ibpworkbench.model.formfieldfactory;

import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.*;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.WorkbenchContentApp;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.pojos.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


/**
 * <b>Description</b>: Field factory for generating Breeding Method fields for Breeding Method class.
 * <p/>
 * <br>
 * <br>
 * <p/>
 * <b>Author</b>: Jeffrey Morales
 * <br>
 * <b>File Created</b>: August 30, 2012
 */
@Configurable
public class BreedingMethodFormFieldFactory extends DefaultFieldFactory {

    private static final long serialVersionUID = 3560059243526106791L;

    private Field methodName;
    private Field methodDescription;
    private Select methodSelectType;
    private Select methodSelectGroup;
    private Field methodCode;
    private Select methodSelectClass;
    
    private Boolean isEditMode;

    // For new item handling and listener of crop type combo box
    //private MethodTypeComboAction methodTypeComboAction;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired
	private ManagerFactoryProvider managerFactoryProvider;

    public BreedingMethodFormFieldFactory(Map<Integer,String> classMap) {
        initFields(classMap);
    }
    
    public BreedingMethodFormFieldFactory(Map<Integer,String> classMap, Boolean isEditMode) {
    	this.isEditMode = isEditMode;
        initFields(classMap);
    }

    private void initFields(Map<Integer,String> classMap) {

        methodName = new TextField();
        methodName.setRequired(true);
        methodName.setRequiredError("Please enter a Breeding Method Name.");
        methodName.addValidator(new StringLengthValidator("Breeding Method Name must be 1-50 characters.", 1, 50, false));
        methodName.addValidator(new Validator(){
        	
			@Override
			public void validate(Object value) throws InvalidValueException {
				
				if (value == null) return;
				
				if (!isValid(value)){
					throw new InvalidValueException(String.format("Breeding Method \"%s\" already exists.", value.toString())); 
				}
			}

			@Override
			public boolean isValid(Object value) {
				
				if (value == null) return true;
				
				SessionData sessionData = null;
				
				try{
					sessionData = WorkbenchContentApp.get().getSessionData();
				}catch(Exception e){}
				
				try{
					if (sessionData == null){
						sessionData = IBPWorkbenchApplication.get().getSessionData();
					}
				}catch(Exception e){}
				
				Method method = null;
				try {
					ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(sessionData.getSelectedProject());
					method = managerFactory.getGermplasmDataManager().getMethodByName(value.toString());
				} catch (MiddlewareQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//If Method ID is not null, then Method already exists
				if (method != null && method.getMid() != null) {
					
					 if (isEditMode && methodName.isModified()){
						 return false;
					 }else if (!isEditMode){
						 return false;
					 }
					
				}
				return true;
			}
        	
        });
        methodName.setWidth("250px");

        methodDescription = new TextArea();
        methodDescription.setRequired(true);
        methodDescription.setRequiredError("Please enter a Breeding Method Description.");
        methodDescription.addValidator(new StringLengthValidator("Breeding Method Description must be 1-255 characters.", 1, 255, false));
        methodDescription.setWidth("375px");
        methodDescription.setHeight("100px");

        methodCode = new TextField();
        methodCode.setRequired(true);
        methodCode.setRequiredError("Please enter a Breeding Method Code.");
        methodCode.addValidator(new StringLengthValidator("Breeding Method Code must be 1-8 characters.", 1, 8, false));
        methodCode.addValidator(new Validator(){
        	
			@Override
			public void validate(Object value) throws InvalidValueException {
				
				if (value == null) return;
				
				if (!isValid(value)){
					throw new InvalidValueException(String.format("Breeding Method with Code \"%s\" already exists.", value.toString())); 
				}
			}

			@Override
			public boolean isValid(Object value) {
				
				if (value == null) return true;
				
				SessionData sessionData = null;
				
				try{
					sessionData = WorkbenchContentApp.get().getSessionData();
				}catch(Exception e){}
				
				try{
					if (sessionData == null){
						sessionData = IBPWorkbenchApplication.get().getSessionData();
					}
				}catch(Exception e){}
				
				
				Method method = null;
				try {
					ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(sessionData.getSelectedProject());
					method = managerFactory.getGermplasmDataManager().getMethodByCode(value.toString());
				} catch (MiddlewareQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//If Method ID is not null, then Method already exists
				if (method != null && method.getMid() != null) {
					
					 if (isEditMode && methodCode.isModified()){
						 return false;
					 }else if (!isEditMode){
						 return false;
					 }
					
				}
				return true;
			}
        	
        });
        methodCode.setWidth("70px");

        methodSelectType = new Select();
        methodSelectType.setWidth("250px");
        methodSelectType.addItem("GEN");
        methodSelectType.setItemCaption("GEN", "Generative");
        methodSelectType.addItem("DER");
        methodSelectType.setItemCaption("DER", "Derivative");
        methodSelectType.addItem("MAN");
        methodSelectType.setItemCaption("MAN", "Maintenance");
        /*methodSelectType.select("GEN");*/
        methodSelectType.setNullSelectionAllowed(false);
        methodSelectType.setRequired(true);
        methodSelectType.setRequiredError("Please select a Generation Advancement Type");

        methodSelectGroup = new Select();
        methodSelectGroup.setWidth("250px");
        methodSelectGroup.addItem("S");
        methodSelectGroup.setItemCaption("S", "Self Fertilizing");
        methodSelectGroup.addItem("O");
        methodSelectGroup.setItemCaption("O", "Cross Pollinating");
        methodSelectGroup.addItem("C");
        methodSelectGroup.setItemCaption("C", "Clonally Propagating");
        methodSelectGroup.addItem("G");
        methodSelectGroup.setItemCaption("G", "All System");
        methodSelectGroup.select("");
        methodSelectGroup.setNullSelectionAllowed(false);
        
        methodSelectClass = new Select();
        methodSelectClass.setWidth("250px");
        for (Integer key : classMap.keySet()) {
			String value = classMap.get(key);
			methodSelectClass.addItem(key);
			methodSelectClass.setItemCaption(key, value);
		}
        methodSelectClass.setNullSelectionAllowed(false);
        methodSelectClass.setRequired(true);
        methodSelectClass.setRequiredError("Please select a Class");        
    }


    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {

        Field field = super.createField(item, propertyId, uiContext);

        if ("mname".equals(propertyId)) {
       //     messageSource.setCaption(methodName, Message.BREED_METH_NAME);
            return methodName;

        } else if ("mdesc".equals(propertyId)) {
        //    messageSource.setCaption(methodDescription, Message.BREED_METH_DESC);
            return methodDescription;
        } else if ("mcode".equals(propertyId)) {
        //    messageSource.setCaption(methodCode, Message.BREED_METH_CODE);
            return methodCode;
        } else if ("mtype".equals(propertyId)) {
        //    messageSource.setCaption(methodSelectType, Message.BREED_METH_TYPE);
            return methodSelectType;
        } else if ("mgrp".equals(propertyId)) {
        //    messageSource.setCaption(methodSelectGroup, Message.BREED_METH_GRP);
            return methodSelectGroup;
        } else if ("geneq".equals(propertyId)) {
            return methodSelectClass;
        }
        return field;
    }
}
