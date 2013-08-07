package by.ales.javascript;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
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
	 * Adds searching directory for JavaScript modules. Note, that directory 
	 * will be searched in classpath (and JARs) - if there is more than one 
	 * directory in classpath entries then all of them will be added.
	 * <br>
	 * For example, in JARs 'foo.jar' and 'bar.jar' you want scan directories 
	 * 'js/modules' for your modules. Then you can do it by
	 * <br>
	 * <pre>
	 * 	builder.addJSDir("js/modules"); //lookupPaths will contain 2 entries
	 * </pre>
	 * 
	 * @param path
	 *            path relative to classpath according to 
	 *            {@link ClassLoader#getResource(String)} which may or not 
	 *            contain trailing '/' - it will be appended to denote directory
	 * @return true if any directory exists and was added to paths
	 * 
	 * <br>TODO change return type and throw exception on problems
	 */
	public boolean addJSDir(String path) {
		int foundDirs = 0;

		path = (!path.endsWith("/")) ? (path + "/") : (path);
		
		Enumeration<URL> dirs;
		try {
			dirs = getClass().getClassLoader().getResources(path);
		} catch (IOException e) {
			return false;
		}
			
		while(dirs.hasMoreElements()) {
			URL jsDir = dirs.nextElement();
			try {
				lookupPaths.add(jsDir.toURI());
				++foundDirs;
			} catch (URISyntaxException e) {
				e.printStackTrace(); //won't be reached
			}
		}
		return foundDirs > 0;
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
