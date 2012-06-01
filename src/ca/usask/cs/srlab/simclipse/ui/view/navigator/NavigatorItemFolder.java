package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;


public class NavigatorItemFolder extends BaseNavigatorViewItem {

	public NavigatorItemFolder (IFolder iFolder){
		super(iFolder);
	}
	
	private IFolder getIFolder(){
		return ((IFolder) iResource);
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;//super.getAdapter(adapter);
	}

	@Override
	public Object getParent() {
		if (iResource.getParent() instanceof IProject)
			return new NavigatorItemProject((IProject) iResource.getParent());
		else
			return new NavigatorItemFolder((IFolder) iResource.getParent());
	}

	public IProject getProject(){
		return ((INavigatorItem)getParent()).getProject();
	}
	
	@Override
	public Object[] getChildren() {
		List<INavigatorItem> ins = Collections.emptyList();
		try {
			ins = new ArrayList<INavigatorItem>(getIFolder().members().length);

			for (IResource ir : getIFolder().members()) {
				if(shouldMakeVisible(ir)){
					if (ir instanceof IFolder)
						ins.add(new NavigatorItemFolder((IFolder) ir));
					else if (ir instanceof IFile)
						ins.add(new NavigatorItemFile((IFile) ir));
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return ins.toArray();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NavigatorItemFolder) {
			NavigatorItemFolder another = (NavigatorItemFolder) obj;
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
