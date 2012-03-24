package ca.usask.cs.srlab.simclipse.command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.dataprovider.IFragmentDataProvider;
import ca.usask.cs.srlab.simcad.dataprovider.filesystem.FileSystemFragmentDataProvider;
import ca.usask.cs.srlab.simcad.dataprovider.filesystem.FileSystemFragmentDataProviderConfiguration;
import ca.usask.cs.srlab.simcad.detection.CloneDetector;
import ca.usask.cs.srlab.simcad.event.CloneFoundEvent;
import ca.usask.cs.srlab.simcad.event.DetectionEndEvent;
import ca.usask.cs.srlab.simcad.event.DetectionProgressEvent;
import ca.usask.cs.srlab.simcad.event.DetectionStartEvent;
import ca.usask.cs.srlab.simcad.index.ICloneIndex;
import ca.usask.cs.srlab.simcad.index.IndexBuilder;
import ca.usask.cs.srlab.simcad.index.IndexFactory;
import ca.usask.cs.srlab.simcad.listener.ICloneDetectionListener;
import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simcad.model.CloneSet;
import ca.usask.cs.srlab.simcad.util.PropsUtil;
import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.SimClipseException;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.actions.CloneIndexManager;
import ca.usask.cs.srlab.simclipse.util.FileUtil;

public class DetectCloneOperation extends WorkspaceModifyOperation implements ICloneDetectionListener{
	
	private List<IProject> scopeForCloneDetection;
	private List<IResource> candidateForCloneDetection;
	private CloneDetector cloneDetector;
	private DetectionSettings detectionSettings;
	private List<CloneSet> detectionResult;
	private IProgressMonitor monitor;
	
	public DetectCloneOperation(final List<IResource> candidateForCloneDetection, final List<IProject> scopeForCloneDetection, DetectionSettings detectionSettings) {
		this.candidateForCloneDetection = candidateForCloneDetection;
		this.scopeForCloneDetection = scopeForCloneDetection;
		this.detectionSettings = detectionSettings;
	}

	public DetectCloneOperation(final List<IProject> scopeForCloneDetection, DetectionSettings detectionSettings) {
		this(null, scopeForCloneDetection, detectionSettings);
	}
	
	public List<CloneSet> getDetectionResult() {
		return detectionResult;
	}

	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException {
		
        SimClipsePlugin.getDefault().printToConsole("Executing clone detection operation...");
		
		this.monitor = monitor;
		//String source_dirs[] = SimClipseUtil.getRootFolderForResource(iResources);
		
		ICloneIndex cloneIndex = CloneIndexManager.getManager().getCloneIndex(scopeForCloneDetection, detectionSettings, false);
		
		//get a detector instance
		cloneDetector = CloneDetector.getInstance(cloneIndex, detectionSettings, this);

		Collection<CloneFragment> candidateFragments = getCandidateFragments(cloneIndex);
		
		//SimClipsePlugin.getDefault().printToConsole("number of fragments : " + candidateFragments.size());
		
		detectionResult = cloneDetector.detect(candidateFragments);
		
//		SimClipsePlugin.getDefault().printToConsole("Printing detection result...");
//	      for(CloneSet cs : detectionResult){
//	    	  SimClipsePlugin.getDefault().printToConsole( cs.toString());
//	      }
	}

	private Collection<CloneFragment> getCandidateFragments(ICloneIndex cloneIndex) {
		Collection<CloneFragment> candidateFragments;// = new ArrayList<CloneFragment>();
		//prepare input for clone detection: here the whole project is the input
		if(candidateForCloneDetection == null || candidateForCloneDetection.isEmpty())
			candidateFragments = cloneIndex.getAllEntries();
		else{
			List<IFile> fileList = new ArrayList<IFile>();
			for(IResource resource : candidateForCloneDetection){
				if(resource instanceof IFolder)
					fileList.addAll(FileUtil.getFilesFromFolder((IFolder)resource));
				else
					fileList.add((IFile) resource);
			}
			
			candidateFragments = new ArrayList<CloneFragment>();
			
			boolean isUrlRelative = PropsUtil.getIsFragmentFileRelativeURL();
			
			for(IFile file :  fileList){
				String relative = System.getProperty("file.separator")+file.getProjectRelativePath().toOSString();
				String full = file.getLocation().toOSString();
				Collection<CloneFragment> cloneFragments;
				if (isUrlRelative)
					cloneFragments = cloneIndex.getByResourceId(relative);
				else
					cloneFragments = cloneIndex.getByResourceId(full);

				if (cloneFragments != null)
					candidateFragments.addAll(cloneFragments);
			}
		}
		
		if(candidateFragments == null || candidateFragments.isEmpty()){
			throw new SimClipseException("Error in identifying the candidate fragments for clone detection");
		}
		return candidateFragments;
	}

	@Override
	public void endDetection(DetectionEndEvent arg0) {
		 monitor.done();
	}

	@Override
	public void foundClone(CloneFoundEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void progressDetection(DetectionProgressEvent arg0) {
		 monitor.worked(1);
	}

	@Override
	public void startDetection(DetectionStartEvent event) {
		monitor.beginTask("Detecting clones in project...", event.getInputSize());
        
	}
}
