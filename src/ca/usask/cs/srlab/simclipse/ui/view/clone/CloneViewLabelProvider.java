package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class CloneViewLabelProvider extends LabelProvider {
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

	public void dispose() {
		for (Iterator<Image> i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}
}
