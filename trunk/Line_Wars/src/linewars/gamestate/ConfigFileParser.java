package linewars.gamestate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ConfigFileParser {
	
	private HashMap<String, String> values;
	private String filepath;
	
	public ConfigFileParser(String filepath) throws FileNotFoundException, InvalidConfigFileException
	{
		this.filepath = filepath;
		values = new HashMap<String, String>();
		
		Scanner file = new Scanner(new File(filepath));
		
		while(file.hasNextLine())
		{
			Scanner line = new Scanner(file.nextLine());
			String key = line.next().toLowerCase();
			
			//allow comments
			if(key.charAt(0) == '#')
				continue;
			
			if(!line.hasNext() || !line.next().equals("=") || !line.hasNext())
				throw new InvalidConfigFileException(filepath + " is not a valid config file.");
			
			String value = line.nextLine().trim();
			
			values.put(key, value);
			
		}
	}
	
	public String getURI()
	{
		return filepath;
	}
	
	public String getStringValue(String key) throws NoSuchKeyException
	{
		checkKey(key);
		return values.get(key.toLowerCase());
	}
	
	public double getNumericValue(String key) throws NoSuchKeyException
	{
		checkKey(key);
		return Double.parseDouble(values.get(key.toLowerCase()));
	}
	
	public String[] getList(String key) throws NoSuchKeyException
	{
		checkKey(key);
		
		ArrayList<String> list = new ArrayList<String>();
		Scanner s = new Scanner(values.get(key.toLowerCase()));
		s.useDelimiter(",");
		
		while(s.hasNext())
			list.add(s.next());
		
		return list.toArray(new String[0]);
	}
	
	private void checkKey(String key) throws NoSuchKeyException
	{
		if(!values.containsKey(key.toLowerCase()))
			throw new NoSuchKeyException("The key \"" + key + "\" is not contained in the config file " + filepath);
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
