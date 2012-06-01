package ca.usask.cs.srlab.simclipse.clone.track;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import ca.usask.cs.srlab.simcad.Constants;
import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsManager;

public class ResourceChangeReporter implements IResourceChangeListener, IResourceDeltaVisitor {
	  
    public static final IResourceChangeListener INSTANCE = new ResourceChangeReporter();

    private ResourceChangeReporter(){
    }
    
    private IProject currentProject;
    
    private DetectionSettings detectionSettings;
    
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		
		final IResource changedResource = delta.getResource();
		
		if(changedResource instanceof IProject) {
			IProject project = (IProject) changedResource;
			if(CloneTrackManager.getManager().isCloneTrackerEnable(project)){
				currentProject = project;
				detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(currentProject);
			}else{
				//TODO: shoe message to update clone index as resource has been changed
				currentProject = null;
				return false;
			}
		}
		
		if (changedResource.getType() == IResource.FOLDER) {
			if (changedResource.getName().contains(
					SimClipseConstants.SIMCLIPSE_DATA_FOLDER)){
				System.out.println(".simclipse folder ignored...");
				return false;
			}
		}		
		
		
		
		if (changedResource.getType() == IResource.FILE 
		   &&  changedResource.getFileExtension() !=null 
		   &&  Constants.LANG_EXTENSION_MAP.get(detectionSettings.getLanguage()).contains(changedResource.getFileExtension())){
		       
			if(currentProject != null && (delta.getKind() == IResourceDelta.ADDED 
					|| delta.getKind() == IResourceDelta.CHANGED
					|| delta.getKind() == IResourceDelta.ADDED_PHANTOM)){
			
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						e.printStackTrace();
					}

					public void run() throws Exception {
						
						System.out.println("Captured new/changed resource from Saferunner: "+changedResource.getFullPath());
						try{
							CloneTrackManager.getManager().addToChangedResource(currentProject, ((IFile)changedResource));
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
				/*
				IWorkspaceRunnable operation = new IWorkspaceRunnable() {
					
					@Override
					public void run(IProgressMonitor monitor) throws CoreException {
						System.out.println("Captured new/changed resource from Saferunner: "+resource.getFullPath());
						
						IPath simclipseDataFolder = //Platform.getLocation();
							currentProject.getLocation().append(SimClipseConstants.SIMCLIPSE_DATA_FOLDER);
						
						String newSourceDir = simclipseDataFolder.append(currentProject.getName()).toOSString();
						String newOutpulDir = newSourceDir+"_simcad_clones";
						
						try{
									
							CloneTrackManager.getManager().addChangedResource(currentProject, ((IFile)resource));

							Set<IFile> changedResources = CloneTrackManager.getManager().getChangedResources(currentProject);
							
							for(IFile changedFile : changedResources){
								IPath tmp_src = simclipseDataFolder.append(changedFile.getFullPath());//.append(SimClipseConstants.SIMCLIPSE_TMP_SRC_FOLDER);
								
								if(!tmp_src.removeLastSegments(1).toFile().exists()){
									tmp_src.removeLastSegments(1).toFile().mkdirs();
								}
								
								System.out.println("copying :" + changedFile.getName());
								Files.copy(((IFile)changedFile).getLocation().toFile(), tmp_src.toFile());
							}
							
							DetectionSettings detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(currentProject);
							
							ICloneIndex cloneIndex = CloneIndexManager.getManager().buildCloneIndex(detectionSettings, newSourceDir , newOutpulDir, true);
							
							boolean isUrlRelative = PropsUtil.getIsFragmentFileRelativeURL();
							
							for(IFile file : changedResources){
								Collection<CloneFragment> cloneFragments = CloneIndexManager.getCloneFragmentsbyResourceId(cloneIndex, isUrlRelative, file);
								if(cloneFragments != null)
								for(CloneFragment cf : cloneFragments){
									System.out.println(cf.toString());
								}
							}
						
						}catch (Exception e) {
							e.printStackTrace();
						}finally{
							FileUtil.deleteDirectory(newSourceDir);
							FileUtil.deleteDirectory(newOutpulDir);
						}
						
					}
				};
				
				
				boolean oldLock = ResourcesPlugin.getWorkspace().isTreeLocked();
				
				try {
					if(ResourcesPlugin.getWorkspace().isTreeLocked())
						((Workspace)ResourcesPlugin.getWorkspace()).setTreeLocked(false);
					
					IResource schedulingRule = changedResource;
					ResourcesPlugin.getWorkspace().run(operation, schedulingRule, IResource.NONE, null);
					
				} catch (OperationCanceledException e) {
					e.printStackTrace();
				}finally{
					((Workspace)ResourcesPlugin.getWorkspace()).setTreeLocked(oldLock);
				}
				
				*/
			}
		    return false;
		}
		
		return true;
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_BUILD
				||  event.getType() == IResourceChangeEvent.POST_BUILD) {
			try {
				//event.getDelta().accept(this);
				System.out.println("ignoreing build event...");
				
				//
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
