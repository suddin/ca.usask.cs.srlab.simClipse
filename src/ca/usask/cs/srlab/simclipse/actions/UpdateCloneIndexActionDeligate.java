package ca.usask.cs.srlab.simclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectView;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewItem;

public class UpdateCloneIndexActionDeligate implements IViewActionDelegate {

	private ProjectView targetPart;
	private Object selectedElement;

	public UpdateCloneIndexActionDeligate() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		if (action.isEnabled() && targetPart != null && selectedElement != null
				&& selectedElement instanceof ProjectViewItem) {
			// CommonViewer viewer = targetPart.getCommonViewer();
			// Object[] expandedElements = viewer.getVisibleExpandedElements();
			// viewer.setInput(selectedElement);
			// viewer.setExpandedElements(expandedElements);
			IProject project = ((ProjectViewItem) selectedElement)
					.getResource().getProject();

			Shell shell = targetPart.getViewSite().getShell();
			MessageDialog.openInformation(shell, "-----TODO-----",
					"Update Clone Index for project : " + project.getName());
			
			/*/test
			try {
				List<IVehicle> vehicleList = VehicleFactory.loadVehicle();
				for (IVehicle vehicle : vehicleList) {
					vehicle.printVehicleName();
				}
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*/
			
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			action.setEnabled(false);
			return;
		}
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() != 1) {
			action.setEnabled(false);
			return;
		}
		Object element = ssel.getFirstElement();
		if (!(element instanceof ProjectViewItem)) {
			action.setEnabled(false);
			return;
		}
		action.setEnabled(true);
		selectedElement = element;
	}

	@Override
	public void init(IViewPart view) {
		if (view instanceof ProjectView)
			this.targetPart = (ProjectView) view;
	}

}
