package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 
 * @author cschenck
 *
 * This class parses config files (or parts of config files) and creates a
 * hashmap of keys to values. It has various ways of looking up values by
 * their keys. It is preferred to use the ParserKeys enum when looking up
 * values by keys (the String methods are deprecated). Each parser knows what
 * parts of the config file that it parses.
 */
public class Parser {

	private HashMap<String, Value> values;
	private ConfigFile configFile;
	private int startLine;
	private int endLine;
	
	/**
	 * Creates a parser with the given config file. 
	 * 
	 * @param cFile		the file to parse
	 * @throws InvalidConfigFileException	if the config file is invalid
	 */
	public Parser(ConfigFile cFile) throws InvalidConfigFileException
	{
		configFile = cFile;
		startLine = cFile.nextLineNumber();
		values = new HashMap<String, Value>();
		
		while(configFile.hasNextLine())
		{
			Scanner line = new Scanner(configFile.nextLine());
			
			//if the line is empty
			if(!line.hasNext())
				continue;
			
			String key = line.next().toLowerCase();
			
			//allow comments
			if(key.charAt(0) == '#')
				continue;
			
			if(key.equals("}"))
				break;
			
			if(!line.hasNext() || !line.next().equals("=") || !line.hasNext())
				throw new InvalidConfigFileException(configFile.getURI() + "ERROR LINE " + (configFile.nextLineNumber() - 1) + 
						": is not a valid config file.");
			
			String value = line.nextLine().trim();
			
			values.put(key, new Value(value));	
		}
		
		endLine = configFile.nextLineNumber() - 1;
	}
	
	/**
	 * gets the string value associated with the key. If the value
	 * can't be represented as a string, retursn null.
	 * 
	 * @param key	the key to look up by
	 * @return		the string representation of the value associated with the key
	 * @throws NoSuchKeyException	if the key wasn't found
	 */
	public String getStringValue(ParserKeys key) throws NoSuchKeyException
	{
		String k = key.toString().toLowerCase();
		checkKey(k);
		return values.get(k).value;
	}
	
	/**
	 * Attempts to return the value associated with the key as a number. If that's
	 * not possible, it will throw an exception.
	 * 
	 * @param key	the key
	 * @return		the numeric represention of the value associated with key
	 * @throws NoSuchKeyException	if the key wasn't found
	 */
	public double getNumericValue(ParserKeys key) throws NoSuchKeyException
	{
		String k = key.toString().toLowerCase();
		checkKey(k);
		return Double.parseDouble(values.get(k).value);
	}
	
	/**
	 * Attempts to return the value associated with key as a list, comma delimited.
	 * If the value cannot be represented as a list, will throw an exception.
	 * 
	 * @param key	the key
	 * @return		a comma delimeted list of the value associated with key
	 * @throws NoSuchKeyException	if the key wasn't found
	 */
	public String[] getList(ParserKeys key) throws NoSuchKeyException
	{
		String k = key.toString().toLowerCase();
		checkKey(k);
		
		ArrayList<String> list = new ArrayList<String>();
		Scanner s = new Scanner(values.get(k).value);
		s.useDelimiter(",");
		
		while(s.hasNext())
			list.add(s.next().trim());
		
		return list.toArray(new String[0]);
	}
	
	/**
	 * Attemps to return the value associated with key as a parser. If the value
	 * associated with key isn't a block that is a valid parser, then this method
	 * return null.
	 * 
	 * @param key	the key
	 * @return		the parser of the value associated with key
	 * @throws NoSuchKeyException	if the key wasn't found
	 */
	public Parser getParser(ParserKeys key) throws NoSuchKeyException
	{
		String k = key.toString().toLowerCase();
		checkKey(k);
		return values.get(k).parser;
	}
	
	@Deprecated
	public String getStringValue(String key) throws NoSuchKeyException
	{
		String k = key.toLowerCase();
		checkKey(k);
		return values.get(k).value;
	}
	
	@Deprecated
	public double getNumericValue(String key) throws NoSuchKeyException
	{
		String k = key.toLowerCase();
		checkKey(k);
		return Double.parseDouble(values.get(k).value);
	}
	
	@Deprecated
	public String[] getList(String key) throws NoSuchKeyException
	{
		String k = key.toLowerCase();
		checkKey(k);
		
		ArrayList<String> list = new ArrayList<String>();
		Scanner s = new Scanner(values.get(k).value);
		s.useDelimiter(",");
		
		while(s.hasNext())
			list.add(s.next());
		
		return list.toArray(new String[0]);
	}
	
	@Deprecated
	public Parser getParser(String key) throws NoSuchKeyException
	{
		String k = key.toLowerCase();
		checkKey(k);
		return values.get(k).parser;
	}
	
	/**
	 * 
	 * @return	the config file associated with this parser
	 */
	public ConfigFile getConfigFile()
	{
		return configFile;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Parser)
		{
			Parser p = (Parser) o;
			boolean ret = configFile.getURI().equals(p.getConfigFile().getURI());
			ret &= startLine == p.startLine;
			ret &= endLine == p.endLine;
			return ret;
		}
		else
			return false;
	}
	
	private void checkKey(String key) throws NoSuchKeyException
	{
		if(!values.containsKey(key))
			throw new NoSuchKeyException("The key \"" + key + "\" is not contained in the config file " + configFile.getURI() + 
					" from line " + startLine + " to " + endLine);
	}
	
	private class Value {
		String value = null;
		Parser parser = null;
		public Value(String data) throws InvalidConfigFileException
		{
			if(data.equals("{"))
				parser = new Parser(configFile);
			else
				value = data;
		}
	}
	
	public static class InvalidConfigFileException extends Exception {
		private static final long serialVersionUID = 8024603342014298051L;

		public InvalidConfigFileException(String s) {
			super(s);
		}
	}
	
	public static class NoSuchKeyException extends RuntimeException {
		private static final long serialVersionUID = -3402614354747155750L;

		public NoSuchKeyException(String s) {
			super(s);
		}
	}
	
}
