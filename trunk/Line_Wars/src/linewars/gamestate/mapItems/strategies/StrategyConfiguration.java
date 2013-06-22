package linewars.gamestate.mapItems.strategies;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import linewars.gamestate.mapItems.MapItem;
import configuration.Configuration;
import editor.ConfigurationEditor;

public abstract class StrategyConfiguration<T> extends Configuration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3670824416568146710L;
	private static Map<String, Class<? extends ConfigurationEditor>> strategyEditors;
	private static Map<String, Class<? extends StrategyConfiguration<?>>> strategyConfigs;
	private static Map<String, Class<? extends StrategyConfiguration<?>>> strategyTypes;
	
	static {
		strategyConfigs = new HashMap<String, Class<? extends StrategyConfiguration<?>>>();
		strategyEditors = new HashMap<String, Class<? extends ConfigurationEditor>>();
		strategyTypes = new HashMap<String, Class<? extends StrategyConfiguration<?>>>();
	}
	
	public static void setStrategyConfigMapping(String name, Class<? extends StrategyConfiguration<?>> config, Class<? extends ConfigurationEditor> editor){
		strategyConfigs.put(name, config);
		strategyEditors.put(name, editor);
	}
	
	public static void setStrategyType(String name, Class<? extends StrategyConfiguration<?>> config) {
		strategyTypes.put(name, config);
	}
	
	public static Class<? extends StrategyConfiguration<?>> getConfig(String name) {
		return strategyConfigs.get(name);
	}
	
	public static Class<? extends ConfigurationEditor> getEditor(String name) {
		return strategyEditors.get(name);
	}
	
	public static Class<? extends StrategyConfiguration<?>> getConfigType(String name) {
		return strategyTypes.get(name);
	}
	
	public static Set<String> getStrategyNameSet() {
		return strategyConfigs.keySet();
	}
	
	public static Set<String> getStrategyTypeNameSet() {
		return strategyTypes.keySet();
	}
	
	public abstract T createStrategy(MapItem m);
	
	public abstract boolean equals(Object obj);

}
