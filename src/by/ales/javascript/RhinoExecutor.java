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
 * Use method {@link RhinoExecutor#execute(String, Map)} to evaluate module by ID.
 * All objects in map will be put into global scope.
 * <br>
 * Executor can be customized by passing {@link ContextFactory} (e.g.
 * sandboxed factory) and {@link RequireBuilder} (e.g. with custom
 * {@link ModuleScriptProvider}).
 * <br>
 * !!! Not threadsafe !!!
 * <br>
 * Executor instance can use JSDT Rhino Debugger 
 * {@linkplain http://wiki.eclipse.org/JSDT/Debug/Rhino}
 * To use scripts debug:
 * <p>1. Put jars for debugging to classpath:</p>
 *   <ul>
 *     <li>org.eclipse.wst.jsdt.debug.rhino.debugger</li>
 *	   <li>org.eclipse.wst.jsdt.debug.transport</li>
 *	   <li>* they can be found at Eclipse IDE home when JSDT for Rhino installed</li>
 *   </ul>
 * <p>2. Use method {@link RhinoExecutor#startDebugger(String)} 
 * with appropriate settings</p>
 * <p>3. Start remote Rhino debugger (usually from Eclipse IDE)</p>
 * <p>4. Enjoy</p>
 * <p>* Tip: use 'Add script load breakpoint' at first usage to bind sources 
 * to debugger</p>
 */
public class RhinoExecutor {

	private ContextFactory contextFactory;
	private RequireBuilder requireBuilder;
	private Listener debugger;
	private boolean debugging;
	private String debugSettings;
		
	/**
	 * Creates instance using {@link ContextFactory}
	 * 
	 * @param requireBuilder
	 *            object which builds {@link Require} instances
	 */
	public RhinoExecutor(RequireBuilder requireBuilder) {
		this(new ContextFactory(), requireBuilder);
	}

	public RhinoExecutor(ContextFactory ctxFactory, RequireBuilder requireBuilder) {
		this.contextFactory = ctxFactory;
		this.requireBuilder = requireBuilder;
	}
	
	public ContextFactory getContextFactory() {
		return contextFactory;
	}
	
	/**
	 * Sets {@link ContextFactory} which will be used to create contexts. If
	 * debugger is used, then {@link RhinoExecutor#stopDebugger()} method should
	 * be called first to detach debugger from current factory.
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
	 * @param requireBuilder
	 *            builder which be called every call of
	 *            {@link RhinoExecutor#execute(String, Map)}
	 */
	public void setRequireBuilder(RequireBuilder requireBuilder) {
		this.requireBuilder = requireBuilder;
	}

	/**
	 * Executes module in it's own scope. Similar to execute("moduleId", null).
	 * 
	 * @param moduleId
	 *            id of module according to CommonJS.module spec
	 */
	public Object execute(String moduleId) {
		return execute(moduleId, null);
	}

	/**
	 * Executes module in it's own scope using additional Java objects in global
	 * scope.
	 * <br>
	 * Note, if require is sandboxed and provided module id isn't present then
	 * this method do nothing in Rhino-1.7R4, possibly due bug.
	 * 
	 * @param moduleId
	 *            id of module according to CommonJS.module spec
	 * @param javaScope
	 *            map of objects which will be put into global scope (will be
	 *            accessible in all modules, required by executed one)
	 */
	public Object execute(String moduleId,
			Map<String, ? extends Object> javaScope) {
		Context ctx = contextFactory.enterContext();
		try {
			ScriptableObject scope = ctx.initStandardObjects();
			if (javaScope != null) {
				for (Map.Entry<String, ? extends Object> entry : javaScope
						.entrySet()) {
					scope.put(entry.getKey(), scope, entry.getValue());
				}
			}

			Require require = requireBuilder.createRequire(ctx, scope);

			// Sandboxed Require.requireMain in Rhino 1.7R4 method does nothing
			// when there is no module with module id (it may be bug).
			// Then may be better to  use Require.call to execute module 
			// in module's scope:
			// return require.call(ctx, scope, null, new String[]{moduleId});
			// But it breaks 'require.main == module' contract
			
			return require.requireMain(ctx, moduleId);
		} finally {
			Context.exit();
		}
	}

	/**
	 * @return true if debugger started
	 */
	public boolean isDebugging() {
		return debugging;
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
	 * if it's accessible in classpath. See page on wiki 
	 * {@linkplain http://wiki.eclipse.org/JSDT/Debug/Embedding_Rhino_Debugger}
	 * 
	 * @param debugSettings
	 *            debug settings for JSDT Rhino Debugger
	 * @return true if debugger is started
	 */
	public boolean startDebugger(String debugSettings) {
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
				debugging = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return debugging;
	}
	
	/**
	 * Stops debugger instance associated with current ContextFactory.
	 * 
	 * @return true if debugger is stopped
	 */
	public boolean stopDebugger() {
		if (debugger != null) {
			try {
				getContextFactory().removeListener(debugger);
				debugger.getClass().getMethod("stop").invoke(debugger);
			} catch (Exception e) {
				e.printStackTrace();
			}
			debugger = null;
			debugging = false;
		}
		return !debugging;
	}
}
