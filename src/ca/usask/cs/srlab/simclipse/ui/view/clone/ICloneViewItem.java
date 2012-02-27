package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;


public interface ICloneViewItem /*extends IAdaptable*/ {
	//String getName();
	ICloneViewItem getParent();
	List<ICloneViewItem> getChildren();
	String getDisplayLabel();
	ImageDescriptor getImageDescriptor();
}