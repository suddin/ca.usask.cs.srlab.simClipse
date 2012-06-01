package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simcad.model.FunctionCloneFragment;

public class FileFragment extends AbstractCodeFragment {

	private CloneFragment codeFragment;
	private IFile parent;
	
	public FileFragment(CloneFragment codeFragment, IFile parent) {
		super();
		this.codeFragment = codeFragment;
		this.parent = parent;
	}

	@Override
	public String getName() {
		if (codeFragment instanceof FunctionCloneFragment){
			return ((FunctionCloneFragment) codeFragment).getFunctionName() + " [ "+ codeFragment.getFromLine() +":"+codeFragment.getToLine()+" ]";
		}
		else
			return "Block-"+codeFragment.getProgramComponentId()  + " [ "+ codeFragment.getFromLine() +":"+codeFragment.getToLine()+" ]";
	}

	public IFile getParentFile() {
		return parent;
	}

	public CloneFragment getCodeFragment() {
		return codeFragment;
	}
	
	@Override
	public IProject getProject() {
		return parent.getProject();
	}
}
