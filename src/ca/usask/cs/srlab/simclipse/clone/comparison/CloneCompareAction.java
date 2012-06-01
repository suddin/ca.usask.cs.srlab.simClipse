/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt McCutchen (hashproduct+eclipse@gmail.com) - Bug 35390 Three-way compare cannot select (mis-selects) )ancestor resource
 *     Aleksandra Wozniak (aleksandra.k.wozniak@gmail.com) - Bug 239959
 *******************************************************************************/
package ca.usask.cs.srlab.simclipse.clone.comparison;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import ca.usask.cs.srlab.simclipse.SimClipseConstants;
import ca.usask.cs.srlab.simclipse.ui.view.clone.CloneFragmentDisplayModel;
import ca.usask.cs.srlab.simclipse.ui.view.clone.CloneSetDisplayModel;
import ca.usask.cs.srlab.simclipse.ui.view.clone.CloneView;
import ca.usask.cs.srlab.simclipse.ui.view.clone.ICloneViewItem;
import ca.usask.cs.srlab.simclipse.util.FileUtil;


/*
 * The "Compare with each other" action
 */
public class CloneCompareAction /*extends BaseCompareAction */ implements IViewActionDelegate {

	private ISelection selection;
	protected ResourceCompareInput comparatorInput;
	protected IWorkbenchPage fWorkbenchPage;
	protected boolean showSelectAncestorDialog = true;
	protected CloneView view; 

	public void run(IAction action) {

		if (!action.isEnabled() || comparatorInput == null 
				|| view == null || selection == null)
			return;
		
		if (!(selection instanceof StructuredSelection))
			return;
		
			
			if(!( ((StructuredSelection)selection).getFirstElement() instanceof CloneSetDisplayModel))
				return;
			
			IProject project = ((CloneSetDisplayModel)((StructuredSelection)selection).getFirstElement()).getParentProject().getProject();
			List<IFile> tmpFileList = new ArrayList<IFile>(((CloneSetDisplayModel)((StructuredSelection)selection).getFirstElement()).size());
			
			IFolder simclipseDataFolder = (IFolder) project.findMember(SimClipseConstants.SIMCLIPSE_DATA_FOLDER);
        	IFolder simclipseTmpFolder = simclipseDataFolder.getFolder(SimClipseConstants.SIMCLIPSE_TMP_SRC_FOLDER);
        	
        	FileUtil.deleteDirectory(simclipseTmpFolder.getLocation().toFile());
			
			
			for(ICloneViewItem cvi : ((CloneSetDisplayModel)((StructuredSelection)selection).getFirstElement()).getCloneFragmentModels()){
				CloneFragmentDisplayModel clonefragment = (CloneFragmentDisplayModel) cvi;
				IFile originalIFile = (IFile) clonefragment.getResource();
				
				LineNumberReader lineNumberReader = null;
				PrintWriter pwr=null;
		        try {
		        	
		        	IPath originalIFilePath = originalIFile.getProjectRelativePath();
		        	IFolder virtualForderToCreate = simclipseTmpFolder.getFolder(originalIFilePath.removeLastSegments(1));
		        	IFile linkedVistualFile = (IFile) virtualForderToCreate.getFile(originalIFilePath.addFileExtension("part("+clonefragment.getFromLine()+"-"+clonefragment.getToLine()+")").lastSegment());		        	

		        	prepareVirtualFolder(virtualForderToCreate, true);
		        	
		        	//local filesystem 
		        	IPath linkedActualFile = project.getLocation().removeLastSegments(1).append(linkedVistualFile.getFullPath());
		        	
		        	if(!linkedActualFile.removeLastSegments(1).toFile().exists()){
						linkedActualFile.removeLastSegments(1).toFile().mkdirs();
					}

		        	File partFile = linkedActualFile.toFile();
		        	
		        	if(partFile.exists()){
		        		partFile.delete();
		        	}
	        		
		            pwr = new PrintWriter(partFile);
		            pwr.println(clonefragment.toShortString());

		            String line = null;
		            lineNumberReader = new LineNumberReader(new FileReader(originalIFile.getLocation().toFile()));
					
		            while ((line = lineNumberReader.readLine()) != null) {
		                int lineNumber = lineNumberReader.getLineNumber();
		                if(lineNumber >= clonefragment.getFromLine() && lineNumber <= clonefragment.getToLine()){
		                	pwr.println(line);
		                }
		            }
		            pwr.close();
		            partFile.setReadOnly();
		            partFile.setWritable(false);
	        		partFile.deleteOnExit();
		        	
					try {
						linkedVistualFile.createLink(partFile.toURI(), IResource.REPLACE | IResource.HIDDEN,  null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
					
		            IFile newIFile = (IFile) project.findMember(linkedActualFile.makeRelativeTo(project.getLocation())); 
		            tmpFileList.add(newIFile);
		            
		        } catch (FileNotFoundException ex) {
		            ex.printStackTrace();
		        } catch (IOException ex) {
		            ex.printStackTrace();
		        } finally {
		            //Close the BufferedWriter
		            try {
		                if (lineNumberReader != null) {
		                    lineNumberReader.close();
		                }
		                
		                if(pwr!=null)
		                	pwr.close();
		            } catch (IOException ex) {
		                ex.printStackTrace();
		            }
		        }
			}
			
			selection = new StructuredSelection(tmpFileList);
			
			boolean ok = comparatorInput.setSelection(selection, view.getSite().getShell(), showSelectAncestorDialog);
			if (!ok) return;
			
			try{
			
			comparatorInput.initializeCompareConfiguration();
			CompareUI.openCompareEditorOnPage(comparatorInput, fWorkbenchPage);
			comparatorInput= null;	// don't reuse this input!
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	}
	
	public void prepareFolder(IFolder folder) {
		try {
			IContainer parent = folder.getParent();
			if (parent instanceof IFolder) {
				prepareFolder((IFolder) parent);
			}
			if (!folder.exists()) {
				folder.create(IResource.FORCE, true, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void prepareVirtualFolder(IFolder folder, boolean isVirtual)
	{
	  IContainer parent = folder.getParent();
	  if (parent instanceof IFolder)
	  {
	    prepareFolder((IFolder) parent);
	  }
	  if (!folder.exists())
	  {
	    try {
			folder.create(IResource.FORCE | IResource.VIRTUAL , true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	  }
	}
	
	protected boolean isEnabled(ISelection selection) {
		
		if(!(selection instanceof StructuredSelection)){
			return false;
		}
		
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() < 1) {
			return false;
		}
		
		if((((StructuredSelection)selection).getFirstElement() instanceof CloneSetDisplayModel))
		{
			List<IFile> fileList = new ArrayList<IFile>(((CloneSetDisplayModel)((StructuredSelection)selection).getFirstElement()).size());
			
			List<? extends ICloneViewItem> cloneFragmentModels = ((CloneSetDisplayModel)((StructuredSelection)selection).getFirstElement()).getCloneFragmentModels();
			if (cloneFragmentModels.size() > 2) {

//				MessageDialog
//						.openWarning(
//								SimClipsePlugin.getActiveWorkbenchShell(),
//								"Warning!",
//								"Fragments more than two cannot be compared. Please select any two fragments and click compare");
				return false;
			}
			
			for(ICloneViewItem cvi : cloneFragmentModels){
				CloneFragmentDisplayModel tempObj = (CloneFragmentDisplayModel) cvi;
	            IFile ifile = (IFile) tempObj.getResource();
	            fileList.add(ifile);
			}
			selection = new StructuredSelection(fileList);
		}
		
		
		
		if (comparatorInput == null) {
			CompareConfiguration cc= new CompareConfiguration();
			// buffered merge mode: don't ask for confirmation
			// when switching between modified resources
			
			//cc.setProperty(CompareEditor.CONFIRM_SAVE_PROPERTY, new Boolean(false));
			
			// uncomment following line to have separate outline view
			//cc.setProperty(CompareConfiguration.USE_OUTLINE_VIEW, new Boolean(true));
						
			comparatorInput= new ResourceCompareInput(cc);
		}
		return comparatorInput.isEnabled(selection);
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		fWorkbenchPage= targetPart.getSite().getPage();
	}

	@Override
	public void init(IViewPart view) {
		if (view instanceof CloneView)
			this.view = (CloneView) view;
	}
	
	final public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(false);
		this.selection = selection;
		if (action != null)
			action.setEnabled(isEnabled(selection));
	}
}
