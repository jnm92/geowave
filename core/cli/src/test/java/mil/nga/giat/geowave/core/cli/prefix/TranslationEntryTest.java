package mil.nga.giat.geowave.core.cli.prefix;

import static org.junit.Assert.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameterized;
import com.beust.jcommander.WrappedParameter;

import junit.framework.Assert;

public class TranslationEntryTest {

	private static class TestClass {
		int field;
		public void method(){
			return;
		}
	}	

	static TranslationEntry entry;
	static Parameterized param;
	static String obj;
	static String prefix;
	static AnnotatedElement aElement; 

	@Parameter(names = "-a",description = "its a")
	private static Integer a;
	
	@Before
	public void setUp(){
		System.out.println("Started setup");
		
		try {
			
			param = new Parameterized(new WrappedParameter((Parameter)null),null,
						TestClass.class.getDeclaredField("field"),
						TestClass.class.getDeclaredMethod("method", null));
		} catch (NoSuchFieldException | SecurityException | NoSuchMethodException e) {
			// Should never trigger
			e.printStackTrace();
		}
		obj = "abc";
		prefix = "123";
		aElement = String.class;
		entry = new TranslationEntry(param, (Object) obj, prefix, aElement);
		System.out.println("Done setup");
	}

	@Test
	public void testGetParam() {
		Assert.assertEquals(param, entry.getParam());
	}
	
	@Test
	public void testGetObject(){
		Assert.assertEquals(obj, entry.getObject());
	}
	
	@Test
	public void testGetPrefix(){
		Assert.assertEquals(prefix, entry.getPrefix());
	}
	
	@Test
	public void testIsMethodFalse(){
		Assert.assertFalse(entry.isMethod());
	}
	
	@Test
	public void testIsMethodTrue(){
		fail("Not implemented");
	}
	
	@Test
	public void testGetMember(){
		Assert.assertEquals(aElement, entry.getMember());
	}
	
	@Test
	public void testGetPrefixedNames(){
		Assert.assertTrue(Arrays.asList(entry.getPrefixedNames()).contains(prefix));
	}
	
	@Test
	public void testGetDescription(){
		Assert.assertEquals("<no descriptions>", entry.getDescription());
	}
	
	@Test
	public void testIsPassword(){
		
	}
	
	
//
//	public boolean isPassword() {
//		if (getParam().getParameter() != null) {
//			return getParam().getParameter().password();
//		}
//		else if (getParam().getWrappedParameter() != null) {
//			return getParam().getWrappedParameter().password();
//		}
//		return false;
//	}
//
//	public boolean isHidden() {
//		if (getParam().getParameter() != null) {
//			return getParam().getParameter().hidden();
//		}
//		else if (getParam().getWrappedParameter() != null) {
//			return getParam().getWrappedParameter().hidden();
//		}
//		return false;
//	}
//
//	public boolean isRequired() {
//		if (getParam().getParameter() != null) {
//			return getParam().getParameter().required();
//		}
//		else if (getParam().getWrappedParameter() != null) {
//			return getParam().getWrappedParameter().required();
//		}
//		return false;
//	}
//
//	/**
//	 * Whether the given object has a value specified. If the current value is
//	 * non null, then return true.
//	 * 
//	 * @return
//	 */
//	public boolean hasValue() {
//		Object value = getParam().get(
//				getObject());
//		return value != null;
//	}
//
//	/**
//	 * Property name is used to write to properties files, but also to report
//	 * option names to Geoserver.
//	 * 
//	 * @return
//	 */
//	public String getAsPropertyName() {
//		return trimNonAlphabetic(getLongestParam(getPrefixedNames()));
//	}
//
}
