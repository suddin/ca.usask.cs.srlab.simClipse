package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import ca.usask.cs.srlab.simclipse.ui.view.project.IProjectViewItem;

public class NavigatorItemProject extends BaseNavigatorViewItem {

	public NavigatorItemProject (IProject iProject){
		super(iProject);
	}
	
	private IProject getIProject(){
		return ((IProject) iResource);
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;//super.getAdapter(adapter);
	}

	@Override
	public Object getParent() {
		return EMPTY_ARRAY;
	}

	public IProject getProject(){
		return getIProject();
	}
	
	@Override
	public Object[] getChildren() {
		List<INavigatorItem> ins = Collections.emptyList();
		
		if (getProject().isOpen()) {
			try {
				ins = new ArrayList<INavigatorItem>(
						getIProject().members().length);

				for (IResource ir : getIProject().members()) {
					if (shouldMakeVisible(ir)) {
						if (ir instanceof IFolder)
							ins.add(new NavigatorItemFolder((IFolder) ir));
						else if (ir instanceof IFile)
							ins.add(new NavigatorItemFile((IFile) ir));
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return ins.toArray();
	}

	public static INavigatorItem[] toNavigatorItemProjectArray(IProject[] projects) {
		List<INavigatorItem> projectList = new ArrayList<INavigatorItem>(projects.length);
		for (IProject iProject : projects) {
			projectList.add(new NavigatorItemProject(iProject));
		}
		return projectList.toArray(new INavigatorItem[projects.length]);
	}
	
	public static INavigatorItem[] toNavigatorItemProjectArray(IProjectViewItem[] projects) {
		List<INavigatorItem> projectList = new ArrayList<INavigatorItem>(projects.length);
		for (IProjectViewItem iProjectViewItem : projects) {
			if(iProjectViewItem.getResource() instanceof IProject)
				projectList.add(new NavigatorItemProject((IProject) iProjectViewItem.getResource()));
		}
		return projectList.toArray(new INavigatorItem[projectList.size()]);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NavigatorItemProject) {
			NavigatorItemProject another = (NavigatorItemProject) obj;
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
