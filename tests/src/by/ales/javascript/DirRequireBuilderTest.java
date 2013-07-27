package by.ales.javascript;

import static org.junit.Assert.assertEquals;

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
	public void addRelativeJSDir() {
		String dirPath = "/js/rhino_executor/";
		boolean added = builder.addJSDir(dirPath);
		assertEquals(true, added);
		
		URI dirInLookup = builder.getLookupPaths().get(0);
		URI dir = null;
		try {
			dir = getClass().getResource(dirPath).toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		assertEquals(dir, dirInLookup);
	}
	
	@Test
	public void addsBackslashToJSDirIfNeeded() {
		builder.addJSDir("/js/rhino_executor");
		builder.addJSDir("/js/rhino_executor/");
		assertEquals(builder.getLookupPaths().get(0), builder.getLookupPaths().get(1));
	}
	
	@Test
	public void notAddsDirIfItNotExists() {
		boolean added = builder.addJSDir("/js/not_existed_dir");
		assertEquals(false, added);
		assertEquals(0, builder.getLookupPaths().size());
	}
	
	@Test
	public void addsDirIfItInList() {
		builder.addJSDir("/js/");
		boolean added = builder.addJSDir("/js");		
		
		assertEquals(true, added);
		assertEquals(2, builder.getLookupPaths().size());
	}
	
}
