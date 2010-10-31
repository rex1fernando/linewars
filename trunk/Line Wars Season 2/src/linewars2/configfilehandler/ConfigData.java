package linewars2.configfilehandler;

/**
 * Represents a body of configuration data which defines an entity or set of entities.
 * 
 * @author Knexer
 *
 */
public class ConfigData {
	
	/**
	 * Returns a list of all of the keys which are defined within this body of data.
	 * @return
	 * A list of all defined keys.
	 */
	ParserKeys[] getDefinedKeys(){
		return null;
	}
	
	/**
	 * Returns a list of all of the ways in which the values mapped to a given key can be interpreted.
	 * @param key
	 * The key to check
	 * @return
	 * A list of all of the ways in which the values mapped to a given key can be interpreted.
	 */
	valueType[] getValidTypes(ParserKeys key){
		return null;
	}
	
	/**
	 * Gets the value mapped to the given key.
	 * @param key
	 * The key to be checked.
	 * @param dataType
	 * The type of data expected.
	 * @return
	 * The value mapped to the given key; guaranteed to be of type dataType.
	 */
	Object get(ParserKeys key, valueType dataType){
		return null;
	}
	
	/**
	 * Maps the given value to the given key
	 * @param key
	 * The key to which the new value is to be mapped.
	 * @param newValue
	 * The data to be mapped.
	 */
	void set(ParserKeys key, Object newValue){
		
	}
	
	/**
	 * Adds the given value to the given key
	 * @param key
	 * The key to which the given value is to be mapped
	 * @param toAdd
	 * The data which is to be added to the given key
	 */
	void add(ParserKeys key, Object toAdd){
		
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
	boolean remove(ParserKeys key, Object toRemove){
		return false;
	}
	
	public enum valueType{
		String, Number, Config, StringList, NumberList, ConfigList;
	}

}
