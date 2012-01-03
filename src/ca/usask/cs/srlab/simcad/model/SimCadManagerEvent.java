package ca.usask.cs.srlab.simcad.model;

import java.util.EventObject;

public class SimCadManagerEvent extends EventObject 
{
   private static final long serialVersionUID = 3697053173951102953L;

   private final ISimCadItem[] added;
   private final ISimCadItem[] removed;

   public SimCadManagerEvent(SimCadManager source, ISimCadItem[] itemsAdded, ISimCadItem[] itemsRemoved) {
      super(source);
      added = itemsAdded;
      removed = itemsRemoved;
   }

   public ISimCadItem[] getItemsAdded() {
      return added;
   }

   public ISimCadItem[] getItemsRemoved() {
      return removed;
   }
}