
package org.generationcp.ibpworkbench.customfields;

import java.util.Map;

import com.vaadin.ui.Table;

public class BreedingManagerTable extends Table {

	private static final long serialVersionUID = 745102380412622592L;

	private TableMultipleSelectionHandler tableMultipleSelectionHandler;

	public BreedingManagerTable(int recordCount, int maxRecords) {
		super();

		Integer pageLength = Math.min(recordCount, maxRecords);
		if (pageLength > 0) {
			this.setPageLength(pageLength);
		} else {
			this.setPageLength(maxRecords);
		}
		this.setImmediate(true);

		this.setTableHandler(new TableMultipleSelectionHandler(this));
	}

	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		super.changeVariables(source, variables);
		tableMultipleSelectionHandler.setValueForSelectedItems();
	}

	/**
	 * Set the instance of tableMultipleSelectionHandler
	 */
	void setTableHandler(TableMultipleSelectionHandler tableMultipleSelectionHandler) {

		// remove this tables listener if exists and replace it with the new handler
		this.removeListener(this.tableMultipleSelectionHandler);
		this.removeShortcutListener(this.tableMultipleSelectionHandler);

		// set this table's current handler
		this.tableMultipleSelectionHandler = tableMultipleSelectionHandler;

		// add the new handler as this table's listener
		this.addListener(this.tableMultipleSelectionHandler);
		this.addShortcutListener(this.tableMultipleSelectionHandler);
	}
}
