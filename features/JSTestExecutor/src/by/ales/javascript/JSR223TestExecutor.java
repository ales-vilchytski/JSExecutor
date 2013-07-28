package by.ales.javascript;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class JSR223TestExecutor extends Task {

	private String options = "{}";
	private String ignoredGlobalVars = "";
	private String jsExecutorVar = "$";
	private String haltOnFirstFailure = "true";
	private String jsDir = "/js";
	private String beforeTestScript = null;
	private List<FileSet> filesets = new LinkedList<FileSet>();
	
	public void setOptions(String opts) {
		this.options = opts;
	}

	public void setIgnoredGlobalVars(String ignoredGlobalVars) {
		this.ignoredGlobalVars = ignoredGlobalVars;
	}
	
	public void setJsExecutorVar(String jsExecutorVar) {
		this.jsExecutorVar = jsExecutorVar;
	}
	
	public void setHaltOnFirstFailure(String haltOnFirstFailure) {
		this.haltOnFirstFailure = haltOnFirstFailure;
	}
	
	public void setJsDir(String jsDir) {
		this.jsDir = jsDir;
	}
	
	public void setBeforeTestScript(String start) {
		this.beforeTestScript = start;
	}
	
	public void add(FileSet fileset) {
		this.filesets.add(fileset);
	}
	
	@Override 
	public void execute() throws BuildException {
		JSR223Executor executor = new JSR223Executor(jsExecutorVar);
		Bindings bindings = executor.getEngine().getBindings(
				ScriptContext.ENGINE_SCOPE);
		
		bindings.put("project", getProject());
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("options", options);
		attributes.put("haltOnFirstFailure", haltOnFirstFailure);
		attributes.put("ignoredGlobalVars", ignoredGlobalVars);
		bindings.put("attributes", attributes);
		
		Map<String, Object> elements = new HashMap<String, Object>();
		elements.put("fileset", filesets);
		bindings.put("elements", elements);
		bindings.put("self", this);
		
		try {
			String jsPath = "/js/rhinounit_1_2_1";
			String rhinounitUtil = jsPath + "/rhinoUnitUtil.js";
			String rhinounit = jsPath + "/rhinoUnitAnt.js";

			executor.setJsDir(jsDir);
			if (beforeTestScript != null) {
				executor.execute(beforeTestScript);
			}
			executor.include(rhinounitUtil, getClass().getResourceAsStream(rhinounitUtil));
			executor.include(rhinounit, getClass().getResourceAsStream(rhinounit));
		} catch (ScriptException e) {
			throw new BuildException(e);
		}
	};
	
	public void fail(String failMessage) {
		throw new BuildException(failMessage);
	}
}
