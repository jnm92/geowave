package mil.nga.giat.geowave.core.cli.prefix;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import com.beust.jcommander.JCommander;

import mil.nga.giat.geowave.core.cli.prefix.PrefixedJCommander.PrefixedJCommanderInitializer;

public class PrefixedJCommanderTest {
	
	private PrefixedJCommander preparePrefixedJCommander(){
		PrefixedJCommander commander = new PrefixedJCommander();
		commander.addPrefixedObject((Object) "howdy, there");
		commander.addPrefixedObject((Object) "hello there");
		return commander;
	}
    
    @Test
    public void testAddCommand(){
    	PrefixedJCommander prefixedJCommander = preparePrefixedJCommander();
    	
    	prefixedJCommander.addCommand("--abc",(Object) "hello, world","a");
    	prefixedJCommander.parse("--abc", "5");
    	System.out.println(prefixedJCommander.getCommands().get("abc")); // nonnull
    	for (Object obj : prefixedJCommander.getPrefixedObjects()){
    		System.out.println(obj);
    	}
    }
    
    private static class ConcretePrefixedJCommanderInitializer implements PrefixedJCommanderInitializer {
    	
    	JCommanderTranslationMap map;
    	
    	public ConcretePrefixedJCommanderInitializer(JCommanderTranslationMap map){
    		this.map = map;
    	}

		@Override
		public void initialize(PrefixedJCommander commander) {
			map.createFacadeObjects();
			for (Object obj : map.getObjects()){
				commander.addObject(obj);
			}
			
		}
    	
    }
    
//    @Test
//    public void testParse() {
//        fail("not implemented");
//    }
//    
//    @Test
//    public void testParseWithoutValidation() {
//        fail("not implemented");
//    }
//    
//    @Test
//    public void testSetDefaultProvider() {
//        fail("not implemented");
//    }
//    
//    @Test
//    public void testSetAcceptUnknownOptions() {
//        fail("not implemented");
//    }
//    
//    

}
