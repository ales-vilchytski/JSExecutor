package by.ales.javascript;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeObject;

public class RhinoExecutorTest {

	private RhinoExecutor executor;
	
	@Before
	public void setUp() throws Exception {
		DirRequireBuilder builder = new DirRequireBuilder();
		builder.addJSDir("js/rhino_executor");
		executor = new RhinoExecutor(new ContextFactory(), builder);
	}
	
	@Test
	public void defaultCtxFactoryAndDirBuilder() {
		DirRequireBuilder builder = new DirRequireBuilder("js/rhino_executor");
		RhinoExecutor executorLoc = new RhinoExecutor(builder);
		assertEquals(executorLoc.getContextFactory().getClass(), ContextFactory.class);
		
		//default ContextFactory and Require doesn't use sandbox
		NativeObject res = (NativeObject) executorLoc.execute("default");
		assertEquals(true, res.get("success"));
	}
	
	@Test 
	public void moduleExecutedAsMainModule() {
		NativeObject res = (NativeObject) executor.execute("main_module");
		assertEquals(true, res.get("success"));
	}
	
	@Test
	public void executeModuleWithoutJavaContext() {
		NativeObject res = (NativeObject) executor.execute("simple_module");
		assertEquals(true, res.get("success"));
	}
	
	@Test
	public void executeModuleWithJavaContext() {
		Map<String, List<String>> ctx = new TreeMap<String, List<String>>();
		ctx.put("collection", new LinkedList<String>());
	
		executor.execute("jcontext_module", ctx);
		assertEquals("success", ctx.get("collection").get(0));
	}
}
