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

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;


/**
 * <b>Description</b>: Displays the workbench login window.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 22, 2012
 */
public class OpenLoginWindowAction implements ClickListener{

    private static final long serialVersionUID = 5784289264247702925L;
    
    @Override
    public void buttonClick(ClickEvent event) {
        // closes the app then reloads if logout url is not set
    	event.getComponent().getApplication().close();

    }

}
