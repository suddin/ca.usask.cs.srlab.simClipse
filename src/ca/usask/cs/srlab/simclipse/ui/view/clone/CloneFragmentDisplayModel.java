package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;

import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simcad.model.ICloneFragment;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;

public class CloneFragmentDisplayModel extends CloneFragment implements ICloneViewItem{
	
	private CloneSetDisplayModel parentCloneSet;
	
	public CloneFragmentDisplayModel(ICloneFragment cloneFragment, CloneSetDisplayModel parentCloneSet) {
		super((CloneFragment) cloneFragment);
		this.parentCloneSet = parentCloneSet;
	}

	public CloneSetDisplayModel getParentCloneSet() {
		return parentCloneSet;
	}

	public String getDisplayLabel(){
		return getFileName() +" [Line: "+getFromLine()+"-"+getToLine()+"]";
	} 
	
	public IFile getResource(){
		IProject project = parentCloneSet.getParentProject().getProject();
		if(getFileName().startsWith(project.getLocation().toOSString()))
			return (IFile)  project.findMember(new Path(getFileName()).makeRelativeTo(project.getLocation()));
		else
			return (IFile) project.findMember(getFileName());
	}

	@Override
	public ICloneViewItem getParent() {
		return parentCloneSet;
	}

	@Override
	public List<ICloneViewItem> getChildren() {
		return new ArrayList<ICloneViewItem>();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SimClipsePlugin.getImageDescriptor("clone_fragment_1.png");
	}
}
