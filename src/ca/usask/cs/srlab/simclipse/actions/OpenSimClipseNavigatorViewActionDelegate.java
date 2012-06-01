package ca.usask.cs.srlab.simclipse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

import ca.usask.cs.srlab.simclipse.SimClipseLog;
import ca.usask.cs.srlab.simclipse.ui.view.navigator.SimEclipseNavigator;

public class OpenSimClipseNavigatorViewActionDelegate implements IWorkbenchWindowActionDelegate {

   private IWorkbenchWindow window;

   public void init(IWorkbenchWindow window) {
      this.window = window;
   }

   public void selectionChanged(IAction action, ISelection selection) {
   }

   public void run(IAction action) {

      // Get the active page.
      if (window == null)
         return;
      IWorkbenchPage page = window.getActivePage();
      if (page == null)
         return;
   
      // Open and activate the Favorites view.
      try {
         page.showView(SimEclipseNavigator.ID);
      }
      catch (PartInitException e) {
         SimClipseLog.logError("Failed to open the SimClipse Navigator view", e);
      }
   }

   public void dispose() {
   }
}
