package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
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
import ca.usask.cs.srlab.simcad.index.ICloneIndex;
import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simcad.util.PropsUtil;
import ca.usask.cs.srlab.simclipse.SimClipseLog;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.actions.CloneIndexManager;
import ca.usask.cs.srlab.simclipse.command.DetectCloneOperation;
import ca.usask.cs.srlab.simclipse.ui.view.navigator.NavigatorItemFileFragment;
import ca.usask.cs.srlab.simclipse.util.FileUtil;

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


	private List<CloneFragment> getCandidateFragments(IResource resource, ICloneIndex cloneIndex) {
		
		if(resource == null) return null;
		
		Collection<CloneFragment> candidateFragments;
		List<IFile> fileList = new ArrayList<IFile>();
		
		 	if(resource instanceof IProject)
		 		candidateFragments = cloneIndex.getAllEntries();
		 	else{
		 		candidateFragments = new ArrayList<CloneFragment>();
			 	if(resource instanceof IFolder)
					fileList.addAll(FileUtil.getFilesFromFolder((IFolder)resource));
				else if(resource instanceof IFile)
					fileList.add((IFile) resource);
				else if(resource instanceof NavigatorItemFileFragment){
					candidateFragments.add(((NavigatorItemFileFragment)resource).getCodeFragment());
				}
				
				boolean isUrlRelative = PropsUtil.getIsFragmentFileRelativeURL();
				
				for(IFile file :  fileList){
					Collection<CloneFragment> cloneFragments = CloneIndexManager.getCloneFragmentsbyResourceId(
							cloneIndex, isUrlRelative, file);
	
					if (cloneFragments != null)
						candidateFragments.addAll(cloneFragments);
				}
		 	}
//		if(candidateFragments == null || candidateFragments.isEmpty()){
//			throw new SimClipseException("Error in identifying the candidate fragments for clone detection");
//		}
		
		return  (List<CloneFragment>) candidateFragments;
	}
	
	
	public void detectClone(IResource candidateForCloneDetection, IProject scopeForCloneDetection, DetectionSettings detectionSettings) {
		//this.detectionSettings = detectionSettings;
		
		Shell shell = SimClipsePlugin.getActiveWorkbenchShell();
		
		SimClipsePlugin.getDefault().printToConsole("detecting clone for : " + scopeForCloneDetection.getName());
		
		ICloneIndex cloneIndex = CloneIndexManager.getManager().getCloneIndex(scopeForCloneDetection, detectionSettings, false);
		
		List<CloneFragment> candidateFragments = getCandidateFragments(candidateForCloneDetection, cloneIndex);
		
		if(candidateFragments == null || candidateFragments.isEmpty() ) return;
		
         //final DetectCloneOperation detectCloneOperation = new DetectCloneOperation(projectForCloneDetection, detectionSettings);
		final DetectCloneOperation detectCloneOperation = new DetectCloneOperation(candidateFragments, cloneIndex, detectionSettings);
			 
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
	         
	         //http://wiki.eclipse.org/FAQ_What_are_IWorkspaceRunnable,_IRunnableWithProgress,_and_WorkspaceModifyOperation%3F
	        	 
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
			
			if(candidateForCloneDetection instanceof IProject ||candidateForCloneDetection instanceof IFolder)
				CloneViewManager.getManager().displayClone(scopeForCloneDetection, detectCloneOperation.getDetectionResult(), detectionSettings);
			else
				CloneViewManager.getManager().displayClone(scopeForCloneDetection, candidateFragments, detectCloneOperation.getDetectionResult(), detectionSettings);
			
		} catch (Exception e) {
			e.printStackTrace();
			SimClipsePlugin.getDefault().printToConsole("error occured in display clone manager...");
		}
		
		//fireCloneviewItemChagned();
	}

	/*
	long-running operation
	
	IWorkspaceRunnable myRunnable = 
	new IWorkspaceRunnable() {
		public void run(IProgressMonitor monitor) throws CoreException {
			//do the actual work in here
			...
		}
	}
	
	*/
	
	private void fireCloneviewItemChagned() {
		CloneViewEvent event = null;// = new CloneViewEvent(this,
									// this.cloneViewItems);
		for (Iterator<CloneViewManagerListener> iter = listeners.iterator(); iter
				.hasNext();)
			iter.next().executeEvent(event);
	}

}
