package by.ales.javascript;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

public class DirRequireBuilderTest {

	DirRequireBuilder builder;
	
	@Before
	public void setUp() {
		builder = new DirRequireBuilder();
	}
	
	@Test
	public void addRelativeJSDir() throws IOException {
		String dirPath = "js/rhino_executor/";
		builder.addJSDir(dirPath);
		
		URI dirInLookup = builder.getLookupPaths().get(
				builder.getLookupPaths().size() - 1);
		URI dir = null;
		try {
			dir = getClass().getClassLoader().getResource(dirPath).toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		assertEquals(dir, dirInLookup);
	}
	
	@Test
	public void addsBackslashToJSDirIfNeeded() throws IOException {
		builder.addJSDir("js/rhino_executor");
		builder.addJSDir("js/rhino_executor/");
		assertEquals(builder.getLookupPaths().get(0), builder.getLookupPaths().get(1));
	}
	
	@Test(expected = IOException.class)
	public void notAddsDirIfItNotExists() throws IOException {
		builder.addJSDir("js/not_existed_dir");
		
		assertEquals(0, builder.getLookupPaths().size());
	}
	
	@Test
	public void addsDirIfItInList() throws IOException {
		DirRequireBuilder builder = new DirRequireBuilder().
		  addJSDir("js/rhino_executor").
		  addJSDir("js/rhino_executor");		
		
		assertEquals(2, builder.getLookupPaths().size());
	}
	
	@Test
	public void addsAllDirsFromClassPathEntries() throws IOException {
		builder.addJSDir("js"); //js dirs in executor and tests
		assertEquals(true, builder.getLookupPaths().size() > 1); 
	}
	
}
