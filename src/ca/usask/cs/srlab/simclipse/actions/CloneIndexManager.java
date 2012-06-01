package ca.usask.cs.srlab.simclipse.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.dataprovider.IFragmentDataProvider;
import ca.usask.cs.srlab.simcad.dataprovider.filesystem.FileSystemFragmentDataProvider;
import ca.usask.cs.srlab.simcad.dataprovider.filesystem.FileSystemFragmentDataProviderConfiguration;
import ca.usask.cs.srlab.simcad.index.ICloneIndex;
import ca.usask.cs.srlab.simcad.index.IndexBuilder;
import ca.usask.cs.srlab.simcad.index.IndexFactory;
import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsManager;

public class CloneIndexManager {

	private final int MAX_CLONE_INDEX_MAP = 20;
	private Map<String, ICloneIndex> cloneIndexCache = new HashMap<String, ICloneIndex>(MAX_CLONE_INDEX_MAP);
	private static CloneIndexManager manager;
	
	public static CloneIndexManager getManager() {
		if(manager == null)
			manager = new CloneIndexManager();
		return manager;
	}

	public ICloneIndex getCloneIndex(IProject scopeForCloneDetection, DetectionSettings detectionSettings){
		String indexKey = scopeForCloneDetection.getName()+"#"+detectionSettings.getCloneGranularity()+"#"+detectionSettings.getSourceTransformation();
		if(cloneIndexCache.containsKey(indexKey)){
			ICloneIndex cloneIndex = cloneIndexCache.get(indexKey);
			if(cloneIndex.isDirty()) cloneIndex.resetDetectionFlags();
			return cloneIndex;
		} else
			return getCloneIndex(scopeForCloneDetection, detectionSettings, true);
	}
	
	public ICloneIndex getCloneIndex(IProject scopeForCloneDetection, DetectionSettings detectionSettings, boolean forceBuild){
		
		if(!forceBuild){
			return getCloneIndex(scopeForCloneDetection, detectionSettings);
		}
		
		String source_dir = scopeForCloneDetection.getLocation().toOSString();
		String output_dir = source_dir + System.getProperty("file.separator") + SimClipseConstants.SIMCLIPSE_DATA_FOLDER;
		
		ICloneIndex cloneIndex = buildCloneIndex(detectionSettings, source_dir, output_dir, forceBuild);
		
		String indexKey = scopeForCloneDetection.getName()+"#"+detectionSettings.getCloneGranularity()+"#"+detectionSettings.getSourceTransformation();
		cloneIndexCache.put(indexKey, cloneIndex);
		
		return cloneIndex;
	}

	public ICloneIndex buildCloneIndex(DetectionSettings detectionSettings, String source_dir, String output_dir, boolean forceBuild) {
		//source data extraction
		FileSystemFragmentDataProviderConfiguration dataProviderConfig = new FileSystemFragmentDataProviderConfiguration(
				source_dir, output_dir,
				SimClipseConstants.SIMCLIPSE_DEFAULT_LANGUAGE,
				detectionSettings.getSourceTransformation(),
				detectionSettings.getCloneGranularity(), forceBuild);
		IFragmentDataProvider cloneFragmentDataProvider = new FileSystemFragmentDataProvider(dataProviderConfig);
		
		//index generation
		ICloneIndex cloneIndex = IndexFactory.LoadIndexHolder();
		IndexBuilder indexBuilder = new IndexBuilder(cloneFragmentDataProvider);
		indexBuilder.buildCloneIndex(cloneIndex, detectionSettings);
		return cloneIndex;
	}

	public static Collection<CloneFragment> getCloneFragmentsbyResourceId(
			ICloneIndex cloneIndex, boolean isUrlRelative, IFile file) {
		String relative = System.getProperty("file.separator")+file.getProjectRelativePath().toOSString();
		String full = file.getLocation().toOSString();
		Collection<CloneFragment> cloneFragments;
		if (isUrlRelative)
			cloneFragments = cloneIndex.getAllByResourceId(relative);
		else
			cloneFragments = cloneIndex.getAllByResourceId(full);
		return cloneFragments;
	}

	public void removeCloneIndexFromCache(IProject project) {
		DetectionSettings detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(project);
		String indexKey = project.getName()+"#"+detectionSettings.getCloneGranularity()+"#"+detectionSettings.getSourceTransformation();
		cloneIndexCache.remove(indexKey);
	}

	public void saveAutoCloneIndexUpdateStatus(IProject project,
			boolean enableAutoCloneIndexUpdate) {
		// TODO Auto-generated method stub
		
	}
	
}
