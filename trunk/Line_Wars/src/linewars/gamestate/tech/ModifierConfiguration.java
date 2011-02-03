package linewars.gamestate.tech;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import linewars.gamestate.shapes.Shape;

import configuration.Configuration;
import configuration.Property;
import configuration.Usage;

public abstract class ModifierConfiguration extends Configuration {
	
	private static HashMap<Usage, List<Class<? extends ModifierConfiguration>>> validModifiersForUsage;
	
	static{
		try {
			List<Class> classes = getClasses(Shape.class.getPackage().getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void addModifierForUsage(Usage key, Class<? extends ModifierConfiguration> value){
		if(validModifiersForUsage == null){
			validModifiersForUsage = new HashMap<Usage, List<Class<? extends ModifierConfiguration>>>();
		}
		if(validModifiersForUsage.get(key) == null){
			validModifiersForUsage.put(key, new ArrayList<Class<? extends ModifierConfiguration>>());
		}
		validModifiersForUsage.get(key).add(value);
	}
	
	public static List<Class<? extends ModifierConfiguration>> getModifiersForUsage(Usage key){
		return validModifiersForUsage.get(key);
	}

	public abstract Property applyTo(Property toModify);
	//do shapes-like static init stuff
}
