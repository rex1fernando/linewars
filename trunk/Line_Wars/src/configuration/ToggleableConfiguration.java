package configuration;

import java.util.Observable;
import java.util.Observer;

public class ToggleableConfiguration extends Configuration implements Observer {

	//a toggle boolean
	private boolean enabled;
	private static final String enabledString = "enabled";
	private static final Usage enabledUsage = Usage.BOOLEAN;

	//a Configuration for all the stuff that can be toggled, which can be changed as necessary
	private Configuration subConfiguration;
	private static final String subConfigurationString = "subConfiguration";
	private static final Usage subConfigurationUsage = Usage.CONFIGURATION;
	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if(enabledString.equals(arg1)){
			Property enabledProperty = this.getPropertyForName((String) arg1);
			enabled = (Boolean) enabledProperty.getValue();
		}else if(subConfigurationString.equals(arg1)){
			Property subConfigurationProperty = this.getPropertyForName((String) arg1);
			subConfiguration = (Configuration) subConfigurationProperty.getValue();
		}
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void enable(){
		this.setPropertyForName(enabledString, new Property(Usage.BOOLEAN, true));
	}
	
	public void disable(){
		this.setPropertyForName(enabledString, new Property(Usage.BOOLEAN, false));
	}

}
