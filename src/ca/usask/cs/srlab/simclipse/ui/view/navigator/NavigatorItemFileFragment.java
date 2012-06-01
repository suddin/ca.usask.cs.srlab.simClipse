package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import ca.usask.cs.srlab.simcad.model.CloneFragment;
import ca.usask.cs.srlab.simcad.model.FunctionCloneFragment;

public class NavigatorItemFileFragment extends BaseNavigatorViewItem{

	private CloneFragment codeFragment;
	private NavigatorItemFile parent;
	
	public NavigatorItemFileFragment(CloneFragment codeFragment, NavigatorItemFile parent) {
		super(parent.iResource);
		this.codeFragment = codeFragment;
		this.parent = parent;
	}
	
	public IFile getIFile(){
		return (IFile) iResource;
	}
	
	public String getName() {
		if (codeFragment instanceof FunctionCloneFragment){
			return ((FunctionCloneFragment) codeFragment).getFunctionName() + " [ "+ codeFragment.getFromLine() +":"+codeFragment.getToLine()+" ]";
		}
		else
			return "Block-"+codeFragment.getProgramComponentId()  + " [ "+ codeFragment.getFromLine() +":"+codeFragment.getToLine()+" ]";
	}

	public CloneFragment getCodeFragment() {
		return codeFragment;
	}
	
	@Override
	public IProject getProject(){
		return ((INavigatorItem)getParent()).getProject();
	}

	@Override
	public Object getParent() {
		return parent;
	}

	@Override
	public Object[] getChildren() {
		return EMPTY_ARRAY;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NavigatorItemFileFragment) {
			NavigatorItemFileFragment another = (NavigatorItemFileFragment) obj;
			return another.getCodeFragment().getFileName().equals(getCodeFragment().getFileName())
					&& another.getCodeFragment().getFromLine() == getCodeFragment().getFromLine() 
					&& another.getCodeFragment().getToLine() == getCodeFragment().getToLine();
		}
		return false;
	}

	@Override
	public int hashCode() {
		int h = getCodeFragment().getFileName().hashCode();
		h = 31 * h + getCodeFragment().getFromLine();
		h = 31 * h + getCodeFragment().getToLine();
		h = 31 * h + getCodeFragment().getSimhash1().intValue();
		return h;
	}
}
