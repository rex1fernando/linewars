package linewars2.configfilehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Represents a body of configuration data which defines an entity or set of entities.
 * 
 * @author Knexer
 *
 */
public class ConfigData {
	
	private HashMap<ParserKeys, Value> map = new HashMap<ParserKeys, Value>();
	private String URI;
	private int startLine = -1;
	private int endLine;
	
	ConfigData(String URI, int startLine)
	{
		this.URI = URI;
		this.startLine = startLine;
	}
	
	void setEndLine(int end)
	{
		endLine = end;
	}
	
	/**
	 * Returns a list of all of the keys which are defined within this body of data.
	 * @return
	 * A list of all defined keys.
	 */
	public List<ParserKeys> getDefinedKeys(){
		List<ParserKeys> keys = new ArrayList<ParserKeys>();
		Set<Entry<ParserKeys, Value>> set = map.entrySet();
		for(Entry<ParserKeys, Value> e : set)
			keys.add(e.getKey());
		
		return keys;
	}
	
	/**
	 * Returns a list of all of the ways in which the values mapped to a given key can be interpreted.
	 * @param key
	 * The key to check
	 * @return
	 * A list of all of the ways in which the values mapped to a given key can be interpreted.
	 */
	public List<valueType> getValidTypes(ParserKeys key){
		checkKey(key);
		List<valueType> list = new ArrayList<valueType>();
		Value v = map.get(key);
		
		//have to check each one individually, no other way
		//first check String
		if(this.getString(key) != null)
		{
			list.add(valueType.String);
			list.add(valueType.StringList);
		}
		if(this.getNumber(key) != null)
		{
			list.add(valueType.Number);
			list.add(valueType.NumberList);
		}
		if(this.getConfig(key) != null)
		{
			list.add(valueType.Config);
			list.add(valueType.ConfigList);
		}
		return list;
	}
	
	public String getString(ParserKeys key)
	{
		checkKey(key);
		Value v = map.get(key);
		if(v.getStringList().size() != 1)
			return null;
		else
			return v.getStringList().get(0);
	}
	
	public List<String> getStringList(ParserKeys key)
	{
		checkKey(key);
		return new ArrayList<String>(map.get(key).getStringList());
	}
	
	public Double getNumber(ParserKeys key)
	{
		checkKey(key);
		Value v = map.get(key);
		if(v.getNumberList().size() != 1)
			return null;
		else
			return v.getNumberList().get(0);
	}
	
	public List<Double> getNumberList(ParserKeys key)
	{
		checkKey(key);
		return new ArrayList<Double>(map.get(key).getNumberList());
	}
	
	public ConfigData getConfig(ParserKeys key)
	{
		checkKey(key);
		Value v = map.get(key);
		if(v.getConfigList().size() != 1)
			return null;
		else
			return v.getConfigList().get(0);
	}
	
	public List<ConfigData> getConfigList(ParserKeys key)
	{
		checkKey(key);
		return new ArrayList<ConfigData>(map.get(key).getConfigList());
	}
	
	
	
	/**
	 * Maps the given value to the given key
	 * @param key
	 * The key to which the new value is to be mapped.
	 * @param newValue
	 * The data to be mapped.
	 */
	public void set(ParserKeys key, Object newValue){
		
	}
	
	/**
	 * Adds the given value to the given key
	 * @param key
	 * The key to which the given value is to be mapped
	 * @param toAdd
	 * The data which is to be added to the given key
	 */
	public void add(ParserKeys key, Object toAdd){
		
	}
	
	/**
	 * Removes the given value from the list of values mapped to the given key.
	 * @param key
	 * The key which currently maps to the given value.
	 * @param toRemove
	 * The value to be removed from the list.
	 * @return
	 * True iff the value was found and removed.
	 */
	public boolean remove(ParserKeys key, Object toRemove){
		return false;
	}
	
	private void checkKey(ParserKeys key)
	{
		if(!map.containsKey(key))
			if(startLine >= 0)
				throw new NoSuchKeyException("The key \"" + key + "\" is not contained in the config file " + URI + 
					" from line " + startLine + " to " + endLine);
			else
				throw new NoSuchKeyException("The key \"" + key + "\" is not contained in the config data.");
	}
	
	public enum valueType{
		String, Number, Config, StringList, NumberList, ConfigList;
	}
	
	private class Value{
		private List<String> strings = new ArrayList<String>();
		private List<ConfigData> configs = new ArrayList<ConfigData>();
		
		public void add(ConfigData cd)
		{
			configs.add(cd);
		}
		
		public void add(String s)
		{
			strings.add(s);
		}
		
		public void add(Double d)
		{
			this.add("" + d);
		}
		
		public List<String> getStringList()
		{
			return strings;
		}
		
		public List<ConfigData> getConfigList()
		{
			return configs;
		}
		
		public List<Double> getNumberList()
		{
			List<Double> list = new ArrayList<Double>();
			for(String s : strings)
			{
				try{
					Double d = Double.valueOf(s);
					list.add(d);
				} catch (NumberFormatException e) {
					
				}
			}
			return list;
		}
	}
	
	public static class NoSuchKeyException extends RuntimeException {
		private static final long serialVersionUID = -3402614354747155750L;

		public NoSuchKeyException(String s) {
			super(s);
		}
	}

}
