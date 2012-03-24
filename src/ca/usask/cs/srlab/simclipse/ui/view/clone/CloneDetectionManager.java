package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.model.CloneSet;
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

	public void detectClone(IResource candidateForCloneDetection, IProject scopeForCloneDetection, DetectionSettings detectionSettings) {
		//this.detectionSettings = detectionSettings;
		
		Shell shell = SimClipsePlugin.getActiveWorkbenchShell();
		
		SimClipsePlugin.getDefault().printToConsole("detecting clone for : " + scopeForCloneDetection.getName());
		
         //final DetectCloneOperation detectCloneOperation = new DetectCloneOperation(projectForCloneDetection, detectionSettings);
		final DetectCloneOperation detectCloneOperation = (candidateForCloneDetection instanceof IProject) ? 
				new DetectCloneOperation(Arrays.asList(scopeForCloneDetection), detectionSettings): 
				new DetectCloneOperation(Arrays.asList(candidateForCloneDetection), Arrays.asList(scopeForCloneDetection), detectionSettings);
			 
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
	         SimClipsePlugin.getDefault().printToConsole("Exception in Clone DetectionManager : "+ e.toString());
	      }
	
		      
		try {
			
			if(detectCloneOperation.getDetectionResult() == null || detectCloneOperation.getDetectionResult().isEmpty()){
				
				CloneViewManager.getManager().resetCloneView();
				
				MessageDialog.openInformation(
						SimClipsePlugin.getActiveWorkbenchShell(),
			            "SimClipse Clone Search", 
			            "No clone found in: " + ((candidateForCloneDetection instanceof IProject)? "Project":(candidateForCloneDetection instanceof IFolder)?"Folder":"File") +" "+ candidateForCloneDetection.getProjectRelativePath().toOSString());
				return;
			}
			
			CloneViewManager.getManager().displayClone(
					scopeForCloneDetection,
					buildCloneDisplayModel(scopeForCloneDetection,
							detectCloneOperation.getDetectionResult(),
							detectionSettings));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			SimClipsePlugin.getDefault().printToConsole("error occured in display clone manager...");
		}
		
		//fireCloneviewItemChagned();
	}

	
	private Collection<CloneProjectDisplayModel> buildCloneDisplayModel(IResource iResource, List<CloneSet> detectionResult, DetectionSettings detectionSettings) throws CloneNotSupportedException {
		List<CloneProjectDisplayModel> cloneProjectModels = new ArrayList<CloneProjectDisplayModel>();
		
		IProject project = iResource.getProject();
		
		try{
		
		//this is root of a tree in the forest
		CloneProjectDisplayModel cpm = new CloneProjectDisplayModel(project, detectionSettings.getCloneSetType());
		cloneProjectModels.add(cpm);
		
		for(CloneSet cloneSet : detectionResult){
			CloneSetDisplayModel cloneSetModel = new CloneSetDisplayModel(cloneSet , cpm);
			cpm.addCloneSetModel(cloneSetModel);
		}
		
		}catch (Exception e) {
			e.printStackTrace();
			SimClipseLog.logError("Error occured in building clone display models", e);
			SimClipsePlugin.getDefault().printToConsole("Error occured in building clone display models...", e);
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
