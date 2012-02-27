package ca.usask.cs.srlab.simclipse.ui.view.project;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class ProjectViewItem implements IProjectViewItem
{
	private ProjectViewItemType type;
	private IResource resource;
	private String name;

	ProjectViewItem(ProjectViewItemType type, IResource resource) {
		this.type = type;
		this.resource = resource;
	}

	public static ProjectViewItem loadProjectViewItem(ProjectViewItemType type,
			String info) {
		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(info)); 
		if (res == null)
			return null;
		return new ProjectViewItem(type, res);
	}

	public String getName() {
		if (name == null)
			name = resource.getName();
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public IResource getResource() {
		return resource;
	}
	
	public String getLocation() {
		IPath path = resource.getLocation().removeLastSegments(1);
		if (path.segmentCount() == 0)
			return "";
		return path.toString();
	}

	public boolean isProjectViewItemFor(Object obj) {
		return resource.equals(obj);
	}

	public ProjectViewItemType getType() {
		return type;
	}

	public boolean equals(Object obj) {
		return this == obj
				|| ((obj instanceof ProjectViewItem) && resource
						.equals(((ProjectViewItem) obj).resource));
	}

	public int hashCode() {
		return resource.hashCode();
	}

	// For now, this is how we suppress a warning that we cannot fix
	// See Bugzilla #163093 and Bugzilla #149805 comment #14
	@SuppressWarnings({"rawtypes" })
	public Object getAdapter(Class adapter) {
      return getAdapterDelegate(adapter);
   }

	private Object getAdapterDelegate(Class<?> adapter) {
		if (adapter.isInstance(resource))
			return resource;
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public String getInfo() {
		return resource.getFullPath().toString();
	}
}