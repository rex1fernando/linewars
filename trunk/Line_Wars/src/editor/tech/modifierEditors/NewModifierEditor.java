package editor.tech.modifierEditors;

import java.util.HashMap;
import java.util.Map;

import configuration.Property;

import linewars.gamestate.tech.ModifierConfiguration;
import utility.ForceLoadPackage;
import editor.ConfigurationEditor;

public abstract class NewModifierEditor implements ConfigurationEditor {	

	public abstract ModifierConfiguration getData();
	
	private static Map<Class<? extends ModifierConfiguration>, Class<? extends NewModifierEditor>> editorForModifier;
	
	static{
		editorForModifier = new HashMap<Class<? extends ModifierConfiguration>, Class<? extends NewModifierEditor>>();
		ForceLoadPackage.forceLoadClassesInPackage(NewModifierEditor.class.getPackage());
	}
	
	public static void setEditorForModifier(Class<? extends ModifierConfiguration> key, Class<? extends NewModifierEditor> toAdd){
		if(editorForModifier.get(key) != null){
			throw new IllegalStateException("An editor already exists for the Modifier " + key);
		}
		editorForModifier.put(key, toAdd);
	}
	
	public static Class<? extends NewModifierEditor> getEditorForModifier(Class<? extends ModifierConfiguration> key){
		return editorForModifier.get(key);
	}
}
