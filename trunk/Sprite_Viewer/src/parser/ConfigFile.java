package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 
 * @author cschenck
 *
 * This class represents a config file. It handles file I/O and getting the
 * lines of a file as well as remembering what line of the file it is currently
 * on. All filepaths are relative to a predefined data directory.
 */
public class ConfigFile  {
	
	//TODO figure out how to find where the relative filepath is (i.e. the "data" folder)
	
	private String filepath;
	private Scanner file;
	private int nextLine;
	
	/**
	 * Opens the given file path for parsing
	 * 
	 * @param filepath	the filepath to open
	 * @throws FileNotFoundException
	 */
	public ConfigFile(String filepath) throws FileNotFoundException
	{
		this.filepath = filepath;
		file = new Scanner(new File(filepath));
		nextLine = 0;
	}
	
	/**
	 * Returns the next line in the file.
	 * 
	 * @return	the next line in the file
	 */
	public String nextLine()
	{
		nextLine++;
		return file.nextLine();
	}
	
	/**
	 * Returns the line number of the next line in the file.
	 * 
	 * @return	the line number of the next line in the file
	 */
	public int nextLineNumber()
	{
		return nextLine;
	}
	
	 /** 
	 * 
	 * @return whether or not there is another line in the file
	 */
	public boolean hasNextLine()
	{
		return file.hasNextLine();
	}
	
	/**
	 * 
	 * @return	the filepath of this file
	 */
	public String getURI()
	{
		return filepath;
	}

}
