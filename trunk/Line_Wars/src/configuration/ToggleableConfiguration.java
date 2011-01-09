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
			//TODO get enabled from superclass
		}else if(subConfigurationString.equals(arg1)){
			//TODO get subConfiguration from superclass
		}
	}
	
	//TODO more methods
}
