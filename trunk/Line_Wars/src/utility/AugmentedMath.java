package utility;

public class AugmentedMath {
	
	public static double getAngleInPiToNegPi(double angle)
	{
		angle /= 2*Math.PI;
		angle -= Math.floor(angle);
		if(angle > 0.5)
			angle -= 1;
		return angle*2*Math.PI;
	}

}
