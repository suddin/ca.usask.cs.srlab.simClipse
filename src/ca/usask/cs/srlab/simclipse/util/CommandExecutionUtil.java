package ca.usask.cs.srlab.simclipse.util;
import java.io.*;

import ca.usask.cs.srlab.simclipse.SimClipseConstants;

public class CommandExecutionUtil {
	public static void main(String[] args) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		//String[] cmd = {"/bin/sh", "-c", "cd /Users/sharif/Desktop/simCad-1.1\n./simcad -version\necho $?"};
		
		String[] cmd = {
				"/bin/sh",
				"-c",
				"cd /Users/sharif/Desktop/simCad-1.1"+
				"\npwd\nbash -x ./simcad functions java 0 /Users/sharif/Desktop/dnsjava-0-3"+
				"\necho $?"
				};
		
		Process proc = runtime.exec("sh "+detectionScriptGenerator(), new String[0], new File("/Users/sharif/Desktop/simCad-1.1"));
		
		InputStream is = proc.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}
	
	
	static String detectionScriptGenerator() throws IOException {
		File scriptFile = new File("/Users/sharif/Desktop/simCad-1.1/detect.sh");

		FileWriter fstreamMethodList = new FileWriter(scriptFile);
		BufferedWriter out = new BufferedWriter(fstreamMethodList);

		out.write("#!/bin/bash");
		out.newLine();
		out.write("cd /Users/sharif/Desktop/simCad-1.1");
		out.newLine();
		out.write("./simcad functions java 0 /Users/sharif/Desktop/dnsjava-0-3");
		out.newLine();
		out.write("echo $?");

		String str = "";

		out.close();

		scriptFile.setExecutable(true);
		return scriptFile.getAbsolutePath();
	}
}
