package ca.usask.cs.srlab.simclipse.ui.handler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simclipse.SimClipseException;
import ca.usask.cs.srlab.simclipse.SimClipseLog;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsManager;
import ca.usask.cs.srlab.simclipse.ui.preferences.PreferenceConstants;
import ca.usask.cs.srlab.simclipse.ui.view.clone.CloneDetectionManager;

public class DetectCloneHandler extends AbstractHandler {
	
	
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

		try {

			ISelection selection = page.getSelection();

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
			
			
			//TODO: Open and simclipse detection settings window
			DetectionSettings detectionSettings;
			Boolean popDetectionSettingPage = SimClipsePlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.SIMCAD_DETECTION_SETTING_POPUP);
			
			if(popDetectionSettingPage){
				throw new SimClipseException("Feature not implemented");
			}else{
				detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(project);
			}

			CloneDetectionManager.getManager().detectClone(project, detectionSettings);
		      
//			boolean displayClone = MessageDialog.openQuestion(shell,
//					"Clone Display", "Do you want to display the clones?");
//			if (displayClone) {
//				CloneViewManager.getManager().displayClone(demoClones());
//			}

		} catch (Exception e) {
			SimClipseLog.logError("Error in detecting clones", e);
		}
		
		return true;
	}

}
