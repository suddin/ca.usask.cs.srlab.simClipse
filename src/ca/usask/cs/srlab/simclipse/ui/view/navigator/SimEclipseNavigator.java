package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.usask.cs.srlab.simclipse.ui.view.project.IProjectViewItem;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewManager;

public class SimEclipseNavigator extends CommonNavigator implements ISelectionListener{
	
	public static final String ID =
        "ca.usask.cs.srlab.simclipse.ui.view.navigatorView";
	
	@Override
	protected /*IAdaptable*/Object getInitialInput() {
		//return new Root();
		return NavigatorManager.getManager();
	}
	
	@Override
	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);
		
		//createContextMenu();
		
		
		
		//getSite().getPage().addSelectionListener(this);
		//getSite().getPage().addPostSelectionListener(pageSelectionListener);
	      // prime the selection
	    //selectionChanged(null, getSite().getPage().getSelection());
	}
	
	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu", "ca.usask.cs.simclipse.navigator.contextMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				SimEclipseNavigator.this.fillContextMenu(m);
			}
		});
		Menu menu = menuMgr.createContextMenu(getCommonViewer().getControl());
		getCommonViewer().getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, getCommonViewer());
		
		Action refresh = new Action() {
			public void run() {
				NavigatorManager.getManager().loadProjectViewItems();
				getCommonViewer().refresh();
			}
		};
		refresh.setText("Refresh");
		menuMgr.add(refresh);
		
	}

	private void fillContextMenu(IMenuManager menuMgr) {
		MenuItem[] items = ((MenuManager)menuMgr).getMenu().getItems();
		
		for(MenuItem mi : items){
			if(mi.getText().contains("Detect Clone"))
				continue;
			else{
				mi.dispose();
			}
		}
		
		menuMgr.removeAll();
		//menuMgr.add(new Separator("edit"));
		// menuMgr.add(removeContributionItem);
		//menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part == this)
			return;
		if (!(selection instanceof IStructuredSelection))
			return;
		IStructuredSelection sel = (IStructuredSelection) selection;
		IProjectViewItem[] items = ProjectViewManager.getManager()
				.existingFavoritesFor(sel.iterator());
//		if (items.length > 0)
//		getCommonViewer().setSelection(new StructuredSelection(items), true);
		//getCommonViewer().getSelection();
		//getCommonViewer().setSelection(sel, true);
		//getCommonViewer().getInput()setInput(sel);
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		getSite().getPage().removeSelectionListener(this);
		//getSite().getPage().removePostSelectionListener(this);
	}
}
