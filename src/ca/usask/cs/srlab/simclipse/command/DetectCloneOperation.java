package ca.usask.cs.srlab.simclipse.command;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

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
import ca.usask.cs.srlab.simclipse.SimClipseConstants;

public class DetectCloneOperation extends WorkspaceModifyOperation implements ICloneDetectionListener{
	
	private List<IResource> iResources;
	private CloneDetector cloneDetector;
	private DetectionSettings detectionSettings;
	private List<CloneSet> detectionResult;
	private IProgressMonitor monitor;
	
	public DetectCloneOperation(final List<IResource> projectForCloneDetection, DetectionSettings detectionSettings) {
		this.iResources = projectForCloneDetection;
		this.detectionSettings = detectionSettings;
	}

	public List<CloneSet> getDetectionResult() {
		return detectionResult;
	}

	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException {
		this.monitor = monitor;
		//String source_dirs[] = SimClipseUtil.getRootFolderForResource(iResources);
		String source_dir = iResources.get(0).getProject().getLocation().toOSString();
		String output_dir = source_dir + System.getProperty("file.separator") + SimClipseConstants.SIMCLIPSE_DATA_FOLDER;
		
		//source data extraction
		FileSystemFragmentDataProviderConfiguration dataProviderConfig = new FileSystemFragmentDataProviderConfiguration(
				source_dir, output_dir,
				SimClipseConstants.SIMCLIPSE_DEFAULT_LANGUAGE,
				detectionSettings.getSourceTransformation(),
				detectionSettings.getCloneGranularity());
		IFragmentDataProvider cloneFragmentDataProvider = new FileSystemFragmentDataProvider(dataProviderConfig);
		
		//index generation
		ICloneIndex cloneIndex = IndexFactory.LoadIndexHolder();
		IndexBuilder indexBuilder = new IndexBuilder(cloneFragmentDataProvider);
		indexBuilder.buildCloneIndex(cloneIndex, detectionSettings);
		
		//get a detector instance
		cloneDetector = CloneDetector.getInstance(cloneIndex, detectionSettings, this);

		Collection<CloneFragment> candidateFragments = getCandidateFragments(cloneIndex);
		
		detectionResult = cloneDetector.detect(candidateFragments);
	}

	private Collection<CloneFragment> getCandidateFragments(ICloneIndex cloneIndex) {
		//Collection<CloneFragment> candidateFragments = new ArrayList<CloneFragment>();
		//prepare input for clone detection: here the whole project is the input
		Collection<CloneFragment> candidateFragments = cloneIndex.getAllEntries();
		
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
