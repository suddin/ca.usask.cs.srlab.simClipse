package ca.usask.cs.srlab.simclipse.ui.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.usask.cs.srlab.simclipse.SimClipseLog;

public class CloneIndexUpdateHandler extends AbstractHandler {

	
	public Object execute(ExecutionEvent event) throws ExecutionException {
	      IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
	      try {
	         window.run(true, true, new IRunnableWithProgress() {
	            public void run(IProgressMonitor monitor)
	               throws InvocationTargetException, InterruptedException { 
	               monitor.beginTask("simulate status bar progress:", 20);
	               for (int i = 20; i > 0; --i) {
	                  monitor.subTask("seconds left = " + i);
	                  Thread.sleep(1000);
	                  monitor.worked(1);
	               }
	               monitor.done();
	            }
	         });
	      }
	      catch (InvocationTargetException e) {
	         SimClipseLog.logError(e);
	      }
	      catch (InterruptedException e) {
	         // User canceled the operation... just ignore.
	      }
	      return null;
	   }
	
	/*
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Get the active window

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)
			return null;

		// Get the active page

		IWorkbenchPage page = window.getActivePage();
		if (page == null)
			return null;

		// Open and simcad settings window

		try {
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			//ISelection selection = page.getSelection();

			if (!(selection instanceof IStructuredSelection))
				return null;
			
			Iterator<?> iter = ((IStructuredSelection) selection).iterator();
			if (!iter.hasNext())
				return null;
			Object elem = iter.next();

			if (!(elem instanceof IProject) && !(elem instanceof IFolder))
				return null;

			IProject project = null;
			
			if (elem instanceof IFolder) {
				// find the main project
				IFolder folder = (IFolder) ((IAdaptable) elem).getAdapter(IFolder.class);
				project = folder.getProject();
			}

			project = (IProject) ((IAdaptable) elem).getAdapter(IProject.class);
			if (project == null)
				return null;

			Shell shell = window.getShell();
			MessageDialog.openInformation(
					shell,
					"TODO",
					"Update Clone Index for project :"
							+ project.getName());

		} catch (Exception e) {
			SimCadLog.logError("Failed to open the view", e);
		}
		return null;
	}
*/
}