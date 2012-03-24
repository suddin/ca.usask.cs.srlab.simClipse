package ca.usask.cs.srlab.simclipse;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SimClipsePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.usask.cs.srlab.simClipse"; //$NON-NLS-1$

	public static final String CONSOLE_ID = PLUGIN_ID + ".console"; //$NON-NLS-1$

	public static final String SEARCH_VIEW_ID= PLUGIN_ID + ".searchView"; //$NON-NLS-1$

	
	// The shared instance
	private static SimClipsePlugin plugin;
	
	private MessageConsoleStream consoleOut;
	
	/**
	 * The constructor
	 */
	public SimClipsePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		MessageConsole console = findConsole(CONSOLE_ID);
		consoleOut = console.newMessageStream();

		printToConsole("Starting simcad plugin...");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		if(consoleOut!=null)
			consoleOut.close();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SimClipsePlugin getDefault() {
		return plugin;
	}

	public IDialogSettings getDialogSettingsSection(String name) {
		IDialogSettings dialogSettings= getDialogSettings();
		IDialogSettings section= dialogSettings.getSection(name);
		if (section == null) {
			section= dialogSettings.addNewSection(name);
		}
		return section;
	}
	
	/**
	 * @return Returns the shell of the active workbench window.
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window= getActiveWorkbenchWindow();
		if (window != null)
			return window.getShell();
		return null;
	}
	
	/**
	 * @return Returns the workbench from which this plugin has been loaded.
	 */
	public IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	/**
	 * @return  Returns the active workbench window's currrent page.
	 */
	public static IWorkbenchPage getActivePage() {
		return getActiveWorkbenchWindow().getActivePage();
	}
	
	/**
	 * Returns the active workbench window.
	 * @return returns <code>null</code> if the active window is not a workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		IWorkbenchWindow window= plugin.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			final WindowRef windowRef= new WindowRef();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					setActiveWorkbenchWindow(windowRef);
				}
			});
			return windowRef.window;
		}
		return window;
	}
	
	private static class WindowRef {
		public IWorkbenchWindow window;
	}
	
	private static void setActiveWorkbenchWindow(WindowRef windowRef) {
		windowRef.window= null;
		Display display= Display.getCurrent();
		if (display == null)
			return;
		Control shell= display.getActiveShell();
		while (shell != null) {
			Object data= shell.getData();
			if (data instanceof IWorkbenchWindow) {
				windowRef.window= (IWorkbenchWindow)data;
				return;
			}
			shell= shell.getParent();
		}
		Shell shells[]= display.getShells();
		for (int i= 0; i < shells.length; i++) {
			Object data= shells[i].getData();
			if (data instanceof IWorkbenchWindow) {
				windowRef.window= (IWorkbenchWindow)data;
				return;
			}
		}
	}
	
	public static void beep() {
		getActiveWorkbenchShell().getDisplay().beep();
	}
	
	public static void log(Exception e) {
		// TODO Auto-generated method stub
		
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		ImageDescriptor id = imageDescriptorFromPlugin(PLUGIN_ID, SimClipseConstants.SIMCLIPSE_ICON_PATH + path);
		return id;
	}

	public static IViewPart getViewIfExists(String id) {
		try{
		for(IViewReference vr : getActiveWorkbenchWindow().getActivePage().getViewReferences()){
			if(vr.getId().equals(id)){
				return vr.getView(false);
			}
		}
		}catch(Exception e){
			
		}
		return null;
	}

	public static void log(Status status) {
		// TODO Auto-generated method stub
		// http://wiki.eclipse.org/FAQ_How_do_I_use_the_platform_debug_tracing_facility%3F
	}
	
	
	public void printToConsole(String message){
		consoleOut.println(message);		
	}
	
	public static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public static void displayConsole(IConsole myConsole) throws PartInitException {
		IWorkbenchPage page = getActivePage();
		String id = IConsoleConstants.ID_CONSOLE_VIEW;
		IConsoleView view = (IConsoleView) page.showView(id);
		view.display(myConsole);
	}

	public void printToConsole(String message, Exception e) {
		printToConsole(message);
		for(StackTraceElement element : e.getStackTrace()){
			printToConsole(element.toString());
		}
	}
	
}
