package linewars.gamestate.tech;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

/**
 * 
 * @author John George
 *
 */
public interface Modifier {
	public void modify(ConfigData cd, ParserKeys p, double d);
	public ConfigData toConfigData();
}


/*

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
}*/