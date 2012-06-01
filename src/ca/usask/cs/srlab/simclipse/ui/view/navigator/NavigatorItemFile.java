package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import ca.usask.cs.srlab.simcad.Constants;
import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.index.ICloneIndex;
import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simcad.util.PropsUtil;
import ca.usask.cs.srlab.simclipse.actions.CloneIndexManager;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsManager;

public class NavigatorItemFile extends BaseNavigatorViewItem {

	//private IFile iFile;
	public NavigatorItemFile (IFile iFile){
		super(iFile);
	}
	
	public IFile getIFile(){
		return (IFile) iResource;
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;//super.getAdapter(adapter);
	}

	@Override
	public IProject getProject(){
		return ((INavigatorItem)getParent()).getProject();
	}
	
	@Override
	public Object getParent() {
		if (iResource.getParent() instanceof IProject)
			return new NavigatorItemProject((IProject) iResource.getParent());
		else
			return new NavigatorItemFolder((IFolder) iResource.getParent());
	}

	@Override
	public Object[] getChildren() {
		
		DetectionSettings detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(iResource.getProject());
		
		if (iResource.getFileExtension() != null
				&& Constants.LANG_EXTENSION_MAP.get(
						detectionSettings.getLanguage()).contains(
								iResource.getFileExtension())) {

			Collection<CloneFragment> codeFragments = getCloneFragmentsForFile((IFile) iResource);

			if (codeFragments == null || codeFragments.isEmpty())
				return EMPTY_ARRAY;

			List<INavigatorItem> ins = new ArrayList<INavigatorItem>(codeFragments.size());

			for (CloneFragment cf : codeFragments) {
				ins.add(new NavigatorItemFileFragment(cf, this));
			}
			return ins.toArray();
		}
		return EMPTY_ARRAY;
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NavigatorItemFile) {
			NavigatorItemFile another = (NavigatorItemFile) obj;
			return another.getIResource().getFullPath().toString().equals(getIResource().getFullPath().toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		int h = getIResource().getFullPath().toString().hashCode();
		h = (int) (31 * h + getIResource().getModificationStamp());
		h = 31 * h + getIResource().getType();
		return h;
	}
	
}
