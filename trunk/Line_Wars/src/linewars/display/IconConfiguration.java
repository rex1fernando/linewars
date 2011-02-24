package linewars.display;

import java.util.ArrayList;
import java.util.List;

import configuration.Configuration;
import configuration.Property;
import configuration.Usage;

public class IconConfiguration extends Configuration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8030663091899136179L;

	public enum IconType {
		regular, pressed, rollover, highlighted, disabled
	}

	public void setIcon(IconType type, String uri)
	{
		super.setPropertyForName(type.toString(), new Property(Usage.STRING, uri));
	}
	
	public List<IconType> getIconTypes()
	{
		List<IconType> ret = new ArrayList<IconType>();
		for(String name : super.getPropertyNames())
			ret.add(IconType.valueOf(name));
		return ret;
	}
	
	public String getIconURI(IconType type)
	{
		return (String) super.getPropertyForName(type.toString()).getValue();
	}
	
}
