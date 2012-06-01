package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class NavigatorViewNewLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider
{
    public String getText(Object element)
    {
        if (element instanceof INavigatorItem)
        {
        	return ((INavigatorItem) element).getName();
        }
       
        if(element instanceof AbstractCodeFragment){
			return ((AbstractCodeFragment) element).getName();
		}
        
        return null;
    }
 
    public String getDescription(Object element)
    {
        String text = getText(element);
        return "This is a description of " + text;
    }
 
	public Image getImage(Object element) {
		if (element instanceof NavigatorItemProject) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT);
		} else if (element instanceof NavigatorItemFolder) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FOLDER);
		} else if (element instanceof NavigatorItemFile) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FILE);
		} else if(element instanceof NavigatorItemFileFragment){
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
		return null;
	}
}
