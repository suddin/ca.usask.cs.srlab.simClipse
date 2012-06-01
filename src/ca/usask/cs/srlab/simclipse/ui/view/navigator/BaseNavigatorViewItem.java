package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import ca.usask.cs.srlab.simcad.Constants;
import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.index.ICloneIndex;
import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simcad.util.PropsUtil;
import ca.usask.cs.srlab.simclipse.actions.CloneIndexManager;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsManager;

public abstract class BaseNavigatorViewItem implements INavigatorItem{

	protected IResource iResource;
	
	protected static final INavigatorItem[] EMPTY_ARRAY = new INavigatorItem[0];

	public BaseNavigatorViewItem(IResource iResource) {
		super();
		this.iResource = iResource;
	}

	@Override
	public IResource getIResource(){
		return iResource;
	}
	
	@Override
	public String getName() {
		return iResource.getName();
	}
	
	
	private static class CacheEntry {
		private boolean isVisible;
		private long lastModificationTimestamp;

		public CacheEntry(boolean isVisible, long lastModificationTimestamp) {
			super();
			this.isVisible = isVisible;
			this.lastModificationTimestamp = lastModificationTimestamp;
		}
	}
	
	private static final Map<String, CacheEntry> visibilityDecisionCache = new HashMap<String, CacheEntry>();
	
	public boolean shouldMakeVisible(IResource resource) {
		if (visibilityDecisionCache.containsKey(resource.getFullPath()
				.toString())) {
			CacheEntry cacheEntry = visibilityDecisionCache.get(resource
					.getFullPath().toString());
			if (cacheEntry.lastModificationTimestamp == resource
					.getLocalTimeStamp()) {
				//System.out.println("Cache hit for : "+ resource.getName());
				return cacheEntry.isVisible;
			}
		}
		//System.out.println("Cache miss for : "+ resource.getName());
		
		Boolean shouldVisible = isVisible(resource);
		CacheEntry cacheEntry = new CacheEntry(shouldVisible,
				resource.getLocalTimeStamp());
		visibilityDecisionCache.put(resource.getFullPath().toString(),
				cacheEntry);
		return shouldVisible;
	}
	
	private boolean isVisible(IResource resource) {
		
		if(resource instanceof IFile){
			
			DetectionSettings detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(resource.getProject());
			
			IFile file = (IFile) resource;
			if (file.getFileExtension() != null
					&& Constants.LANG_EXTENSION_MAP.get(
							detectionSettings.getLanguage()).contains(
							file.getFileExtension())) {
				return true;
				/*
				Collection<CloneFragment> codeFragments = getCloneFragmentsForFile(file);
				return codeFragments != null ? true : false;
				*/
			}
		}
		else if(resource instanceof IFolder){
			IFolder folder = (IFolder) resource;
			try {
				for(IResource childResource : folder.members()){
					 if(shouldMakeVisible(childResource)){
						 return true;
					 }
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public Collection<CloneFragment> getCloneFragmentsForFile(IFile iFile) {
		boolean isUrlRelative = PropsUtil.getIsFragmentFileRelativeURL();
		DetectionSettings detectionSettings = DetectionSettingsManager
				.getManager().getSavedDetectionSettingsForProject(((IFile) iFile).getProject());
		
		ICloneIndex cloneIndex = CloneIndexManager.getManager().getCloneIndex(
				((IFile) iFile).getProject(), detectionSettings);
		
		Collection<CloneFragment> codeFragments = CloneIndexManager
				.getCloneFragmentsbyResourceId(cloneIndex, isUrlRelative, iFile);
		return codeFragments;
	}

}