package linewars.gamestate.shapes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Taylor Bergquist
 * 
 * This class encapsulates an algorithm for computing whether two given shapes intersect.  
 *
 */
public strictfp abstract class ShapeCollisionStrategy {
	
	//a map that allows runtime lookup of Strategies for detecting collisions
	private static HashMap<Class<? extends Shape>, HashMap<Class<? extends Shape>, ShapeCollisionStrategy>> definedStrategies;
	
	static{
		try {
			List<Class> classes = getClasses(Shape.class.getPackage().getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 * Call this method to add your Strategy to the pool of usable strategies
	 */
	protected static void addStrategy(ShapeCollisionStrategy toAdd, Class<? extends Shape> first, Class<? extends Shape> second){
		//instantiate if this hasn't been (singleton)
		if(definedStrategies == null){
			definedStrategies = new HashMap<Class<? extends Shape>, HashMap<Class<? extends Shape>, ShapeCollisionStrategy>>();
		}
		
		//add strat to map both ways (symetrically)
		addStrategyHelper(toAdd, first, second);
		addStrategyHelper(toAdd, second, first);
		
		//add entry for agg-anything strat to first and second symmetrically
		addStrategyHelper(new AggregateAnythingStrategy(), first, ShapeAggregate.class);
		addStrategyHelper(new AggregateAnythingStrategy(), ShapeAggregate.class, first);
		addStrategyHelper(new AggregateAnythingStrategy(), second, ShapeAggregate.class);
		addStrategyHelper(new AggregateAnythingStrategy(), ShapeAggregate.class, second);
	}
	

	private static void addStrategyHelper(ShapeCollisionStrategy toAdd, Class<? extends Shape> first, Class<? extends Shape> second){
		if(definedStrategies.get(first) == null){
			definedStrategies.put(first, new HashMap<Class<? extends Shape>, ShapeCollisionStrategy>());
		}
		HashMap<Class<? extends Shape>, ShapeCollisionStrategy> subMap = definedStrategies.get(first);
		if(subMap.get(second) == null){
			subMap.put(second, toAdd);
		}
	}
	
	/**
	 * Returns a ShapeCollisionStrategy that can compute whether the two given shapes are colliding.
	 * @param first
	 * One Class<? extends Shape> which is to be tested for collision.
	 * @param second
	 * One Class<? extends Shape> which is to be tested for collision.
	 * @return A ShapeCollisionStrategy which can compute whether two given Shapes of the specified types can collide.
	 */
	public static final ShapeCollisionStrategy getStrategyForShapes(Class<? extends Shape> first, Class<? extends Shape> second){
		HashMap<Class<? extends Shape>, ShapeCollisionStrategy> subMap = definedStrategies.get(first);
		//No HashMap in that spot means collision is not supported
		if(subMap == null) throw new UnsupportedOperationException("Collision detection between " + first + " and " + second + " is not supported.");
		ShapeCollisionStrategy detector = subMap.get(second);
		//No ShapeCollision in that spot also means collision is not supported
		if(detector == null) throw new UnsupportedOperationException("Collision detection between " + first + " and " + second + " is not supported.");
		return detector;
	}
	
	/**
	 * Computes whether the given Shapes are colliding.  Two Shapes are defined to be colliding if the area of their intersection is nonzero.
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public abstract boolean collides(Shape first, Shape second);
}
