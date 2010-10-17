package linewars.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ConfigFile  {
	
	//TODO figure out how to find where the relative filepath is (i.e. the "data" folder)
	
	private String filepath;
	private Scanner file;
	private int nextLine;
	
	public ConfigFile(String filepath) throws FileNotFoundException
	{
		this.filepath = filepath;
		file = new Scanner(new File(filepath));
		nextLine = 0;
	}
	
	public String nextLine()
	{
		nextLine++;
		return file.nextLine();
	}
	
	public int nextLineNumber()
	{
		return nextLine;
	}
	
	public boolean hasNextLine()
	{
		return file.hasNextLine();
	}
	
	public String getURI()
	{
		return filepath;
	}

}
