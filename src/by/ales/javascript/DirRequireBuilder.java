package by.ales.javascript;

import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

/**
 * Wraps {@link UrlModuleSourceProvider} and {@link SoftCachingModuleScriptProvider}
 * and creates {@link Require}, which will search modules in specified directories.
 * Default value for sandboxing is 'false'. 
 */
public class DirRequireBuilder extends RequireBuilder {

	private static final long serialVersionUID = 6028017679038725225L;
	private List<URI> lookupPaths = new LinkedList<URI>();
	private ModuleScriptProvider scriptProvider;
	
	public DirRequireBuilder() {
		setSandboxed(false);
	}
	
	public DirRequireBuilder(List<URI> lookupPaths) {
		this.lookupPaths = lookupPaths;
	}
	
	/**
	 * Adds JavaScript modules search directory.
	 * 
	 * @param path path to directory for searching modules
	 * @throws Exception if there is no such directory //TODO choose appropriate exception
	 */
	public void addJSDir(String path) throws Exception { //TODO choose appropriate exception
		URL jsDir = getClass().getResource(path);
		if (jsDir != null) {
			lookupPaths.add(jsDir.toURI());
		}
	}
	
	@Override
	public Require createRequire(Context ctx, Scriptable scope) {
		if (scriptProvider == null) {
			ModuleSourceProvider sourceProvider = 
					new UrlModuleSourceProvider(lookupPaths, null);
			scriptProvider = new SoftCachingModuleScriptProvider(sourceProvider);
			super.setModuleScriptProvider(scriptProvider);
		}
		return super.createRequire(ctx, scope);
	}

}
