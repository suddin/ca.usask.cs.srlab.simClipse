package ca.usask.cs.srlab.simclipse.ui.view.project;

import java.util.EventObject;

public class ProjectViewManagerEvent extends EventObject 
{
   private static final long serialVersionUID = 3697053173951102953L;

   private final IProjectViewItem[] added;
   private final IProjectViewItem[] removed;

   public ProjectViewManagerEvent(ProjectViewManager source, IProjectViewItem[] itemsAdded, IProjectViewItem[] itemsRemoved) {
      super(source);
      added = itemsAdded;
      removed = itemsRemoved;
   }

   public IProjectViewItem[] getItemsAdded() {
      return added;
   }

   public IProjectViewItem[] getItemsRemoved() {
      return removed;
   }
}