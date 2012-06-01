package ca.usask.cs.srlab.simclipse.ui.view.project;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

public interface IProjectViewItem extends IAdaptable {
	String getName();

	void setName(String newName);

	String getLocation();
	
	IResource getResource();

	boolean isProjectViewItemFor(Object obj);

	ProjectViewItemType getType();

	String getInfo();

	static IProjectViewItem[] NONE = new IProjectViewItem[] {};

	boolean isDetectOnChangeEnable();

	void setDetectOnChangeEnable(boolean isDetectOnChangeEnable);

	boolean isAutoCloneIndexUpdate();

	void setAutoCloneIndexUpdate(boolean isAutoCloneIndexUpdate);
}