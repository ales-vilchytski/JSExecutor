package by.ales.javascript;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.script.ScriptContext;
import javax.script.ScriptException;

import org.junit.Before;
import org.junit.Test;

public class JSR223ExecutorTest {
	protected static final String COUNT_INCLUDES_JS = "count_includes.js";
	protected JSR223Executor executor = null;
	protected static final String JS_DIR = "/js/executor_js/";
	protected static final String SCRIPT_DIR = "bin/js/executor_js/";
	
	@Before
	public void setUp() {
		this.executor = new JSR223Executor();
		executor.setJsDir(JS_DIR);
	}
	
	@Test
	public void testDefaultExecutorVar() throws ScriptException {
		JSR223Executor executor = new JSR223Executor();
		executor.setJsDir(JS_DIR);
		assertEquals("$", executor.getExecutorVar());
		
		Object res = executor.execute("test_defvar.js");
		assertEquals("ok", res.toString());
	}
	
	@Test
	public void testSetExecutorVar() throws ScriptException {
		JSR223Executor executor = new JSR223Executor("ExecutorVar");
		executor.setJsDir(JS_DIR);
		assertEquals("ExecutorVar", executor.getExecutorVar());
		
		Object res = executor.execute("test_defvar.js");
		assertNotSame("ok", res.toString());
	}
		
	@Test
	public void testExecuteStream() throws FileNotFoundException, ScriptException {
		File test = new File(SCRIPT_DIR + COUNT_INCLUDES_JS);
		executor.getEngine().getBindings(ScriptContext.ENGINE_SCOPE).put("counter", 0);
		Object res = executor.execute(test.getAbsolutePath(), new FileInputStream(test));
		assertEquals("ok", res.toString());
		
		Object count = executor.getEngine().get("counter");
		assertEquals("1.0", count.toString());
		
		executor.execute(test.getAbsolutePath(), new FileInputStream(test));
		count = executor.getEngine().get("counter");
		assertEquals("2.0", count.toString());
	}
	
	@Test
	public void testExecuteRelative() throws ScriptException {
		executor.getEngine().getBindings(ScriptContext.ENGINE_SCOPE).put("counter", 0);
		Object res = executor.execute(COUNT_INCLUDES_JS);
		assertEquals("ok", res.toString());
		
		Object count = executor.getEngine().get("counter");
		assertEquals("1.0", count.toString());
		
		executor.execute(COUNT_INCLUDES_JS);
		count = executor.getEngine().get("counter");
		assertEquals("2.0", count.toString());
	}
	
	@Test
	public void testExecuteFile() throws ScriptException {
		File test = new File(SCRIPT_DIR + COUNT_INCLUDES_JS);
		executor.getEngine().getBindings(ScriptContext.ENGINE_SCOPE).put("counter", 0);
		Object res = executor.execute(test);
		assertEquals("ok", res.toString());
		
		Object count = executor.getEngine().get("counter");
		assertEquals("1.0", count.toString());
		
		executor.execute(test);
		count = executor.getEngine().get("counter");
		assertEquals("2.0", count.toString());
	}
	
	@Test
	public void testIncludeStream() throws ScriptException, FileNotFoundException {
		File test = new File(SCRIPT_DIR + COUNT_INCLUDES_JS);
		executor.getEngine().getBindings(ScriptContext.ENGINE_SCOPE).put("counter", 0);
		executor.include(test.getPath(), new FileInputStream(test));
		Object count = executor.getEngine().get("counter");
		assertEquals("1.0", count.toString());
		
		executor.include(test.getPath(), new FileInputStream(test));
		count = executor.getEngine().get("counter");
		assertEquals("1.0", count.toString());
	}

	@Test
	public void testIncludeRelative() throws ScriptException {
		executor.getEngine().getBindings(ScriptContext.ENGINE_SCOPE).put("counter", 0);
		executor.include(COUNT_INCLUDES_JS);
		Object count = executor.getEngine().get("counter");
		assertEquals("1.0", count.toString());
		
		executor.include(COUNT_INCLUDES_JS);
		count = executor.getEngine().get("counter");
		assertEquals("1.0", count.toString());
	}
	
	@Test
	public void testIncludeFile() throws ScriptException {
		executor.getEngine().getBindings(ScriptContext.ENGINE_SCOPE).put("counter", 0);
		executor.include(new File(SCRIPT_DIR, COUNT_INCLUDES_JS));
		Object count = executor.getEngine().get("counter");
		assertEquals("1.0", count.toString());
		
		executor.include(new File(SCRIPT_DIR, COUNT_INCLUDES_JS));
		count = executor.getEngine().get("counter");
		assertEquals("1.0", count.toString());
	}
	
}
