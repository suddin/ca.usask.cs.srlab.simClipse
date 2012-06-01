package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.util.EventObject;

public class NavigatorItemChangeEvent extends EventObject 
{
   private static final long serialVersionUID = 3697053173951102953L;

   private final INavigatorItem[] added;
   private final INavigatorItem[] removed;

   public NavigatorItemChangeEvent(NavigatorManager source, INavigatorItem[] itemsAdded, INavigatorItem[] itemsRemoved) {
      super(source);
      added = itemsAdded;
      removed = itemsRemoved;
   }

   public INavigatorItem[] getItemsAdded() {
      return added;
   }

   public INavigatorItem[] getItemsRemoved() {
      return removed;
   }
}