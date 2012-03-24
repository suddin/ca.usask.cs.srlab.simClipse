package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.SimClipseException;
import ca.usask.cs.srlab.simclipse.SimClipseLog;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.SimEclipseProjectNature;
import ca.usask.cs.srlab.simclipse.ui.view.clone.CloneViewManager;
import ca.usask.cs.srlab.simclipse.util.FileUtil;
import ca.usask.cs.srlab.simclipse.util.PropertyUtil;

public class NavigatorManager extends PlatformObject
{
   private static NavigatorManager manager;
   
   private static final IProject[] EMPTY_ARRAY = new IProject[0];
   //private IProject[] projects;
   
   private Set<IProject> simClipseProjectItems;
   
   private List<INavigatorManagerListener> listeners =
         new ArrayList<INavigatorManagerListener>();

   private NavigatorManager() {
   }

   // /////////////////////////////////////////////////////////////////////////
   //
   // Singleton
   //
   // /////////////////////////////////////////////////////////////////////////

   public static NavigatorManager getManager() {
      if (manager == null)
         manager = new NavigatorManager();
      return manager;
   }

   public IProject[] getSimClipseProjects() {
      if (simClipseProjectItems == null)
         loadProjectViewItems();
      return simClipseProjectItems.toArray(new IProject[simClipseProjectItems.size()]);
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
      Collection<IProject> items =
            new HashSet<IProject>(objects.length);
      for (int i = 0; i < objects.length; i++) {
         IProject item = existingSimClipseProjectItemFor(objects[i]);
         if (item == null) {
            item = (IProject) objects[i];
            if (simClipseProjectItems.add(item))
               items.add(item);
         }
      }
      if (items.size() > 0) {
         IProject[] added =
               items.toArray(new IProject[items.size()]);
         fireProjectViewItemsChanged(added, EMPTY_ARRAY);
      }
   }
   
   public void removeProjectViewItems(Object[] objects) {
      if (objects == null)
         return;
      if (simClipseProjectItems == null)
         loadProjectViewItems();
      Collection<IProject> items =
            new HashSet<IProject>(objects.length);
      for (int i = 0; i < objects.length; i++) {
         IProject item = existingSimClipseProjectItemFor(objects[i]);
         if (item != null && simClipseProjectItems.remove(item))
            items.add(item);
      }
      if (items.size() > 0) {
         IProject[] removed =
               items.toArray(new IProject[items.size()]);
         fireProjectViewItemsChanged(EMPTY_ARRAY, removed);
      }
   }

   private IProject existingSimClipseProjectItemFor(Object obj) {
      if (obj == null)
         return null;
      if (obj instanceof IProject)
         return (IProject) obj;
      Iterator<IProject> iter = simClipseProjectItems.iterator();
      while (iter.hasNext()) {
         IProject item = iter.next();
         if(item.equals(obj))
        	 return item;
      }
      return null;
   }

   public IProject[] existingFavoritesFor(Iterator<?> iter) {
      List<IProject> result = new ArrayList<IProject>(10);
      while (iter.hasNext()) {
         IProject item = existingSimClipseProjectItemFor(iter.next());
         if (item != null)
            result.add(item);
      }
      return (IProject[]) result.toArray(new IProject[result.size()]);
   }

   // /////////////////////////////////////////////////////////////////////////
   //
   // Event Handling
   //
   // /////////////////////////////////////////////////////////////////////////

   public void addNavigatorManagerListener(
         INavigatorManagerListener listener) {
      if (!listeners.contains(listener))
         listeners.add(listener);
   }

   public void removeNavigatorManagerListener(
		   INavigatorManagerListener listener) {
      listeners.remove(listener);
   }

	private void fireProjectViewItemsChanged(IProject[] itemsAdded,
			IProject[] itemsRemoved) {

		IWorkbenchPage page = SimClipsePlugin.getActivePage();

		try {
			if (page != null) {
				IViewPart view = SimClipsePlugin.getViewIfExists(SimEclipseNavigator.ID);
					//page.findView(ProjectView.ID);
				if (view != null)
					page.bringToTop(view);
				else{
					page.showView(SimEclipseNavigator.ID);
					//new item will be picked up by a call to getProjectViewItems()
					return;
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		NavigatorItemChangeEvent event = new NavigatorItemChangeEvent(this,
				itemsAdded, itemsRemoved);
		for (Iterator<INavigatorManagerListener> iter = listeners.iterator(); iter
				.hasNext();)
			iter.next().itemsChanged(event);
	}

   
   // 
   // simcad project specific setup
   //
   
   public void activateSimclipseForProject(final IProject project){
		
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
			NavigatorManager.getManager().addProjectViewItems(new IProject[] { project });
			project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());

//			addSimEclipseNature(project);
			
//			new Job("updating simeclipse nature") {
//				protected IStatus run(IProgressMonitor monitor) {
//					try {
//						addSimEclipseNature(project);
//					} catch (Exception e) {
//						new SimClipseException(
//								"Error updating simeclipse nature", e);
//					}
//					return Status.OK_STATUS;
//				}
//			}.schedule();

		} catch (CoreException e) {
			e.printStackTrace();
			new SimClipseException("Error enabling simeclipse", e);
		}
	}

   
   private void addSimEclipseNature(IProject project) {
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = SimEclipseProjectNature.NATURE_ID;

			updateSimEclipseNature(project, newNatures);
		} catch (CoreException e) {
			throw new SimClipseException("Unable to set simeclipse nature", e);
		}
	}
   
	private void removeSimEclipseNature(IProject project) {
		try {
			IProjectDescription description = project.getDescription();
			List<String> oldNatures = Arrays.asList(description.getNatureIds());
			
			if(! oldNatures.remove(SimEclipseProjectNature.NATURE_ID)){
				throw new SimClipseException("Error removing simeclipse nature, nature not present!");
			}
			updateSimEclipseNature(project, oldNatures.toArray(new String[oldNatures.size()]));
		} catch (CoreException e) {
			throw new SimClipseException("Unable to set simeclipse nature", e);
		}
	}
   
   private void updateSimEclipseNature(final IProject project, final String[] newNatures) {
	   
		new Job("updating simeclipse nature") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					IProjectDescription description = project.getDescription();
					IStatus status = project.getWorkspace().validateNatureSet(newNatures);
					// check the status and decide what to do
					if (status.getCode() == IStatus.OK) {
						description.setNatureIds(newNatures);
						project.setDescription(description, null);
					} else {
						throw new SimClipseException("Unable to set simeclipse nature");
					}
				} catch (Exception e) {
					new SimClipseException(
							"Error updating simeclipse nature", e);
				}
				return Status.OK_STATUS;
			}
		}.schedule();
	   
//	   
//		try {
//			IProjectDescription description = project.getDescription();
//			IStatus status = project.getWorkspace().validateNatureSet(newNatures);
//			// check the status and decide what to do
//			if (status.getCode() == IStatus.OK) {
//				description.setNatureIds(newNatures);
//				project.setDescription(description, null);
//			} else {
//				throw new SimClipseException("Unable to set simeclipse nature");
//			}
//		} catch (CoreException e) {
//			throw new SimClipseException("Unable to set simeclipse nature", e);
//		}
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
//			removeSimEclipseNature(project);
			 
			NavigatorManager.getManager().removeProjectViewItems(new IProject[]{ project});
			CloneViewManager.getManager().removeFromDisplayCache(project);
			
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

   protected void loadProjectViewItems() {
      IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      simClipseProjectItems = new HashSet<IProject>(projects.length); 
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
					simClipseProjectItems.add(project);
    	  }
      }
   }
}
