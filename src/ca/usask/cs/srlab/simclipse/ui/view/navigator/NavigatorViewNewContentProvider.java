package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.navigator.CommonViewer;

import ca.usask.cs.srlab.simcad.Constants;
import ca.usask.cs.srlab.simcad.DetectionSettings;
import ca.usask.cs.srlab.simcad.SimcadException;
import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.SimClipseLog;
import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.clone.track.CloneTrackManager;
import ca.usask.cs.srlab.simclipse.ui.DetectionSettingsManager;
import ca.usask.cs.srlab.simclipse.ui.handler.DisableSimClipseHandler;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectView;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewManager;

public class NavigatorViewNewContentProvider implements ITreeContentProvider, INavigatorManagerListener, 
	IResourceChangeListener, IResourceDeltaVisitor{

	static ResourceSetImpl resourceSet = new ResourceSetImpl();
	private CommonViewer viewer;
	private NavigatorManager manager;
	
    private IProject currentProject;
    private DetectionSettings currentDetectionSettings;
	
	public NavigatorViewNewContentProvider(/*AdapterFactory ap*/) {
		//super(NavigatorComposedAdapterFactory.getAdapterFactory());
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				this, IResourceChangeEvent.POST_CHANGE
					| IResourceChangeEvent.POST_BUILD
					| IResourceChangeEvent.PRE_CLOSE
					| IResourceChangeEvent.PRE_DELETE);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		this.viewer = (CommonViewer) viewer;
		
		if(newInput instanceof NavigatorManager){
		if (manager != null)
			manager.removeNavigatorManagerListener(this);
		manager = (NavigatorManager) newInput;
		if (manager != null)
			manager.addNavigatorManagerListener(this);
		}
	}
	
	@Override
	public Object[] getElements(Object object) {
		return getChildren(object);
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Root
				|| parentElement instanceof NavigatorManager) {
			return NavigatorItemProject.toNavigatorItemProjectArray(manager.getSimClipseProjects());
		} else
			return ((INavigatorItem) parentElement).getChildren();
	}
 
	@Override
	public Object getParent(Object element) {
		if (element instanceof Root || element instanceof NavigatorManager) {
			return null;
		}

		if (element instanceof NavigatorItemProject) {
			//return new Root();
			return NavigatorManager.getManager();
		}
		return ((INavigatorItem) element).getParent();
	}
 
	@Override
    public boolean hasChildren(Object element) {
		if(element instanceof NavigatorItemFileFragment) return false;
        return (element instanceof NavigatorManager || element instanceof Root || element instanceof  INavigatorItem);
    }

	@Override
	public void itemsChanged(NavigatorItemChangeEvent event) {
		viewer.getTree().setRedraw(false);
		try {
			viewer.getTree().removeAll();
			viewer.getTree().update();
//			if(event.getItemsRemoved().length > 0)
//				viewer.remove(event.getItemsRemoved());
//			if(event.getItemsAdded().length > 0)
//				viewer.add(viewer.getTree().getTopItem(), event.getItemsAdded());
		} finally {
//			viewer.refresh();
//			viewer.getTree().setRedraw(true);
			doViewerRefresh();
			viewer.getTree().setRedraw(true);
		}		
	}
	
	@Override
	public void dispose() {
		this.viewer = null;
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}
	
	
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		
		final IResource changedResource = delta.getResource();
		
		if(changedResource.getType()  == IResource.PROJECT) {
			
			IProject project = (IProject) changedResource;
			if(!CloneTrackManager.getManager().isCloneTrackerEnable(project)){
				
				if(ProjectViewManager.getManager().existingSimClipseProjectItemFor(project) != null){
					currentProject = null;
					currentDetectionSettings = null;
					return false;
				}

				currentProject = project;
				currentDetectionSettings = DetectionSettingsManager.getManager().getSavedDetectionSettingsForProject(currentProject);
				
				if(currentDetectionSettings == null){
					return false;
				}

			} else {
				currentProject = null;
				currentDetectionSettings = null;
				return false;
			}
		}
		
		if (changedResource.getType() == IResource.FOLDER) {
			if (changedResource.getName().equals("bin") 
					|| changedResource.getFullPath().toString().contains("/bin/") 
					|| changedResource.getName().equals("build")
					|| changedResource.getFullPath().toString().contains("/build/")){
				System.out.println("bin/build folder ignored...");
				return false;
			}
			if (changedResource.getName().contains(
					SimClipseConstants.SIMCLIPSE_DATA_FOLDER)){
				System.out.println(".simclipse folder ignored...");
				return false;
			}
		}		
		
		if (changedResource.getType() == IResource.FILE && 
		changedResource.getFileExtension() !=null && currentDetectionSettings != null
		   &&  Constants.LANG_EXTENSION_MAP.get(currentDetectionSettings.getLanguage())
		   		.contains(changedResource.getFileExtension())){
		       
			if(currentProject != null && currentProject.isOpen() && (delta.getKind() == IResourceDelta.ADDED 
					|| delta.getKind() == IResourceDelta.CHANGED
					|| delta.getKind() == IResourceDelta.ADDED_PHANTOM)){
			
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						e.printStackTrace();
					}

					public void run() throws Exception {
						
						System.out.println("Captured new/changed resource from Saferunner: "+changedResource.getFullPath());
						try{
							//CloneTrackManager.getManager().addToChangedResource(currentProject, ((IFile)changedResource));
						
						
							
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
			}
		    return false;
		}
		return true;
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		
		if (event.getType() == IResourceChangeEvent.PRE_BUILD
				||  event.getType() == IResourceChangeEvent.POST_BUILD) {
			try {
				//event.getDelta().accept(this);
				System.out.println("ignoreing build event...");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			try {
				System.out.println("processing pre close event for...: "+event.getResource());
				
				//ProjectViewManager.getManager().removeProjectViewItems(new IProject[] {currentProject});
				
				if((event.getResource() instanceof IProject) && ProjectViewManager.getManager().existingSimClipseProjectItemFor(event.getResource()) != null){
				
					final IHandlerService handlerService = (IHandlerService) SimClipsePlugin.getViewIfExists(ProjectView.ID)
							.getViewSite().getService(IHandlerService.class);
					
					IEvaluationContext evaluationContext = handlerService
							.createContextSnapshot(true);
					
					Map parameter = new HashMap();
					parameter.put("isCloseProjectEvent", "true");
					
					final ExecutionEvent executionEvent = new ExecutionEvent(null,
							parameter, event.getResource(), evaluationContext);
	
					final DisableSimClipseHandler handler = new DisableSimClipseHandler();
					
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							try {
								handler.execute(executionEvent);
							} catch (ExecutionException e) {
								e.printStackTrace();
								SimClipseLog.logError(e);
								throw new SimcadException(
										"Error in performing view clone action", e);
							}
							
						}
					});
				
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (event.getType() == IResourceChangeEvent.PRE_DELETE) {
			try {
				System.out.println("processing pre delete event for..." + event.getResource());
				
				ProjectViewManager.getManager().removeProjectViewItems(new IProject[] {currentProject});
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
				try {
					System.out.println("processing post change event for..." + event.getResource());
					event.getDelta().accept(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}

	
	public void doViewerRefresh(){
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				TreePath[] treePaths = viewer.getExpandedTreePaths();
				viewer.refresh();
				viewer.setExpandedTreePaths(treePaths);

			}
		});
		
	}
	
}
