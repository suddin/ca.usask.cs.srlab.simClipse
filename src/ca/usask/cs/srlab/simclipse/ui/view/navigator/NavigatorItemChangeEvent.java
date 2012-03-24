package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.util.EventObject;

import org.eclipse.core.resources.IProject;

public class NavigatorItemChangeEvent extends EventObject 
{
   private static final long serialVersionUID = 3697053173951102953L;

   private final IProject[] added;
   private final IProject[] removed;

   public NavigatorItemChangeEvent(NavigatorManager source, IProject[] itemsAdded, IProject[] itemsRemoved) {
      super(source);
      added = itemsAdded;
      removed = itemsRemoved;
   }

   public IProject[] getItemsAdded() {
      return added;
   }

   public IProject[] getItemsRemoved() {
      return removed;
   }
}