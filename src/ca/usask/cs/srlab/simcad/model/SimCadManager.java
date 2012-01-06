package ca.usask.cs.srlab.simcad.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

import ca.usask.cs.srlab.simcad.SimCadConstants;
import ca.usask.cs.srlab.simcad.SimCadLog;
import ca.usask.cs.srlab.simcad.util.FileUtil;
import ca.usask.cs.srlab.simcad.util.PropertyUtil;

public class SimCadManager
{
//   private static final String TAG_FAVORITES = "Favorites";
//   private static final String TAG_FAVORITE = "Favorite";
//   private static final String TAG_TYPEID = "TypeId";
//   private static final String TAG_INFO = "Info";

   private static SimCadManager manager;
   private Collection<ISimCadItem> simCadItems;
   private List<SimCadManagerListener> listeners =
         new ArrayList<SimCadManagerListener>();

   private SimCadManager() {
   }

   // /////////////////////////////////////////////////////////////////////////
   //
   // Singleton
   //
   // /////////////////////////////////////////////////////////////////////////

   public static SimCadManager getManager() {
      if (manager == null)
         manager = new SimCadManager();
      return manager;
   }

   public ISimCadItem[] getSimCadItems() {
      if (simCadItems == null)
         loadSimCadItems();
      return simCadItems.toArray(new ISimCadItem[simCadItems.size()]);
   }

   // /////////////////////////////////////////////////////////////////////////
   //
   // Accessing Favorite Items
   //
   // /////////////////////////////////////////////////////////////////////////

   public void addSimCadItems(Object[] objects) {
      if (objects == null)
         return;
      if (simCadItems == null)
         loadSimCadItems();
      Collection<ISimCadItem> items =
            new HashSet<ISimCadItem>(objects.length);
      for (int i = 0; i < objects.length; i++) {
         ISimCadItem item = existingSimCadItemFor(objects[i]);
         if (item == null) {
            item = newSimCadItemFor(objects[i]);
            if (simCadItems.add(item))
               items.add(item);
         }
      }
      if (items.size() > 0) {
         ISimCadItem[] added =
               items.toArray(new ISimCadItem[items.size()]);
         fireSimCadItemsChanged(added, ISimCadItem.NONE);
      }
   }
   
   public void removeSimCadItems(Object[] objects) {
      if (objects == null)
         return;
      if (simCadItems == null)
         loadSimCadItems();
      Collection<ISimCadItem> items =
            new HashSet<ISimCadItem>(objects.length);
      for (int i = 0; i < objects.length; i++) {
         ISimCadItem item = existingSimCadItemFor(objects[i]);
         if (item != null && simCadItems.remove(item))
            items.add(item);
      }
      if (items.size() > 0) {
         ISimCadItem[] removed =
               items.toArray(new ISimCadItem[items.size()]);
         fireSimCadItemsChanged(ISimCadItem.NONE, removed);
      }
   }

   public ISimCadItem newSimCadItemFor(Object obj) {
	   SimCadItemType[] types = SimCadItemType.getTypes();
      for (int i = 0; i < types.length; i++) {
         ISimCadItem item = types[i].newSimCadItem(obj);
         if (item != null)
            return item;
      }
      return null;
   }

   private ISimCadItem existingSimCadItemFor(Object obj) {
      if (obj == null)
         return null;
      if (obj instanceof ISimCadItem)
         return (ISimCadItem) obj;
      Iterator<ISimCadItem> iter = simCadItems.iterator();
      while (iter.hasNext()) {
         ISimCadItem item = iter.next();
         if (item.isFavoriteFor(obj))
            return item;
      }
      return null;
   }

   public ISimCadItem[] existingFavoritesFor(Iterator<?> iter) {
      List<ISimCadItem> result = new ArrayList<ISimCadItem>(10);
      while (iter.hasNext()) {
         ISimCadItem item = existingSimCadItemFor(iter.next());
         if (item != null)
            result.add(item);
      }
      return (ISimCadItem[]) result.toArray(new ISimCadItem[result.size()]);
   }

   // /////////////////////////////////////////////////////////////////////////
   //
   // Event Handling
   //
   // /////////////////////////////////////////////////////////////////////////

   public void addSimCadManagerListener(
         SimCadManagerListener listener) {
      if (!listeners.contains(listener))
         listeners.add(listener);
   }

   public void removeSimCadManagerListener(
		   SimCadManagerListener listener) {
      listeners.remove(listener);
   }

   private void fireSimCadItemsChanged(ISimCadItem[] itemsAdded,
         ISimCadItem[] itemsRemoved) {
      SimCadManagerEvent event =
            new SimCadManagerEvent(this, itemsAdded, itemsRemoved);
      for (Iterator<SimCadManagerListener> iter =
            listeners.iterator(); iter.hasNext();)
         iter.next().itemsChanged(event);
   }

   
   // 
   // simcad project specific setup
   //
   
   public void activateSimcadForProject(IProject project){
		
		IPath simcadDataFolder = project.getLocation().append(SimCadConstants.SIMCAD_DATA_FOLDER);
		
		if (simcadDataFolder.toFile().exists()
				&& simcadDataFolder
						.append(SimCadConstants.SIMCAD_SETTINGS_FILE).toFile()
						.exists()) {

			PropertyUtil.addOrUpdateSimcadProperties(simcadDataFolder, "simcad.settings.local.active", "true");
			
		} else {
			Map<Object, Object> propsMap = new HashMap<Object, Object>();
			propsMap.put("simcad.settings.local.active", "true");
			propsMap.put("simcad.settings.local.preprocessing.minfragmentsize","5");
			propsMap.put("simcad.settings.local.preprocessing.mincloneclasssize","2");
			propsMap.put("simcad.settings.local.preprocessing.granularity","function");
			propsMap.put("simcad.settings.local.preprocessing.rename","none");
			propsMap.put("simcad.settings.local.detection.simthreshold","0");
			
			propsMap.put("simcad.status.local.preprocessing.extract.function","");
			propsMap.put("simcad.status.local.preprocessing.extract.block","");
			propsMap.put("simcad.status.local.preprocessing.indexing.function","");
			propsMap.put("simcad.status.local.preprocessing.indexing.block","");
			
			PropertyUtil.addOrUpdateSimcadProperties(simcadDataFolder, propsMap);
		}
		
		/*
		
		if(!simcadDataFolder.toFile().exists()){
			
			try {
			PropertiesConfiguration config = new PropertiesConfiguration(simcadDataFolder.append(SimCadConstants.SIMCAD_SETTINGS_FILE).toString());
			config.addProperty("!project specific settings for simcad", "");
			config.addProperty("simcad.settings.local.preprocessing.minfragmentsize","5");
			config.addProperty("simcad.settings.local.preprocessing.mincloneclasssize","2");
			config.addProperty("simcad.settings.local.preprocessing.granularity","function");
			config.addProperty("simcad.settings.local.preprocessing.rename","none");
			config.addProperty("simcad.settings.local.detection.simthreshold","0");
			
			config.addProperty("simcad.status.local.preprocessing.extract.function","");
			config.addProperty("simcad.status.local.preprocessing.extract.block","");
			config.addProperty("simcad.status.local.preprocessing.indexing.function","");
			config.addProperty("simcad.status.local.preprocessing.indexing.block","");
			
			simcadDataFolder.toFile().mkdir();
			
			
				config.save();
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		*/
		
		try {
			SimCadManager.getManager().addSimCadItems(new IProject[]{ project});
			project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

   public void deactivateSimcadForProject(IProject project, boolean keepSimcadData) {
		IPath simcadDataFolder = project.getLocation().append(SimCadConstants.SIMCAD_DATA_FOLDER);
		
		if(!keepSimcadData && simcadDataFolder.toFile().exists()){
			boolean cleanedUp = FileUtil.deleteDirectory(simcadDataFolder.toFile());
			if(!cleanedUp){
				//should not be happened, something went wrong!
				SimCadLog.logError(new Exception("Could not remode simcad data directory"));
			}
		} else if (simcadDataFolder.toFile().exists()
				&& simcadDataFolder
						.append(SimCadConstants.SIMCAD_SETTINGS_FILE).toFile()
						.exists()) {

			PropertyUtil.addOrUpdateSimcadProperties(simcadDataFolder, "simcad.settings.local.active", "false");
		} else {
			// should not be happened!
		}
		
		try {
			SimCadManager.getManager().removeSimCadItems(new IProject[]{ project});
			project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
   
   // /////////////////////////////////////////////////////////////////////////
   //
   // Persisting Simcad projects listing
   //
   // /////////////////////////////////////////////////////////////////////////

   private void loadSimCadItems() {
      
      IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      simCadItems = new HashSet<ISimCadItem>(projects.length); 
      for (int i = 0; i < projects.length; i++){
    	  IProject project = projects[i];
    	  IPath simcadDataFolder = project.getLocation().append(SimCadConstants.SIMCAD_DATA_FOLDER);
    	  if(simcadDataFolder.toFile().exists()
    			  && simcadDataFolder.append(SimCadConstants.SIMCAD_SETTINGS_FILE).toFile().exists()){
    		  
    		  Properties properties = new Properties();
    		  try {
    			  properties.load(new FileInputStream(simcadDataFolder.append(SimCadConstants.SIMCAD_SETTINGS_FILE).toFile()));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		  
				if(properties.getProperty("simcad.settings.local.active").equals("true"))
					simCadItems.add(new SimCadResource( SimCadItemType.WORKBENCH_PROJECT, project));
    	  }
      }
      /*
      simCadItems = new HashSet<ISimCadItem>(20);
      FileReader reader = null;
      try {
         reader = new FileReader(getFavoritesFile());
         loadSimCadItems(XMLMemento.createReadRoot(reader));
      }
      catch (FileNotFoundException e) {
         // Ignored... no Favorites items exist yet.
      }
      catch (Exception e) {
         // Log the exception and move on.
         e.printStackTrace();
    	  //FavoritesLog.logError(e);
      }
      finally {
         try {
            if (reader != null)
               reader.close();
         }
         catch (IOException e) {
           e.printStackTrace();
        	 //FavoritesLog.logError(e);
         }
      }
      */
      
   }

   /*
   private void loadSimCadItems(XMLMemento memento) {
      IMemento[] children = memento.getChildren(TAG_FAVORITE);
      for (int i = 0; i < children.length; i++) {
         ISimCadItem item =
               newSimCadItemFor(children[i].getString(TAG_TYPEID),
                     children[i].getString(TAG_INFO));
         if (item != null)
            simCadItems.add(item);
      }
   }

   public ISimCadItem newSimCadItemFor(String typeId, String info) {
	  SimCadItemType[] types = SimCadItemType.getTypes();
      for (int i = 0; i < types.length; i++)
         if (types[i].getId().equals(typeId))
            return types[i].loadSimCadItem(info);
      return null;
   }

   public void saveSimCadItems() {
      if (simCadItems == null)
         return;
      XMLMemento memento = XMLMemento.createWriteRoot(TAG_FAVORITES);
      saveFavorites(memento);
      FileWriter writer = null;
      try {
         writer = new FileWriter(getFavoritesFile());
         memento.save(writer);
      }
      catch (IOException e) {
         e.printStackTrace();
    	  //FavoritesLog.logError(e);
      }
      finally {
         try {
            if (writer != null)
               writer.close();
         }
         catch (IOException e) {
            e.printStackTrace();
        	 //FavoritesLog.logError(e);
         }
      }
   }

   private void saveFavorites(XMLMemento memento) {
      Iterator<ISimCadItem> iter = simCadItems.iterator();
      while (iter.hasNext()) {
         ISimCadItem item = iter.next();
         IMemento child = memento.createChild(TAG_FAVORITE);
         child.putString(TAG_TYPEID, item.getType().getId());
         child.putString(TAG_INFO, item.getInfo());
      }
   }

   private File getFavoritesFile() {
      return SimCadActivator.getDefault()
            .getStateLocation()
            .append("favorites.xml")
            .toFile();
   }
   */
}
