package ca.usask.cs.srlab.simclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.SimClipsePluginImages;
import ca.usask.cs.srlab.simclipse.clone.search.SimClipseMessages;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsDialog;

/**
 * Opens the Search Dialog.
 */
public class OpenDetectionSettingsDialogAction extends Action implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow fWindow;
	//private String fPageId;
	private IProject iProject;

	public OpenDetectionSettingsDialogAction() {
		super(SimClipseMessages.OpenDetectionSettingDialogAction_label);
		SimClipsePluginImages.setImageDescriptors(this, SimClipsePluginImages.T_TOOL, SimClipsePluginImages.IMG_TOOL_DETECTION_SETTINGS);
		setToolTipText(SimClipseMessages.OpenDetectionSettingDialogAction_tooltip);
	}

	public OpenDetectionSettingsDialogAction(IWorkbenchWindow window, IProject iProject/*, String pageId*/) {
		this();
		//fPageId= pageId;
		this.iProject = iProject;
		fWindow= window;
	}

	public void init(IWorkbenchWindow window) {
		fWindow= window;
	}

	public void run(IAction action) {
		run();
	}

	public void run() {
		if (getWorkbenchWindow().getActivePage() == null) {
			SimClipsePlugin.beep();
			return;
		}
		DetectionSettingsDialog dialog= new DetectionSettingsDialog(getWorkbenchWindow(), iProject);
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing since the action isn't selection dependent.
	}

	private IWorkbenchWindow getWorkbenchWindow() {
		if (fWindow == null)
			fWindow= SimClipsePlugin.getActiveWorkbenchWindow();
		return fWindow;
	}

	public void dispose() {
		fWindow= null;
	}
}
