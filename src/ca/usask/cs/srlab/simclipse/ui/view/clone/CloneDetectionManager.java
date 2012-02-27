package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import ca.usask.cs.srlab.simcad.Constants;
import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.model.CloneSet;
import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.SimClipseException;
import ca.usask.cs.srlab.simclipse.SimClipseLog;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.command.DetectCloneOperation;

public final class CloneDetectionManager {
	private static CloneDetectionManager manager;
	//private DetectionSettings detectionSettings;
	private List<CloneViewManagerListener> listeners = new ArrayList<CloneViewManagerListener>();

	private CloneDetectionManager() {
	}

	public static CloneDetectionManager getManager() {
		if (manager == null)
			manager = new CloneDetectionManager();
		return manager;
	}

	public void addCloneViewManagerListener(CloneViewManagerListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void removeCloneViewManagerListener(CloneViewManagerListener listener) {
		listeners.remove(listener);
	}

	public void detectClone(IProject project, DetectionSettings detectionSettings) {
		//this.detectionSettings = detectionSettings;
		
		Shell shell = SimClipsePlugin.getActiveWorkbenchShell();//window.getShell();
//		MessageDialog.openInformation(
//				shell,
//				"TODO",
//				"Detecting Clones for project :"
//						+ project.getName());
		
		 final List<IResource> projectForCloneDetection = Arrays.asList((IResource)project);
         final DetectCloneOperation detectCloneOperation = new DetectCloneOperation(projectForCloneDetection, detectionSettings);
         
	      // Execute the operation
	     try {
	         
	         // Display progress either using the ProgressMonitorDialog ...
	         //Shell shell = HandlerUtil.getActiveShell(event);
	         IRunnableContext context = new ProgressMonitorDialog(shell);
	         	
	         // ... or using the window's status bar
	         // IWorkbenchWindow context = HandlerUtil.getActiveWorkbenchWindow(event);

	         // ... or using the workbench progress service
	         //IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
	         //IRunnableContext context = window.getWorkbench().getProgressService();
	         
	         context.run(true, false, new IRunnableWithProgress() {
	            public void run(IProgressMonitor monitor) throws InvocationTargetException,
	                  InterruptedException {
	            	detectCloneOperation.run(monitor);
	            }
	         });
	      }
	      catch (Exception e) {
	         SimClipseLog.logError(e);
	      }
		
		// display clone
	      
	      try {
				CloneViewManager.getManager().displayClone(project, buildCloneDisplayModel(project, detectCloneOperation.getDetectionResult(), detectionSettings));
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		//fireCloneviewItemChagned();
	}

	
	private Collection<CloneProjectDisplayModel> buildCloneDisplayModel(IResource iResource, List<CloneSet> detectionResult, DetectionSettings detectionSettings) throws CloneNotSupportedException {
		List<CloneProjectDisplayModel> cloneProjectModels = new ArrayList<CloneProjectDisplayModel>();
		
		IProject project = iResource.getProject();
		
		//this is root of a tree in the forest
		CloneProjectDisplayModel cpm = new CloneProjectDisplayModel(project, detectionSettings.getCloneSetType());
		cloneProjectModels.add(cpm);
		
		for(CloneSet cloneSet : detectionResult){
			CloneSetDisplayModel cloneSetModel = new CloneSetDisplayModel(cloneSet , cpm);
			cpm.addCloneSetModel(cloneSetModel);
		}
		
		return cloneProjectModels;
	}
	
	
	
	private void fireCloneviewItemChagned() {
		CloneViewEvent event = null;// = new CloneViewEvent(this,
									// this.cloneViewItems);
		for (Iterator<CloneViewManagerListener> iter = listeners.iterator(); iter
				.hasNext();)
			iter.next().executeEvent(event);
	}

}
