package by.ales.javascript;

import java.io.FileNotFoundException;
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
	 * Convenient constructor creating builder with list of searching 
	 * directories.
	 * <br>
	 * Swallows exceptions if errors occur
	 * 
	 * @param jsDir
	 *            path to directory according to
	 *            {@link Class#getResource(String)} which may not contain
	 *            trailing '/', it will be appended
	 */
	public DirRequireBuilder(String... jsDirs) {
		this();
		for (String jsDir : jsDirs) {
		    try {
			addJSDir(jsDir);
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
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
	 * @return this (fluent interface)
	 * @throws IOException - If I/O errors occur
	 */
	public DirRequireBuilder addJSDir(String path) throws IOException {
		path = (!path.endsWith("/")) ? (path + "/") : (path);
		
		Enumeration<URL> dirs;
		int foundDirs = 0;
		dirs = getClass().getClassLoader().getResources(path);
		
		while(dirs.hasMoreElements()) {
			URL jsDir = dirs.nextElement();
			try {
				lookupPaths.add(jsDir.toURI());
				++foundDirs;
			} catch (URISyntaxException e) {
				e.printStackTrace(); //won't be reached
			}
		}
		if (foundDirs == 0) {
		    throw new FileNotFoundException(
			    "There is no any directory '" + path + "' in classpath");
		}
		return this;
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
