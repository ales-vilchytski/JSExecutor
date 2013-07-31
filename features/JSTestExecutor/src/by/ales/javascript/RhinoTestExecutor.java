package by.ales.javascript;

import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


public class RhinoTestExecutor extends Task {

	//idiom of running tests by RhinoExecutor with Narwhal CommonJS/Unit modules
	public static void main(String[] args) {
		
		DirRequireBuilder builder = new DirRequireBuilder("/js/narwhal");
		builder.addJSDir("/");
		RhinoExecutor executor = new RhinoExecutor(builder);
				
		executor.execute("test1");
		executor.execute("test2");
		
	}
	
	@Override
	public void execute() throws BuildException {
		//TODO create ant-task
	}
}
