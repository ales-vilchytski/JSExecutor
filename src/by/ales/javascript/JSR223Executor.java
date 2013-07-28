package by.ales.javascript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Simple wrapper for JSR-223 JavaScript (js) engine. Main purpose of this class
 * is to bring useful functionality like:
 * <ul>
 * 	<li> including js-files while executing scripts to modularize source code,
 * 		 like '#include' in C or 'require' in Ruby.
 *  <li> useful methods, implemented in Java and JS (*in progress*)
 * </ul>
 * <br>
 * This wrapper is useful to implement js-testing classes.
 * 
 * !!!NOT THREAD SAFE!!!
 * 
 * @see by.ales.javascript.JSR223TestExecutor
 *  
 * @author Ales Vilchytski
 */
public class JSR223Executor {

	private String jsDir = "";
	private final String executorVar;
	
	private ScriptEngine engine;
	private Set<String> executedIds = new HashSet<String>(); //as value use source id (filename, url, etc)
	
	/**
	 * Creates new executor specifying name of variable associated with this object
	 * which be injected to js-context while executing scripts
	 * 
	 * @param executorVar name of executor variable
	 */
	public JSR223Executor(String executorVar) {
		this.executorVar = executorVar;
	}
	
	/**
	 * Uses default executor variable name '$'
	 */
	public JSR223Executor() {
		this("$");
	}
	
	/**
	 * Directory containing js-files for lookup by relative path
	 * 
	 * @param jsDir relative path to directory
	 */
	public void setJsDir(String jsDir) {
		this.jsDir = jsDir;
	}
	
	public String getJsDir() {
		return jsDir;
	}
	
	/**
	 * Name of js-variable, associated with this executor in js-context. Can be 
	 * used to access executor from executing script
	 */
	public String getExecutorVar() {
		return executorVar;
	}
	
	/**
	 * Lazily returns JSR223 script engine for js
	 * 
	 * @return JSR223 js-engine
	 */
	public ScriptEngine getEngine() {
		if (engine == null) {
			this.engine = 
				new ScriptEngineManager().getEngineByName("JavaScript");
			engine.getBindings(ScriptContext.ENGINE_SCOPE).put(executorVar, this);
		}
		return engine;
	}
	
	/**
	 * Executes stream with js using current context and saves id of this file.
	 * 
	 * @param id unique name (e.g. URL or filename) to distinct script from others 
	 * @param source stream with js-source
	 * @return object returned by underlying script engine
	 * @throws ScriptException if any script evaluation error occurs
	 */
	public Object execute(String id, InputStream source) throws ScriptException {
		try {
			getEngine().put(ScriptEngine.FILENAME, id);

			Object res = getEngine().eval(
				new BufferedReader(new InputStreamReader(source)));
			executedIds.add(id);
			return res;
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	private String idFromRelPath(String relPath) {
		return jsDir + "/" + relPath;
	}
	
	/**
	 * Executes file from relative path using current context and saves id of this file.
	 *  
	 * @param relPath
	 * @return object returned by underlying script engine
	 * @throws ScriptException if any script evaluation error occurs or source file not found
	 * 
	 * @see {@link #execute(String, InputStream)}
	 */
	public Object execute(String relPath) throws ScriptException {
		String file = idFromRelPath(relPath);
		InputStream stream = getClass().getResourceAsStream(file);
		if (stream == null) {
			throw new ScriptException("File not found - " + file);
		}
		try {
			return execute(file, stream);
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	private String idFromFile(File source) {
		return source.getPath();
	}
	
	/**
	 * Executes file with js-source using current context and saves id of this file.
	 * 
	 * @param source file with js-source
	 * @return object returned by underlying script engine
	 * @throws ScriptException if any script evaluation error occurs or source file not found
	 * 
	 * @see {@link #execute(String, InputStream)}
	 */
	public Object execute(File source) throws ScriptException {
		try {
			return execute(idFromFile(source), new FileInputStream(source));
		} catch (FileNotFoundException e) {
			throw new ScriptException(e);
		}
	}
	
	/**
	 * Executes file from stream if it haven't been executed before according to it's ID.
	 * 
	 * @param id Unique name (e.g. URL or filename) to distinct script from others
	 * @param source Stream containing js-source
	 * @throws ScriptException
	 * 
	 * @see {@link #execute(String, InputStream)}
	 */	
	public void include(String id, InputStream source) throws ScriptException {
		if (!executedIds.contains(id)) {
			execute(id, source);
		}
	}
	
	/**
	 * Executes file from relative path if it haven't been executed before 
	 * according to {@link #idFromRelPath(String)}
	 * 
	 * @param relPath relative path to {@link #getJsDir()}
	 * @throws ScriptException
	 * 
	 * @see {@link #execute(String)}
	 */
	public void include(String relPath) throws ScriptException {
		if (!executedIds.contains(idFromRelPath(relPath))) {
			execute(relPath);
		}
	}
	
	/**
	 * Executes file if it haven't been executed before according to {@link #idFromFile(File)}.
	 * 
	 * @param source file with js source
	 * @throws ScriptException
	 * 
	 * @see #execute(File)
	 */
	public void include(File source) throws ScriptException {
		if (!executedIds.contains(idFromFile(source))) {
			execute(source);
		}
	}
	
	/**
	 * Naive implementation of debugging method for scripts. 
	 * Needs improvements, m.b. something like .inspect method in Ruby.
	 * 
	 * @param info object to debug
	 */
	public void debug(Object info) {
		System.out.printf("[DEBUG] %s\n", info);
	}
}
