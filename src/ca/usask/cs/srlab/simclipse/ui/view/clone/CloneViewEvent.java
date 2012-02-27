package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.util.Collection;
import java.util.EventObject;

public class CloneViewEvent extends EventObject {

	private static final long serialVersionUID = -8042202545704461257L;
	private final Collection<CloneProjectDisplayModel> itemsToDisplay;

	public CloneViewEvent(CloneViewManager source,
			Collection<CloneProjectDisplayModel> cloneViewItems) {
		super(source);
		this.itemsToDisplay = cloneViewItems;
	}

	public Collection<CloneProjectDisplayModel> getItemsToDisplay() {
		return itemsToDisplay;
	}

}