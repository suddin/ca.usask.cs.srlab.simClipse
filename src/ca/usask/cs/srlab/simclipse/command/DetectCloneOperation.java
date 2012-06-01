package ca.usask.cs.srlab.simclipse.command;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.detection.CloneDetector;
import ca.usask.cs.srlab.simcad.event.CloneFoundEvent;
import ca.usask.cs.srlab.simcad.event.DetectionEndEvent;
import ca.usask.cs.srlab.simcad.event.DetectionProgressEvent;
import ca.usask.cs.srlab.simcad.event.DetectionStartEvent;
import ca.usask.cs.srlab.simcad.index.ICloneIndex;
import ca.usask.cs.srlab.simcad.listener.ICloneDetectionListener;
import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simcad.model.CloneSet;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;

public class DetectCloneOperation extends WorkspaceModifyOperation implements ICloneDetectionListener{
	
	//private List<IProject> scopeForCloneDetection;
	ICloneIndex cloneIndex;
	private List<CloneFragment> candidateForCloneDetection;
	private CloneDetector cloneDetector;
	private DetectionSettings detectionSettings;
	private List<CloneSet> detectionResult;
	private IProgressMonitor monitor;
	
	public DetectCloneOperation(final List<CloneFragment> candidateForCloneDetection, ICloneIndex cloneIndex, DetectionSettings detectionSettings) {
		this.candidateForCloneDetection = candidateForCloneDetection;
		this.cloneIndex = cloneIndex;
		this.detectionSettings = detectionSettings;
	}

//	public DetectCloneOperation(final List<IProject> scopeForCloneDetection, DetectionSettings detectionSettings) {
//		this(null, scopeForCloneDetection, detectionSettings);
//	}
	
	public List<CloneSet> getDetectionResult() {
		return detectionResult;
	}

	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException {
		
        SimClipsePlugin.getDefault().printToConsole("Executing clone detection operation...");
		
		this.monitor = monitor;
		//String source_dirs[] = SimClipseUtil.getRootFolderForResource(iResources);
		
		//get a detector instance
		cloneDetector = CloneDetector.getInstance(cloneIndex, detectionSettings, this);

		//SimClipsePlugin.getDefault().printToConsole("number of fragments : " + candidateFragments.size());
		detectionResult = cloneDetector.detect(candidateForCloneDetection);
//		
		SimClipsePlugin.getDefault().printToConsole("Printing detection result...");
//	      for(CloneSet cs : detectionResult){
//	    	  SimClipsePlugin.getDefault().printToConsole( cs.toString());
//	      }
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
		monitor.beginTask("Clone Detecting in progress...", event.getInputSize());
        
	}
}
