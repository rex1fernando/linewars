package linewars.test;

import linewars.gamestate.BezierCurve;
import linewars.gamestate.Position;

public class JohnTest {
	public static void main(String[] args)
	{
		Position p0 = new Position(0.0, 0.0);
		Position p1 = new Position(1.0, 0.0);
		Position p2 = new Position(2.0, 0.0);
		Position p3 = new Position(3.0, 0.0);
		
		Position test = new Position(3.0, 0.0);
		BezierCurve curve = new BezierCurve(p0, p1, p2, p3);
		double CPR = curve.getClosestPointRatio(test);
		System.out.print(CPR +"\n");
		System.out.print(curve.getPosition(CPR) +"\n");
	}
}
