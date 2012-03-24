package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ca.usask.cs.srlab.simclipse.SimClipsePlugin;

public class CNFContentProvider implements ITreeContentProvider {

    private static final Object[] EMPTY_ARRAY = new Object[0];
    private IProject[] projects;
 
    public Object[] getChildren(Object parentElement)
    {
    	if (parentElement instanceof IWorkspaceRoot) {
			if (projects == null) {
				initializeParents(parentElement);
			}
			return projects;
		} else
    	if (parentElement instanceof Root) {
			if (projects == null) {
				initializeParents(parentElement);
			}
			return projects;
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
 
	public Object getParent(Object element) {
		if (element instanceof Root) {
			return null;
		}
		
		if (element instanceof IProject) {
			return new Root();
		}
		return ((IResource) element).getParent();
	}
 
    public boolean hasChildren(Object element)
    {
        return (element instanceof Root || element instanceof IResource);
    }
 
    public Object[] getElements(Object inputElement)
    {
        return getChildren(inputElement);
    }
 
    public void dispose()
    {
        this.projects = null;
    }
 
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    { /* ... */
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
    
    
    private void initializeParents(Object parentElement)
    {
    	//getSite().getPage().getInput();
    	//projects = ((IWorkspaceRoot)parentElement).getProjects();
    	//IAdaptable input = SimClipsePlugin.getDefault().getActivePage().getInput();
    	projects = SimClipsePlugin.getDefault().getWorkspace().getRoot().getProjects();
//        this.parents = new Parent[3];
//        for (int i = 0; i < this.parents.length; i++)
//        {
//            this.parents[i] = new Parent("Parent " + i);
//            this.parents[i].setRoot(parentElement);
//            Child[] children = new Child[3];
//            for (int j = 0; j < children.length; j++)
//            {
//                children[j] = new Child("Child " + i + j);
//            }
//            this.parents[i].setChildren(children);
//        }
    }
}