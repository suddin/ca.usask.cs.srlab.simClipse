package ca.usask.cs.srlab.simcad.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public abstract class SimCadItemType implements Comparable<SimCadItemType> {
	
	private static final ISharedImages PLATFORM_IMAGES = PlatformUI
			.getWorkbench().getSharedImages();
	
	private final String id;
	private final String printName;
	private final int ordinal;

	private SimCadItemType(String id, String name, int position) {
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

	public abstract ISimCadItem newSimCadItem(Object obj);

	public abstract ISimCadItem loadSimCadItem(String info);
	
	public int compareTo(SimCadItemType other) { 
		return this.ordinal - other.ordinal;
	}
	
	
	   public static final SimCadItemType UNKNOWN 
	      = new SimCadItemType("Unknown", "Unknown", 0) 
	   {
	      public Image getImage() {
	         return null;
	      }
	   
	      public ISimCadItem newSimCadItem(Object obj) {
	         return null;
	      }
	   
	      public ISimCadItem loadSimCadItem(String info) {
	         return null;
	      }
	   };
	
	   public static final SimCadItemType WORKBENCH_PROJECT 
	      = new SimCadItemType("WBProj", "WorkbenchProject", 3) 
	   {
	      public Image getImage() {
	         return PLATFORM_IMAGES
	               .getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
	      }

	      public ISimCadItem newSimCadItem(Object obj) {
	         if (!(obj instanceof IProject))
	            return null;
	         return new SimCadResource(this, (IProject) obj);
	      }

	      public ISimCadItem loadSimCadItem(String info) {
	         return SimCadResource.loadSimCadItem(this, info);
	      }
	   };

	   
	   private static final SimCadItemType[] TYPES = { UNKNOWN, WORKBENCH_PROJECT, };
	   
	   public static SimCadItemType[] getTypes() {
	      return TYPES;
	   }
}