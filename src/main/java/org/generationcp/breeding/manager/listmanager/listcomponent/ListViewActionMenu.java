package org.generationcp.breeding.manager.listmanager.listcomponent;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.vaadin.peter.contextmenu.ContextMenu;

/**
 * This class is used as the Context Menu for Action Buttons in View List Germplasm List Data Table
 */
@Configurable
public class ListViewActionMenu extends ContextMenu implements InitializingBean, InternationalizableComponent {

	private static final String CONTEXT_MENU_WIDTH = "295px";

	private static final long serialVersionUID = 1L;
	private ContextMenuItem menuExportList;
	private ContextMenuItem menuCopyToList;
	private ContextMenuItem menuAddEntry;
	private ContextMenuItem menuSaveChanges;
	private ContextMenuItem menuDeleteEntries;
	private ContextMenuItem menuEditList;
	private ContextMenuItem menuDeleteList;
	private ContextMenuItem menuSelectAll;
	private ContextMenuItem listEditingOptions;
	private ContextMenuItem removeSelectedGermplasm;
	private ContextMenuItem createInventoryLots;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setWidth(CONTEXT_MENU_WIDTH);

		// Generate main level items
		//Re-arranging Menu Items
		this.listEditingOptions = this.addItem(this.messageSource.getMessage(Message.LIST_EDITING_OPTIONS));
		this.menuSaveChanges = this.listEditingOptions.addItem(this.messageSource.getMessage(Message.SAVE_CHANGES));
		this.menuSelectAll = this.listEditingOptions.addItem(this.messageSource.getMessage(Message.SELECT_ALL));
		this.menuAddEntry = this.listEditingOptions.addItem(this.messageSource.getMessage(Message.ADD_ENTRIES));
		this.menuDeleteEntries = this.listEditingOptions.addItem(this.messageSource.getMessage(Message.DELETE_SELECTED_ENTRIES));
		this.menuEditList = this.listEditingOptions.addItem(this.messageSource.getMessage(Message.EDIT_LIST));
		this.menuDeleteList = this.listEditingOptions.addItem(this.messageSource.getMessage(Message.DELETE_LIST));
		this.menuCopyToList = this.listEditingOptions.addItem(this.messageSource.getMessage(Message.COPY_TO_LIST));
		this.menuExportList = this.addItem(this.messageSource.getMessage(Message.EXPORT_LIST));
		try {
			this.addCreateInventoryLotsLink();
		} catch (final AccessDeniedException e) {
			// NOOP
		}
		try {
			this.addDeleteGermplasmLink();
		} catch (final AccessDeniedException e) {
			// do nothing if the user is not authorized to access Admin link
		}

	}

	@Override
	public void updateLabels() {
		// do nothing
	}

	public ContextMenuItem getMenuExportList() {
		return this.menuExportList;
	}

	public ContextMenuItem getMenuCopyToList() {
		return this.menuCopyToList;
	}

	public ContextMenuItem getMenuAddEntry() {
		return this.menuAddEntry;
	}

	public ContextMenuItem getMenuSaveChanges() {
		return this.menuSaveChanges;
	}

	public ContextMenuItem getMenuDeleteEntries() {
		return this.menuDeleteEntries;
	}

	public ContextMenuItem getMenuEditList() {
		return this.menuEditList;
	}

	public ContextMenuItem getMenuDeleteList() {
		return this.menuDeleteList;
	}

	public ContextMenuItem getListEditingOptions() {
		return this.listEditingOptions;
	}

	public ContextMenuItem getRemoveSelectedGermplasm() {
		return this.removeSelectedGermplasm;
	}

	/**
	 * When the Germplasm List is not locked, and when not accessed directly from URL or popup window
	 */
	public void setActionMenuWhenListIsUnlocked(final boolean isLocalUserListOwner) {
		this.menuEditList.setVisible(true);
		this.menuDeleteEntries.setVisible(true);
		// show only Delete List when user is owner
		this.menuDeleteList.setVisible(isLocalUserListOwner);
		this.menuSaveChanges.setVisible(true);
		this.menuAddEntry.setVisible(true);
		//need to show when List is unlocked
		try {
			this.setRemoveSelectedGermplasmWhenListIsLocked(true);
		} catch (final AccessDeniedException e) {
			// do nothing if the user is not authorized to access Admin button
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GERMPLASM', 'ROLE_MANAGE_GERMPLASM', 'ROLE_DELETE_GERMPLASM')")
	private void setRemoveSelectedGermplasmWhenListIsLocked(final boolean visible) {
		if (this.removeSelectedGermplasm != null) {
			this.removeSelectedGermplasm.setVisible(visible);
		}
	}

	public void setActionMenuWhenListIsLocked() {
		this.menuEditList.setVisible(false);
		this.menuDeleteList.setVisible(false);
		this.menuDeleteEntries.setVisible(false);
		this.menuSaveChanges.setVisible(false);
		this.menuAddEntry.setVisible(false);
		try {
			this.setRemoveSelectedGermplasmWhenListIsLocked(false);
		} catch (final AccessDeniedException e) {
			// do nothing if the user is not authorized to access Admin button
		}
	}

	/**
	 * Update the Action Menu in List View base on the following paramenters:
	 *
	 * @param fromUrl             - if it is loaded from the URL directly
	 * @param listBuilderIsLocked - if the list loaded in Build New List pane is locked
	 * @param hasSource           - if the source, ListManagerMain.class the parent component, is not null
	 */
	public void updateListViewActionMenu(final boolean fromUrl, final boolean listBuilderIsLocked, final boolean hasSource) {
		if (fromUrl) {
			this.menuExportList.setVisible(false);
			this.menuCopyToList.setVisible(false);
		}

		if (hasSource) {
			this.menuCopyToList.setVisible(!listBuilderIsLocked);
		}
	}

	/**
	 * For Test Purposes
	 */
	void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GERMPLASM', 'ROLE_MANAGE_GERMPLASM', 'ROLE_DELETE_GERMPLASM')")
	protected void addDeleteGermplasmLink() {
		this.removeSelectedGermplasm = this.listEditingOptions.addItem(this.messageSource.getMessage(Message.REMOVE_SELECTED_GERMPLASM));
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LISTS', 'ROLE_GERMPLASM_LISTS', 'ROLE_MG_MANAGE_INVENTORY', 'ROLE_MG_CREATE_LOTS')")
	private void addCreateInventoryLotsLink() {
		this.createInventoryLots = this.addItem(this.messageSource.getMessage(Message.CREATE_INVENTORY_LOTS_MENU_ITEM));
	}

	protected void setListEditingOptions(final ContextMenuItem listEditingOptions) {
		this.listEditingOptions = listEditingOptions;
	}

}
