package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

public interface INavigatorItem {

	public Object getAdapter(Class adapter);
	
	Object getParent();
	
	Object[] getChildren();
	
	IProject getProject();
	
	String getName();
	
	IResource getIResource();
	
	static INavigatorItem[] NONE = new INavigatorItem[] {};
}
