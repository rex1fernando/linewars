package configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;

public abstract class Configuration extends Observable implements Serializable {
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
	
	protected Set<String> getPropertyNames(){
		return props.keySet();
	}
	
	protected Property getPropertyForName(String name){
		return props.get(name);
	}
	
	//can this throw an exception at all?
	protected Property setPropertyForName(String name, Property toSet){
		Property ret = props.put(name, toSet);
		notifyObservers();
		return ret;
	}
}
