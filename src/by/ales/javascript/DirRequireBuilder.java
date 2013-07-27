package by.ales.javascript;

import java.net.URI;
import java.net.URISyntaxException;
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
 * <br>
 * Note, default value for Require.sandboxed is 'false'. 
 */
public class DirRequireBuilder extends RequireBuilder {

	private static final long serialVersionUID = 6028017679038725225L;
	private List<URI> lookupPaths = new LinkedList<URI>();
	private ModuleScriptProvider scriptProvider;
	
	/**
	 * Creates empty builder with no paths
	 */
	public DirRequireBuilder() {
		setSandboxed(false);
	}
	
	/**
	 * Creates builder with one searh directory
	 * 
	 * @param jsDir
	 *            path to directory according to
	 *            {@link Class#getResource(String)} which may not contain
	 *            trailing '/', it will be appended
	 */
	public DirRequireBuilder(String jsDir) {
		this();
		addJSDir(jsDir);
	}
	
	public DirRequireBuilder(List<URI> lookupPaths) {
		this();
		this.lookupPaths = lookupPaths;
	}
	
	public List<URI> getLookupPaths() {
		return lookupPaths;
	}
	
	/**
	 * Adds JavaScript modules search directory.
	 * 
	 * @param path
	 *            path to directory according to 
	 *            {@link Class#getResource(String)} which may not contain
	 *            trailing '/', it will be appended
	 * @return true if directory exists and was added to paths
	 */
	public boolean addJSDir(String path) {
		URL jsDir = getClass().getResource(
				(path.endsWith("/") || path.endsWith("\\")) ? (path) : (path + "/"));
		boolean result = false;
		if (jsDir != null) {
			try {
				lookupPaths.add(jsDir.toURI());
				result = true;
			} catch (URISyntaxException e) {}
		}
		return result;
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
