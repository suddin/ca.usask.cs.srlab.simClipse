package ca.usask.cs.srlab.simclipse.clone.comparison;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.internal.Utilities;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;

import ca.usask.cs.srlab.simclipse.SimClipsePlugin;
import ca.usask.cs.srlab.simclipse.ui.view.clone.CloneSetDisplayModel;



public final class CloneComparisonManager {
	private static CloneComparisonManager manager;
	//private DetectionSettings detectionSettings;

	private CloneComparisonManager() {
		
	}

	public static CloneComparisonManager getManager() {
		if (manager == null)
			manager = new CloneComparisonManager();
		return manager;
	}

	public void compareClones(CloneSetDisplayModel selectedCloneSet) {
		
		
		
		//asCompareInput(selectedCloneSet);
		CompareEditorInput compareEditorInput = null;
		openCompareEditor(compareEditorInput, SimClipsePlugin.getActivePage());
	}

	
    public static void openCompareEditor(CompareEditorInput input, IWorkbenchPage page) {
		// this is how it worked before opening compare editors for multiple
		// selection was enabled
		openCompareEditor(input, page, false);
	}
    
    public static void openCompareEditor(CompareEditorInput input, IWorkbenchPage page, boolean reuseEditorIfPossible) {
        if (page == null || input == null) 
            return;
        IEditorPart editor = findReusableCompareEditor(input, page);
        // reuse editor only for single selection
        if(editor != null && reuseEditorIfPossible) {
        	IEditorInput otherInput = editor.getEditorInput();
        	if(otherInput.equals(input)) {
        		// simply provide focus to editor
        		page.activate(editor);
        	} else {
        		// if editor is currently not open on that input either re-use existing
        		CompareUI.reuseCompareEditor(input, (IReusableEditor)editor);
        		page.activate(editor);
        	}
        } else {
        	CompareUI.openCompareEditorOnPage(input, page);
        }
    }
    
	public static IEditorPart findReusableCompareEditor(CompareEditorInput input, IWorkbenchPage page) {
		IEditorReference[] editorRefs = page.getEditorReferences();
		// first loop looking for an editor with the same input 
		for (int i = 0; i < editorRefs.length; i++) {
			IEditorPart part = editorRefs[i].getEditor(false);
			if(part != null 
					&& (part.getEditorInput() instanceof ICompareInput) 
					&& part instanceof IReusableEditor
					&& part.getEditorInput().equals(input)) {
				return part;
			}
		}
		// if none found and "Reuse open compare editors" preference is on use
		// a non-dirty editor
		if (isReuseOpenEditor()) {
			for (int i = 0; i < editorRefs.length; i++) {
				IEditorPart part = editorRefs[i].getEditor(false);
				if(part != null 
						&& (part.getEditorInput() instanceof ICompareInput) 
						&& part instanceof IReusableEditor
						&& !part.isDirty()) {
					return part;
				}
			}
		}
		
		// no re-usable editor found
		return null;
	}
	
	
	public ICompareInput asCompareInput(Object object) {
		if (object instanceof ICompareInput) {
			return (ICompareInput) object;
		}
		// Get a compare input from the model provider's compare adapter
		//ISynchronizationCompareAdapter adapter = 
//			Utils.getCompareAdapter(object);
//		if (adapter != null)
//			return adapter.asCompareInput(getContext(), object);
		return null;
	}

	
	private static boolean isReuseOpenEditor() {
		return true;
		//return TeamUIPlugin.getPlugin().getPreferenceStore().getBoolean(IPreferenceIds.REUSE_OPEN_COMPARE_EDITOR);
	}
}
