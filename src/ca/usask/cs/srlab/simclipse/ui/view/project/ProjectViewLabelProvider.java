/**
 * 
 */
package ca.usask.cs.srlab.simclipse.ui.view.project;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


class ProjectViewLabelProvider extends LabelProvider
		implements ITableLabelProvider
{
	public String getColumnText(Object obj, int index) {
		switch (index) {
		case 0: // Type column
			return "";
		case 1: // Name column
			if (obj instanceof IProjectViewItem)
				return ((IProjectViewItem) obj).getName();
			if (obj != null)
				return obj.toString();
			return "";
		case 2: // Location column
			if (obj instanceof IProjectViewItem)
				return ((IProjectViewItem) obj).getLocation();
			return "";
		default:
			return "";
		}
	}

	public Image getColumnImage(Object obj, int index) {
		if ((index == 0) && (obj instanceof IProjectViewItem))
			return ((IProjectViewItem) obj).getType().getImage();
		return null;
	}
}