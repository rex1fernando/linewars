package menu;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.Map;
import linewars.gamestate.Race;

public class ContentProvider
{
	public static Race[] getAvailableRaces()
	{
		String from = "resources/races";
		String ext = "race";
		return (Race[]) deserializeObjects(from, ext);
	}
	
	public static Color[] getAvailableColors()
	{
		// TODO implement
		return new Color[] { Color.black, Color.white, Color.red, Color.blue, Color.green };
	}
	
	public static Object[] getAvailableReplays() // FIXME
	{
		String from = "resources/replays";
		String ext = "replay";
		return (Object[]) deserializeObjects(from, ext);
	}
	
	public static Map[] getAvailableMaps()
	{
		String from = "resources/maps";
		String ext = "map";
		return (Map[]) deserializeObjects(from, ext);
	}
	
	private static Object[] deserializeObjects(String from, String extension)
	{
		File[] files = getFiles(from, extension);
		Object[] objs = new Object[files.length];
		for (int i = 0; i < files.length; ++i)
			objs[i] = deserialize(files[i]);
		return objs;
	}
	
	private static File[] getFiles(String from, String extension)
	{
		File dir = new File(from);
		if (dir.isDirectory() == false) throw new IllegalArgumentException("The path given is not a directory!");
		
		File[] allFiles = dir.listFiles();
		List<File> selFiles = new ArrayList<File>();
		for (File f : allFiles)
		{
			if (f.getName().endsWith("." + extension))
				selFiles.add(f);
		}
		
		return selFiles.toArray(new File[0]);
	}
	
	private static Object deserialize(File f)
	{
		Object obj = null;
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			obj = ois.readObject();
		} catch (Exception e) {}
		
		return obj;
	}
}
