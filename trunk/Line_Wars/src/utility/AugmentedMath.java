package utility;

public strictfp class AugmentedMath {
	
	public static double getAngleInPiToNegPi(double angle)
	{
		angle = angle / (2*Math.PI);
		double ret = angle - Math.floor(angle);
		if(ret > 0.5)
			ret -= 1;
		return ret*2*Math.PI;
	}

}
