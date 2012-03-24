package ca.usask.cs.srlab.simclipse;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class SimEclipseProjectNature implements IProjectNature {

	public static final String NATURE_ID = "ca.usask.cs.srlab.simeclipseNature";
	 

    private IProject project;

    public void configure() throws CoreException {
       // Add nature-specific information
       // for the project, such as adding a builder
       // to a project's build spec.
    }
    public void deconfigure() throws CoreException {
       // Remove the nature-specific information here.
    }
    
    public IProject getProject() {
       return project;
    }
    
    public void setProject(IProject value) {
       project = value;
    }

}
