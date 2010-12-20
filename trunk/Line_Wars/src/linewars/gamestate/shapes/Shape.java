package linewars.gamestate.shapes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

/**
 * Represents an area in 2D space.
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp abstract class Shape {
	
	private static HashMap<String, Class<? extends Shape>> typeToClass;
	
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
     * 
     * Adds the given Class object to a HashMap so it can be looked up by the factory method.
     * 
     * When creating a new Shape, call this method with the value of the 'type' ParserKey you are using for your Shape.
     * 
     * @argument type
     * A String that identifies the type of the Shape you being added
     * @argument entry
     * The Class of the Shape being added
     */
	protected static void addClassForInitialization(String type, Class<? extends Shape> entry){
		if(typeToClass == null){
			typeToClass = new HashMap<String, Class<? extends Shape>>();
		}
		
		//to lower case to reduce incidence of errors in the config file
		typeToClass.put(type.toLowerCase(), entry);
	}
	
	/**
	 * Computes whether this Shape and the given Shape intersect.  Two Shapes are defined to intersect if the area of their intersection is nonzero.
	 * @param other
	 * The Shape with which this Shape may be colliding.
	 * @return
	 */
	public final boolean isCollidingWith(Shape other){
		//get correct ShapeCollisionStrategy
		ShapeCollisionStrategy detector = ShapeCollisionStrategy.getStrategyForShapes(this.getClass(), other.getClass());
		//compute the collision
		return detector.collides(this, other);
	}
	
	/**
	 * Returns a new Shape object which contains all the space occupied by this Shape as it moves from its current location to the new location
	 * specified by the given Transformation.
	 * @param change
	 * Defines the difference between the current and final positions, such that adding this Transformation to the current position 
	 * computes the new position.
	 * @return
	 */
	public abstract Shape stretch(Transformation change);
	
	/**
	 * Returns a new Shape which is a transformed instance of this shape.
	 * @param change
	 * Defines the difference between the current and final positions, such that adding this Transformation to the current position 
	 * computes the new position.
	 * @return
	 */
	public abstract Shape transform(Transformation change);
	
	/**
	 * Returns a Transformation which defines the current position of this Shape.
	 * @return
	 */
	public abstract Transformation position();

	/**
	 * Constructs a Shape on the data in the given ConfigData object.
	 * 
	 * @param parser
	 * @return
	 */
	public static Shape buildFromParser(ConfigData parser) {
		String type = parser.getString(ParserKeys.shapetype);
		Class<? extends Shape> initializer = typeToClass.get(type.toLowerCase());
		Shape ret = null;
		try{
			ret = initializer.getConstructor(ConfigData.class).newInstance(parser);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(ret == null){
			throw new IllegalArgumentException("A Shape could not be constructed from the given Parser " + parser.toString());
		}
		return ret;
	}
	
	/**
	 * Returns a ConfigData object that contains all the information needed to reconstruct this shape.
	 * @return
	 */
	public abstract ConfigData getData();
	
	/**
	 * Returns a Circle which bounds the Shape, such that anything which intersects the Shape also
	 * intersects the Circle and any point which is contained in the Shape is also
	 * contained in the Circle.
	 * 
	 * May not compute the smallest possible bounding circle.
	 * 
	 * @return A Circle bounding the object.
	 */
	public abstract Circle boundingCircle();
	
	/**
	 * Returns a Rectangle which bounds the Shape, such that anything which intersects the Shape also
	 * intersects the Rectangle and any point which is contained in the Shape is also
	 * contained in the Rectangle.
	 * 
	 * May not compute the smallest possible bounding rectangle.
	 * 
	 * @return A Rectangle bounding the object.
	 */
	public abstract Rectangle boundingRectangle();
	
	/**
	 * Computes whether a given position is contained in the Shape.
	 * The result of this method may be undefined for Positions
	 * exactly on the boundary of the Shape.
	 * 
	 * @param toTest The position to be tested.
	 * @return true if the Position is contained within the Shape, false otherwise.
	 */
	public abstract boolean positionIsInShape(Position toTest);
}
