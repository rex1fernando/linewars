package linewars.gamestate.playerabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import linewars.gamestate.Player;
import linewars.gamestate.Position;
import configuration.Configuration;
import configuration.Usage;
import editor.ConfigurationEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public abstract class PlayerAbility extends Configuration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4214386428248985305L;
	private static Map<String, Class<? extends ConfigurationEditor>> abilityEditors;
	private static Map<String, Class<? extends PlayerAbility>> abilityConfigs;
	
	static {
		abilityConfigs = new HashMap<String, Class<? extends PlayerAbility>>();
		abilityEditors = new HashMap<String, Class<? extends ConfigurationEditor>>();
	}
	
	public static void setAbilityConfigMapping(String name, Class<? extends PlayerAbility> config, Class<? extends ConfigurationEditor> editor){
		abilityConfigs.put(name, config);
		abilityEditors.put(name, editor);
	}
	
	public static Class<? extends PlayerAbility> getConfig(String name) {
		return abilityConfigs.get(name);
	}
	
	public static Class<? extends ConfigurationEditor> getEditor(String name) {
		return abilityEditors.get(name);
	}
	
	public static Set<String> getAbilityNameSet() {
		return abilityConfigs.keySet();
	}
	
	public PlayerAbility()
	{
		super.setPropertyForName("cost", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The player energy cost of this player ability"));
		super.setPropertyForName("tooltip", new EditorProperty(Usage.STRING,
				"", EditorUsage.text, "The tooltip for this player ability"));
		super.setPropertyForName("icons", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.IconConfig, "The icons to dipslay for this player ability"));
	}
	
	public double getEnergyCost()
	{
		return (Double)super.getPropertyForName("cost").getValue();
	}
	
	public String getTooltip()
	{
		return (String)super.getPropertyForName("tooltip").getValue();
	}
	
	public Configuration getIconConfiguration()
	{
		return (Configuration)super.getPropertyForName("icons").getValue();
	}
	
	/**
	 * Returns true if this ability requires a valid map position
	 * to activate (e.g. a spot to nuke). False if not.
	 * 
	 * @return
	 */
	public abstract boolean requiresPosition();
	
	/**
	 * Applies this player ability at position p. If requiresPosition
	 * returns false, then ignores p.
	 * 
	 * @param p
	 */
	public abstract void apply(Position p, Player player);
	
	public abstract boolean equals(Object obj);

}
