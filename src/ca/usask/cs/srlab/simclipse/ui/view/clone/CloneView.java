package ca.usask.cs.srlab.simclipse.ui.view.clone;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import ca.usask.cs.srlab.simclipse.SimClipseLog;

public class CloneView extends ViewPart {

	private TreeViewer viewer;
	
	public static final String ID =
        "ca.usask.cs.srlab.simclipse.ui.view.ClonesView";
	
	public CloneView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.setContentProvider(new CloneViewContentProvider()); 
		viewer.setLabelProvider(new CloneViewLabelProvider()); 
		viewer.setInput(CloneViewManager.getManager());
		viewer.expandAll();
		getSite().setSelectionProvider(viewer);
		
		createContextMenu() ;
		
		hookDoubleCLickAction();
	}

	
	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu", "ca.usask.cs.simclipse.cloneView.popupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				CloneView.this.fillContextMenu(m);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager menuMgr) {
		menuMgr.add(new Separator("edit"));
		// menuMgr.add(removeContributionItem);
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	@Override
	public void setFocus() {
		// viewer.getControl().setFocus();

	}

	public IStructuredSelection getSelection() {
		return (IStructuredSelection) viewer.getSelection();
	}
	
	private void hookDoubleCLickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
		public void doubleClick(DoubleClickEvent event) {
		                           ISelection selection = event.getSelection();
		                           Object obj = ((IStructuredSelection) selection).getFirstElement();
		                           if (!(obj instanceof CloneFragmentDisplayModel)) {
		                                return;
		                           }else {
		                        	  CloneFragmentDisplayModel tempObj = (CloneFragmentDisplayModel) obj;
		                              IFile ifile = (IFile) tempObj.getResource();
		                            	  //ResourcesPlugin.getWorkspace().getRoot().
							      //getFile(tempObj.getResource().getFullPath());
		                               IWorkbenchPage dpage =
		                            	   CloneView.this.getViewSite()
		       							  .getWorkbenchWindow().getActivePage();
		                                if (dpage != null) {
		                                	try {
		                                		ITextEditor editor = (ITextEditor) IDE.openEditor(dpage, ifile, true);
		                                    	
		                                		IDocumentProvider dp = editor.getDocumentProvider();
		                                		FileEditorInput fileEditorInput = new FileEditorInput(ifile);
		                                        IDocument doc = dp.getDocument(fileEditorInput);
		                                		
		                                		int offset = tempObj.getFromLine();
		                                    	int length = tempObj.getToLine() - offset + 1;
		                                    	Position position= new Position(offset-1, length);
		                                    	position = convertToCharacterPosition(position, doc);
		                                    	
		                                    	editor.selectAndReveal(position.getOffset() , position.getLength());
												//editor.setHighlightRange(offset, length, true);
		                                     }catch (Exception e) {
		                                    	 SimClipseLog.logError(e);
		                                                    // log exception
		                             }
		                         }
		                  }
		                   };
		             });
		         }
	
	
	public static Position convertToCharacterPosition(Position linePosition, IDocument doc) throws BadLocationException {
		int lineOffset= linePosition.getOffset();
		int lineLength= linePosition.getLength();

		int charOffset= doc.getLineOffset(lineOffset);
		int charLength= 0;
		if (lineLength > 0) {
			int lastLine= lineOffset+lineLength-1;
			int endPosition= doc.getLineOffset(lastLine)+doc.getLineLength(lastLine);
			charLength= endPosition-charOffset;
		}
		return new Position(charOffset, charLength);
	}
	
}
