package by.ales.javascript;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;


public class RhinoTestExecutor extends Task {

	private List<FileSet> filesets = new LinkedList<FileSet>();
	
	public void add(FileSet fileset) {
		this.filesets.add(fileset);
	}
	
	@Override
	public void execute() throws BuildException {
		for (FileSet fs : filesets) {
			DirectoryScanner scanner = fs.getDirectoryScanner(getProject());
			String[] files = scanner.getIncludedFiles();
			
			DirRequireBuilder builder = new DirRequireBuilder();
			builder.addJSDir("js");
			builder.addJSDir("js/narwhal");
			
			builder.getLookupPaths().add(fs.getDir().toURI());
						
			RhinoExecutor executor = new RhinoExecutor(builder);
						
			for (String file : files) {
				Map<String, Object> ctx = new TreeMap<String, Object>();
				
				Map<String, Object> options = new TreeMap<String, Object>();
				
				//TODO move to configuration
				options.put("out", System.out);
				options.put("name", file);
				options.put("showPasses", true);
				
				ctx.put("options", options);
				ctx.put("id", file.replace(File.separator,"/"));

				executor.execute("unit_runner", ctx);

				System.out.println("\n");
			}
		}
		
	}
}
