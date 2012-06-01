package ca.usask.cs.srlab.simclipse.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simclipse.clone.search.Messages;
import ca.usask.cs.srlab.simclipse.clone.search.SimClipseMessages;

public class DetectionSettingsDialog extends ExtendedDialogWindow{

	private static final int SAVE_ID= IDialogConstants.CLIENT_ID + 1;
	private DetectionSettingsPage detectionSettingsPagePage;
	private final IProject iProject;

	public DetectionSettingsDialog(IWorkbenchWindow window, IProject iProject) {
		super(window.getShell());
		this.iProject= iProject;
		DetectionSettings savedDetectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(iProject);
		RuntimeSettings savedRuntimeSettings = RuntimeSettingsManager.getManager().getSavedRuntimeSettingsForProject(iProject);
		detectionSettingsPagePage = new DetectionSettingsPage(savedDetectionSettings, savedRuntimeSettings);
	}

	@Override
	protected Point getInitialSize() {
		Point requiredSize= getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		Point lastSize= super.getInitialSize();
		if (requiredSize.x > lastSize.x || requiredSize.y > lastSize.y) {
			return requiredSize;
		}
		return lastSize;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setSize(400, 500);
		shell.setLocation(shell.getParent().getLocation().x +(shell.getParent().getSize().x - shell.getSize().x)/2 ,shell.getParent().getLocation().y + (shell.getParent().getSize().y - shell.getSize().y)/2);
		shell.setText(Messages.format(SimClipseMessages.DetectionSettingsDialog_title, iProject.getName()));
	}

	@Override
	public void create() {
		super.create();
		if (detectionSettingsPagePage != null) {
			detectionSettingsPagePage.setVisible(true);
		}
	}

	@Override
	protected Control createPageArea(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Control pageControl= createPageControl(composite);
		
		pageControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return composite;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite= new Composite(parent, SWT.BORDER_SOLID);
		GridLayout layout= new GridLayout();
		layout.numColumns= 0;   // create
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// create help control if needed
        if (isHelpAvailable()) {
        	createHelpControl(composite);
        }

		Label filler= new Label(composite, SWT.NONE);
		filler.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		layout.numColumns++;

		Button searchButton= createActionButton(composite, SAVE_ID, SimClipseMessages.DetectionSettingsDialog_save, true);
		searchButton.setEnabled(true);
		super.createButtonsForButtonBar(composite);  // cancel button

		return composite;
	}

	@Override
	protected boolean performAction(int actionID) {
		switch (actionID) {
			case CANCEL:
				return true;
			case SAVE_ID:
				if (detectionSettingsPagePage != null) {
					if(detectionSettingsPagePage.performAction(iProject)){
						MessageDialog.openInformation(getShell(), "Message", "Detection Settings saved successfully!");
						return true;
					}
					return false;
				}
				return true;
			default:
				return false;
		}
	}

	public void notifySettingsChanged() {
		//TODO: need to implement
	}

	private Control createPageControl(Composite parent) {

		// Page wrapper
		final Composite pageWrapper= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		pageWrapper.setLayout(layout);

		applyDialogFont(pageWrapper);

		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				SafeRunner.run(new ISafeRunnable() {
					public void run() throws Exception {
						// create page and control
						DetectionSettingsPage page = detectionSettingsPagePage;
						if (page != null) {
							page.createControl(pageWrapper);
						}
					}
					public void handleException(Throwable ex) {
						if (ex instanceof CoreException) {
						} else {
						}
					}
				});
			}
		});

		return pageWrapper;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 */
	public boolean close() {
		if(detectionSettingsPagePage!=null)
			detectionSettingsPagePage.dispose();
		return super.close();
	}

}
