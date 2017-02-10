package mil.nga.giat.geowave.core.cli.prefix;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import junit.framework.Assert;

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
//  
//  @Test
//  public void testCopyClassAnnontations() {
//      fail("not yet implemented");
//  }
//  
//  @Test
//  public void testCopyMethodAnnotationsToField() {
//      fail("not yet impl");
//  }
    
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
