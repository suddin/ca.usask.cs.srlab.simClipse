package ca.usask.cs.srlab.simcad.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.usask.cs.srlab.simcad.model.SimCadManager;

/**
 * Add each currently selected object to the Favorites collection if it has not
 * already been added.
 */
public class AddToSimCadHandler extends AbstractHandler
{
   public Object execute(ExecutionEvent event)
         throws ExecutionException {
      ISelection selection = HandlerUtil.getCurrentSelection(event);
      if (selection instanceof IStructuredSelection)
         SimCadManager.getManager().addSimCadItems(
               ((IStructuredSelection) selection).toArray());
      return null;
   }
}
