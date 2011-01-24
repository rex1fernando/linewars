package linewars.gamestate.tech;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

/**
 * Modifies a numeric attribute in an exponential or polynomial way
 * 
 * @author John George, Taylor Bergquist
 *
 */
public class NumericModifier implements Modifier {
	
	//The Function that defines how the Modifier modifies.
	Function f;
	
	public NumericModifier(ConfigData toParse){
		if(!toParse.getString(ParserKeys.modifiertype).equalsIgnoreCase("numeric")){
			throw new IllegalArgumentException("A NumericModifier cannot be constructed on config data of type " + toParse.getString(ParserKeys.modifiertype));
		}
		ConfigData functionDescriptor = toParse.getConfig(ParserKeys.valueFunction);
		f = new Function(functionDescriptor);		
	}
	
	@Override
	public void modify(ConfigData cd, ParserKeys p, double d) {
		cd.set(p, f.f(d));
	}
	
	@Override
	public ConfigData toConfigData(){
		ConfigData ret = new ConfigData();
		ret.add(ParserKeys.modifiertype, "numeric");
		ret.add(ParserKeys.valueFunction, f.toConfigData());
		return ret;
	}

}
