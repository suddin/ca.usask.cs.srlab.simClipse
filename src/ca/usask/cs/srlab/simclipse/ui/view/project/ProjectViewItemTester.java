package ca.usask.cs.srlab.simclipse.ui.view.project;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;

/**
 * Tests whether an object is part of the SimClipse Item collection.
 */
public class ProjectViewItemTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IProject){
			if(! ((IProject)receiver).isOpen()){
				return false;
			}
		}
		boolean found = false;
		IProjectViewItem[] projects = ProjectViewManager.getManager().getProjectViewItems();
		for (int i = 0; i < projects.length; i++) {
			IProjectViewItem item = projects[i];
			found = item.isProjectViewItemFor(receiver);
			if (found){
				break;
			}
		}
		
		if ("isSimClipseItem".equals(property))
			return found;
		
		if ("notSimClipseItem".equals(property))
			return !found;
		
		return false;
	}

}
