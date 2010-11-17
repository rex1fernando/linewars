package linewars.gamestate;

import java.util.ArrayList;
import java.util.List;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

/**
 * 
 * @author John George
 *
 */
public strictfp class BezierCurve {
	/*
 	 * These points represent the 4 control
	 * points in a bezier curve, with p0 and p3 being the end points.
	 * 
	 * -Ryan Tew
	 */
	private Position p0;
	private Position p1;
	private Position p2;
	private Position p3;
	
	private double length;
	private static final double STEP_SIZE = 0.001;
	
	/*
	 * These are for the getClosestPointRatio method.
	 */
	int		MAXDEPTH = 5;	/*  Maximum depth for recursion */
	double	EPSILON = Math.pow(2.0, -MAXDEPTH-1); /*Flatness control value */
	int 	DEGREE = 3;		/*  Cubic Bezier curve		*/
	int		W_DEGREE = 5;		/*  Degree of eqn to find roots of */
	
	public static BezierCurve buildCurve(ConfigData configuration){
		List<ConfigData> positions = configuration.getConfigList(ParserKeys.controlPoint);
		ArrayList<Position> parsedPositions = new ArrayList<Position>();
		for(ConfigData toParse : positions){
			parsedPositions.add(new Position(toParse));
		}
		return buildCurve(parsedPositions);
	}
	
	public static BezierCurve buildCurve(List<Position> orderedControlPoints){
		if(orderedControlPoints.size() == 2){
			return buildCurve(orderedControlPoints.get(0), orderedControlPoints.get(1));
		}else if(orderedControlPoints.size() == 3){
			return buildCurve(orderedControlPoints.get(0), orderedControlPoints.get(1), orderedControlPoints.get(2));
		}else if(orderedControlPoints.size() == 4){
			return new BezierCurve(orderedControlPoints.get(0), orderedControlPoints.get(1), orderedControlPoints.get(2), orderedControlPoints.get(3));
		}
		throw new IllegalArgumentException("2 - 4 control points expected; " + orderedControlPoints.size() + " found.");
	}
	
	public static BezierCurve buildCurve(Position p0, Position p1){
		Position newP1 = p0.scale(.5).add(p1.scale(.5));
		return buildCurve(p0, newP1, p1);
	}
	
	public static BezierCurve buildCurve(Position p0, Position p1, Position p2){
		Position newP1 = p0.scale(1.0 / 3).add(p1.scale(1 - (1.0 / 3)));
		Position newP2 = p1.scale(2.0 / 3).add(p2.scale(1 - (2.0 / 3)));
		return new BezierCurve(p0, newP1, newP2, p2);
	}
	
	public static BezierCurve buildCurve(Position p0, Position p1, Position p2, Position p3){
		return new BezierCurve(p0, p1, p2, p3);
	}
	
	public BezierCurve(Position p0, Position p1, Position p2, Position p3)
	{
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		
		calculateLength(STEP_SIZE);
	}
	
	public Position getP0()
	{
		return p0;
	}

	public Position getP1()
	{
		return p1;
	}
	
	public Position getP2()
	{
		return p2;
	}

	public Position getP3()
	{
		return p3;
	}
	
	public void setP0(Position p)
	{
		p0 = p;
	}
	
	public void setP1(Position p)
	{
		p1 = p;
	}
	
	public void setP2(Position p)
	{
		p2 = p;
	}
	
	public void setP3(Position p)
	{
		p3 = p;
	}
	
	public double getLength()
	{
		return length;
	}
	
	/**
	 * Approximates the length of this Lane by stepping along it and calculating line segment lengths.
	 * @param stepSize The size of the step to be taken. Smaller steps increase accuracy and calculation time.
	 */
	private void calculateLength(double stepSize)
	{
		if(stepSize <= 0 || stepSize >= 1)
		{
			throw new IllegalArgumentException("stepSize must be between 0 and 1");
		}
		double total = 0;
		Transformation t1 = this.getPosition(0);
		Transformation t2;

		for(double d = stepSize; d <= 1; d += stepSize)
		{
			t2 = this.getPosition(d);
			double distance = Math.sqrt(t1.getPosition().distanceSquared(t2.getPosition()));
			total = total + distance;
			t1 = t2;
		}
		length = total;
	}
	
	/**
	 * Gets the position along the bezier curve represented by the percentage
	 * pos. This follows the equation found at
	 * 		<a href="http://en.wikipedia.org/wiki/Bezier_curve#Cubic_B.C3.A9zier_curves">http://en.wikipedia.org/wiki/Bezier_curve</a>
	 * B(t)= (1-t)^3 * P0 + 3(1-t)^2 * t * P1 + 3(1-t) * t^2 * P 2 + t^3 * P3 where t = [0,1].
	 * 
	 * @param pos
	 *            The percentage along the bezier curve to get a position.
	 * 
	 * @return The position along the bezier curve represented by the percentage
	 *         pos.
	 */
	public Transformation getPosition(double pos)
	{
		if(pos < 0 || pos > 1)
			throw new IllegalArgumentException("pos " + pos + " is out of range!");
		if(pos == 0){
			pos = .001;
		}else if(pos == 1){
			pos = .999;
		}
		
		double term0 = Math.pow((1 - pos), 3);
		double term1 = 3 * Math.pow(1 - pos, 2) * pos;
		double term2 = 3 * (1 - pos) * Math.pow(pos, 2);
		double term3 = Math.pow(pos, 3);

		double posX = term0 * getP0().getX() + term1 * getP1().getX()
				+ term2 * getP2().getX() + term3 * getP3().getX();
		double posY = term0 * getP0().getY() + term1 * getP1().getY()
				+ term2 * getP2().getY() + term3 * getP3().getY();

		Position quad = getQuadraticPos(pos);
		Position cube = new Position(posX, posY);
		double rot = calculateRot(quad, cube);
		return new Transformation(cube, rot);
	}
	
	/**
	 * Calculate the rotation at a point on the cubic bezier curve given the position on it.
	 * Implements the algorithm found at http://bimixual.org/AnimationLibrary/beziertangents.html
	 * @param quad The position along the quadratic bezier curve represented by the first 3 points.
	 * @param cube The position along the cubic bezier curve (all 4 points)
	 * @return The rotation at point cube.
	 */
	private double calculateRot(Position quad, Position cube)
	{
		double ret;
		
		double dy = (cube.getY() - quad.getY());
		double dx = (cube.getX() - quad.getX());

		ret = Math.atan2(dy, dx);

		return ret;
	}
	
	/**
	 * This method calculates the position along the 3-point bezier curve based on the actual 4-point curve.
	 * This will be used in getPosition to get the rotatation of the curve using the formula found at
	 * http://bimixual.org/AnimationLibrary/beziertangents.html
	 * @param pos
	 * @return
	 */
	private Position getQuadraticPos(double pos)
	{
		double term0 = Math.pow((1-pos), 2);
		double term1 = 2 * (1-pos) * pos;
		double term2 = Math.pow(pos, 2);
		
		double posX = term0 * getP0().getX() + term1 * getP1().getX()
						+ term2 * getP2().getX();
		double posY = term0 * getP0().getY() + term1 * getP1().getY()
						+ term2 * getP2().getY();
		
		return new Position(posX, posY);
	}
	
	
	/*
	 * Solving the Nearest Point-on-Curve Problem and
	 *	A Bezier Curve-Based Root-Finder
	 *	by Philip J. Schneider
	 *	from "Graphics Gems", Academic Press, 1990
	 *  NearestPointOnCurve :
	 *  	Compute the parameter value of the point on a Bezier
	 *		curve segment closest to some arbitrary, user-input point.
	 *		Return the point on the curve at that parameter value.
	 *		Param P: The user-supplied point
	 *		Param V: Control points of cubic Bezier 
	 */
	public double getClosestPointRatio(Position P)	{
	    Position[] w;			/* Ctl pts for 5th-degree eqn	*/
	    double[] t_candidate = new double[W_DEGREE];	/* Possible roots		*/     
	    int 	n_solutions;		/* Number of roots found	*/
	    double	t;			/* Parameter value of closest pt*/

	    Position[] V = { p0, p1, p2, p3 };
	    
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
	private Position Bezier(Position[] V, int degree, double t, Position[] Left, Position[] Right)

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

	private Position V2ScaleII(Position v, double s)
	{
	    Position result;

	    result = new Position(v.getX() * s, v.getY() * s);
	    return (result);
	}
	
	/* returns squared length of input vector */
	private double V2SquaredLength(Position a){
		return((a.getX() * a.getX())+(a.getY() * a.getY()));
	}

	/* return vector difference c = a-b */
	private Position V2Sub(Position a, Position b){
		double x =  a.getX() - b.getX();
		double y = a.getY() - b.getY();
		Position c = new Position(x, y);
		return c;
	}


/* return the dot product of vectors a and b */
	private double V2Dot(Position a, Position b){
		return((a.getX() * b.getX())+(a.getY() * b.getY()));
	}
	
}
