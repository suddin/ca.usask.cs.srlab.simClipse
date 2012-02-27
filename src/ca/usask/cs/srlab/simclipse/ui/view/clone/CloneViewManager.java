package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simclipse.SimClipseException;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsManager;
import ca.usask.cs.srlab.simclipse.ui.preferences.PreferenceConstants;

public class CloneViewManager
{
   private static CloneViewManager manager;
   
   private static final int DETECTION_CACHE_MAX_ENTRY = 5;
   private static Map<String, Collection<CloneProjectDisplayModel>> detectionCache;// = new HashMap<String, Collection<CloneProjectDisplayModel>>(5);
   
   private Collection<CloneProjectDisplayModel> cloneViewItems = new ArrayList<CloneProjectDisplayModel>();
   private List<CloneViewManagerListener> listeners =  new ArrayList<CloneViewManagerListener>();

	private CloneViewManager() {
	}

	public static CloneViewManager getManager() {
		if (manager == null){
			manager = new CloneViewManager();
			detectionCache = new HashMap<String, Collection<CloneProjectDisplayModel>>(5);
		}
		return manager;
	}

	public void addCloneViewManagerListener(CloneViewManagerListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void removeCloneViewManagerListener(CloneViewManagerListener listener) {
		listeners.remove(listener);
	}
	
	public void displayClone(IProject project) {
		if(detectionCache.containsKey(project.getFullPath().toOSString())){
			displayClone(project, detectionCache.get(project.getFullPath().toOSString()));
		}else{
			
			//TODO: Open and simclipse detection settings window
			DetectionSettings detectionSettings;
			Boolean popDetectionSettingPage = SimClipsePlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.SIMCAD_DETECTION_SETTING_POPUP);
			
			if(popDetectionSettingPage){
				throw new SimClipseException("Feature not implemented");
			}else{
				detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(project);
			}
			
			CloneDetectionManager.getManager().detectClone(project, detectionSettings);
		}
	}
	
	public void displayClone(IProject project, Collection<CloneProjectDisplayModel> cloneViewItems) {
		this.cloneViewItems.clear();
		this.cloneViewItems.addAll(cloneViewItems);
		
		if(detectionCache.size() < DETECTION_CACHE_MAX_ENTRY){
			detectionCache.put(project.getFullPath().toOSString(), cloneViewItems);
		}else{
			System.out.println("Warning! Detection Cache full. Deactivate SimClipse for other project to free cache space");
		}

		IWorkbenchPage page = SimClipsePlugin.getActivePage();
		if (page == null)
			return;

		try {
			if (page != null) {
				IViewPart view = SimClipsePlugin.getViewIfExists(CloneView.ID);
					//page.findView(ProjectView.ID);
				if (view != null)
					page.bringToTop(view);
				else{
					page.showView(CloneView.ID);
					//new item will be picked up by a call to getProjectViewItems()
					return;
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		fireCloneviewItemChagned();
	}

	public Object[] getCloneViewItems() {
		if(cloneViewItems == null || cloneViewItems.size() == 0){
			return new CloneProjectDisplayModel[0];
		}
		return cloneViewItems.toArray();
	}
	
	
	private void fireCloneviewItemChagned() {
		CloneViewEvent event = new CloneViewEvent(this, this.cloneViewItems);
		for (Iterator<CloneViewManagerListener> iter = listeners.iterator(); iter
				.hasNext();)
			iter.next().executeEvent(event);
	}

}
