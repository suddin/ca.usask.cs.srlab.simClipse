package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.ui.view.project.IProjectViewItem;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewManager;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewManagerEvent;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewManagerListener;

public class NavigatorManager extends PlatformObject implements ProjectViewManagerListener{
   private static NavigatorManager manager;
   
   private List<INavigatorManagerListener> listeners =
         new ArrayList<INavigatorManagerListener>();

   private NavigatorManager() {
	   ProjectViewManager.getManager().addProjectViewManagerListener(this);
   }

   public static NavigatorManager getManager() {
      if (manager == null)
         manager = new NavigatorManager();
      return manager;
   }

   public IProjectViewItem[] getSimClipseProjects() {
      return ProjectViewManager.getManager().getProjectViewItems();
   }

   public void addNavigatorManagerListener(
         INavigatorManagerListener listener) {
      if (!listeners.contains(listener))
         listeners.add(listener);
   }

   public void removeNavigatorManagerListener(
		   INavigatorManagerListener listener) {
      listeners.remove(listener);
   }

	private void fireNavigatorViewItemsChanged(INavigatorItem[] itemsAdded, INavigatorItem[] itemsRemoved) {

		IWorkbenchPage page = SimClipsePlugin.getActivePage();

		try {
			if (page != null) {
				IViewPart view = SimClipsePlugin.getViewIfExists(SimEclipseNavigator.ID);
					//page.findView(ProjectView.ID);
				if (view != null)
					page.bringToTop(view);
				else{
					page.showView(SimEclipseNavigator.ID);
					//new item will be picked up by a call to getProjectViewItems()
					return;
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		NavigatorItemChangeEvent event = new NavigatorItemChangeEvent(this,
				itemsAdded == null ? INavigatorItem.NONE:itemsAdded, itemsRemoved == null ? INavigatorItem.NONE:itemsRemoved);
		for (Iterator<INavigatorManagerListener> iter = listeners.iterator(); iter
				.hasNext();)
			iter.next().itemsChanged(event);
	}

	@Override
	public void itemsChanged(ProjectViewManagerEvent event) {
		INavigatorItem[] itemsAdded = NavigatorItemProject
				.toNavigatorItemProjectArray(event.getItemsAdded());
		INavigatorItem[] itemsRemoved = NavigatorItemProject
				.toNavigatorItemProjectArray(event.getItemsRemoved());

		//fireNavigatorViewItemsChanged(itemsAdded, itemsRemoved);
	}
}
