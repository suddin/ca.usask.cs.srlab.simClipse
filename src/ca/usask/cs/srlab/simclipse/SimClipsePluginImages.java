/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ca.usask.cs.srlab.simclipse;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;


/**
 * Bundle of all images used by the plugin.
 */
public class SimClipsePluginImages {

	// The plugin registry
	private final static ImageRegistry PLUGIN_REGISTRY= SimClipsePlugin.getDefault().getImageRegistry();

	private static final IPath ICONS_PATH= new Path("$nl$/icons"); //$NON-NLS-1$

	public static final String T_OBJ= "obj16/"; //$NON-NLS-1$
	public static final String T_WIZBAN= "wizban/"; //$NON-NLS-1$
	public static final String T_LCL= "lcl16/"; //$NON-NLS-1$
	public static final String T_TOOL= "tool16/"; //$NON-NLS-1$
	public static final String T_EVIEW= "eview16/"; //$NON-NLS-1$

	private static final String NAME_PREFIX = "ca.usask.cs.srlab.simclipse.ui."; //$NON-NLS-1$
	private static final int    NAME_PREFIX_LENGTH = NAME_PREFIX.length();

	// Define image names
	//public static final String IMG_TOOL_SEARCH = NAME_PREFIX + "search.gif"; //$NON-NLS-1$

	public static final String IMG_TOOL_DETECTION_SETTINGS = NAME_PREFIX + "settings.png"; //$NON-NLS-1$

	
	// Define images
//	public static final ImageDescriptor DESC_OBJ_TSEARCH_DPDN= createManaged(T_OBJ, IMG_OBJ_TSEARCH_DPDN);

	public static Image get(String key) {
		return PLUGIN_REGISTRY.get(key);
	}

	private static ImageDescriptor createManaged(String prefix, String name) {
		ImageDescriptor result= create(prefix, name.substring(NAME_PREFIX_LENGTH), true);
		PLUGIN_REGISTRY.put(name, result);
		return result;
	}

	/*
	 * Creates an image descriptor for the given prefix and name in the Search plugin bundle. The path can
	 * contain variables like $NL$.
	 * If no image could be found, <code>useMissingImageDescriptor</code> decides if either
	 * the 'missing image descriptor' is returned or <code>null</code>.
	 * or <code>null</code>.
	 */
	private static ImageDescriptor create(String prefix, String name, boolean useMissingImageDescriptor) {
		IPath path= ICONS_PATH.append(prefix).append(name);
		return createImageDescriptor(SimClipsePlugin.getDefault().getBundle(), path, useMissingImageDescriptor);
	}

	/*
	 * Sets all available image descriptors for the given action.
	 */
	public static void setImageDescriptors(IAction action, String type, String relPath) {
		relPath= relPath.substring(NAME_PREFIX_LENGTH);

		action.setDisabledImageDescriptor(create("d" + type, relPath, false)); //$NON-NLS-1$

		ImageDescriptor desc= create("e" + type, relPath, true); //$NON-NLS-1$
		action.setHoverImageDescriptor(desc);
		action.setImageDescriptor(desc);
	}

	/*
	 * Creates an image descriptor for the given path in a bundle. The path can contain variables
	 * like $NL$.
	 * If no image could be found, <code>useMissingImageDescriptor</code> decides if either
	 * the 'missing image descriptor' is returned or <code>null</code>.
	 * Added for 3.1.1.
	 */
	public static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path, boolean useMissingImageDescriptor) {
		URL url= FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		if (useMissingImageDescriptor) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}

}
