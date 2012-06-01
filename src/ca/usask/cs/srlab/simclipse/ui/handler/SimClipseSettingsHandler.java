package ca.usask.cs.srlab.simclipse.ui.handler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.usask.cs.srlab.simclipse.SimClipseLog;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsDialog;
import ca.usask.cs.srlab.simclipse.ui.view.navigator.INavigatorItem;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewItem;

public class SimClipseSettingsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Get the active window

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)
			return null;

		// Get the active page

		IWorkbenchPage page = window.getActivePage();
		if (page == null){
			SimClipsePlugin.beep();
			return null;
		}
		// Open and SimClipse settings window

		try {

			ISelection selection = page.getSelection();

			if (!(selection instanceof IStructuredSelection))
				return null;
			
			Iterator<?> iter = ((IStructuredSelection) selection).iterator();
			if (!iter.hasNext())
				return null;
			Object elem = iter.next();

			if (!(elem instanceof IJavaProject || elem instanceof ProjectViewItem || elem instanceof INavigatorItem ))
				return null;

			IProject project = null;
			
			if (elem instanceof INavigatorItem) {
				elem = ((INavigatorItem) elem).getProject();
			}

			if (elem instanceof ProjectViewItem) {
				elem = ((ProjectViewItem)elem).getResource().getProject();
			} 
			
			if (elem instanceof IJavaProject) {
				elem = ((IJavaProject)elem).getProject();
			}

			project = (IProject) ((IAdaptable) elem).getAdapter(IProject.class);
			
			if (project == null)
				return null;

			Shell shell = window.getShell();
			
			
//			MessageDialog.openInformation(
//					shell,
//					"TODO",
//					"Open SimClipse Settings Page for project :"
//							+ project.getName());
			
			
			DetectionSettingsDialog dialog= new DetectionSettingsDialog(window, project);
			dialog.open();
			

		} catch (Exception e) {
			SimClipseLog.logError("Failed to open the view", e);
		}
		return null;
	}

}
