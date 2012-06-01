package ca.usask.cs.srlab.simclipse.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class FileUtil {

	static public boolean deleteDirectory(String path){
		return deleteDirectory(new File(path)) ;
	}
	
	static public boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return path.delete();
	  }
	
	static public List<IFile> getFilesFromFolder(IFolder folder) {
		List<IFile> fileList = new ArrayList<IFile>();

		IResource[] resoureses = null;
		try {
			resoureses = folder.members();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < resoureses.length; i++) {
			if (resoureses[i] instanceof IFolder) {
				fileList.addAll(getFilesFromFolder((IFolder) resoureses[i]));
			} else {
				fileList.add((IFile) resoureses[i]);
			}
		}
		return fileList;
	}

//	public static void setHidden(File file) throws InterruptedException, IOException {
//	    Process p = Runtime.getRuntime().exec("attrib +H " + file.getPath());
//	    p.waitFor();
//	}
}
