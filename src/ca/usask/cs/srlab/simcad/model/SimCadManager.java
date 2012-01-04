package ca.usask.cs.srlab.simcad.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import ca.usask.cs.srlab.simcad.SimCadActivator;

public class SimCadManager
{
   private static final String TAG_FAVORITES = "Favorites";
   private static final String TAG_FAVORITE = "Favorite";
   private static final String TAG_TYPEID = "TypeId";
   private static final String TAG_INFO = "Info";

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

   // /////////////////////////////////////////////////////////////////////////
   //
   // Persisting favorites
   //
   // /////////////////////////////////////////////////////////////////////////

   private void loadSimCadItems() {
      
      IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      simCadItems = new HashSet<ISimCadItem>(projects.length); 
      //for (int i = 0; i < projects.length; i++)
    	//  simCadItems.add(new SimCadResource( SimCadItemType.WORKBENCH_PROJECT, projects[i]));
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
}
