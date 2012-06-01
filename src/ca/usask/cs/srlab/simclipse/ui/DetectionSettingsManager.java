package ca.usask.cs.srlab.simclipse.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.model.CloneSet;
import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.SimClipseException;
import ca.usask.cs.srlab.simclipse.util.PropertyUtil;

public class DetectionSettingsManager {
	
	private static DetectionSettingsManager manager;
	private static Map<String, DetectionSettings> cache;
	
	private DetectionSettingsManager() {
	}

	public static DetectionSettingsManager getManager() {
		if (manager == null){
			manager = new DetectionSettingsManager();
			cache = new HashMap<String, DetectionSettings>(10);
		}
		return manager;
	}
	
	public DetectionSettings getSavedDetectionSettingsForProject(IProject project){
		
		if(cache.containsKey(project.getName())){
			return cache.get(project.getName());
		}
		
		IPath simclipseDataFolder = project.getLocation().append(
				SimClipseConstants.SIMCLIPSE_DATA_FOLDER);

		Properties properties = new Properties();
		try {
			
			if(!simclipseDataFolder.append(
					SimClipseConstants.SIMCLIPSE_SETTINGS_FILE).toFile().exists()){
				return null;
			}
			
			properties.load(new FileInputStream(simclipseDataFolder.append(
					SimClipseConstants.SIMCLIPSE_SETTINGS_FILE).toFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SimClipseException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SimClipseException(e);
		}

		String transformation = properties
				.getProperty("simclipse.settings.local.preprocessing.sourceTransformation");
//		if(transformation.equals(Constants.SOURCE_TRANSFORMATION_APPROACH_GENEROUS))
//			transformation = Constants.SOURCE_TRANSFORMATION_ACTION_CONSISTENT;
//		else
//			transformation = Constants.SOURCE_TRANSFORMATION_ACTION_BLIND;
		
		String language = properties.getProperty("simclipse.settings.local.detection.language");
		
		String[] cloneTypes = CloneSet.CloneTypeMapper
				.getTypeFromString(properties
						.getProperty("simclipse.settings.local.detection.cloneTypes"));
		String granularity = properties
				.getProperty("simclipse.settings.local.detection.cloneGranularity");
		String cloneGrouping = properties
				.getProperty("simclipse.settings.local.detection.cloneSetType");

		DetectionSettings detectionSettings = new DetectionSettings(language,
				granularity, cloneGrouping, transformation, false, cloneTypes);

		return detectionSettings;
	}
		
	
	public void saveDetectionSettingsForProject(IProject project, DetectionSettings detectionSettings) {
		IPath simclipseDataFolder = project.getLocation().append(SimClipseConstants.SIMCLIPSE_DATA_FOLDER);
		
		Map<Object, Object> propsMap = new HashMap<Object, Object>();
		
		propsMap.put("simclipse.settings.local.preprocessing.sourceTransformation",detectionSettings.getSourceTransformation());
		propsMap.put("simclipse.settings.local.detection.language",detectionSettings.getLanguage());
		propsMap.put("simclipse.settings.local.detection.cloneTypes", CloneSet.CloneTypeMapper.getTypeStringFromArray(detectionSettings.getCloneTypes()));
		propsMap.put("simclipse.settings.local.detection.cloneGranularity",detectionSettings.getCloneGranularity());
		propsMap.put("simclipse.settings.local.detection.cloneSetType",detectionSettings.getCloneSetType());

		PropertyUtil.addOrUpdateSimClipseProperties(project, propsMap);
		
		cache.put(project.getName(), detectionSettings);
	}
}
