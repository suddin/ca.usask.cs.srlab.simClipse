package ca.usask.cs.srlab.simclipse.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.dataprovider.IFragmentDataProvider;
import ca.usask.cs.srlab.simcad.dataprovider.filesystem.FileSystemFragmentDataProvider;
import ca.usask.cs.srlab.simcad.dataprovider.filesystem.FileSystemFragmentDataProviderConfiguration;
import ca.usask.cs.srlab.simcad.index.ICloneIndex;
import ca.usask.cs.srlab.simcad.index.IndexBuilder;
import ca.usask.cs.srlab.simcad.index.IndexFactory;
import ca.usask.cs.srlab.simclipse.SimClipseConstants;

public class CloneIndexManager {

	private final int MAX_CLONE_INDEX_MAP = 20;
	private Map<String, ICloneIndex> cloneIndexCache = new HashMap<String, ICloneIndex>(MAX_CLONE_INDEX_MAP);
	private static CloneIndexManager manager;
	
	public static CloneIndexManager getManager() {
		if(manager == null)
			manager = new CloneIndexManager();
		return manager;
	}

	public ICloneIndex getCloneIndex( List<IProject> scopeForCloneDetection, DetectionSettings detectionSettings){
		return getCloneIndex(scopeForCloneDetection, detectionSettings, false);
	}
	
	public ICloneIndex getCloneIndex( List<IProject> scopeForCloneDetection, DetectionSettings detectionSettings, boolean forceBuild){
		String indexKey = scopeForCloneDetection.get(0).getProject().getName()+"#"+detectionSettings.getCloneGranularity()+"#"+detectionSettings.getSourceTransformation();
		
		if(!forceBuild){
			if(cloneIndexCache.containsKey(indexKey)){
				return cloneIndexCache.get(indexKey).resetDetectionFlags();
			}
		}
		
		String source_dir = scopeForCloneDetection.get(0).getProject().getLocation().toOSString();
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
		
		cloneIndexCache.put(indexKey, cloneIndex);
		
		return cloneIndex;
	}
	
	
}
