package linewars.test;
import java.lang.Math;
import linewars.gamestate.BezierCurve;
import linewars.gamestate.Position;

/**
 * 
 * @author John George
 *
 *This class is a temporary repository for me to transform the c code for finding the closest point on a 
 *bezier curve to an arbitrary point found at http://tog.acm.org/resources/GraphicsGems/gemsiv/GraphicsGems.c
 *and http://tog.acm.org/resources/GraphicsGems/gems/NearestPoint.c to Java code for replacing the current hack
 *
 */
public class BezierCToJavaTest {

	/*
	Solving the Nearest Point-on-Curve Problem 
	and
	A Bezier Curve-Based Root-Finder
	by Philip J. Schneider
	from "Graphics Gems", Academic Press, 1990
	*/

	int		MAXDEPTH = 10;	/*  Maximum depth for recursion */
	double	EPSILON = Math.pow(2.0, -MAXDEPTH-1); /*Flatness control value */
	int 	DEGREE = 3;		/*  Cubic Bezier curve		*/
	int		W_DEGREE = 5;		/*  Degree of eqn to find roots of */

	/*
	 *  NearestPointOnCurve :
	 *  	Compute the parameter value of the point on a Bezier
	 *		curve segment closest to some arbitrary, user-input point.
	 *		Return the point on the curve at that parameter value.
	 *		Param P: The user-supplied point
	 *		Param V: Control points of cubic Bezier 
	 */
	public double NearestPointOnCurve(Position P, Position[] V)	{
	    Position[] w;			/* Ctl pts for 5th-degree eqn	*/
	    double[] t_candidate = new double[W_DEGREE];	/* Possible roots		*/     
	    int 	n_solutions;		/* Number of roots found	*/
	    double	t;			/* Parameter value of closest pt*/

	    /*  Convert problem to 5th-degree Bezier form	*/
	    w = ConvertToBezierForm(P, V);

	    /* Find all possible roots of 5th-degree equation */
	    n_solutions = FindRoots(w, W_DEGREE, t_candidate, 0);

	    /* Compare distances of P to all candidates, and to t=0, and t=1 */
	    {
			double 	dist, new_dist;
			Position p;
			int		i;

		
		/* Check distance to beginning of curve, where t = 0	*/
			dist = V2SquaredLength(V2Sub(P, V[0]));
	        t = 0.0;

		/* Find distances for candidate points	*/
	        for (i = 0; i < n_solutions; i++) {
		    	p = Bezier(V, DEGREE, t_candidate[i], null, null);
		    	new_dist = V2SquaredLength(V2Sub(P, p));
		    	if (new_dist < dist) {
	                	dist = new_dist;
		        		t = t_candidate[i];
	    	    }
	        }

		/* Finally, look at distance to end point, where t = 1.0 */
			new_dist = V2SquaredLength(V2Sub(P, V[DEGREE]));
	        	if (new_dist < dist) {
	            	dist = new_dist;
	            	t = 1.0;
	        }
	    }

	    return t;
	    /*  Return the point on the curve at parameter value t */
//	    return (Bezier(V, DEGREE, t, null, null));
	}


	/*
	 *  ConvertToBezierForm :
	 *		Given a point and a Bezier curve, generate a 5th-degree
	 *		Bezier-format equation whose solution finds the point on the
	 *      curve nearest the user-defined point.
	 *      Point2 	P;			The point to find t for
	    	Point2 	*V;			The control points
	 */
	public Position[] ConvertToBezierForm(Position P, Position[] V){

	    int 	i, j, k, m, n, ub, lb;	
	    int 	row, column;		/* Table indices		*/
	    Position[] c = new Position[DEGREE+1];		/* V(i)'s - P			*/
	    Position[] d = new Position[DEGREE];		/* V(i+1) - V(i)		*/
	    Position[] 	w;			/* Ctl pts of 5th-degree curve  */
	    double[][] cdTable = new double[3][4];		/* Dot product of c, d		*/
	    double[][] z = {	/* Precomputed "z" for cubics	*/
		{1.0, 0.6, 0.3, 0.1},
		{0.4, 0.6, 0.6, 0.4},
		{0.1, 0.3, 0.6, 1.0},
	    };


	    /*Determine the c's -- these are vectors created by subtracting*/
	    /* point P from each of the control points				*/
	    for (i = 0; i <= DEGREE; i++) {
			c[i] = V2Sub(V[i], P);
	    }
	    /* Determine the d's -- these are vectors created by subtracting*/
	    /* each control point from the next					*/
	    for (i = 0; i <= DEGREE - 1; i++) { 
			d[i] = V2ScaleII(V2Sub(V[i+1], V[i]), 3.0);
	    }

	    /* Create the c,d table -- this is a table of dot products of the */
	    /* c's and d's							*/
	    for (row = 0; row <= DEGREE - 1; row++) {
			for (column = 0; column <= DEGREE; column++) {
		    	cdTable[row][column] = V2Dot(d[row], c[column]);
			}
	    }

	    /* Now, apply the z's to the dot products, on the skew diagonal*/
	    /* Also, set up the x-values, making these "points"		*/
	    w = new Position[W_DEGREE + 1];
	    for (i = 0; i <= W_DEGREE; i++) {
			w[i] = new Position((double)(i)/W_DEGREE, 0.0);
	    }

	    n = DEGREE;
	    m = DEGREE-1;
	    for (k = 0; k <= n + m; k++) {
			lb = Math.max(0, k - m);
			ub = Math.min(k, n);
			for (i = lb; i <= ub; i++) {
		    	j = k - i;
		    	w[i+j] = new Position(w[i+j].getX(), w[i+j].getY() + cdTable[j][i] * z[j][i]);
			}
	    }

	    return (w);
	}


	/*
	 *  FindRoots :
	 *	Given a 5th-degree equation in Bernstein-Bezier form, find
	 *	all of the roots in the interval [0, 1].  Return the number
	 *	of roots found.
	 *  Point2 	*w;			 The control points		
	    int 	degree;		 The degree of the polynomial	
	    double 	*t;			 RETURN candidate t-values	
	    int 	depth;		 The depth of the recursion	
	 */
	public int FindRoots(Position[] w, int degree, double[] t, int depth){  
	    int 	i;
	    Position[] Left = new Position[W_DEGREE+1];	/* New left and right 		*/
	    Position[] Right = new Position[W_DEGREE+1];	/* control polygons		*/
	    int 	left_count,		/* Solution count from		*/
			right_count;		/* children			*/
	    double[] left_t = new double[W_DEGREE+1];	/* Solutions from kids		*/
		double[] right_t = new double[W_DEGREE+1];

	    switch (CrossingCount(w, degree)) {
	       	case 0 : {	/* No solutions here	*/
		     return 0;	
		}
		case 1 : {	/* Unique solution	*/
		    /* Stop recursion when the tree is deep enough	*/
		    /* if deep enough, return 1 solution at midpoint 	*/
		    if (depth >= MAXDEPTH) {
				t[0] = (w[0].getX() + w[W_DEGREE].getX()) / 2.0;
				return 1;
		    }
		    if (ControlPolygonFlatEnough(w, degree)) {
				t[0] = ComputeXIntercept(w, degree);
				return 1;
		    }
		    break;
		}
	}

	    /* Otherwise, solve recursively after	*/
	    /* subdividing control polygon		*/
	    Bezier(w, degree, 0.5, Left, Right);
	    left_count  = FindRoots(Left,  degree, left_t, depth+1);
	    right_count = FindRoots(Right, degree, right_t, depth+1);


	    /* Gather solutions together	*/
	    for (i = 0; i < left_count; i++) {
	        t[i] = left_t[i];
	    }
	    for (i = 0; i < right_count; i++) {
	 		t[i+left_count] = right_t[i];
	    }

	    /* Send back total number of solutions	*/
	    return (left_count+right_count);
	}


	/*
	 * CrossingCount :
	 *	Count the number of times a Bezier control polygon 
	 *	crosses the 0-axis. This number is >= the number of roots.
	 *
	 *	Point2	*V;			  Control pts of Bezier curve	
	    int		degree;		  Degreee of Bezier curve
	 *
	 */
	public int CrossingCount(Position[] V, int degree){
	    int 	i;	
	    int 	n_crossings = 0;	/*  Number of zero-crossings	*/
	    double		sign, old_sign;		/*  Sign of coefficients	*/

	    sign = old_sign = Math.signum(V[0].getY());
	    for (i = 1; i <= degree; i++) {
			sign = Math.signum(V[i].getY());
			if (sign != old_sign) n_crossings++;
			old_sign = sign;
	    }
	    return n_crossings;
	}



	/*
	 *  ControlPolygonFlatEnough :
	 *	Check if the control polygon of a Bezier curve is flat enough
	 *	for recursive subdivision to bottom out.
	 *
	 *  Corrections by James Walker, jw@jwwalker.com, as follows:

	There seem to be errors in the ControlPolygonFlatEnough function in the
	Graphics Gems book and the repository (NearestPoint.c). This function
	is briefly described on p. 413 of the text, and appears on pages 793-794.
	I see two main problems with it.

	The idea is to find an upper bound for the error of approximating the x
	intercept of the Bezier curve by the x intercept of the line through the
	first and last control points. It is claimed on p. 413 that this error is
	bounded by half of the difference between the intercepts of the bounding
	box. I don't see why that should be true. The line joining the first and
	last control points can be on one side of the bounding box, and the actual
	curve can be near the opposite side, so the bound should be the difference
	of the bounding box intercepts, not half of it.

	Second, we come to the implementation. The values distance[i] computed in
	the first loop are not actual distances, but squares of distances. I
	realize that minimizing or maximizing the squares is equivalent to
	minimizing or maximizing the distances.  But when the code claims that
	one of the sides of the bounding box has equation
	a * x + b * y + c + max_distance_above, where max_distance_above is one of
	those squared distances, that makes no sense to me.

	I have appended my version of the function. If you apply my code to the
	cubic Bezier curve used to test NearestPoint.c,

	 static Point2 bezCurve[4] = {    /  A cubic Bezier curve    /
	    { 0.0, 0.0 },
	    { 1.0, 2.0 },
	    { 3.0, 3.0 },
	    { 4.0, 2.0 },
	    };

	my code computes left_intercept = -3.0 and right_intercept = 0.0, which you
	can verify by sketching a graph. The original code computes
	left_intercept = 0.0 and right_intercept = 0.9.

	 */

    /*Point2	*V;		/* Control points
    int 	degree;		/* Degree of polynomial
	/* static int ControlPolygonFlatEnough( const Point2* V, int degree ) */
	public boolean ControlPolygonFlatEnough(Position[] V, int degree){
	    int     i;        /* Index variable        */
	    double  value;
	    double  max_distance_above;
	    double  max_distance_below;
	    double  error;        /* Precision of root        */
	    double  intercept_1,
	            intercept_2,
	            left_intercept,
	            right_intercept;
	    double  a, b, c;    /* Coefficients of implicit    */
	            /* eqn for line from V[0]-V[deg]*/
	    double  det, dInv;
	    double  a1, b1, c1, a2, b2, c2;

	    /* Derive the implicit equation for line connecting first *'
	    /*  and last control points */
	    a = V[0].getY() - V[degree].getY();
	    b = V[degree].getX() - V[0].getX();
	    c = V[0].getX() * V[degree].getY() - V[degree].getX() * V[0].getY();

	    max_distance_above = max_distance_below = 0.0;
	    
	    for (i = 1; i < degree; i++)
	    {
	        value = a * V[i].getX() + b * V[i].getY() + c;
	       
	        if (value > max_distance_above)
	        {
	            max_distance_above = value;
	        }
	        else if (value < max_distance_below)
	        {
	            max_distance_below = value;
	        }
	    }

	    /*  Implicit equation for zero line */
	    a1 = 0.0;
	    b1 = 1.0;
	    c1 = 0.0;

	    /*  Implicit equation for "above" line */
	    a2 = a;
	    b2 = b;
	    c2 = c - max_distance_above;

	    det = a1 * b2 - a2 * b1;
	    dInv = 1.0/det;

	    intercept_1 = (b1 * c2 - b2 * c1) * dInv;

	    /*  Implicit equation for "below" line */
	    a2 = a;
	    b2 = b;
	    c2 = c - max_distance_below;

	    det = a1 * b2 - a2 * b1;
	    dInv = 1.0/det;

	    intercept_2 = (b1 * c2 - b2 * c1) * dInv;

	    /* Compute intercepts of bounding box    */
	    left_intercept = Math.min(intercept_1, intercept_2);
	    right_intercept = Math.max(intercept_1, intercept_2);

	    error = right_intercept - left_intercept;

	    return (error < EPSILON)? true : false;
	}


	/*
	 *  ComputeXIntercept :
	 *	Compute intersection of chord from first control point to last
	 *  	with 0-axis.
	 * 
	 *
	 * 	    Point2 	*V;			  Control points	
	 *      int		degree; 	  Degree of curve
	 */
	public double ComputeXIntercept(Position[] V, int degree)
	{
	    double	XNM, YNM, XMK, YMK;
	    double	det, detInv;
	    double	S;

	    XNM = V[degree].getX() - V[0].getX();
	    YNM = V[degree].getY() - V[0].getY();
	    XMK = V[0].getX();
	    YMK = V[0].getY();

	    det =  -YNM;
	    detInv = 1.0/det;

	    S = (XNM*YMK - YNM*XMK) * detInv;

	    return S;
	}


	/*
	 *  Bezier : 
	 *	Evaluate a Bezier curve at a particular parameter value
	 *      Fill in control points for resulting sub-curves if "Left" and
	 *	"Right" are non-null.
	 * 
	 * 	    int 	degree;		 Degree of bezier curve	
	    Point2 	*V;			 Control pts			
	    double 	t;			 Parameter value		
	    Point2 	*Left;		 RETURN left half ctl pts	
	    Point2 	*Right;		 RETURN right half ctl pts	
	 * 
	 */
	public Position Bezier(Position[] V, int degree, double t, Position[] Left, Position[] Right)

	{
	    int 	i, j;		/* Index variables	*/
	    Position[][] Vtemp = new Position[W_DEGREE+1][W_DEGREE+1];


	    /* Copy control points	*/
	    for (j =0; j <= degree; j++) {
			Vtemp[0][j] = V[j];
	    }

	    /* Triangle computation	*/
	    for (i = 1; i <= degree; i++) {	
			for (j =0 ; j <= degree - i; j++) {
				Vtemp[i][j] = new Position((1.0 - t) * Vtemp[i-1][j].getX() + t * Vtemp[i-1][j+1].getX(), 
						(1.0 - t) * Vtemp[i-1][j].getY() + t * Vtemp[i-1][j+1].getY());
			}
	    }
	    
	    if (Left != null) {
			for (j = 0; j <= degree; j++) {
		    	Left[j]  = Vtemp[j][0];
			}
	    }
	    if (Right != null) {
			for (j = 0; j <= degree; j++) {
		    	Right[j] = Vtemp[degree-j][j];
			}
	    }

	    return (Vtemp[degree][0]);
	}

//ORIGINAL FUNCTION
//	static Vector2 V2ScaleII(v, s)
//    Vector2	*v;
//    double	s;
//	{
//	    Vector2 result;
//	
//	    result.x = v->x * s; result.y = v->y * s;
//	    return (result);
//	}
	public static Position V2ScaleII(Position v, double s)
	{
	    Position result;

	    result = new Position(v.getX() * s, v.getY() * s);
	    return (result);
	}
	
	/* returns squared length of input vector */
	double V2SquaredLength(Position a){
		return((a.getX() * a.getX())+(a.getY() * a.getY()));
	}

	/* return vector difference c = a-b */
	Position V2Sub(Position a, Position b){
		double x =  a.getX() - b.getX();
		double y = a.getY() - b.getY();
		Position c = new Position(x, y);
		return c;
	}


/* return the dot product of vectors a and b */
	double V2Dot(Position a, Position b){
		return((a.getX() * b.getX())+(a.getY() * b.getY()));
	}


	public static void main(String[] args)	{
	   
		Position[] bezCurve = {	/*  A cubic Bezier curve	*/
		new Position( 0.0, 0.0 ),
		new Position( 1.0, 0.0 ),
		new Position( 3.0, 0.0 ),
		new Position( 4.0, 0.0 ),
	    };
	    Position arbPoint = new Position( 2.5, 9.0 ); /*Some arbitrary point*/
	    double pointRatio = 0.0;
	    
	    BezierCToJavaTest blah = new BezierCToJavaTest();
	    BezierCurve bc = new BezierCurve(bezCurve[0], bezCurve[1], bezCurve[2], bezCurve[3]);
	    System.out.println(bc.getPosition(.6));
	    System.out.println(blah.Bezier(bezCurve, 3, .6, null, null));
	    /*  Find the closest point */
//	    for(int i = 0; i < 1; i++){
//	    	pointRatio = blah.NearestPointOnCurve(arbPoint, bezCurve);
//	    	System.out.println(pointRatio);
//	    }
//	    for(int i = 0; i < 1; i++){
//	    	pointRatio = bc.getClosestPointRatio(arbPoint);
//	    }
	    System.out.println(pointRatio);
	    
	}
	

//	STORAGE FOR THE OLD getClosestPointRatio method in case of emergency
//	 * first finds the closest point in the curve to p, then returns that
//	 * position's ratio along the curve (ie [0,1])
//	 * 
//	 * @param p		the position to find the ratio for
//	 * @return		the ratio along the curve [0,1] of p
//	 */
//	public double getClosestPointRatio(Position p) 
//	{
//		return getClosestPointRatioRec(p, 0.0, 1.0, 0.1, 4);
//	}
//	
//	/**
//	 * Recursive helper method for getClosestPointRatio.
//	 * @param p The position in question.
//	 * @param leftBound The leftmost percent of the curve to be considered for this execution.
//	 * @param rightBound The rightmost percent of the curve to be considered for this execution.
//	 * @param stepSize The percentage of the curve between the bounds to advance each iteration.
//	 * @param numRecursions The number of recursions to execute before termination.
//	 * @return
//	 */
//	private double getClosestPointRatioRec(Position p, double leftBound, double rightBound, double stepSize, int numRecursions)
//	{
//		double minVal = Double.POSITIVE_INFINITY;
//		double ret = Double.POSITIVE_INFINITY;
//		Position comp;
//		int slice = 0;
//		int i = 0;
//		for(double d = leftBound; d < (rightBound - .00005); d += stepSize)
//		{
//			comp = getPosition(d).getPosition();
//			double value = p.distanceSquared(comp);
//			if(value < minVal)
//			{
//				minVal = value;
//				slice = i;
//				ret = d;
//			}
//			i++;
//		}
//		
//		if(numRecursions == 0)
//		{
//			return ret + slice*stepSize;
//		}
//		leftBound = leftBound + slice*stepSize;
//		return getClosestPointRatioRec(p, leftBound, leftBound + stepSize, stepSize / 10, numRecursions - 1);
//	}

}
