package ca.usask.cs.srlab.simclipse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

public class GenericActionDelegate
      implements IObjectActionDelegate, IViewActionDelegate, IEditorActionDelegate {

   /**
    * The part in which the context menu appears
    */
   private IWorkbenchPart targetPart;
   
   /**
    * Sets the active part for the delegate. This method will be called
    * every time the action appears in a context menu. The targetPart
    * may change with each invocation.
    */
   public void setActivePart(IAction action, IWorkbenchPart part) {
      this.targetPart = part;
   }

   /**
    * Initializes this action delegate with the view it will work in.
    * 
    * @param view the view that provides the context for this delegate
    * @see IViewActionDelegate#init(IViewPart)
    */
   public void init(IViewPart view) {
      this.targetPart = view;
   }

   /**
    * Sets the active editor for the delegate. Implementors should
    * disconnect from the old editor, connect to the new editor, and
    * update the action to reflect the new editor.
    * 
    * @param action the action proxy that handles presentation portion
    *           of the action
    * @param editor the new editor target
    * @see IEditorActionDelegate #setActiveEditor(IAction, IEditorPart)
    */
   public void setActiveEditor(IAction action, IEditorPart editor) {
      this.targetPart = editor;
   }
   
   /**
    * Notifies this action delegate that the selection in the
    * workbench has changed.
    * 
    * @param action the action proxy that handles presentation portion
    *           of the action
    * @param selection the current selection, or <code>null</code>
    *           if there is no selection.
    * @see IActionDelegate#selectionChanged(IAction, ISelection)
    */
   public void selectionChanged(IAction action, ISelection selection) {
   }

   /**
    * Called when the user has selected this action to be executed.
    */
   public void run(IAction action) {
      MessageDialog.openInformation(
            targetPart.getSite().getShell(),
            "Enable SimClipse", 
            "Triggered the " + getClass().getName() + " action");
   }

}
