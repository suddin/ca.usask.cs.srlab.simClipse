package ca.usask.cs.srlab.simclipse.ui.view.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public abstract class ProjectViewItemType implements Comparable<ProjectViewItemType> {
	
	private static final ISharedImages PLATFORM_IMAGES = PlatformUI
			.getWorkbench().getSharedImages();
	
	private final String id;
	private final String printName;
	private final int ordinal;

	private ProjectViewItemType(String id, String name, int position) {
		this.id = id;
		this.ordinal = position;
		this.printName = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return printName;
	}

	public abstract Image getImage();

	public abstract IProjectViewItem newProjectViewItem(Object obj);

	public abstract IProjectViewItem loadProjectViewItem(String info);
	
	public int compareTo(ProjectViewItemType other) { 
		return this.ordinal - other.ordinal;
	}
	
	
	   public static final ProjectViewItemType UNKNOWN 
	      = new ProjectViewItemType("Unknown", "Unknown", 0) 
	   {
	      public Image getImage() {
	         return null;
	      }
	   
	      public IProjectViewItem newProjectViewItem(Object obj) {
	         return null;
	      }
	   
	      public IProjectViewItem loadProjectViewItem(String info) {
	         return null;
	      }
	   };
	
	   public static final ProjectViewItemType WORKBENCH_PROJECT 
	      = new ProjectViewItemType("WBProj", "WorkbenchProject", 3) 
	   {
	      public Image getImage() {
	         return PLATFORM_IMAGES
	               .getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
	      }

	      public IProjectViewItem newProjectViewItem(Object obj) {
	         if (!(obj instanceof IProject))
	            return null;
	         return new ProjectViewItem(this, (IProject) obj);
	      }

	      public IProjectViewItem loadProjectViewItem(String info) {
	         return ProjectViewItem.loadProjectViewItem(this, info);
	      }
	   };

	   
	   private static final ProjectViewItemType[] TYPES = { UNKNOWN, WORKBENCH_PROJECT, };
	   
	   public static ProjectViewItemType[] getTypes() {
	      return TYPES;
	   }
}