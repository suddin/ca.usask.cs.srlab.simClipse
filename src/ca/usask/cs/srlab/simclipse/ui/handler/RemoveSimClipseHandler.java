package ca.usask.cs.srlab.simclipse.ui.handler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewManager;


public class RemoveSimClipseHandler extends AbstractHandler
{
   public Object execute(ExecutionEvent event)
         throws ExecutionException {
      ISelection selection = HandlerUtil.getCurrentSelection(event);
      boolean sureRemoveSimClipse = false;
      
      if (selection instanceof IStructuredSelection){
    	  
    	  Iterator<?> iter = ((IStructuredSelection) selection).iterator();
			
			if (!iter.hasNext())
				return null;
			
			while (iter.hasNext()) {
				Object elem = iter.next();

				if (!(elem instanceof IProject) && !(elem instanceof IFolder))
					return null;

				IProject project = null;

				if (elem instanceof IFolder) {
					// find the main project
					IFolder folder = (IFolder) ((IAdaptable) elem)
							.getAdapter(IFolder.class);
					project = folder.getProject();
				}

				project = (IProject) ((IAdaptable) elem)
						.getAdapter(IProject.class);
				
				if (project == null)
					return null;

				//want to keep settings?
				boolean keepSimclipseData = false;
				
				Shell shell = (Shell) project.getAdapter(Shell.class);
				
				sureRemoveSimClipse = MessageDialog.openQuestion(shell, "Remove SimClipse",
		                  "Are you sure you want to remove SimClipse?");
				
				if(sureRemoveSimClipse){
					keepSimclipseData = MessageDialog.openQuestion(shell, "Keep SimClipse Data",
	                  "Do you want to keep SimClipse settings and data?");
					
					ProjectViewManager.getManager()
							.deactivateSimclipseForProject(project, keepSimclipseData);
					
				}
		
			}
    		  
      }
      return null;
   }
}
