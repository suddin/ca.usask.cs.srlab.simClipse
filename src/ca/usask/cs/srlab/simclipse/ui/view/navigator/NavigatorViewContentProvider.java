package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.navigator.CommonViewer;

public class NavigatorViewContentProvider /*extends AdapterFactoryContentProvider*/ implements ITreeContentProvider, IResourceChangeListener, IResourceDeltaVisitor, INavigatorManagerListener{

	static ResourceSetImpl resourceSet = new ResourceSetImpl();
	
	private CommonViewer viewer;
	private NavigatorManager manager;
	private static final IProject[] EMPTY_ARRAY = new IProject[0];
	
	public NavigatorViewContentProvider(/*AdapterFactory ap*/) {
		//super(NavigatorComposedAdapterFactory.getAdapterFactory());
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		this.viewer = (CommonViewer) viewer;
		
		if(newInput instanceof NavigatorManager){
		if (manager != null)
			manager.removeNavigatorManagerListener(this);
		manager = (NavigatorManager) newInput;
		if (manager != null)
			manager.addNavigatorManagerListener(this);
		}
	}
	
	@Override
	public Object[] getElements(Object object) {
		return getChildren(object);
	}
	
	@Override
    public Object[] getChildren(Object parentElement)
    {
    	if (parentElement instanceof Root || parentElement instanceof NavigatorManager) {
			return manager.getSimClipseProjects();
		} else
    	if (parentElement instanceof IProject || parentElement instanceof IFolder) {
			try {
				List<IResource> childList = new ArrayList<IResource>();
				
				IResource resources[];
				if(parentElement instanceof IProject)
					resources = ((IProject) parentElement).members();
				else 
					resources = ((IFolder) parentElement).members();
				
				for(IResource resource : resources){
					if(checkVisibility(resource))
					childList.add(resource);
				}
				
				return childList.toArray(new IResource[childList.size()]);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else
    	
    	if (parentElement instanceof IFile) {
			return EMPTY_ARRAY;
		} else {
			return EMPTY_ARRAY;
		}
    	return EMPTY_ARRAY;
    }
 
	@Override
	public Object getParent(Object element) {
		if (element instanceof Root || element instanceof NavigatorManager) {
			return null;
		}

		if (element instanceof IProject) {
			//return new Root();
			return NavigatorManager.getManager();
		}
		return ((IResource) element).getParent();
	}
 
	@Override
    public boolean hasChildren(Object element) {
        return (element instanceof NavigatorManager || element instanceof Root || element instanceof IResource);
    }

    public boolean checkVisibility(IResource resource) {
		if(resource instanceof IFile){
			IFile file = (IFile) resource;
			if(file.getFileExtension().equals("java"))
				return true;
		}
		else if(resource instanceof IFolder){
			IFolder folder = (IFolder) resource;
			try {
				for(IResource childResource : folder.members()){
					 if(checkVisibility(childResource)){
						 return true;
					 }
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
    
    

	@Override
	public void itemsChanged(NavigatorItemChangeEvent event) {
		viewer.getTree().setRedraw(false);
		try {
			viewer.remove(event.getItemsRemoved());
			viewer.add(viewer.getTree().getTopItem(), event.getItemsAdded());
		} finally {
			viewer.refresh();
			viewer.getTree().setRedraw(true);
		}		
	}
    
    //resource change management
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource changedResource = delta.getResource();
		if (changedResource.getType() == IResource.FILE 
		   && changedResource.getFileExtension().equals("java"))
		{
		    try
		    {
		        String path = ((IFile)changedResource).getFullPath().toString();
		        URI uri = URI.createPlatformResourceURI(path, true);
		        
		        /*
		        
		        Resource res = resourceSet.getResource(uri, true);
		        res.unload();
		        res.load(resourceSet.getLoadOptions());
		        */
		        
		    }
		    catch(/*IO*/Exception ie)
		    {
		        System.out.println("Error reloading resource - " + ie.toString());
		    }	
		    return false;
		}
		return true;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			delta.accept(this);
		} catch (CoreException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public void dispose() {
//		super.dispose();
		this.viewer = null;
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

}
