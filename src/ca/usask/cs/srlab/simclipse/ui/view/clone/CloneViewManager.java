package ca.usask.cs.srlab.simclipse.ui.view.clone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simcad.model.CloneSet;
import ca.usask.cs.srlab.simcad.model.ICloneFragment;
import ca.usask.cs.srlab.simclipse.SimClipseException;
import ca.usask.cs.srlab.simclipse.SimClipseLog;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsManager;
import ca.usask.cs.srlab.simclipse.ui.preferences.PreferenceConstants;

public class CloneViewManager
{
   private static CloneViewManager manager;
   
   private static final int DETECTION_CACHE_MAX_ENTRY = 10;
   private static Map<String, Collection<CloneProjectDisplayModel>> displayCache;// = new HashMap<String, Collection<CloneProjectDisplayModel>>(5);
   
   private Collection<CloneProjectDisplayModel> cloneViewItems = new ArrayList<CloneProjectDisplayModel>();
   private List<CloneViewManagerListener> listeners =  new ArrayList<CloneViewManagerListener>();

	private CloneViewManager() {
	}

	public static CloneViewManager getManager() {
		if (manager == null){
			manager = new CloneViewManager();
			displayCache = new HashMap<String, Collection<CloneProjectDisplayModel>>(5);
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
	
	public void displayClone(IResource selection) {
		if(selection instanceof IProject && displayCache.containsKey(selection.getFullPath().toOSString())){
			displayClone((IProject)selection, displayCache.get(selection.getFullPath().toOSString()));
		}else{
			//TODO: Open and simclipse detection settings window
			DetectionSettings detectionSettings;
			Boolean popDetectionSettingPage = SimClipsePlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.SIMCAD_DETECTION_SETTING_POPUP);
			
			if(popDetectionSettingPage){
				throw new SimClipseException("Feature not implemented");
			}else{
				detectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(selection.getProject());
			}
			
			CloneDetectionManager.getManager().detectClone(selection, selection.getProject(), detectionSettings);
		}
	}
	
	public void resetCloneView() {
		this.cloneViewItems.clear();
		
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
	
	
	public void displayClone(IResource selection, Collection<CloneProjectDisplayModel> cloneViewItems) {
		
		if(cloneViewItems == null || cloneViewItems.isEmpty()) return;
		
		this.cloneViewItems.clear();
		this.cloneViewItems.addAll(cloneViewItems);
		if(selection instanceof IProject)
			addToDisplayCache((IProject) selection, cloneViewItems);

//		// display clone
//	      SimClipsePlugin.getDefault().printToConsole("Inside clone view manager...");
//	      for(CloneProjectDisplayModel model : cloneViewItems){
//	    	  SimClipsePlugin.getDefault().printToConsole(model.getDisplayLabel());
//	      }
//		
		final IWorkbenchPage page = SimClipsePlugin.getActivePage();
		if (page == null) return;

		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
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
			}
		});
		
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

	public void removeFromDisplayCache(IProject project) {
		displayCache.remove(project.getFullPath().toOSString());
	}
	
	public void addToDisplayCache(IProject project, Collection<CloneProjectDisplayModel> cloneViewItems) {
		if(displayCache.size() < DETECTION_CACHE_MAX_ENTRY){
			displayCache.put(project.getFullPath().toOSString(), new ArrayList<CloneProjectDisplayModel>(cloneViewItems));
		}else{
			SimClipsePlugin.getDefault().printToConsole("Warning! Detection Cache full. Deactivate SimClipse for other project to free cache space");
		}
	}
	
	public void displayClone(IResource selectedResource, List<CloneFragment> candidateFragments, List<CloneSet> detectionResult, DetectionSettings detectionSettings) {
		try {
			
			Collection<CloneProjectDisplayModel> cloneDisplayModel = buildCloneDisplayModel(selectedResource, candidateFragments, detectionResult, detectionSettings);
			displayClone(selectedResource, cloneDisplayModel);

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public void displayClone(IResource selectedResource, List<CloneSet> detectionResult, DetectionSettings detectionSettings) {
		try {
			
			Collection<CloneProjectDisplayModel> cloneDisplayModel = buildCloneDisplayModel(selectedResource, detectionResult, detectionSettings);
			displayClone(selectedResource, cloneDisplayModel);

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public Collection<CloneProjectDisplayModel> buildCloneDisplayModel(IResource selectedResource, List<CloneFragment> candidateFragments, List<CloneSet> detectionResult, DetectionSettings detectionSettings) throws CloneNotSupportedException {
		
		List<CloneProjectDisplayModel> cloneProjectModels = new ArrayList<CloneProjectDisplayModel>();
		try{
			//this is root of a tree in the forest
			CloneProjectDisplayModel cpm = new CloneProjectDisplayModel(selectedResource.getProject(), detectionSettings.getCloneSetType());
			cloneProjectModels.add(cpm);
			
			for(CloneSet cloneSet : detectionResult){
				CloneSetDisplayModel cloneSetModel = new CloneSetDisplayModel(cloneSet , cpm);
				
				for(ICloneFragment iCloneFragment : cloneSet.getCloneFragments()){
					CloneFragment cf = (CloneFragment) iCloneFragment;
					CloneFragmentDisplayModel cloneFragmentModel = new CloneFragmentDisplayModel(cf.clone() , cloneSetModel);
					
					if(candidateFragments.contains(cf)){
						cloneFragmentModel.setLabelTextColorCode(SWT.COLOR_RED);
					}
					
					cloneSetModel.addCloneFragmentModel(cloneFragmentModel);
				}
				
				cpm.addCloneSetModel(cloneSetModel);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			SimClipseLog.logError("Error occured in building clone display models", e);
			SimClipsePlugin.getDefault().printToConsole("Error occured in building clone display models...", e);
		}
		
		return cloneProjectModels;
	}
	
	public Collection<CloneProjectDisplayModel> buildCloneDisplayModel(IResource selectedResource, List<CloneSet> detectionResult, DetectionSettings detectionSettings) throws CloneNotSupportedException {
		List<CloneProjectDisplayModel> cloneProjectModels = new ArrayList<CloneProjectDisplayModel>();
		
		try{
			//this is root of a tree in the forest
			CloneProjectDisplayModel cpm = new CloneProjectDisplayModel(selectedResource.getProject(), detectionSettings.getCloneSetType());
			cloneProjectModels.add(cpm);
			
			for(CloneSet cloneSet : detectionResult){
				CloneSetDisplayModel cloneSetModel = new CloneSetDisplayModel(cloneSet , cpm);
				for(ICloneFragment iCloneFragment : cloneSet.getCloneFragments()){
					CloneFragment cf = (CloneFragment) iCloneFragment;
					CloneFragmentDisplayModel cloneFragmentModel = new CloneFragmentDisplayModel(cf.clone() , cloneSetModel);
					cloneSetModel.addCloneFragmentModel(cloneFragmentModel);
				}
				cpm.addCloneSetModel(cloneSetModel);
			}
			
			}catch (Exception e) {
				e.printStackTrace();
				SimClipseLog.logError("Error occured in building clone display models", e);
				SimClipsePlugin.getDefault().printToConsole("Error occured in building clone display models...", e);
			}
		
		return cloneProjectModels;
	}

}
