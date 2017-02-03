package mil.nga.giat.geowave.core.cli.prefix;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.Assert;

public class JavassistUtilsTest {

//  @Test
//  public void testCloneAnnotationsAttribute() {
//      fail("not yet implemented");
//  }
//  
//  @Test
//  public void testFindMethod() {
//      fail("not yet implemented");
//  }
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
    
//  @Test 
//  public void testGetNextUniqueFieldName() {
//      fail("not done");
//  }
//  
//  @Test 
//  public void testGenerateEmptyClass() {
//      fail("not done");
//  }

}
