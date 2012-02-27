package ca.usask.cs.srlab.simclipse.clone.search;

import org.eclipse.osgi.util.NLS;

public final class SimClipseMessages extends NLS {

	private static final String BUNDLE_NAME= "ca.usask.cs.srlab.simclipse.clone.search.SimclipseMessages";//$NON-NLS-1$
	
	public static final String DetectionSettingsDialogClosingDialog_title = "TODO";
	public static final String DetectionSettingsDialogClosingDialog_message = "TODO";

	public static final String DetectionSettingsDialog_save = "Save";

	public static String DetectionSettingsDialog_title;
    
	public static String OpenDetectionSettingDialogAction_label;
	public static String OpenDetectionSettingDialogAction_tooltip;

	private SimClipseMessages() {
	}

    static {
        NLS.initializeMessages(BUNDLE_NAME, SimClipseMessages.class);
    }

}
