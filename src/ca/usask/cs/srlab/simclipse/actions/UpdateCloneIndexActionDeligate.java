package ca.usask.cs.srlab.simclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsManager;
import ca.usask.cs.srlab.simclipse.ui.view.navigator.INavigatorItem;
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
				&& (selectedElement instanceof ProjectViewItem || selectedElement instanceof INavigatorItem )) {

			final IProject project = ((ProjectViewItem) selectedElement)
					.getResource().getProject();

			
			BusyIndicator.showWhile(SimClipsePlugin.getActiveWorkbenchShell().getDisplay(), new Runnable() {
				public void run() {
					DetectionSettings detectionSettings = DetectionSettingsManager
							.getManager().getSavedDetectionSettingsForProject(project);
					CloneIndexManager.getManager().getCloneIndex(
							project,detectionSettings, true);
				}
			});
			
			Shell shell = targetPart.getViewSite().getShell();
			MessageDialog.openInformation(shell, "Project :"+project.getName(),
					"Clone Index updated successfully");
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
