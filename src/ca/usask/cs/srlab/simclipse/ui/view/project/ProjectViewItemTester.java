package ca.usask.cs.srlab.simclipse.ui.view.project;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.IJavaProject;


/**
 * Tests whether an object is part of the SimClipse Item collection.
 */
public class ProjectViewItemTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		
//		if(!(receiver instanceof IProject
//			|| receiver instanceof IJavaProject)){
//			return false;
//		}
//		
//		SimClipsePlugin.getDefault().printToConsole("receiver : " + receiver.getClass());
//		
//		if(receiver instanceof IResource){
//			SimClipsePlugin.getDefault().printToConsole( ((IResource) receiver).getName() +" : " + receiver.getClass());
//		}
//		
		if(receiver instanceof IJavaProject){
			IJavaProject jp = (IJavaProject) receiver;
			receiver = jp.getResource();
		}
		
		boolean found = false;
		IProjectViewItem[] favorites = ProjectViewManager.getManager().getProjectViewItems();
		for (int i = 0; i < favorites.length; i++) {
			IProjectViewItem item = favorites[i];
			found = item.isProjectViewItemFor(receiver);
			if (found)
				break;
		}
		
//		SimClipsePlugin.getDefault().printToConsole("FOUND : "+found);
		
		if ("isSimClipseItem".equals(property))
			return found;
		
		if ("notSimClipseItem".equals(property))
			return !found;
		
		return false;
	}

}
