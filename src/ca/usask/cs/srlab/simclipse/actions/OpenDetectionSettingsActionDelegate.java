package ca.usask.cs.srlab.simclipse.actions;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import ca.usask.cs.srlab.simcad.SimcadException;
import ca.usask.cs.srlab.simclipse.SimClipseLog;
import ca.usask.cs.srlab.simclipse.ui.handler.SimClipseSettingsHandler;
import ca.usask.cs.srlab.simclipse.ui.view.navigator.INavigatorItem;
import ca.usask.cs.srlab.simclipse.ui.view.navigator.SimEclipseNavigator;
import ca.usask.cs.srlab.simclipse.ui.view.project.IProjectViewItem;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectView;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewItem;

public class OpenDetectionSettingsActionDelegate implements IViewActionDelegate {

	private ViewPart targetPart;
	private Object selectedElement;

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		if (action.isEnabled() && targetPart != null && selectedElement != null
				&& (selectedElement instanceof ProjectViewItem || selectedElement instanceof INavigatorItem)) {
			// CommonViewer viewer = targetPart.getCommonViewer();
			// Object[] expandedElements = viewer.getVisibleExpandedElements();
			// viewer.setInput(selectedElement);
			// viewer.setExpandedElements(expandedElements);
			IProject project = null;
			if(selectedElement instanceof ProjectViewItem)
				project = ((ProjectViewItem) selectedElement)
					.getResource().getProject();
			else if (selectedElement instanceof INavigatorItem)
				project = ((INavigatorItem) selectedElement).getProject();
			
			if(project == null) return;
			
//			Shell shell = targetPart.getViewSite().getShell();
//			MessageDialog.openInformation(shell, "-----TODO-----",
//					"Update Clone Index for project : " + project.getName());
			
			// Setup execution context
			final IHandlerService handlerService = (IHandlerService) targetPart
					.getViewSite().getService(IHandlerService.class);
			IEvaluationContext evaluationContext = handlerService
					.createContextSnapshot(true);
			ExecutionEvent event = new ExecutionEvent(null,
					Collections.EMPTY_MAP, project, evaluationContext);

			SimClipseSettingsHandler handler = new SimClipseSettingsHandler();
			try {
				handler.execute(event);
			} catch (ExecutionException e) {
				e.printStackTrace();
				SimClipseLog.logError(e);
				throw new SimcadException("Error in opening detection settings page", e);
			}
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
		if (!(element instanceof INavigatorItem
				|| element instanceof IProjectViewItem)) {
			action.setEnabled(false);
			return;
		}
		action.setEnabled(true);
		selectedElement = element;
	}

	@Override
	public void init(IViewPart view) {
		if (view instanceof SimEclipseNavigator)
			this.targetPart = (SimEclipseNavigator) view;
		else if (view instanceof ProjectView)
			this.targetPart = (ProjectView) view;
	}

}
