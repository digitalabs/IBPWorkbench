/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.listmanager.listeners;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.customfields.ListSelectorComponent;
import org.generationcp.ibpworkbench.customfields.ListTreeComponent;
import org.generationcp.ibpworkbench.listmanager.dialog.AddEntryDialog;
import org.generationcp.ibpworkbench.listmanager.dialog.ListManagerCopyToNewListDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;

public class GermplasmListButtonClickListener implements Button.ClickListener {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListButtonClickListener.class);
	private static final long serialVersionUID = 2185217915388685523L;

	private final Component source;

	public GermplasmListButtonClickListener(Component source) {
		this.source = source;
	}

	@Override
	public void buttonClick(ClickEvent event) {

		if (event.getButton().getData().equals(ListSelectorComponent.REFRESH_BUTTON_ID) // "Refresh"
				&& this.source instanceof ListTreeComponent) {
			((ListTreeComponent) this.source).refreshComponent();
		} else if (event.getButton().getData().equals(ListManagerCopyToNewListDialog.SAVE_BUTTON_ID)
				&& this.source instanceof ListManagerCopyToNewListDialog) {
			// "Save Germplasm List"
			((ListManagerCopyToNewListDialog) this.source).saveGermplasmListButtonClickAction();
		} else if (event.getButton().getData().equals(ListManagerCopyToNewListDialog.CANCEL_BUTTON_ID)
				&& this.source instanceof ListManagerCopyToNewListDialog) {
			// "Cancel Germplasm List"
			((ListManagerCopyToNewListDialog) this.source).cancelGermplasmListButtonClickAction();
		} else if (event.getButton().getData().equals(AddEntryDialog.DONE_BUTTON_ID) && this.source instanceof AddEntryDialog) {
			try {
				((AddEntryDialog) this.source).nextButtonClickAction(event);
			} catch (InternationalizableException e) {
				GermplasmListButtonClickListener.LOG.error(e.toString() + "\n" + e.getStackTrace());
				MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
			}
		} else {
			GermplasmListButtonClickListener.LOG
					.error("GermplasmListButtonClickListener: Error with buttonClick action. Source not identified.");
		}

	}

}
