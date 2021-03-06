package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;

public class GxeCheckBoxGroup extends HorizontalLayout {

	private static final long serialVersionUID = 2449518272837130888L;

	private final GxeTable table;
	private final Property.ValueChangeListener listener;
	private Boolean value = true;

	public GxeCheckBoxGroup(Integer cellType, Integer rowIndex, GxeTable table, Property.ValueChangeListener listener) {
		super();
		this.table = table;
		this.listener = listener;

		if (rowIndex == 1) {
			CheckBox cbRow = new CheckBox("All Rows");
			cbRow.setDebugId("cbRow");
			CheckBox cbCol = new CheckBox("All Columns");
			cbCol.setDebugId("cbCol");

			cbRow.setValue(true);
			cbCol.setValue(true);
			cbRow.addListener(new GxeCheckBoxGroupListener(null, table, cbRow));
			cbCol.addListener(new GxeCheckBoxGroupListener(null, table, cbCol));

			cbRow.setImmediate(true);
			cbCol.setImmediate(true);

			this.addComponent(cbRow);
			this.addComponent(cbCol);

		} else {
			CheckBox cb = new CheckBox(" ");
			cb.setDebugId("cb");
			cb.setValue(true);
			cb.addListener(new GxeCheckBoxGroupListener(null, table, cb));
			cb.addListener(listener);
			cb.setImmediate(true);
			this.value = true;
			this.addComponent(cb);

		}

		this.setImmediate(true);

	}

	public void refresh() {
		this.removeAllComponents();
		CheckBox cb = new CheckBox(" ");
		cb.setDebugId("cb");
		cb.setValue(this.value);
		cb.addListener(new GxeCheckBoxGroupListener(null, this.table, cb));
		cb.setImmediate(true);
		this.addComponent(cb);

	}

	public Boolean getValue() {
		return this.value;
	}

	public void setValue(Boolean val) {
		this.value = val;
		this.refresh();
	}
}
