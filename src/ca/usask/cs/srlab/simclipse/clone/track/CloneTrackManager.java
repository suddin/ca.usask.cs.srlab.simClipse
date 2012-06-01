package ca.usask.cs.srlab.simclipse.clone.track;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.Environment;
import ca.usask.cs.srlab.simcad.detection.CloneDetector;
import ca.usask.cs.srlab.simcad.index.ICloneIndex;
import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simcad.model.ClonePair;
import ca.usask.cs.srlab.simcad.model.CloneSet;
import ca.usask.cs.srlab.simcad.util.FastStringComparator;
import ca.usask.cs.srlab.simcad.util.PropsUtil;
import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.actions.CloneIndexManager;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsManager;
import ca.usask.cs.srlab.simclipse.ui.view.clone.CloneViewManager;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewManager;
import ca.usask.cs.srlab.simclipse.util.FileUtil;
import ca.usask.cs.srlab.simclipse.util.PropertyUtil;

import com.google.common.io.Files;

/**
 * @author sharif
 * 
 * 1. get the list of file changed
 * 2. extract the fragments
 * 		2.1 create a temp src folder with exact src structure
 * 		2.2 extract fragments on that temp folder
 * 		2.3 build a temp index of those   
 * 		2.4 (optional)
 * 			call updateIndex (resourceID, newFragmentList){
 * 				> remove old fragments
 * 				> add new fragments
 * 			}
 * 
 * 2. for each file changed
 *      2.2 map fragments with existing fragments
 *      2.3 identify list of (a) changed fragments (b)  new fragments
 *      2.4 for each changed fragment in (a)
 *              2.4.1 find if old one is not a clone but new one is
 *              2.4.2 report it
 *      2.5 for each new fragment in (b)
 *              2.5.1 find if the fragment is a clone (old code + new/unsaved code)
 *              2.5.2 report it
 *
 * TODO:
 * when tracker is disable
 * popup message, resource has been changed do you want to update clone index
 *
 *
 */
public class CloneTrackManager {
	private static CloneTrackManager manager;
	//private List<CloneViewManagerListener> listeners = new ArrayList<CloneViewManagerListener>();
//	private IProject currentProject;
	private Map<String, HashMap<IFile, Long>> trackedProjectMap = new HashMap<String, HashMap<IFile, Long>>();
	
	private CloneTrackManager() {
	}

	public static CloneTrackManager getManager() {
		if (manager == null)
			manager = new CloneTrackManager();
		return manager;
	}
	
	private void enableCloneTracker(IProject project){
		enableCloneTracker(project, false);
	}
	
	private void enableCloneTracker(final IProject project, final boolean forceFreshIndexBuild){
		trackedProjectMap.put(project.getName(), new HashMap<IFile, Long>());

		final DetectionSettings detectionSettings = DetectionSettingsManager
				.getManager().getSavedDetectionSettingsForProject(project);
		
		Job job = new Job("Index buider Job") {
			protected IStatus run(IProgressMonitor monitor) {
				// fresh index build and store into cache
				CloneIndexManager.getManager().getCloneIndex(project,
						detectionSettings, forceFreshIndexBuild);

				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	private void disableCloneTracker(IProject project){
		trackedProjectMap.remove(project.getName());
//		if(currentProject!=null && project.getName().equals(currentProject.getName()))
//			currentProject = null;
		CloneIndexManager.getManager().removeCloneIndexFromCache(project);
	}

	public boolean isCloneTrackerEnable(IProject project){
//		return currentProject !=null && project.getName().equals(currentProject.getName()) && 
		return trackedProjectMap.containsKey(project.getName());
	}

	public boolean shouldInvokeNewDetection = false;
	
	public void addToChangedResource(final IProject project, IFile changedFile) {
		if (isCloneTrackerEnable(project)){
			HashMap<IFile, Long> existingFilesMap = trackedProjectMap.get(project.getName());

			boolean doAdd = true;
			for (Entry<IFile, Long> existingFileEntry : existingFilesMap.entrySet()){
				if(existingFileEntry.getKey().getFullPath().toOSString().equals(changedFile.getFullPath().toOSString())){
					if(existingFileEntry.getValue() != changedFile.getModificationStamp()){ //file changed
						existingFilesMap.remove(existingFileEntry.getKey());
						doAdd = true;
						break;
					}else{
						doAdd = false;
						break;
					}
				}
			}
			
			if(doAdd){
				existingFilesMap.put(changedFile, changedFile.getModificationStamp());
				shouldInvokeNewDetection = true;
			}
			
			if (shouldInvokeNewDetection) {

				Job configureDetectOnChangeJob = new Job("Processing for clone detection on resource change...") {
					@Override
					protected IStatus run(final IProgressMonitor monitor) {
						// this involves index update of project where DetectOnChange feature is Enabled
						
//						Display.getDefault().asyncExec(new Runnable() {
//							@Override
//							public void run() {

								invokeDetectionForChangedresource(project, monitor);
//							}
//						});
						return Status.OK_STATUS;
					}
				};
				
				configureDetectOnChangeJob.schedule();
				
				//ProgressMonitorDialog pd = new ProgressMonitorDialog(SimClipsePlugin.getActiveWorkbenchWindow().getShell());
				//pd.run(fork, cancelable, runnable);
				
				/*
				IWorkbenchWindow window = SimClipsePlugin.getActiveWorkbenchWindow();
				try {
					window.run(true, true, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException,InterruptedException {
							
							invokeDetectionForChangedresource(project, monitor);
						}
					});
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// User canceled the operation... just ignore.
				}
				*/
				
				shouldInvokeNewDetection = false;
			}
		}
	}
	
	Set<IFile> getChangedResources(IProject project){
		if (isCloneTrackerEnable(project))
			return trackedProjectMap.get(project.getName()).keySet();
		else
			return Collections.emptySet();
	}
	
	public void saveDetectionOnResourceChangeStatus(IProject project, boolean enableDetectionOnResourceChange) {
		//TODO: 
		//RuntimeSettingsManager.getManager().saveRuntimeSettingsForProject(project, new RuntimeSettings(enableDetectionOnResourceChange));
		
		IPath simclipseDataFolder = project.getLocation().append(SimClipseConstants.SIMCLIPSE_DATA_FOLDER);
		
		if (simclipseDataFolder.toFile().exists()
				&& simclipseDataFolder
						.append(SimClipseConstants.SIMCLIPSE_SETTINGS_FILE).toFile()
						.exists()) {
			if(enableDetectionOnResourceChange){
				if(!isCloneTrackerEnable(project)){
					enableCloneTracker(project);
				} else
					return;
			}else{
				if(isCloneTrackerEnable(project)){
					disableCloneTracker(project);
				} else
					return;
			}
			PropertyUtil.addOrUpdateSimclipseProperties(project, "simclipse.settings.local.runtime.detectionOnResourceChange", Boolean.toString(enableDetectionOnResourceChange));
			ProjectViewManager.getManager().refreshProjectViewItems(project, enableDetectionOnResourceChange);
		}		
	}
	
	public void configureDetectOnChangeEnabledProjects() {
	      IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
	      for (int i = 0; i < projects.length; i++){
	    	  IProject project = projects[i];
	    	  IPath simclipseDataFolder = project.getLocation().append(SimClipseConstants.SIMCLIPSE_DATA_FOLDER);
	    	  if(simclipseDataFolder.toFile().exists()
	    			  && simclipseDataFolder.append(SimClipseConstants.SIMCLIPSE_SETTINGS_FILE).toFile().exists()){

	    		  boolean isSimEclipseEnable = Boolean.valueOf(PropertyUtil.getSimClipsePropertyValue(project, "simclipse.settings.local.active"));
	    		  if(isSimEclipseEnable){
	    			  boolean enableDetectionOnResourceChange = Boolean.valueOf(PropertyUtil.getSimClipsePropertyValue(project, "simclipse.settings.local.runtime.detectionOnResourceChange"));
	    		  
	    			  if(enableDetectionOnResourceChange)
	    				  enableCloneTracker(project, true);
	    	  		}
	    	  }
	      }
	  }

	private IStatus invokeDetectionForChangedresource(final IProject project, IProgressMonitor monitor) {
		
		monitor.beginTask("...", 5);
		
		Set<IFile> changedResources = getChangedResources(project);
		Set<String> modifiedResourceIDs = new HashSet<String>();
		
		IPath simclipseTmpDataFolder =  new Path(Environment.getSimLibRoot()).removeLastSegments(2).append("tmp"); 
		
		String newSourceDir = simclipseTmpDataFolder.append(project.getName()).toOSString();
		String newOutpulDir = newSourceDir+"_simcad_clones";
		
		ICloneIndex newCloneIndex = null;
		ICloneIndex oldCloneIndex = null;
		
		try{
		
			monitor.subTask("Locating changed files...");
			SubProgressMonitor subMonitor1 = new SubProgressMonitor(monitor, 1);
			subMonitor1.beginTask("",changedResources.size());
			for(IFile changedFile : changedResources){
				
				subMonitor1.subTask(changedFile.getFullPath().toString());
				subMonitor1.worked(1);
				
				if(!changedFile.exists()){
					trackedProjectMap.get(project.getName()).remove(changedFile);
					continue;
				}
				
				IPath tmp_src = simclipseTmpDataFolder.append(changedFile.getFullPath());//.append(SimClipseConstants.SIMCLIPSE_TMP_SRC_FOLDER);
		
				if(!tmp_src.removeLastSegments(1).toFile().exists()){
					tmp_src.removeLastSegments(1).toFile().mkdirs();
				}
//				System.out.println("copying :" + changedFile.getName());
				Files.copy(((IFile)changedFile).getLocation().toFile(), tmp_src.toFile());
			}
			
			subMonitor1.done();
			
			if (monitor.isCanceled())
		        return Status.CANCEL_STATUS;
			
			final DetectionSettings detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(project);
			newCloneIndex = CloneIndexManager.getManager().buildCloneIndex(detectionSettings, newSourceDir, newOutpulDir, true);
			oldCloneIndex = CloneIndexManager.getManager().getCloneIndex(project, detectionSettings);
			
			boolean isUrlRelative = PropsUtil.getIsFragmentFileRelativeURL();
			
			final List<CloneFragment> filteredNewCloneFragments = new ArrayList<CloneFragment>();
			
			monitor.subTask("Identifying changed fragments...");
			SubProgressMonitor subMonitor2 = new SubProgressMonitor(monitor, 1);
			subMonitor2.beginTask("",changedResources.size());
			
			for(IFile file : changedResources){
				
				subMonitor2.subTask(file.getFullPath().toString());
				subMonitor2.worked(1);
				
				Collection<CloneFragment> newCloneFragments = CloneIndexManager.getCloneFragmentsbyResourceId(newCloneIndex, isUrlRelative, file);
				
				Collection<CloneFragment> oldCloneFragments = new ArrayList<CloneFragment>();
				oldCloneFragments.addAll(CloneIndexManager.getCloneFragmentsbyResourceId(oldCloneIndex, isUrlRelative, file));
				
//				for(CloneFragment cf : oldCloneIndex.getAllEntries()){
//					System.out.println(cf.getFileName());
//				}
				
				//find new and changed clone fragments, filter out the unchanged ones
				for(CloneFragment newF : newCloneFragments){
					ClonePair oldFCloseToNewF = findClosestMatch(newF, oldCloneFragments);
					if(oldFCloseToNewF == null){ //newF is a fresh one
						filteredNewCloneFragments.add(newF);
					}else{
						if(oldFCloseToNewF.getCloneType().equals(CloneSet.CLONE_TYPE_1)){ //similar fragment in two sets, hence both discarded
							oldCloneFragments.remove(oldFCloseToNewF.getMember(1));
							continue;
						}else{ //this fNew fragment is a existing one with some change or a new one that was cloned from here and then changed
							filteredNewCloneFragments.add(newF); //no matter its new or old, since its modified, it will be shown if matches with other clone
							/*
							List<CloneSet> detectionResultForOldF = cloneDetector.detect(Arrays.asList(fOldCloseToFnew));
							if(detectionResultForOldF == null || detectionResultForOldF.isEmpty()){ //old fragment was not a clone, new one might be!
								filteredNewCloneFragments.add(fNew);
							}else{ 
								List<CloneSet> detectionResultForNewF = cloneDetector.detect(Arrays.asList(fNew));
								if(detectionResultForNewF !=null && detectionResultForNewF.size() > detectionResultForOldF.size()){ //new one grabs more clones than older
									filteredNewCloneFragments.add(fNew);
								}
							}
							*/
						}
						
					}
				}
//				if(newCloneFragments != null)
//				for(CloneFragment cf : newCloneFragments){
//					System.out.println(cf.toString());
//				}
			}
			
			subMonitor2.done();
			
			if (monitor.isCanceled())
		        return Status.CANCEL_STATUS;
			
			if (filteredNewCloneFragments == null
					|| filteredNewCloneFragments.isEmpty())
				return Status.OK_STATUS;
			
//			//test code
//			System.out.println("Filtered new clone fragments...");
//			for(CloneFragment cf : filteredNewCloneFragments){
//				System.out.println(cf.toString());
//			}
			
			// do the new clone detection and other stuff
			
			monitor.subTask("Updating clone index with modified fragments...");
			SubProgressMonitor subMonitor3 = new SubProgressMonitor(monitor, 1);
			subMonitor3.beginTask("",filteredNewCloneFragments.size());
			
			//modify the clone index
			for(CloneFragment cf : filteredNewCloneFragments){
				
				subMonitor3.subTask(cf.toShortString());
				subMonitor3.worked(1);
				
				if(!modifiedResourceIDs.contains(cf.getFileName())){
					modifiedResourceIDs.add(cf.getFileName());
					
					//make the old fragments ignored by the detector
					Collection<CloneFragment> oldFragments = oldCloneIndex.getAllByResourceId(cf.getFileName());
					for (CloneFragment oldCloneFragment : oldFragments) {
						oldCloneFragment.isProceessed=true;
					}
					
					//add the new fragments as newly injected
					Collection<CloneFragment> changedOrNewFragments = newCloneIndex.getAllByResourceId(cf.getFileName());{}
					for (CloneFragment changedFragment : changedOrNewFragments) {
						changedFragment.isInjected = true;
						oldCloneIndex.insert(changedFragment);
					}
				}
			}
			
			subMonitor3.done();

			if (monitor.isCanceled())
		        return Status.CANCEL_STATUS;
			
			//invoke detection job
			monitor.subTask("Detecting clones...");
			SubProgressMonitor subMonitor4 = new SubProgressMonitor(monitor, 1);
			subMonitor4.beginTask("",1);
			
			CloneDetector cloneDetector = CloneDetector.getInstance(oldCloneIndex, detectionSettings);
			final List<CloneSet> detectionResult = cloneDetector.detect(filteredNewCloneFragments);
			
			subMonitor4.worked(1);
			subMonitor4.done();
			
			if(detectionResult == null || detectionResult.isEmpty()){
				//invoke display
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						// ... do any work that updates the screen ...
						CloneViewManager.getManager().resetCloneView();					
//						//test code
//						for (CloneSet cloneSet : detectionResult) {
//							System.out.println(cloneSet.toString());
//						}
					}
				});
			}
			
			
			if (monitor.isCanceled())
		        return Status.CANCEL_STATUS;
			
			
			monitor.subTask("Preparing for clone display...");
			
			SubProgressMonitor subMonitor5 = new SubProgressMonitor(monitor, 1);
			subMonitor5.beginTask("",1);
			
			//invoke display
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					// ... do any work that updates the screen ...
					CloneViewManager.getManager().displayClone(project, filteredNewCloneFragments, detectionResult, detectionSettings);
					
//					//test code
//					for (CloneSet cloneSet : detectionResult) {
//						System.out.println(cloneSet.toString());
//					}
				}
			});
			
			subMonitor5.worked(1);
			subMonitor5.done();
			
			monitor.done();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			FileUtil.deleteDirectory(newSourceDir);
			FileUtil.deleteDirectory(newOutpulDir);
			
			//restore index to original state
			if(oldCloneIndex !=null ){
				if(modifiedResourceIDs != null)
					oldCloneIndex.removeAnythingInjectedIn(modifiedResourceIDs);
				if(oldCloneIndex.isDirty()){
					oldCloneIndex.resetDetectionFlags();
				}
			}
		}
		
	    monitor.done();
	    return Status.OK_STATUS;
	}

	private ClonePair findClosestMatch(CloneFragment newF, Collection<CloneFragment> oldCloneFragments) {
		ClonePair closestMatch = null;
		
		for (CloneFragment oldF : oldCloneFragments){
			
			if (newF.getLineOfCode() != oldF.getLineOfCode()
	    			|| !newF.getSimhash1().equals(oldF.getSimhash1())) {
	    		 //if difference in line or hash code (note: line might be same in case of type 3 where a line has been replaced by another)
				//do nothing!
	        }else {
	        	//either type 1 (equal simhash and equal original source) or 2 (equal simhash but dissimilar original source)
	        	if (newF.getSimhash1().equals(oldF.getSimhash1())
	        			&& FastStringComparator.INSTANCE.compare(newF.getOriginalCodeBlock(), oldF.getOriginalCodeBlock()) != 0) {
	        		//type 2
	        		closestMatch = new ClonePair(newF, oldF, CloneSet.CLONE_TYPE_2, 0);
	        	}else{ 
	        		//type 1
	        		closestMatch = new ClonePair(newF, oldF, CloneSet.CLONE_TYPE_1, 0);
	        		break;
	        	}
	        }
		}
		return closestMatch;
	}

}
