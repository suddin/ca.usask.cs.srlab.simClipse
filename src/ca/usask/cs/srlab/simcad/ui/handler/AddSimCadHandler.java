package ca.usask.cs.srlab.simcad.ui.handler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.usask.cs.srlab.simcad.model.SimCadManager;

/**
 * Add each currently selected object to the Favorites collection if it has not
 * already been added.
 */
public class AddSimCadHandler extends AbstractHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			Iterator<?> iter = ((IStructuredSelection) selection).iterator();
			
			if (!iter.hasNext())
				return null;
			
			while (iter.hasNext()) {
				Object elem = iter.next();

				if (!(elem instanceof IProject) && !(elem instanceof IFolder))
					return null;

				IProject project = null;

				if (elem instanceof IFolder) {
					// find the main project
					IFolder folder = (IFolder) ((IAdaptable) elem)
							.getAdapter(IFolder.class);
					project = folder.getProject();
				}

				project = (IProject) ((IAdaptable) elem)
						.getAdapter(IProject.class);
				
				if (project == null)
					return null;

				SimCadManager.getManager()
						.activateSimcadForProject(project);
			}
			
		}
		return null;
	}
}
