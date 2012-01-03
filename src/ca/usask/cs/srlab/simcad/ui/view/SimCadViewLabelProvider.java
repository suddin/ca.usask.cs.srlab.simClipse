/**
 * 
 */
package ca.usask.cs.srlab.simcad.ui.view;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ca.usask.cs.srlab.simcad.model.ISimCadItem;

class SimCadViewLabelProvider extends LabelProvider
		implements ITableLabelProvider
{
	public String getColumnText(Object obj, int index) {
		switch (index) {
		case 0: // Type column
			return "";
		case 1: // Name column
			if (obj instanceof ISimCadItem)
				return ((ISimCadItem) obj).getName();
			if (obj != null)
				return obj.toString();
			return "";
		case 2: // Location column
			if (obj instanceof ISimCadItem)
				return ((ISimCadItem) obj).getLocation();
			return "";
		default:
			return "";
		}
	}

	public Image getColumnImage(Object obj, int index) {
		if ((index == 0) && (obj instanceof ISimCadItem))
			return ((ISimCadItem) obj).getType().getImage();
		return null;
	}
}