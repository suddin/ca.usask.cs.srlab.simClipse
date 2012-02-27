package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;

import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simcad.model.CloneSet;
import ca.usask.cs.srlab.simcad.model.ICloneFragment;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;

public class CloneSetDisplayModel extends CloneSet implements ICloneViewItem{
	
	private CloneProjectDisplayModel parentProject;
	
	public CloneSetDisplayModel(CloneSet cloneSet, CloneProjectDisplayModel parentProject) throws CloneNotSupportedException {
		super(cloneSet.clone());
		this.parentProject = parentProject;
		
		this.getCloneFragments().clear();
		
		List<ICloneFragment> cloneFragments = cloneSet.getCloneFragments();
		for(ICloneFragment iCloneFragment : cloneFragments){
			CloneFragment cf = (CloneFragment) iCloneFragment;
			CloneFragmentDisplayModel cloneFragmentModel = new CloneFragmentDisplayModel(cf.clone() , this);
			addCloneFragmentModel(cloneFragmentModel);
		}
	}
	
	public List<? extends ICloneViewItem> getCloneFragmentModels() {
		return (List<? extends ICloneViewItem>) this.getCloneFragments();
	}

	public CloneProjectDisplayModel getParentProject() {
		return parentProject;
	}

	@Override
	public String getDisplayLabel() {
		return "Clone "+parentProject.getCloneSetType()+"-"+ getCloneSetId().toString() +" ("+this.getCloneType()+", Size : "+this.getCloneFragments().size()+")";
	}

	@Override
	public ICloneViewItem getParent() {
		return parentProject;
	}

	@Override
	public List<ICloneViewItem> getChildren() {
		return this.getCloneFragments() != null ? Arrays.asList(this.getCloneFragments().toArray(new ICloneViewItem[this.getCloneFragments().size()])) : new ArrayList<ICloneViewItem>();
	}
	
	private void addCloneFragmentModel(CloneFragmentDisplayModel cloneFragmentModel){
		if(this.getCloneFragments() == null)
			this.setCloneFragments(new ArrayList<ICloneFragment>());
		this.getCloneFragments().add(cloneFragmentModel);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SimClipsePlugin.getImageDescriptor("clone_group_4.png");
	}
}
