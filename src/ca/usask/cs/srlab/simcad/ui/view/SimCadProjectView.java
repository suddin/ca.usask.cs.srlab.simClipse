package ca.usask.cs.srlab.simcad.ui.view;

import java.util.Comparator;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.internal.dialogs.ViewContentProvider;
import org.eclipse.ui.internal.dialogs.ViewLabelProvider;
import org.eclipse.ui.part.ViewPart;

import ca.usask.cs.srlab.simcad.model.ISimCadItem;
import ca.usask.cs.srlab.simcad.model.SimCadManager;

public class SimCadProjectView extends ViewPart {

	private TableViewer viewer;
	private TableColumn typeColumn; 
	private TableColumn nameColumn; 
	private TableColumn locationColumn;
	
	private SimCadProjectsViewSorter sorter;
	private IMemento memento;

	
	public SimCadProjectView() {
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
		
		viewer.setContentProvider(new SimCadViewContentProvider()); 
		viewer.setLabelProvider(new SimCadViewLabelProvider()); 
		viewer.setInput(SimCadManager.getManager());
		
		getSite().setSelectionProvider(viewer);
		
		createTableSorter();
		
	}

	private void createTableSorter() {
		Comparator<ISimCadItem> nameComparator = new Comparator<ISimCadItem>() {
			public int compare(ISimCadItem i1, ISimCadItem i2) {
				return i1.getName().compareTo(i2.getName());
			}
		};
		Comparator<ISimCadItem> locationComparator = new Comparator<ISimCadItem>() {
			public int compare(ISimCadItem i1, ISimCadItem i2) {
				return i1.getLocation().compareTo(i2.getLocation());
			}
		};
		Comparator<ISimCadItem> typeComparator = new Comparator<ISimCadItem>() {
			public int compare(ISimCadItem i1, ISimCadItem i2) {
				return i1.getType().compareTo(i2.getType());
			}
		};
		sorter = new SimCadProjectsViewSorter(viewer, new TableColumn[] {
				nameColumn, locationColumn, typeColumn }, new Comparator[] {
				nameComparator, locationComparator, typeComparator });
		if (memento != null)
			sorter.init(memento);
		viewer.setSorter(sorter);
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public IStructuredSelection getSelection() {
		return (IStructuredSelection) viewer.getSelection();
	}
	
}
