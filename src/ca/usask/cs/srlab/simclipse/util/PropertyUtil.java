package ca.usask.cs.srlab.simclipse.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;

import ca.usask.cs.srlab.simclipse.SimClipseConstants;

public class PropertyUtil {

	
	public static void addOrUpdateSimclipseProperties(IPath simclipseDataFolder, Object key, Object value) {
		Map<Object, Object> properties = new HashMap<Object, Object>();
		properties.put(key, value);
		addOrUpdateSimClipseProperties( simclipseDataFolder, properties);
	}
	
	
	public static void addOrUpdateSimClipseProperties(IPath simclipseDataFolder, Map<Object, Object> propsMap) {
		if(!simclipseDataFolder.toFile().exists())
			simclipseDataFolder.toFile().mkdir();
		
		Properties properties = new Properties();
		try {
			if (simclipseDataFolder.append(SimClipseConstants.SIMCLIPSE_SETTINGS_FILE).toFile().exists())
				properties.load(new FileInputStream(simclipseDataFolder.append(SimClipseConstants.SIMCLIPSE_SETTINGS_FILE).toFile()));

			properties.putAll(propsMap);

			properties.store(new FileOutputStream(simclipseDataFolder.append(SimClipseConstants.SIMCLIPSE_SETTINGS_FILE)
							.toFile()), "project specific settings for simclipse");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
