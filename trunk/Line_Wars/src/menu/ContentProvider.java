package menu;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.MapConfiguration;
import linewars.gamestate.Race;

public class ContentProvider
{
	public static Race[] getAvailableRaces()
	{
		String from = "resources/races";
		String ext = "cfg";
		Object[] objs = deserializeObjects(from, ext);
		List<Race> maps = new ArrayList<Race>();
		for (int i = 0; i < objs.length; ++i)
		{
			try {
				Race r = (Race) objs[i];
				maps.add(r);
			} catch (ClassCastException e) {}
		}
		
		return maps.toArray(new Race[0]);
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
		//return (Object[]) deserializeObjects(from, ext);
		return new String[] {"Replay 1", "Replay 2" };
	}
	
	public static MapConfiguration[] getAvailableMaps()
	{	
		String from = "resources/maps";
		String ext = "cfg";
		Object[] objs = deserializeObjects(from, ext);
		List<MapConfiguration> maps = new ArrayList<MapConfiguration>();
		for (int i = 0; i < objs.length; ++i)
		{
			try {
				MapConfiguration m = (MapConfiguration) objs[i];
				maps.add(m);
			} catch (ClassCastException e) {}
		}
		
		return maps.toArray(new MapConfiguration[0]);
	}
	
	private static Object[] deserializeObjects(String from, String extension)
	{
		File[] files = getFiles(from, extension);
		List<Object> objs = new ArrayList<Object>();
		for (int i = 0; i < files.length; ++i)
		{
			Object o = deserialize(files[i]);
			if (o != null)
				objs.add(o);
		}
		return objs.toArray();
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
