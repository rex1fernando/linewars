package linewars.gamestate;

import java.util.ArrayList;
import java.util.List;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;


//this class simulates a function

/**
 * 
 * @author Connor Schenck
 *
 */
public strictfp class Function {
	
	private enum FunctionClass {
		Polynomial, Exponential
	}
	
	private FunctionClass type;
	private ArrayList<Double> coefficients = new ArrayList<Double>();
	
	public Function(ConfigData p)
	{
		type = FunctionClass.valueOf(p.getString(ParserKeys.functionType));
		List<Double> coefs = p.getNumberList(ParserKeys.coefficients);
		for(Double c : coefs)
			coefficients.add(Double.valueOf(c));
		
		if(type == FunctionClass.Exponential && coefficients.size() != 3)
			throw new IllegalArgumentException("Coefficients for the exponential function must be c0*c1^(c2*x)");
	}
	
	public double f(double x)
	{
		if(type == FunctionClass.Polynomial)
		{
			double ret = 0;
			for(int i = 0; i < coefficients.size(); i++)
				ret += coefficients.get(i)*Math.pow(x, i);
			return ret;
		}
		else if(type == FunctionClass.Exponential)
			return coefficients.get(0)*Math.pow(coefficients.get(1), coefficients.get(2)*x);
		else
			return 0;
	}
	
	/**
	 * 
	 * @return	A ConfigData object containing the representation of this Function.
	 */
	public ConfigData toConfigData()
	{
		ConfigData ret = new ConfigData();
		ret.add(ParserKeys.functionType, type.toString());
		for(Double d : coefficients){
			ret.add(ParserKeys.coefficients, d);
		}
		
		return ret;
	}

}
