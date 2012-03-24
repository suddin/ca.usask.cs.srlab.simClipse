package ca.usask.cs.srlab.simclipse.ui.view.navigator;

import java.util.ArrayList;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;

public class NavigatorComposedAdapterFactory {
    private static ComposedAdapterFactory mnCompAdapterFactory;

    public final static ComposedAdapterFactory getAdapterFactory()
    {
        if (mnCompAdapterFactory == null)
            mnCompAdapterFactory = new ComposedAdapterFactory(createFactoryList());
        return mnCompAdapterFactory;
    }

    public final static ArrayList<AdapterFactory> createFactoryList()
    {
        ArrayList<AdapterFactory> factories = new ArrayList<AdapterFactory>();
        factories.add(new ResourceItemProviderAdapterFactory());
//        factories.add(new EcoreItemProviderAdapterFactory());
        factories.add(new ReflectiveItemProviderAdapterFactory());
        return factories;
    }
}
