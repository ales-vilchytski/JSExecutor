package by.ales.javascript;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ContextFactory.Listener;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;


/**
 * Wraps Rhino classes to evaluate JavaScript using CommonJS.module.
 * Use method {@link RhinoExecutor#eval(String, Map)} to evaluate module by ID.
 * All objects in map will be put into global scope.
 * <br>
 * Executor can be customized by passing {@link ContextFactory} (e.g.
 * sandboxed factory) and {@link RequireBuilder} (e.g. with custom
 * {@link ModuleScriptProvider}).
 * <br>
 * Default constructor creates instance which will search modules in '/js/' dir
 * using default {@link DirRequireBuilder} to create {@link Require} instances.
 */
public class RhinoExecutor {

	private ContextFactory contextFactory;
	private RequireBuilder requireBuilder;
	private Listener debugger;
	private boolean debug;
	private String debugSettings;
	
	private static DirRequireBuilder createDirRequireBuilder(String jsDir) {
		DirRequireBuilder builder = new DirRequireBuilder();
		try {
			builder.addJSDir(jsDir);
			return builder;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Default constructor using {@link ContextFactory} and '/js/' directory
	 * to look for modules.
	 */
	public RhinoExecutor() {
		this(new ContextFactory(), createDirRequireBuilder("/js/"));
	}
	
	/**
	 * Creates instance using {@link ContextFactory}
	 * 
	 * @param jsDir path to directory with modules
	 */
	public RhinoExecutor(String jsDir) {
		this(new ContextFactory(), createDirRequireBuilder(jsDir));
	}

	public RhinoExecutor(ContextFactory ctxFactory, RequireBuilder requireBuilder) {
		this.contextFactory = ctxFactory;
		this.requireBuilder = requireBuilder;
	}
	
	public ContextFactory getContextFactory() {
		return contextFactory;
	}
	
	/**
	 * Sets {@link ContextFactory} which be used to create contexts.
	 * If debugger is used, then {@link RhinoExecutor#stopDebugger()} method
	 * should be called first to detach debugger from factory.
	 */
	public void setContextFactory(ContextFactory ctxFactory) {
		this.contextFactory = ctxFactory;
	}
		
	public RequireBuilder getRequireBuilder() {
		return requireBuilder;
	}

	/**
	 * Sets current builder of {@link Require} instances
	 * 
	 * @param requireBuilder builder which be called every call of 
	 * 						 {@link RhinoExecutor#eval(String, Map)}
	 */
	public void setRequireBuilder(RequireBuilder requireBuilder) {
		this.requireBuilder = requireBuilder;
	}

	/**
	 * Executes module in it's own scope. Similar to eval("moduleId", null).
	 * 
	 * @param moduleId id of module according to CommonJS.module spec
	 */
	public void eval(String moduleId) {
		eval(moduleId, null);
	}

	/**
	 * Executes module in it'w own scope using additional Java objects in global
	 * scope.
	 * 
	 * @param moduleId id of module according to CommonJS.module spec
	 * @param javaScope map of objects which will be put into global scope (will
	 * 					be accessible in all modules, required by executed) 
	 */
	public void eval(String moduleId, Map<String, Object> javaScope) {
		Context ctx = contextFactory.enterContext();
		try {
			ScriptableObject scope = ctx.initStandardObjects();
			if (javaScope != null) {
				for (Map.Entry<String, Object> entry : javaScope.entrySet()) {
					scope.put(entry.getKey(), scope, entry.getValue());
				}
			}
			
			Require require = requireBuilder.createRequire(ctx, scope);
			
			//Sandboxed Require.requireMain method does nothing when 
			//there is no module with module id (it may be bug). 
			//Then use Require.call to execute module in module's scope
			require.call(ctx, scope, null, new String[]{moduleId});
		} finally {
			Context.exit();
		}
	}

	/**
	 * @return true if debugger started
	 */
	public boolean isDebug() {
		return debug;
	}
	
	/**
	 * @return current debugger settings
	 */
	public String getDebugSettings() {
		return debugSettings;
	}
	
	/**
	 * Starts debugger with default settings: 'transport=socket,suspend=y,address=9000'.
	 */
	public void startDebugger() {
		startDebugger("transport=socket,suspend=y,address=9000");
	}
	
	/**
	 * Starts Eclipse JSDT Rhino Debugger associated with current ContextFactory
	 * if it's accessible in classpath.
	 * See page on wiki {@linkplain http://wiki.eclipse.org/JSDT/Debug/Embedding_Rhino_Debugger}
	 * 
	 * @param debugSettings debug settings for JSDT Rhino Debugger
	 */
	public void startDebugger(String debugSettings) {
		if (debugger == null) {
			Constructor<?> debuggerConstructor;
			try {
				debuggerConstructor = Class
						.forName(
								"org.eclipse.wst.jsdt.debug.rhino.debugger.RhinoDebugger",
								true, RhinoExecutor.class.getClassLoader())
						.getConstructor(String.class);
				debugger = (Listener) debuggerConstructor
						.newInstance(debugSettings);

				contextFactory.addListener(debugger);
				debugger.getClass().getMethod("start").invoke(debugger);
				debug = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Stops debugger instance associated with current ContextFactory.
	 */
	public void stopDebugger() {
		if (debugger != null) {
			try {
				getContextFactory().removeListener(debugger);
				debugger.getClass().getMethod("stop").invoke(debugger);
			} catch (Exception e) {
				e.printStackTrace();
			}
			debugger = null;
			debug = false;
		}
	}
}
