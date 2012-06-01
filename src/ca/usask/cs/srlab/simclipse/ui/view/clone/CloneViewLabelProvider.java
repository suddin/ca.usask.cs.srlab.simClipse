package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class CloneViewLabelProvider extends ColumnLabelProvider {
	private Map<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>(11);
	
	@Override
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		if (element instanceof ICloneViewItem) {
			descriptor = ((ICloneViewItem)element).getImageDescriptor();
		} else {
			throw unknownElement(element);
		}

		//obtain the cached image corresponding to the descriptor
		Image image = (Image)imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}
	/*
	 * @see ILabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof ICloneViewItem) {
			if(((ICloneViewItem)element).getDisplayLabel() == null) {
				return "-";
			} else {
				return ((ICloneViewItem)element).getDisplayLabel();
			}
		} else {
			throw unknownElement(element);
		}
	}

	@Override
	public void dispose() {
		for (Iterator<Image> i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}
	
	
	 @Override
     public Color getForeground(Object element) {
             if (element instanceof ICloneViewItem) {
            	 //CloneFragmentDisplayModel node = (CloneFragmentDisplayModel) element;
                 return Display.getCurrent().getSystemColor(((ICloneViewItem) element).getLabelTextColor());
             }
             return super.getForeground(element);
     }
	

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}
}
