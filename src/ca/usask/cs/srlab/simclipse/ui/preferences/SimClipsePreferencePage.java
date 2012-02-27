package ca.usask.cs.srlab.simclipse.ui.preferences;

import java.io.File;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.command.SimclipseCommandManager;

/**
 * Main Preference Page
 * @author sharif
 *
 */
public class SimClipsePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public static final String LIMIT_HISTORY= "org.eclipse.search.limitHistory"; //$NON-NLS-1$

	public static final String DEFAULT_PERSPECTIVE= "org.eclipse.search.defaultPerspective"; //$NON-NLS-1$
	
	private static final String NO_DEFAULT_PERSPECTIVE= "org.eclipse.search.defaultPerspective.none"; //$NON-NLS-1$
	
	private DirectoryFieldEditor simclipseDirPrefEditor;
	
	private BooleanFieldEditor askForDetectionSettingsOnEachQuery;
	
	public static final String SIMCAD_PATH_DEFAULT = "Path to Simcad";
	public static final String SIMCAD_DETECTION_SETTING_POPUP_MODE = "Ask for detection settings on each detection query";
	
	public SimClipsePreferencePage() {
		super(GRID);
		setPreferenceStore(SimClipsePlugin.getDefault().getPreferenceStore());
		setDescription("SimClipse preference page");
	}
	

	public void createFieldEditors() {
		simclipseDirPrefEditor = new DirectoryFieldEditor(PreferenceConstants.SIMCAD_PATH, 
				"&"+SIMCAD_PATH_DEFAULT+":", getFieldEditorParent());
		addField(simclipseDirPrefEditor);
		
		askForDetectionSettingsOnEachQuery = new BooleanFieldEditor(PreferenceConstants.SIMCAD_DETECTION_SETTING_POPUP, 
				"&"+SIMCAD_DETECTION_SETTING_POPUP_MODE+":", getFieldEditorParent());
		addField(askForDetectionSettingsOnEachQuery);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	
	protected void checkState() {
	      super.checkState();
	      if (!isValid())
	         return;
//	      String simcadPath = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.SIMCAD_PATH);
//	      Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
//	      MessageDialog.openInformation(shell,  simcadPath, simcadPath);
	      
	      if (!isSimCadInstallationValid(simclipseDirPrefEditor.getStringValue())) {
	         //setErrorMessage("Invalid SimCad installation");
	         setValid(false);
	      }
	      else {
	         setErrorMessage(null);
	         setValid(true);
	      }
	}

	private boolean isSimCadInstallationValid(String simcadPath) {
		boolean result = false;
		
		if (!(new File(simcadPath)).isDirectory()){
			setErrorMessage("Directory does not exists...");
			result = false;
		}else{
			//check running simcad
			boolean runstate = SimclipseCommandManager.getInstance().validateSimcadPath(simcadPath);
			if(!runstate){
				setErrorMessage("Invalid simcad installation...");
				result = false;
			}
			else
				result = true;
		}
		return result;
	}

	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if (event.getProperty().equals(FieldEditor.VALUE)) {
			if (event.getSource() == simclipseDirPrefEditor)
				checkState();
		}
	}

	public static int getHistoryLimit() {
		IPreferenceStore store= SimClipsePlugin.getDefault().getPreferenceStore();
		int limit= store.getInt(LIMIT_HISTORY);
		if (limit < 1) {
			limit= 1;
		} else if (limit >= 100) {
			limit= 99;
		}
		return limit;
	}
	
	// Accessors to preference values
	public static String getDefaultPerspectiveId() {
		handleDeletedPerspectives();
		IPreferenceStore store= SimClipsePlugin.getDefault().getPreferenceStore();
		String id= store.getString(DEFAULT_PERSPECTIVE);
		if (id == null || id.length() == 0 || id.equals(NO_DEFAULT_PERSPECTIVE))
			return null;
		else if (PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(id) == null) {
			store.putValue(DEFAULT_PERSPECTIVE, id);
			return null;
		}
		return id;
	}
	

	private static void handleDeletedPerspectives() {
		IPreferenceStore store= SimClipsePlugin.getDefault().getPreferenceStore();
		String id= store.getString(DEFAULT_PERSPECTIVE);
		if (PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(id) == null) {
			store.putValue(DEFAULT_PERSPECTIVE, NO_DEFAULT_PERSPECTIVE);
		}
	}

	
}