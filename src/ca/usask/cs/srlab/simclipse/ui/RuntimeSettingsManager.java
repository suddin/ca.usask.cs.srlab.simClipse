package ca.usask.cs.srlab.simclipse.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.SimClipseException;
import ca.usask.cs.srlab.simclipse.util.PropertyUtil;

public class RuntimeSettingsManager {
	
	private static RuntimeSettingsManager manager;
	private static Map<String, RuntimeSettings> cache;
	
	private RuntimeSettingsManager() {
	}

	public static RuntimeSettingsManager getManager() {
		if (manager == null){
			manager = new RuntimeSettingsManager();
			cache = new HashMap<String, RuntimeSettings>(5);
		}
		return manager;
	}
	
	public RuntimeSettings getSavedRuntimeSettingsForProject(IProject project){
		
		if(cache.containsKey(project.getFullPath().toOSString())){
			return cache.get(project.getFullPath().toOSString());
		}
		
		IPath simclipseDataFolder = project.getLocation().append(
				SimClipseConstants.SIMCLIPSE_DATA_FOLDER);

		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(simclipseDataFolder.append(
					SimClipseConstants.SIMCLIPSE_SETTINGS_FILE).toFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SimClipseException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SimClipseException(e);
		}

		boolean enableDetectionOnResourceChange = Boolean.valueOf(properties
				.getProperty("simclipse.settings.local.runtime.detectionOnResourceChange"));

		boolean enableAutoCloneIndexUpdate = Boolean.valueOf(properties
				.getProperty("simclipse.settings.local.runtime.autoCloneIndexUpdate"));
		
		return new RuntimeSettings(enableDetectionOnResourceChange, enableAutoCloneIndexUpdate);
	}
		
	
	public void saveRuntimeSettingsForProject(IProject project, RuntimeSettings runtimeSettings) {
		Map<Object, Object> propsMap = new HashMap<Object, Object>();
		propsMap.put("simclipse.settings.local.runtime.detectionOnResourceChange",runtimeSettings.isEnableDetectionOnResourceChange());
		propsMap.put("simclipse.settings.local.runtime.autoCloneIndexUpdate",runtimeSettings.isEnableAutoCloneIndexUpdate());
		PropertyUtil.addOrUpdateSimClipseProperties(project, propsMap);
		cache.put(project.getFullPath().toOSString(), runtimeSettings);
	}
}
