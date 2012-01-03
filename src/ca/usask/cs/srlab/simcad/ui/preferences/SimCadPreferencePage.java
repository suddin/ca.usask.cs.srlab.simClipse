package ca.usask.cs.srlab.simcad.ui.preferences;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import ca.usask.cs.srlab.simcad.SimCadActivator;

/**
 * Main Preference Page
 * @author sharif
 *
 */
public class SimCadPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	private DirectoryFieldEditor simcadDirPrefEditor;
	
	public SimCadPreferencePage() {
		super(GRID);
		setPreferenceStore(SimCadActivator.getDefault().getPreferenceStore());
		setDescription("SimCad preference page");
	}
	

	public void createFieldEditors() {
		simcadDirPrefEditor = new DirectoryFieldEditor(PreferenceConstants.SIMCAD_PATH, 
				"&Path To SimCad:", getFieldEditorParent());
		addField(simcadDirPrefEditor);
		
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
	      
	      if (!isSimCadInstallationValid(simcadDirPrefEditor.getStringValue())) {
	         //setErrorMessage("Invalid SimCad installation");
	         setValid(false);
	      }
	      else {
	         setErrorMessage(null);
	         setValid(true);
	      }
	}

	private boolean isSimCadInstallationValid(String simcadDir) {
		boolean result = false;
		
		if (!(new File(simcadDir)).isDirectory()){
			setErrorMessage("Directory does not exists...");
			result = false;
		}else{
			boolean runstate = true;
			//check running simcad
			
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
	         if (event.getSource() == simcadDirPrefEditor)
	            checkState();
	      }
	   }
	
}