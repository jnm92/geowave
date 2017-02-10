package mil.nga.giat.geowave.core.cli.prefix;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import junit.framework.Assert;
import mil.nga.giat.geowave.core.cli.prefix.JavassistUtils;

public class JavassistUtilsTest {

  @Test
  public void testCloneAnnotationsAttribute() {
	  ConstPool constPool = new ConstPool("cloneAnnotationsAttrTest");
	  AnnotationsAttribute origAttr = new AnnotationsAttribute(constPool, "original");
      AnnotationsAttribute clonedAttr = JavassistUtils
    		  .cloneAnnotationsAttribute(constPool, origAttr, null);
      
      System.out.println(clonedAttr.getName());
  }
  
  private static class FindMethodTest {
	  public void doNothing() {
		  return;
	  }
  }
  
  @Test
  public void testFindMethod() {
	  
      CtClass ctclass = ClassPool.getDefault().makeClass("testFindMethodClass");
      CtMethod ctmethod;
      
      try {
		  ctmethod = CtNewMethod.make("public void doNothing() { return; }", ctclass);
		  ctclass.addMethod(ctmethod); // TODO do need to make and add?
		  
      } catch (CannotCompileException e) {
		  // Should never trigger since src attr is fixed
		  e.printStackTrace();
		  fail("Could not compile method");
	  }
      
      Method m = null;
      for (Method method : FindMethodTest.class.getMethods()){
    	  if (method.getName().equals("doNothing")){
    		  m = method;
    		  break;
    	  }
      }
      
      if (m == null){
    	  fail("Could not find method in Java class");
      }
      
      try {
		CtMethod foundMethod = JavassistUtils.findMethod(ctclass, m);
	} catch (NotFoundException e) {
		e.printStackTrace();
		fail("Could not find method in CtClass");
	}
      
      
  }
  
  @Test
  public void testCopyClassAnnontations() {
      CtClass class1 = ClassPool.getDefault().makeClass("testCopyClassAnnotations1");
      CtClass class2 = ClassPool.getDefault().makeClass("testCopyClassAnnotations2");
      
      ClassFile class1file = class1.getClassFile();
      ConstPool class1pool = class1file.getConstPool();
      
      AnnotationsAttribute attribute = new AnnotationsAttribute(class1pool, AnnotationsAttribute.visibleTag);
      attribute.addAnnotation(new Annotation("test1", class1pool));
      
      class1file.addAttribute(attribute);
      
      JavassistUtils.copyClassAnnotations(class1, class2);
      
      // For some reason, getClassFile2 gets a read-only copy of the class file
      ClassFile class2file = class2.getClassFile2();
      AnnotationsAttribute classAnnotations = (AnnotationsAttribute) class2file.getAttribute(AnnotationsAttribute.visibleTag);
      
      // TODO might have to extract annotations or something
      Assert.assertEquals(attribute, classAnnotations);
  }
  
  class TestClass {
	  int field1;
	  String field2;
	  
	  public void doNothing(){
		  return;
	  }
  }
  
  @Test
  public void testCopyMethodAnnotationsToField() {
	  
	  CtClass ctclass;
	  CtMethod ctmethod = null;
	  CtField ctfield1 = null, ctfield2 = null;
	  
	  try {
		  ctclass = ClassPool.getDefault().get("mil.nga.giat.geowave.core.cli.prefix.TestClass");
		  ClassFile ctfile = ctclass.getClassFile();
		  ConstPool ctpool = ctfile.getConstPool();
		  ctfield1 = ctclass.getField("field1");
		  ctfield2 = ctclass.getField("field2");
		  ctmethod = ctclass.getDeclaredMethod("doNothing");
	  } catch (NotFoundException e) {
		  e.printStackTrace();
	  }

	  
	  ctmethod.getMethodInfo().addAttribute(new AnnotationsAttribute(null, "method annotation"));
	  
	  JavassistUtils.copyMethodAnnotationsToField(ctmethod, ctfield1);
  }
    
    @Test
    public void testGetNextUniqueClassName() {
        String unique1 = JavassistUtils.getNextUniqueClassName();
        String unique2 = JavassistUtils.getNextUniqueClassName();
        
        Assert.assertFalse(unique1.equals(unique2));
    }
    
  @Test 
  public void testGetNextUniqueFieldName() {
      String unique1 = JavassistUtils.getNextUniqueFieldName();
      String unique2 = JavassistUtils.getNextUniqueFieldName();
      
      Assert.assertFalse(unique1.equals(unique2));
  }
  
  @Test 
  public void testGenerateEmptyClass() {
      CtClass emptyClass = JavassistUtils.generateEmptyClass();
      CtClass anotherEmptyClass = JavassistUtils.generateEmptyClass();
      
      Assert.assertFalse(emptyClass.equals(anotherEmptyClass));
  }

}
