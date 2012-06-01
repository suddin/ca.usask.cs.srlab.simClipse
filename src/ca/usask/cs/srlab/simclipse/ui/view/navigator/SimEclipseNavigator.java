package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simclipse.SimClipseLog;
import ca.usask.cs.srlab.simclipse.ui.view.project.IProjectViewItem;
import ca.usask.cs.srlab.simclipse.ui.view.project.ProjectViewManager;

public class SimEclipseNavigator extends CommonNavigator /*implements ISelectionListener*/{
	
	public static final String ID =
        "ca.usask.cs.srlab.simclipse.ui.view.navigatorView";
	
	@Override
	protected Object getInitialInput() {
		return NavigatorManager.getManager();
	}
	
	@Override
	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);
		
		createContextMenu();
		
		//getSite().getPage().addPostSelectionListener(pageSelectionListener);
		
		hookDoubleCLickAction();
	}
	
	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu", "ca.usask.cs.simclipse.navigator.contextMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				SimEclipseNavigator.this.fillContextMenu(m);
			}
		});
		Menu menu = menuMgr.createContextMenu(getCommonViewer().getControl());
		getCommonViewer().getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, getCommonViewer());
		
		Action refresh = new Action() {
			public void run() {
				NavigatorManager.getManager().getSimClipseProjects();
				getCommonViewer().refresh();
			}
		};
		refresh.setEnabled(true);
		refresh.setText("Refresh");
		menuMgr.add(refresh);
		
	}

	private void fillContextMenu(IMenuManager menuMgr) {
		menuMgr.add(new Separator("edit"));
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part == this)
			return;
		if (!(selection instanceof IStructuredSelection))
			return;
		IStructuredSelection sel = (IStructuredSelection) selection;
		IProjectViewItem[] items = ProjectViewManager.getManager()
				.existingFavoritesFor(sel.iterator());
//		if (items.length > 0)
//		getCommonViewer().setSelection(new StructuredSelection(items), true);
		//getCommonViewer().getSelection();
		//getCommonViewer().setSelection(sel, true);
		//getCommonViewer().getInput()setInput(sel);
	}
	
	
	private void hookDoubleCLickAction() {
		
//		getCommonViewer(). addDoubleClickListener(new IDoubleClickListener() {
//		public void doubleClick(DoubleClickEvent event) {
		getCommonViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
		                           ISelection selection = event.getSelection();
		                           final Object obj = ((IStructuredSelection) selection).getFirstElement();
		                           if (!(obj instanceof NavigatorItemFile || obj instanceof  NavigatorItemFileFragment)) {
		                                return;
		                           }
		                        	   
		                           IFile tempObj = null;
		                           
		                           if(obj instanceof NavigatorItemFile){
		                        	   tempObj = ((NavigatorItemFile) obj).getIFile();
		                           }else if(obj instanceof NavigatorItemFileFragment){
		                        	   tempObj = ((NavigatorItemFileFragment) obj).getIFile();
		                           }
		                        	  
		                              final IFile ifile = tempObj;
		                              
		                              Display.getCurrent().asyncExec(new Runnable() {
		                      			public void run() {
		                      				
		                              
		                               IWorkbenchPage dpage =
		                            	   SimEclipseNavigator.this.getViewSite()
		       							  .getWorkbenchWindow().getActivePage();
		                                if (dpage != null) {
		                                	try {
		                                		ITextEditor editor = (ITextEditor) IDE.openEditor(dpage, ifile, true);
		                                    	
		                                		if(obj instanceof NavigatorItemFileFragment){
		                                		
			                                		IDocumentProvider dp = editor.getDocumentProvider();
			                                		FileEditorInput fileEditorInput = new FileEditorInput(ifile);
			                                        IDocument doc = dp.getDocument(fileEditorInput);
			                                		
			                                        CloneFragment cf = ((NavigatorItemFileFragment) obj).getCodeFragment();
			                                        
			                                		int offset = cf.getFromLine();
			                                    	int length = cf.getToLine() - offset + 1;
			                                    	Position position= new Position(offset-1, length);
			                                    	position = convertToCharacterPosition(position, doc);
			                                    	
			                                    	editor.selectAndReveal(position.getOffset() , position.getLength());
		                                		}
		                                    	
												//editor.setHighlightRange(offset, length, true);
		                                     }catch (Exception e) {
		                                    	 SimClipseLog.logError(e);
		                                                    // log exception
		                                     }
		                                }
		                                
		                               
		                        			}
		                        		});
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
	
	@Override
	public void dispose() {
		super.dispose();
		//getSite().getPage().removeSelectionListener(this);
		//getSite().getPage().removePostSelectionListener(this);
	}
}
