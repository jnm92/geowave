//package mil.nga.giat.geowave.core.cli.prefix;
//
//import static org.junit.Assert.*;
//
//import java.lang.reflect.AnnotatedElement;
//import java.lang.reflect.Method;
//
//import org.junit.Test;
//
//import com.beust.jcommander.Parameterized;
//
//public class TranslationEntryTest {
//	
//	private TranslationEntry createTranslationEntry(){
//		WrappedParameter wrapped = new WrappedParameter();
//		Parameterized param = new Parameterized(null, null, null, null);
//		return new TranslationEntry(null, null, null, null);
//	}
//
//	public Parameterized getParam() {
//		return param;
//	}
//
//	public Object getObject() {
//		return object;
//	}
//
//	public String getPrefix() {
//		return prefix;
//	}
//
//	public boolean isMethod() {
//		return member instanceof Method;
//	}
//
//	public AnnotatedElement getMember() {
//		return member;
//	}
//
//	public String[] getPrefixedNames() {
//		return prefixedNames;
//	}
//
//	public String getDescription() {
//		String description = null;
//		if (getParam().getParameter() != null && getParam().getParameter().description() != null) {
//			description = getParam().getParameter().description();
//		}
//		else if (getParam().isDynamicParameter()) {
//			description = getParam().getWrappedParameter().getDynamicParameter().description();
//		}
//		return description == null ? "<no description>" : description;
//	}
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
//}
