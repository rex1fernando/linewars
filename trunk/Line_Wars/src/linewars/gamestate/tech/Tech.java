package linewars.gamestate.tech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import linewars.gamestate.Function;
import linewars.gamestate.Player;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.configfilehandler.*;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;

public class Tech {
	
	private static HashMap<String, Class<? extends Tech>> typeToClass;
	
	static{
		try {
			List<Class> classes = getClasses(Tech.class.getPackage().getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Tech.addClassForInitialization("deprecatedbaseclass", Tech.class);
	}
	
	/**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	private static List<Class> getClasses(String packageName)
            throws ClassNotFoundException, IOException 
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String fileName = resource.getFile();
            String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
            dirs.add(new File(fileNameDecoded));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }
    
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException 
	{
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
        	String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
            	classes.addAll(findClasses(file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
            	Class _class;
				try {
					_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
				} catch (ExceptionInInitializerError e) {
					// happen, for example, in classes, which depend on 
					// Spring to inject some beans, and which fail, 
					// if dependency is not fulfilled
					_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6),
							false, Thread.currentThread().getContextClassLoader());
				}
				classes.add(_class);
            }
        }
        return classes;
    }
    
    /**
     * 
     * Adds the given Class object to a HashMap so it can be looked up by the factory method.
     * 
     * When creating a new Tech, call this method with the value of the 'techtype' ParserKey you are using for your Tech.
     * 
     * @argument type
     * A String that identifies the type of the Tech you being added
     * @argument entry
     * The Class of the Tech being added
     */
	protected static void addClassForInitialization(String type, Class<? extends Tech> entry){
		if(typeToClass == null){
			typeToClass = new HashMap<String, Class<? extends Tech>>();
		}
		
		//TODO any checks here?
		//to lower case to reduce incidence of errors in the config file
		typeToClass.put(type.toLowerCase(), entry);
	}
	
	public static Tech buildFromURI(String URI, Player owner) throws FileNotFoundException, InvalidConfigFileException {
		ConfigData config = new ConfigFileReader(URI).read();
		String type = config.getString(ParserKeys.techtype);
		Class<? extends Tech> initializer = typeToClass.get(type.toLowerCase());
		Tech ret = null;
		try{
			ret = initializer.getConstructor(String.class, Player.class).newInstance(URI, owner);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(ret == null){
			throw new IllegalArgumentException("A Tech could not be constructed from the given Parser " + URI.toString());//TODO add owner to this debug print
		}
		return ret;
	}
	
	private MapItemDefinition definition;
	private ParserKeys field;
	private Function f;
	private int currentResearch = 0;
	private Tech[] prereqs;
	private int maxTimesResearchable;
	private String name;
	private String iconURI;
	private String pressedIconURI;
	private String rolloverIconURI;
	private String selectedIconURI;
	
	public Tech(String URI, Player owner) throws FileNotFoundException, InvalidConfigFileException
	{
		ConfigData config = new ConfigFileReader(URI).read();
		iconURI = config.getString(ParserKeys.icon);
		pressedIconURI = config.getString(ParserKeys.pressedIcon);
		rolloverIconURI = config.getString(ParserKeys.rolloverIcon);
		selectedIconURI = config.getString(ParserKeys.selectedIcon);
		definition = owner.getMapItemDefinition(config.getString(ParserKeys.mapItemURI));
		field = ParserKeys.valueOf(config.getString(ParserKeys.field));
		f = new Function(config.getConfig(ParserKeys.valueFunction));
		try
		{
		String[] list = config.getStringList(ParserKeys.preReqs).toArray(new String[0]);
		prereqs = new Tech[list.length];
		for(int i = 0; i < list.length; i++)
			prereqs[i] = owner.getTech(list[i]);
		}
		catch (NoSuchKeyException e)
		{
			prereqs = new Tech[0];
		}
		
		try
		{
			maxTimesResearchable = config.getNumber(ParserKeys.maxTimesResearchable).intValue();
		}
		catch(NoSuchKeyException e)
		{
			maxTimesResearchable = Integer.MAX_VALUE;
		}
		name = config.getString(ParserKeys.name);
	}
	
	/**
	 * This method checks to see if the pre-req's for this
	 * tech have been researched.
	 * 
	 * @return	true if the pre-req's have been researched, false otherwise
	 */
	public boolean researchable()
	{
		for(Tech t : prereqs)
			if(t.currentResearch <= 0)
				return false;
		
		return true;
	}
	
	/**
	 * This method returns the maximum number of times this tech
	 * is allowed to be researched.
	 * 
	 * @return	the number of times to allow research
	 */
	public int maxTimesResearchable()
	{
		return maxTimesResearchable;
	}
	
	/**
	 * researches the tech
	 */
	public void research()
	{
		if(field == ParserKeys.cost)
			((BuildingDefinition)definition).setCost(f.f(++currentResearch));
		else if(field == ParserKeys.buildTime)
			((BuildingDefinition)definition).setBuildTime(f.f(++currentResearch));
		else if(field == ParserKeys.maxHP)
			((UnitDefinition)definition).setMaxHP(f.f(++currentResearch));
		else if(field == ParserKeys.velocity)
			((ProjectileDefinition)definition).setVelocity(f.f(++currentResearch));
	}
	
	/**
	 * gets the name of this tech
	 * 
	 * @return	the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * returns the tooltip description of the tech
	 * 
	 * @return	the description
	 */
	public String getDescription()
	{
		return "Modifies " + field.toString() + " in " + definition.getName() + " by " + f.f((double)(currentResearch + 1));
	}
	
	public Function getCostFunction()
	{
		return f;
	}
		
	public String getIconURI()
	{
		return iconURI;
	}
	
	public String getPressedIconURI()
	{
		return pressedIconURI;
	}
	
	public String getRolloverIconURI()
	{
		return rolloverIconURI;
	}
	
	public String getSelectedIconURI()
	{
		return selectedIconURI;
	}
	
	public MapItemDefinition getMapItemDefinition()
	{
		return definition;
	}

}
