package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class CNFLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider
{
    public String getText(Object element)
    {
        if (element instanceof IResource)
        {
        	return ((IResource) element).getName();
        }
        return null;
    }
 
    public String getDescription(Object element)
    {
        String text = getText(element);
        return "This is a description of " + text;
    }
 
	public Image getImage(Object element) {
		if (element instanceof IProject) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT);
		} else if (element instanceof IFolder) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FOLDER);
		} else if (element instanceof IFile) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FILE);
		}
		return null;
	}
}
