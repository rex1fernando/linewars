package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import utility.Observable;

/**
 * This class encapsulates the way in which a configurable game object is configured in a generic way.
 * It provides methods for Techs to access and modify this configuration.
 * Subclasses should either directly use their parent's data structure to store the configuration or
 * observe themselves.
 * 
 * @author Knexer
 *
 */
public abstract class Configuration extends Observable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2482810578956653165L;
	private HashMap<String, Property> props;
	//generic access methods for techs
		//collection of properties
			//there is an enum that specifies how the thing can be modified?
			//each Modifier then takes a Property and outputs a Property
		//get property names
		//get property
		//set property
	
	protected Configuration(){
		props = new HashMap<String, Property>();
	}

	public Set<String> getPropertyNames(){
		return props.keySet();
	}
	
	public Property getPropertyForName(String name){
		return props.get(name);
	}
	
	//can this throw an exception at all?
	public Property setPropertyForName(String name, Property toSet){
		Property ret = props.put(name, toSet);
		setChanged();
		notifyObservers(name);
		return ret;
	}
	
	public void removeProperty(String name){
		props.remove(name);
		setChanged();
		notifyObservers(name);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Configuration)
			return props.equals(((Configuration)o).props);
		else
			return false;
	}
	
	public static Configuration copyConfiguration(Configuration toCopy) throws IOException, ClassNotFoundException
	{
		File f = File.createTempFile("temp", ".tmp");
		String path = f.getAbsolutePath();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(toCopy);
		oos.flush();
		oos.close();
		
		return (Configuration) new ObjectInputStream(new FileInputStream(path)).readObject();
	}
}
