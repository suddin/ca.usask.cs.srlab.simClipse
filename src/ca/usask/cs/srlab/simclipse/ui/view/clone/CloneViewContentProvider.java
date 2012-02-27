package ca.usask.cs.srlab.simclipse.ui.view.clone;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewPart;

import ca.usask.cs.srlab.simclipse.SimClipsePlugin;

public class CloneViewContentProvider implements ITreeContentProvider, CloneViewManagerListener {

	private TreeViewer viewer;
	private CloneViewManager manager;
	private final Object[] EMPTY_ARRAY= new Object[0];
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;
		if (manager != null)
			manager.removeCloneViewManagerListener(this);
		manager = (CloneViewManager) newInput;
		if (manager != null)
			manager.addCloneViewManagerListener(this);
	}

	@Override
	public Object[] getElements(Object parent) {
		IViewPart cloneView = SimClipsePlugin.getViewIfExists(CloneView.ID);
		if (cloneView == null || parent.equals(cloneView.getViewSite())
				|| (parent instanceof CloneViewManager) ) {
			return manager.getCloneViewItems();
		}
		return getChildren(parent);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof ICloneViewItem
				&& hasChildren(parentElement) ) {
	        return ((ICloneViewItem) parentElement).getChildren().toArray();
	    }
	    return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object child) {
		if(child instanceof ICloneViewItem) {
	        return ((ICloneViewItem)child).getParent();
	    }
	    return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ICloneViewItem)
			return ((ICloneViewItem) element).getChildren() != null
					&& ((ICloneViewItem) element).getChildren().size() > 0;
		return false;
	}

	@Override
	public void executeEvent(CloneViewEvent event) {
		viewer.getTree().setRedraw(false);
		try {
			viewer.getTree().clearAll(true);
			viewer.setInput(event.getSource());
			viewer.expandAll();
			//viewer.refresh(event.getItemsToDisplay(), false);
		} finally {
			viewer.getTree().setRedraw(true);
		}
		
	}

}
