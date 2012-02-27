package ca.usask.cs.srlab.simclipse.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ca.usask.cs.srlab.simclipse.SimClipsePlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = SimClipsePlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.SIMCAD_PATH, "select path");
		store.setDefault(PreferenceConstants.SIMCAD_DETECTION_SETTING_POPUP, false);
	}

}
