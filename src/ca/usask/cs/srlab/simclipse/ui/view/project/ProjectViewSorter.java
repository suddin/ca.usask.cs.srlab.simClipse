package ca.usask.cs.srlab.simclipse.ui.view.project;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;

/**
 * A viewer sorter is used by a structured viewer to reorder the elements
 * provided by its content provider. In our case, the
 * <code>FavoritesViewSorter</code> delegates sorting to the comparators. In
 * addition, the FavoritesViewSorter listens for mouse clicks in the column
 * headers and resorts the table content based on the column that was selected.
 * Clicking on a column a second time toggles the sort order.
 */
public class ProjectViewSorter extends ViewerSorter
{
   private static final String TAG_DESCENDING = "descending";
   private static final String TAG_COLUMN_INDEX = "columnIndex";
   private static final String TAG_TYPE = "SortInfo";
   private static final String TAG_TRUE = "true";

   // Simple data structure for grouping
   // sort information by column.
   private class SortInfo
   {
      int columnIndex;
      Comparator<Object> comparator;
      boolean descending;
   }

   private TableViewer viewer;
   private SortInfo[] infos;

   public ProjectViewSorter(TableViewer viewer,
         TableColumn[] columns, Comparator<Object>[] comparators) {
      this.viewer = viewer;
      infos = new SortInfo[columns.length];
      for (int i = 0; i < columns.length; i++) {
         infos[i] = new SortInfo();
         infos[i].columnIndex = i;
         infos[i].comparator = comparators[i];
         infos[i].descending = false;
         createSelectionListener(columns[i], infos[i]);
      }
   }

   public int compare(Viewer viewer, Object item1,
         Object item2) {
      for (int i = 0; i < infos.length; i++) {
         int result =
               infos[i].comparator.compare(item1, item2);
         if (result != 0) {
            if (infos[i].descending)
               return -result;
            return result;
         }
      }
      return 0;
   }

   private void createSelectionListener(final TableColumn column,
         final SortInfo info) {
      column.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            sortUsing(info);
         }
      });
   }

   protected void sortUsing(SortInfo info) {
      if (info == infos[0])
         info.descending = !info.descending;
      else {
         for (int i = 0; i < infos.length; i++) {
            if (info == infos[i]) {
               System.arraycopy(infos, 0, infos, 1, i);
               infos[0] = info;
               info.descending = false;
               break;
            }
         }
      }
      viewer.refresh();
   }

   public void saveState(IMemento memento) {
      for (int i = 0; i < infos.length; i++) {
         SortInfo info = infos[i];
         IMemento mem = memento.createChild(TAG_TYPE);
         mem.putInteger(TAG_COLUMN_INDEX, info.columnIndex);
         if (info.descending)
            mem.putString(TAG_DESCENDING, TAG_TRUE);
      }
   }

   public void init(IMemento memento) {
      List<SortInfo> newInfos = new ArrayList<SortInfo>(infos.length);
      IMemento[] mems = memento.getChildren(TAG_TYPE);
      for (int i = 0; i < mems.length; i++) {
         IMemento mem = mems[i];
         Integer value = mem.getInteger(TAG_COLUMN_INDEX);
         if (value == null)
            continue;
         int index = value.intValue();
         if (index < 0 || index >= infos.length)
            continue;
         SortInfo info = infos[index];
         if (newInfos.contains(info))
            continue;
         info.descending =
               TAG_TRUE.equals(mem.getString(TAG_DESCENDING));
         newInfos.add(info);
      }
      for (int i = 0; i < infos.length; i++)
         if (!newInfos.contains(infos[i]))
            newInfos.add(infos[i]);
      infos = newInfos.toArray(new SortInfo[newInfos.size()]);
   }
}