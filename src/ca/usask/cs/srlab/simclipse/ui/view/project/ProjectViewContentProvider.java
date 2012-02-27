package ca.usask.cs.srlab.simclipse.ui.view.project;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;


class ProjectViewContentProvider implements IStructuredContentProvider, ProjectViewManagerListener 
{
	private TableViewer viewer;
	private ProjectViewManager manager;

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (manager != null)
			manager.removeSimClipseManagerListener(this);
		manager = (ProjectViewManager) newInput;
		if (manager != null)
			manager.addSimClipseManagerListener(this);
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		return manager.getProjectViewItems();
	}

	public void itemsChanged(ProjectViewManagerEvent event) {
		viewer.getTable().setRedraw(false);
		try {
			viewer.remove(event.getItemsRemoved());
			viewer.add(event.getItemsAdded());
		} finally {
			viewer.getTable().setRedraw(true);
		}
	}
}