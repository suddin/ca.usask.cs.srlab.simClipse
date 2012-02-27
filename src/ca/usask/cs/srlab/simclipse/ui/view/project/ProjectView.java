package ca.usask.cs.srlab.simclipse.ui.view.project;

import java.util.Comparator;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;


public class ProjectView extends ViewPart {

	private TableViewer viewer;
	private TableColumn typeColumn; 
	private TableColumn nameColumn; 
	private TableColumn locationColumn;
	
	private ProjectViewSorter sorter;
	//private IMemento memento;
	
	private ISelectionListener pageSelectionListener;

	public static final String ID =
        "ca.usask.cs.srlab.simclipse.ui.views.ProjectsView";
	
	public ProjectView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION); 
		final Table table = viewer.getTable();
		
	    TableColumnLayout layout = new TableColumnLayout();
	    parent.setLayout(layout);

	    typeColumn = new TableColumn(table, SWT.LEFT);
	    typeColumn.setText("");
	    layout.setColumnData(typeColumn, new ColumnPixelData(18));

	    nameColumn = new TableColumn(table, SWT.LEFT);
	    nameColumn.setText("Name");
	    layout.setColumnData(nameColumn, new ColumnWeightData(4));

	    locationColumn = new TableColumn(table, SWT.LEFT);
	    locationColumn.setText("Location");
	    layout.setColumnData(locationColumn, new ColumnWeightData(9));
		
		table.setHeaderVisible(true); 
		table.setLinesVisible(false);
		
		viewer.setContentProvider(new ProjectViewContentProvider()); 
		viewer.setLabelProvider(new ProjectViewLabelProvider()); 
		viewer.setInput(ProjectViewManager.getManager());
		
		getSite().setSelectionProvider(viewer);
		
		createTableSorter();
		
		//hookPageSelection();
		
	}

	@SuppressWarnings("unchecked")
	private void createTableSorter() {
		Comparator<IProjectViewItem> nameComparator = new Comparator<IProjectViewItem>() {
			public int compare(IProjectViewItem i1, IProjectViewItem i2) {
				return i1.getName().compareTo(i2.getName());
			}
		};
		Comparator<IProjectViewItem> locationComparator = new Comparator<IProjectViewItem>() {
			public int compare(IProjectViewItem i1, IProjectViewItem i2) {
				return i1.getLocation().compareTo(i2.getLocation());
			}
		};
		Comparator<IProjectViewItem> typeComparator = new Comparator<IProjectViewItem>() {
			public int compare(IProjectViewItem i1, IProjectViewItem i2) {
				return i1.getType().compareTo(i2.getType());
			}
		};
		sorter = new ProjectViewSorter(viewer, new TableColumn[] {
				nameColumn, locationColumn, typeColumn }, new Comparator[] {
				nameComparator, locationComparator, typeComparator });
//		if (memento != null)
//			sorter.init(memento);
		viewer.setSorter(sorter);
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
	
	private void hookPageSelection() {
		pageSelectionListener = new ISelectionListener() {
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				pageSelectionChanged(part, selection);
			}
		};
		getSite().getPage().addPostSelectionListener(pageSelectionListener);
	}

	protected void pageSelectionChanged(IWorkbenchPart part,
			ISelection selection) {
		if (part == this)
			return;
		if (!(selection instanceof IStructuredSelection))
			return;
		IStructuredSelection sel = (IStructuredSelection) selection;
		IProjectViewItem[] items = ProjectViewManager.getManager()
				.existingFavoritesFor(sel.iterator());
		if (items.length > 0)
			viewer.setSelection(new StructuredSelection(items), true);
	}
	
	@Override
	public void dispose() {
	      if (pageSelectionListener != null)
	       getSite().getPage().removePostSelectionListener(
	       pageSelectionListener);
	      super.dispose();
	}
	
	public IStructuredSelection getSelection() {
		return (IStructuredSelection) viewer.getSelection();
	}
	
}
