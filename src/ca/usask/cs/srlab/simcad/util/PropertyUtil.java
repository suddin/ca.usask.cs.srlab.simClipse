package ca.usask.cs.srlab.simcad.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;

import ca.usask.cs.srlab.simcad.SimCadConstants;

public class PropertyUtil {

	
	public static void addOrUpdateSimcadProperties(IPath simcadDataFolder, Object key, Object value) {
		Map<Object, Object> properties = new HashMap<Object, Object>();
		properties.put(key, value);
		addOrUpdateSimcadProperties( simcadDataFolder, properties);
	}
	
	
	public static void addOrUpdateSimcadProperties(IPath simcadDataFolder, Map<Object, Object> propsMap) {
		if(!simcadDataFolder.toFile().exists())
			simcadDataFolder.toFile().mkdir();
		
		Properties properties = new Properties();
		try {
			if (simcadDataFolder.append(SimCadConstants.SIMCAD_SETTINGS_FILE).toFile().exists())
				properties.load(new FileInputStream(simcadDataFolder.append(SimCadConstants.SIMCAD_SETTINGS_FILE).toFile()));

			properties.putAll(propsMap);

			properties.store(new FileOutputStream(simcadDataFolder.append(SimCadConstants.SIMCAD_SETTINGS_FILE)
							.toFile()), "project specific settings for simcad");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
