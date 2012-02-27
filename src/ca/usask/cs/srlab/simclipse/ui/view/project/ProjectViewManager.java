package ca.usask.cs.srlab.simclipse.ui.view.project;

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
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.SimClipseLog;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.util.FileUtil;
import ca.usask.cs.srlab.simclipse.util.PropertyUtil;

public class ProjectViewManager
{
//   private static final String TAG_FAVORITES = "Favorites";
//   private static final String TAG_FAVORITE = "Favorite";
//   private static final String TAG_TYPEID = "TypeId";
//   private static final String TAG_INFO = "Info";

   private static ProjectViewManager manager;
   private Set<IProjectViewItem> simClipseProjectItems;
   private List<ProjectViewManagerListener> listeners =
         new ArrayList<ProjectViewManagerListener>();

   private ProjectViewManager() {
   }

   // /////////////////////////////////////////////////////////////////////////
   //
   // Singleton
   //
   // /////////////////////////////////////////////////////////////////////////

   public static ProjectViewManager getManager() {
      if (manager == null)
         manager = new ProjectViewManager();
      return manager;
   }

   public IProjectViewItem[] getProjectViewItems() {
      if (simClipseProjectItems == null)
         loadProjectViewItems();
      return simClipseProjectItems.toArray(new IProjectViewItem[simClipseProjectItems.size()]);
   }

   // /////////////////////////////////////////////////////////////////////////
   //
   // Accessing Favorite Items
   //
   // /////////////////////////////////////////////////////////////////////////

   public void addProjectViewItems(Object[] objects) {
      if (objects == null)
         return;
      if (simClipseProjectItems == null)
         loadProjectViewItems();
      Collection<IProjectViewItem> items =
            new HashSet<IProjectViewItem>(objects.length);
      for (int i = 0; i < objects.length; i++) {
         IProjectViewItem item = existingSimClipseProjectItemFor(objects[i]);
         if (item == null) {
            item = newSimClipseProjectItemFor(objects[i]);
            if (simClipseProjectItems.add(item))
               items.add(item);
         }
      }
      if (items.size() > 0) {
         IProjectViewItem[] added =
               items.toArray(new IProjectViewItem[items.size()]);
         fireProjectViewItemsChanged(added, IProjectViewItem.NONE);
      }
   }
   
   public void removeProjectViewItems(Object[] objects) {
      if (objects == null)
         return;
      if (simClipseProjectItems == null)
         loadProjectViewItems();
      Collection<IProjectViewItem> items =
            new HashSet<IProjectViewItem>(objects.length);
      for (int i = 0; i < objects.length; i++) {
         IProjectViewItem item = existingSimClipseProjectItemFor(objects[i]);
         if (item != null && simClipseProjectItems.remove(item))
            items.add(item);
      }
      if (items.size() > 0) {
         IProjectViewItem[] removed =
               items.toArray(new IProjectViewItem[items.size()]);
         fireProjectViewItemsChanged(IProjectViewItem.NONE, removed);
      }
   }

   public IProjectViewItem newSimClipseProjectItemFor(Object obj) {
	   ProjectViewItemType[] types = ProjectViewItemType.getTypes();
      for (int i = 0; i < types.length; i++) {
         IProjectViewItem item = types[i].newProjectViewItem(obj);
         if (item != null)
            return item;
      }
      return null;
   }

   private IProjectViewItem existingSimClipseProjectItemFor(Object obj) {
      if (obj == null)
         return null;
      if (obj instanceof IProjectViewItem)
         return (IProjectViewItem) obj;
      Iterator<IProjectViewItem> iter = simClipseProjectItems.iterator();
      while (iter.hasNext()) {
         IProjectViewItem item = iter.next();
         if (item.isProjectViewItemFor(obj))
            return item;
      }
      return null;
   }

   public IProjectViewItem[] existingFavoritesFor(Iterator<?> iter) {
      List<IProjectViewItem> result = new ArrayList<IProjectViewItem>(10);
      while (iter.hasNext()) {
         IProjectViewItem item = existingSimClipseProjectItemFor(iter.next());
         if (item != null)
            result.add(item);
      }
      return (IProjectViewItem[]) result.toArray(new IProjectViewItem[result.size()]);
   }

   // /////////////////////////////////////////////////////////////////////////
   //
   // Event Handling
   //
   // /////////////////////////////////////////////////////////////////////////

   public void addSimClipseManagerListener(
         ProjectViewManagerListener listener) {
      if (!listeners.contains(listener))
         listeners.add(listener);
   }

   public void removeSimClipseManagerListener(
		   ProjectViewManagerListener listener) {
      listeners.remove(listener);
   }

	private void fireProjectViewItemsChanged(IProjectViewItem[] itemsAdded,
			IProjectViewItem[] itemsRemoved) {

		IWorkbenchPage page = SimClipsePlugin.getActivePage();

		try {
			if (page != null) {
				IViewPart view = SimClipsePlugin.getViewIfExists(ProjectView.ID);
					//page.findView(ProjectView.ID);
				if (view != null)
					page.bringToTop(view);
				else{
					page.showView(ProjectView.ID);
					//new item will be picked up by a call to getProjectViewItems()
					return;
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		ProjectViewManagerEvent event = new ProjectViewManagerEvent(this,
				itemsAdded, itemsRemoved);
		for (Iterator<ProjectViewManagerListener> iter = listeners.iterator(); iter
				.hasNext();)
			iter.next().itemsChanged(event);
	}

   
   // 
   // simcad project specific setup
   //
   
   public void activateSimclipseForProject(IProject project){
		
		IPath simclipseDataFolder = project.getLocation().append(SimClipseConstants.SIMCLIPSE_DATA_FOLDER);
		
		if (simclipseDataFolder.toFile().exists()
				&& simclipseDataFolder
						.append(SimClipseConstants.SIMCLIPSE_SETTINGS_FILE).toFile()
						.exists()) {

			PropertyUtil.addOrUpdateSimclipseProperties(simclipseDataFolder, "simclipse.settings.local.active", "true");
			
		} else {
			Map<Object, Object> propsMap = new HashMap<Object, Object>();
			propsMap.put("simclipse.settings.local.active", "true");
			
			propsMap.put("simclipse.settings.local.preprocessing.sourceTransformation","generous");
			propsMap.put("simclipse.settings.local.detection.language","java");
			propsMap.put("simclipse.settings.local.detection.cloneTypes","123");
			propsMap.put("simclipse.settings.local.detection.cloneGranularity","function");
			propsMap.put("simclipse.settings.local.detection.cloneSetType","group");

			
			propsMap.put("simclipse.status.local.preprocessing.extract.function","");
			propsMap.put("simclipse.status.local.preprocessing.extract.block","");
			propsMap.put("simclipse.status.local.preprocessing.indexing.function","");
			propsMap.put("simclipse.status.local.preprocessing.indexing.block","");
			
			PropertyUtil.addOrUpdateSimClipseProperties(simclipseDataFolder, propsMap);
		}
		
		/*
		
		if(!simclipseDataFolder.toFile().exists()){
			
			try {
			PropertiesConfiguration config = new PropertiesConfiguration(simclipseDataFolder.append(SimCadConstants.SIMCAD_SETTINGS_FILE).toString());
			config.addProperty("!project specific settings for simclipse", "");
			config.addProperty("simclipse.settings.local.preprocessing.minfragmentsize","5");
			config.addProperty("simclipse.settings.local.preprocessing.mincloneclasssize","2");
			config.addProperty("simclipse.settings.local.preprocessing.granularity","function");
			config.addProperty("simclipse.settings.local.preprocessing.rename","none");
			config.addProperty("simclipse.settings.local.detection.simthreshold","0");
			
			config.addProperty("simclipse.status.local.preprocessing.extract.function","");
			config.addProperty("simclipse.status.local.preprocessing.extract.block","");
			config.addProperty("simclipse.status.local.preprocessing.indexing.function","");
			config.addProperty("simclipse.status.local.preprocessing.indexing.block","");
			
			simclipseDataFolder.toFile().mkdir();
			
			
				config.save();
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		*/
		
		try {
			ProjectViewManager.getManager().addProjectViewItems(new IProject[]{ project});
			project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

   public void deactivateSimclipseForProject(IProject project, boolean keepSimclipseData) {
		IPath simclipseDataFolder = project.getLocation().append(SimClipseConstants.SIMCLIPSE_DATA_FOLDER);
		
		if(!keepSimclipseData && simclipseDataFolder.toFile().exists()){
			boolean cleanedUp = FileUtil.deleteDirectory(simclipseDataFolder.toFile());
			if(!cleanedUp){
				//should not be happened, something went wrong!
				SimClipseLog.logError(new Exception("Could not remode simclipse data directory"));
			}
		} else if (simclipseDataFolder.toFile().exists()
				&& simclipseDataFolder
						.append(SimClipseConstants.SIMCLIPSE_SETTINGS_FILE).toFile()
						.exists()) {

			PropertyUtil.addOrUpdateSimclipseProperties(simclipseDataFolder, "simclipse.settings.local.active", "false");
		} else {
			// should not be happened!
		}
		
		try {
			ProjectViewManager.getManager().removeProjectViewItems(new IProject[]{ project});
			project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
   
   // /////////////////////////////////////////////////////////////////////////
   //
   // Persisting Simclipse projects listing
   //
   // /////////////////////////////////////////////////////////////////////////

   private void loadProjectViewItems() {
      
      IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      simClipseProjectItems = new HashSet<IProjectViewItem>(projects.length); 
      for (int i = 0; i < projects.length; i++){
    	  IProject project = projects[i];
    	  IPath simclipseDataFolder = project.getLocation().append(SimClipseConstants.SIMCLIPSE_DATA_FOLDER);
    	  if(simclipseDataFolder.toFile().exists()
    			  && simclipseDataFolder.append(SimClipseConstants.SIMCLIPSE_SETTINGS_FILE).toFile().exists()){
    		  
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
    		  
				if(properties.getProperty("simclipse.settings.local.active").equals("true"))
					simClipseProjectItems.add(new ProjectViewItem( ProjectViewItemType.WORKBENCH_PROJECT, project));
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
