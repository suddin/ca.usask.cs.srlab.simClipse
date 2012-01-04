package ca.usask.cs.srlab.simcad.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.usask.cs.srlab.simcad.model.SimCadManager;


/**
 * Remove each currently selected object from the Favorites collection if it has
 * not already been removed.
 */
public class RemoveFromSimCadHandler extends AbstractHandler
{
   public Object execute(ExecutionEvent event)
         throws ExecutionException {
      ISelection selection = HandlerUtil.getCurrentSelection(event);
      if (selection instanceof IStructuredSelection)
    	  SimCadManager.getManager().removeSimCadItems(
               ((IStructuredSelection) selection).toArray());
      return null;
   }
}
