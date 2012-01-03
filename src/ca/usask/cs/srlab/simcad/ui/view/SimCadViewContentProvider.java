package ca.usask.cs.srlab.simcad.ui.view;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import ca.usask.cs.srlab.simcad.model.SimCadManager;
import ca.usask.cs.srlab.simcad.model.SimCadManagerEvent;
import ca.usask.cs.srlab.simcad.model.SimCadManagerListener;

class SimCadViewContentProvider implements IStructuredContentProvider, SimCadManagerListener 
{
	private TableViewer viewer;
	private SimCadManager manager;

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (manager != null)
			manager.removeSimCadManagerListener(this);
		manager = (SimCadManager) newInput;
		if (manager != null)
			manager.addSimCadManagerListener(this);
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		return manager.getSimCadItems();
	}

	public void itemsChanged(SimCadManagerEvent event) {
		viewer.getTable().setRedraw(false);
		try {
			viewer.remove(event.getItemsRemoved());
			viewer.add(event.getItemsAdded());
		} finally {
			viewer.getTable().setRedraw(true);
		}
	}
}