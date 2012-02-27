package ca.usask.cs.srlab.simclipse.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.SimClipseDetectionSettings;
import ca.usask.cs.srlab.simclipse.ui.preferences.PreferenceConstants;
import ca.usask.cs.srlab.simclipse.ui.preferences.SimClipsePreferencePage;

public class SimclipseCommandManager {

	private static SimclipseCommandManager commandManager;
	private static Runtime rt;
	
	private SimclipseCommandManager(){
		
	}
	
	public static SimclipseCommandManager getInstance(){
		if(commandManager == null)
			commandManager = new SimclipseCommandManager();
		rt = Runtime.getRuntime();
		return commandManager;
	}
	
	public boolean detectClone(IResource project){
		
	    String simcadPath = SimClipsePlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.SIMCAD_PATH);
	    if(simcadPath != null && !simcadPath.contains(SimClipsePreferencePage.SIMCAD_PATH_DEFAULT)){
	    	
	    	SimClipseDetectionSettings sds = getDetectionSettingsForProject((IProject)project); 
			
			String[] cmd = {
					"/bin/sh",
					"-c",
					"cd "+simcadPath+
				/*	"\n./simCad "+sds.getGranularity()+" "+sds.getLanguage()+" "+sds.getSimthreshold()+" "+ project.getLocation() +" "+ project.getLocation() +"/"+ SimCadConstants.SIMCAD_DATA_FOLDER+ */
					"\necho $?"
					};
			BufferedReader in = null;
			try{
				//Process proc = rt.exec(cmd,new String[0], new File(simcadPath));
				Process proc = rt.exec("pwd");
			    in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			    String line = null;
			    while ((line = in.readLine()) != null) {
			    	System.out.println(line);
			    }
			    
			    boolean commandSuccess = true;
				//TODO : check success!
			    

			    return commandSuccess;
	    	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
	    		if(in != null)
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
			}
	    	
	    }
		return false;
	}
		
	public SimClipseDetectionSettings getDetectionSettingsForProject(
			IProject project) {
		SimClipseDetectionSettings sds = new SimClipseDetectionSettings();
		
		IPath simclipseDataFolder = project.getLocation().append(SimClipseConstants.SIMCLIPSE_DATA_FOLDER);
		
		Properties properties = new Properties();
		  try {
			  properties.load(new FileInputStream(simclipseDataFolder.append(SimClipseConstants.SIMCLIPSE_SETTINGS_FILE).toFile()));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  
			sds.setGranularity(properties.getProperty("simclipse.settings.local.preprocessing.granularity"));
			sds.setSimthreshold(properties.getProperty("simclipse.settings.local.detection.simthreshold"));
			sds.setLanguage(properties.getProperty("simclipse.settings.local.detection.language"));
		
		return sds;
	}

	public boolean validateSimcadPath(String simcadPath) {
		String[] cmd = {
				"/bin/sh",
				"-c",
				"cd "+simcadPath+"\n./simcad -version"
				};
		
				BufferedReader in = null;
				try{
					Process proc = rt.exec(cmd);
					
				    in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				    String line = in.readLine();
				    
				    return line !=null && line.startsWith("SimCad v");
		    	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
		    		if(in != null)
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
				}
		
		return false;
	}
	
}
