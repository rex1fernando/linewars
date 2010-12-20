package linewars.test;

import linewars.gamestate.Position;

import org.junit.Test;


public class BezierCurveTest {
	
	/*
	 *  main :
	 *	Given a cubic Bezier curve (i.e., its control points), and some
	 *	arbitrary point in the plane, find the point on the curve
	 *	closest to that arbitrary point.
	 */
	public static void main(String[] args)	{
	 Position[] bezCurve = new Position[4]; /*  A cubic Bezier curve	*/
	 bezCurve[0] = new Position(0.0, 0.0);
	 bezCurve[1] = new Position(1.0, 2.0);
	 bezCurve[2] = new Position(3.0, 3.0);
	 bezCurve[0] = new Position(4.0, 2.0);
	 Position  arbPoint =  new Position( 3.5, 2.0 ); /*Some arbitrary point*/
	 Position  pointOnCurve;		 /*  Nearest point on the curve */

	    /*  Find the closest point */
//	    pointOnCurve = blah.NearestPointOnCurve(arbPoint, bezCurve);
//	    System.out.println(pointOnCurve.getX());
//	    System.out.println(pointOnCurve.getY());
	}
}
