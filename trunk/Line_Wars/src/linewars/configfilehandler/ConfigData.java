package linewars.configfilehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Represents a body of configuration data which defines an entity or set of entities.
 * 
 * @author Connor Schenck
 *
 */
public class ConfigData {
	
	/**
	 * Takes in a string and returns a ParserKeys object that has the
	 * same value as the string. Ignores case.
	 * 
	 * @param s	the string value
	 * @return	the parser key with s as its string value
	 */
	public static ParserKeys getKey(String s)
	{
		for(ParserKeys key : ParserKeys.values())
			if(key.toString().toLowerCase().equals(s.toLowerCase()))
				return key;
		throw new IllegalArgumentException(s + " is not a key you retard.");
	}
	
	private HashMap<String, Value> map = new HashMap<String, Value>();
	private String URI;
	private int startLine = -1;
	private int endLine;
	
	ConfigData(String URI, int startLine)
	{
		this.URI = URI;
		this.startLine = startLine;
	}
	
	/**
	 * Constructs an empty config data object
	 */
	public ConfigData()
	{
		URI = "";
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
		Set<Entry<String, Value>> set = map.entrySet();
		for(Entry<String, Value> e : set)
		{
			keys.add(ConfigData.getKey(e.getKey()));
		}
		
		return keys;
	}
	
	/**
	 * Returns a list of all of the ways in which the values mapped to a given key can be interpreted.
	 * @param key
	 * The key to check
	 * @return
	 * A list of all of the ways in which the values mapped to a given key can be interpreted.
	 */
	public List<valueType> getValidTypes(ParserKeys k){
		checkKey(k);
		List<valueType> list = new ArrayList<valueType>();
		
		//have to check each one individually, no other way
		//first check String
		if(this.getString(k) != null)
		{
			list.add(valueType.String);
			list.add(valueType.StringList);
		}
		else if(this.getStringList(k).size() > 0)
			list.add(valueType.StringList);
		if(this.getNumber(k) != null)
		{
			list.add(valueType.Number);
			list.add(valueType.NumberList);
		}

		else if(this.getNumberList(k).size() > 0)
			list.add(valueType.NumberList);
		if(this.getConfig(k) != null)
		{
			list.add(valueType.Config);
			list.add(valueType.ConfigList);
		}

		else if(this.getConfigList(k).size() > 0)
			list.add(valueType.ConfigList);
		return list;
	}
	
	/**
	 * Gets the string associated with the key k. If there is no
	 * single string associated with the key k, then it returns
	 * null (ie if there are multiple or no strings associated with
	 * k). If k is not defined in the config, then this method
	 * throws an exception.
	 * 
	 * @param k	the key too look up	
	 * @return	the string associated with k
	 */
	public String getString(ParserKeys k)
	{
		checkKey(k);
		String key = k.toString().toLowerCase();
		Value v = map.get(key);
		if(v.getStringList().size() != 1)
			return null;
		else
			return v.getStringList().get(0);
	}
	
	/**
	 * Returns a list of strings associated with k. If there are no strings
	 * associated with k, the list is empty. Never returns null. If k
	 * is not defined, then this method throws an exception.
	 * 
	 * @param k	the key to look up
	 * @return	the list of strings associated with k
	 */
	public List<String> getStringList(ParserKeys k)
	{
		checkKey(k);
		String key = k.toString().toLowerCase();
		return new ArrayList<String>(map.get(key).getStringList());
	}
	
	/**
	 * Gets the number associated with the key k. If there is no
	 * single number associated with the key k, then it returns
	 * null (ie if there are multiple or no numbers associated with
	 * k). If k is not defined in the config, then this method
	 * throws an exception.
	 * 
	 * @param k	the key too look up	
	 * @return	the number associated with k
	 */
	public Double getNumber(ParserKeys k)
	{
		checkKey(k);
		String key = k.toString().toLowerCase();
		Value v = map.get(key);
		if(v.getNumberList().size() != 1)
			return null;
		else
			return v.getNumberList().get(0);
	}
	
	/**
	 * Returns a list of numbers associated with k. If there are no numbers
	 * associated with k, the list is empty. Never returns null. If k
	 * is not defined, then this method throws an exception.
	 * 
	 * @param k	the key to look up
	 * @return	the list of numbers associated with k
	 */
	public List<Double> getNumberList(ParserKeys k)
	{
		checkKey(k);
		String key = k.toString().toLowerCase();
		return new ArrayList<Double>(map.get(key).getNumberList());
	}
	
	/**
	 * Gets the config associated with the key k. If there is no
	 * single config associated with the key k, then it returns
	 * null (ie if there are multiple or no configs associated with
	 * k). If k is not defined in the config, then this method
	 * throws an exception.
	 * 
	 * @param k	the key too look up	
	 * @return	the config associated with k
	 */
	public ConfigData getConfig(ParserKeys k)
	{
		checkKey(k);
		String key = k.toString().toLowerCase();
		Value v = map.get(key);
		if(v.getConfigList().size() != 1)
			return null;
		else
			return v.getConfigList().get(0);
	}
	
	/**
	 * Returns a list of configs associated with k. If there are no configs
	 * associated with k, the list is empty. Never returns null. If k
	 * is not defined, then this method throws an exception.
	 * 
	 * @param k	the key to look up
	 * @return	the list of configs associated with k
	 */
	public List<ConfigData> getConfigList(ParserKeys k)
	{
		checkKey(k);
		String key = k.toString().toLowerCase();
		return new ArrayList<ConfigData>(map.get(key).getConfigList());
	}
	
	
	/**
	 * Sets the value that k is associated with in this config to newValue.
	 * If k is already associated with anything else, this method DELETES 
	 * the values it is associated with. If you want to append more data
	 * to associate with this key and retain what it already is associated
	 * with, then use add instead.
	 * 
	 * @param k			the key to associate the new value with
	 * @param newValue	the value to set k to
	 */
	public void set(ParserKeys k, String newValue){
		String key = k.toString().toLowerCase();
		if(map.containsKey(key))
			map.remove(key);
		Value v = new Value();
		v.add(newValue);
		map.put(key, v);
	}
	
	/**
	 * Sets the value that k is associated with in this config to newValue.
	 * If k is already associated with anything else, this method DELETES 
	 * the values it is associated with. If you want to append more data
	 * to associate with this key and retain what it already is associated
	 * with, then use add instead.
	 * 
	 * @param k			the key to associate the new value with
	 * @param newValue	the value to set k to
	 */
	public void set(ParserKeys k, String[] newValue){
		String key = k.toString().toLowerCase();
		if(map.containsKey(key))
			map.remove(key);
		Value v = new Value();
		for(String s : newValue)
			v.add(s);
		map.put(key, v);
	}
	
	/**
	 * Sets the value that k is associated with in this config to newValue.
	 * If k is already associated with anything else, this method DELETES 
	 * the values it is associated with. If you want to append more data
	 * to associate with this key and retain what it already is associated
	 * with, then use add instead.
	 * 
	 * @param k			the key to associate the new value with
	 * @param newValue	the value to set k to
	 */
	public void set(ParserKeys k, Double newValue){
		String key = k.toString().toLowerCase();
		if(map.containsKey(key))
			map.remove(key);
		Value v = new Value();
		v.add(newValue);
		map.put(key, v);
	}
	
	/**
	 * Sets the value that k is associated with in this config to newValue.
	 * If k is already associated with anything else, this method DELETES 
	 * the values it is associated with. If you want to append more data
	 * to associate with this key and retain what it already is associated
	 * with, then use add instead.
	 * 
	 * @param k			the key to associate the new value with
	 * @param newValue	the value to set k to
	 */
	public void set(ParserKeys k, Double[] newValue){
		String key = k.toString().toLowerCase();
		if(map.containsKey(key))
			map.remove(key);
		Value v = new Value();
		for(Double s : newValue)
			v.add(s);
		map.put(key, v);
	}
	
	/**
	 * Sets the value that k is associated with in this config to newValue.
	 * If k is already associated with anything else, this method DELETES 
	 * the values it is associated with. If you want to append more data
	 * to associate with this key and retain what it already is associated
	 * with, then use add instead.
	 * 
	 * @param k			the key to associate the new value with
	 * @param newValue	the value to set k to
	 */
	public void set(ParserKeys k, ConfigData newValue){
		String key = k.toString().toLowerCase();
		if(map.containsKey(key))
			map.remove(key);
		Value v = new Value();
		v.add(newValue);
		map.put(key, v);
	}
	
	/**
	 * Sets the value that k is associated with in this config to newValue.
	 * If k is already associated with anything else, this method DELETES 
	 * the values it is associated with. If you want to append more data
	 * to associate with this key and retain what it already is associated
	 * with, then use add instead.
	 * 
	 * @param k			the key to associate the new value with
	 * @param newValue	the value to set k to
	 */
	public void set(ParserKeys k, ConfigData[] newValue){
		String key = k.toString().toLowerCase();
		if(map.containsKey(key))
			map.remove(key);
		Value v = new Value();
		for(ConfigData s : newValue)
			v.add(s);
		map.put(key, v);
	}
	
	/**
	 * Adds toAdd to the list of values associated with k. Does not alter
	 * values already associated with k. Add preserves the order in which
	 * the values are added for each type (string, number, config).
	 * 
	 * @param k			the key to associate toAdd with
	 * @param toAdd		the value to add to the list of values associated with k
	 */
	public void add(ParserKeys k, String toAdd){
		String key = k.toString().toLowerCase();
		Value v = map.get(key);
		if(v == null)
		{
			v = new Value();
			map.put(key, v);
		}
		v.add(toAdd);
	}
	
	/**
	 * Adds toAdd to the list of values associated with k. Does not alter
	 * values already associated with k. Add preserves the order in which
	 * the values are added for each type (string, number, config).
	 * 
	 * @param k			the key to associate toAdd with
	 * @param toAdd		the value to add to the list of values associated with k
	 */
	public void add(ParserKeys k, Double toAdd){
		String key = k.toString().toLowerCase();
		Value v = map.get(key);
		if(v == null)
		{
			v = new Value();
			map.put(key, v);
		}
		v.add(toAdd);
	}
	
	/**
	 * Adds toAdd to the list of values associated with k. Does not alter
	 * values already associated with k. Add preserves the order in which
	 * the values are added for each type (string, number, config).
	 * 
	 * @param k			the key to associate toAdd with
	 * @param toAdd		the value to add to the list of values associated with k
	 */
	public void add(ParserKeys k, ConfigData toAdd){
		String key = k.toString().toLowerCase();
		Value v = map.get(key);
		if(v == null)
		{
			v = new Value();
			map.put(key, v);
		}
		v.add(toAdd);
	}
	
	/**
	 * Adds toAdd to the list of values associated with k. Does not alter
	 * values already associated with k. Add preserves the order in which
	 * the values are added for each type (string, number, config).
	 * 
	 * @param k			the key to associate toAdd with
	 * @param toAdd		the value to add to the list of values associated with k
	 */
	public void add(ParserKeys toAdd, String[] array) {
		for(String s : array)
			this.add(toAdd, s);
	}

	/**
	 * Adds toAdd to the list of values associated with k. Does not alter
	 * values already associated with k. Add preserves the order in which
	 * the values are added for each type (string, number, config).
	 * 
	 * @param k			the key to associate toAdd with
	 * @param toAdd		the value to add to the list of values associated with k
	 */
	public void add(ParserKeys toAdd, ConfigData[] array) {
		for(ConfigData cd : array)
			this.add(toAdd, cd);
	}
	
	/**
	 * Adds toAdd to the list of values associated with k. Does not alter
	 * values already associated with k. Add preserves the order in which
	 * the values are added for each type (string, number, config).
	 * 
	 * @param k			the key to associate toAdd with
	 * @param toAdd		the value to add to the list of values associated with k
	 */
	public void add(ParserKeys toAdd, Double[] array) {
		for(Double d : array)
			this.add(toAdd, d);
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
	public boolean remove(ParserKeys k, String toRemove){
		checkKey(k);
		String key = k.toString().toLowerCase();
		Value v = map.get(key);
		boolean ret = v.getStringList().contains(toRemove);
		v.getStringList().remove(toRemove);
		return ret;
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
	public boolean remove(ParserKeys k, Double toRemove){
		checkKey(k);
		String key = k.toString().toLowerCase();
		Value v = map.get(key);
		boolean ret = v.getNumberList().contains(toRemove);
		v.getNumberList().remove(toRemove);
		return ret;
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
	public boolean remove(ParserKeys k, ConfigData toRemove){
		checkKey(k);
		String key = k.toString().toLowerCase();
		Value v = map.get(key);
		boolean ret = v.getConfigList().contains(toRemove);
		v.getConfigList().remove(toRemove);
		return ret;
	}
	
	/**
	 * 
	 * @return	the URI this config data was generated from. If it wasn't generated
	 * 			from a URI, then returns an empty string.
	 */
	public String getURI()
	{
		return URI;
	}
	
	private void checkKey(ParserKeys key)
	{
		if(!map.containsKey(key.toString().toLowerCase()))
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
