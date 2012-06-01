package ca.usask.cs.srlab.simclipse.ui.handler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.actions.CloneIndexManager;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsManager;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewItem;

public class CloneIndexUpdateHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		ISelection selection = window.getActivePage().getSelection();

		if (!(selection instanceof IStructuredSelection))
			return null;

		Iterator<?> iter = ((IStructuredSelection) selection).iterator();
		if (!iter.hasNext())
			return null;
		Object elem = iter.next();

		if (!(elem instanceof IJavaProject || elem instanceof ProjectViewItem || elem instanceof IProject || elem instanceof IFolder))
			return null;

		IProject project = null;
		
		if (elem instanceof IFolder) {
			// find the main project
			IFolder folder = (IFolder) ((IAdaptable) elem).getAdapter(IFolder.class);
			project = folder.getProject();
		}

		if (elem instanceof ProjectViewItem) {
			// find the main project
			elem = ((ProjectViewItem)elem).getResource().getProject();
		} 
		
		if (elem instanceof IJavaProject) {
			// find the main project
			elem = ((IJavaProject)elem).getProject();
		}
		
		project = (IProject) ((IAdaptable) elem).getAdapter(IProject.class);
		if (project == null)
			return null;

		Shell shell = window.getShell();
		final IProject fProject = project;
		
		BusyIndicator.showWhile(SimClipsePlugin.getActiveWorkbenchShell().getDisplay(), new Runnable() {
			public void run() {
				DetectionSettings detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(fProject);
				CloneIndexManager.getManager().getCloneIndex(fProject,detectionSettings, true);
			}
		});
		
		/*
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				DetectionSettings detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(fProject);
				CloneIndexManager.getManager().getCloneIndex(fProject,detectionSettings, true);
			}
		});
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				DetectionSettings detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(fProject);
				CloneIndexManager.getManager().getCloneIndex(fProject,detectionSettings, true);
			}
		});
		*/
		
		MessageDialog.openInformation(shell, "Project : "+project.getName(), "Clone Index updated successfully");

		return null;
	}
}
