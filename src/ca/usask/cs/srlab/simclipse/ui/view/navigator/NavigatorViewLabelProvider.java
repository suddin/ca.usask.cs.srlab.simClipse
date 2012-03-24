package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;

public class NavigatorViewLabelProvider extends AdapterFactoryLabelProvider {
	
	public NavigatorViewLabelProvider(){
		super(NavigatorComposedAdapterFactory.getAdapterFactory());
	}
	
	@Override
	public Image getImage(Object element) 
	{
	    return super.getImage(element);
	}

	@Override
	public String getText(Object element) 
	{
	    return super.getText(element);
	}
}
