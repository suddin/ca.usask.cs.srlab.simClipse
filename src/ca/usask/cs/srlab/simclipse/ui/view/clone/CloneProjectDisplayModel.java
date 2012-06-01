package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;

import ca.usask.cs.srlab.simclipse.SimClipsePlugin;

public class CloneProjectDisplayModel implements ICloneViewItem{
	
	private IProject project;
	
	private List<ICloneViewItem> cloneSets;
	
	private String cloneSetType;
	
	private int fragmentCount = 0;
	
	public CloneProjectDisplayModel(IProject project, String cloneSetType) {
		super();
		this.project = project;
		this.cloneSetType = cloneSetType.substring(0,1).toUpperCase()+cloneSetType.substring(1);
	}

	public List<ICloneViewItem> getCloneSets() {
		return cloneSets;
	}

	public IProject getProject(){
		return project;
	}

	public String getCloneSetType() {
		return cloneSetType;
	}
	
	@Override
	public ICloneViewItem getParent() {
		return null;
	}

	@Override
	public List<ICloneViewItem> getChildren() {
		return cloneSets != null ? cloneSets : new ArrayList<ICloneViewItem>();
	}

	@Override
	public String getDisplayLabel() {
		//String set = ((CloneSetModel) cloneSets).getCloneSet()  instanceof CloneGroup ? "classes":"pairs"; 
		return project.getName() +"( Clone "+cloneSetType+"s: "+cloneSets.size()+", Clone Fragments: "+fragmentCount+")";
	} 
	
	public void addCloneSetModel(CloneSetDisplayModel cloneSetModel){
		if(cloneSets == null)
			cloneSets = new ArrayList<ICloneViewItem>();
		cloneSets.add(cloneSetModel);
		fragmentCount += cloneSetModel.getChildren().size();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SimClipsePlugin.getImageDescriptor("clone_project.png");
	}

	@Override
	public int getLabelTextColor() {
		return SWT.COLOR_BLACK;
	}
}
