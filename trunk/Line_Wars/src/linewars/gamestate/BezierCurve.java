package linewars.gamestate;

import java.util.ArrayList;
import java.util.List;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

/**
 * 
 * @author John George
 * This class represents a Bezier Curve represented by four control points and calculated by the formula found at
 * http://en.wikipedia.org/wiki/Bezier_curve.
 * It contains methods for calculating various attributes of the curve and finding positions along it.
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
	
	//The length of this curve.
	private double length;
	
	//The step size used in the calculateLength method.
	private static final double STEP_SIZE = 0.001;
	
	/*
	 * These are for the getClosestPointRatio method.
	 */
	//Maximum depth for recursion
	int		MAXDEPTH = 10;
	
	//Flatness control value
	double	EPSILON = Math.pow(2.0, -MAXDEPTH-1);
	
	//Cubic Bezier curve
	int 	DEGREE = 3;
	
	//Degree of equation to find roots of.
	int		W_DEGREE = 5;
	
	/**
	 * Creates a BezierCurve object using the information found in the supplied ConfigData.
	 * @param configuration 
	 * 		The ConfigData object which contains the information used to construct this curve.
	 * @return 
	 * 		A new BezierCurve object using the information from configuration.
	 */
	public static BezierCurve buildCurve(ConfigData configuration){
		List<ConfigData> positions = configuration.getConfigList(ParserKeys.controlPoint);
		ArrayList<Position> parsedPositions = new ArrayList<Position>();
		for(ConfigData toParse : positions){
			parsedPositions.add(new Position(toParse));
		}
		return buildCurve(parsedPositions);
	}
	
	/**
	 * Creates a BezierCurve object using the points in the supplied list as the ordered control points of the curve.
	 * @param orderedControlPoints
	 * 		The list of points to create a curve from.
	 * @return
	 * 		A new BezierCurve object consisting of the control points supplied in orderedControlPoints.
	 */
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
	
	/**
	 * Creates a new BezierCurve object representing a linear Bezier curve using the two supplied positions as control points.
	 * @param p0
	 * 		The start position for the new curve.
	 * @param p1
	 * 		The end position for the new curve.
	 * @return
	 * 		A new BezierCurve object with the specified start and endpoints.
	 */
	public static BezierCurve buildCurve(Position p0, Position p1){
		Position newP1 = p0.scale(.5).add(p1.scale(.5));
		return buildCurve(p0, newP1, p1);
	}
	
	/**
	 * Creates a new BezierCurve object representing a quadratic Bezier curve using the three supplied positions as control
	 * points.
	 * @param p0
	 * 		The start position for the new curve.
	 * @param p1
	 * 		The intermediate control point for the new curve.
	 * @param p2
	 * 		The end position for the new curve.
	 * @return
	 * 		A new BezierCurve object with the specified control points.
	 */
	public static BezierCurve buildCurve(Position p0, Position p1, Position p2){
		Position newP1 = p0.scale(1.0 / 3).add(p1.scale(1 - (1.0 / 3)));
		Position newP2 = p1.scale(2.0 / 3).add(p2.scale(1 - (2.0 / 3)));
		return new BezierCurve(p0, newP1, newP2, p2);
	}
	
	/**
	 * Creates a new BezierCurve object representing a cubic Bezier curve using the three supplied positions as control
	 * points. 
	 * @param p0
	 * 		The start position for the new curve.
	 * @param p1
	 * 		The first intermediate control point for the new curve.
	 * @param p2
	 * 		The second intermediate control point for the new curve.
	 * @param p3
	 * 		The end position for the new curve.
	 * @return
	 * 		A new BezierCurve object with the specified control points.
	 */
	public static BezierCurve buildCurve(Position p0, Position p1, Position p2, Position p3){
		return new BezierCurve(p0, p1, p2, p3);
	}
	
	/**
	 * Creates a new BezierCurve object representing a cubic Bezier curve using the three supplied positions as control
	 * points and calculates the length. 
	 * @param p0
	 * 		The start position for the new curve.
	 * @param p1
	 * 		The first intermediate control point for the new curve.
	 * @param p2
	 * 		The second intermediate control point for the new curve.
	 * @param p3
	 * 		The end position for the new curve.
	 * @return
	 * 		A new BezierCurve object with the specified control points.
	 */
	public BezierCurve(Position p0, Position p1, Position p2, Position p3)
	{
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		
		calculateLength(STEP_SIZE);
	}
	
	/**
	 * 
	 * @return
	 * 		The first control point of this Bezier curve.
	 */
	public Position getP0()
	{
		return p0;
	}

	/**
	 * 
	 * @return
	 * 		The second control point of this Bezier curve.
	 */
	public Position getP1()
	{
		return p1;
	}
	
	
	/**
	 * 
	 * @return
	 * 		The third control point of this Bezier curve.
	 */
	public Position getP2()
	{
		return p2;
	}

	/**
	 * 
	 * @return
	 * 		The fourth control point of this Bezier curve.
	 */
	public Position getP3()
	{
		return p3;
	}
	
	/**
	 * Sets the first control point to p.
	 * @param p
	 * 		The new value for the first control point.
	 */
	public void setP0(Position p)
	{
		p0 = p;
	}
	
	/**
	 * Sets the second control point to p.
	 * @param p
	 * 		The new value for the second control point.
	 */
	public void setP1(Position p)
	{
		p1 = p;
	}
	
	/**
	 * Sets the third control point to p.
	 * @param p
	 * 		The new value for the third control point.
	 */
	public void setP2(Position p)
	{
		p2 = p;
	}
	
	/**
	 * Sets the fourth control point to p.
	 * @param p
	 * 		The new value for the fourth control point.
	 */
	public void setP3(Position p)
	{
		p3 = p;
	}
	
	/**
	 * 
	 * @return
	 * 		The approximate length of this BezierCurve.
	 */
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
	 * 
	 * @param quad 
	 * 		The position along the quadratic bezier curve represented by the first 3 points.
	 * @param cube
	 * 		 The position along the cubic bezier curve (all 4 points)
	 * @return 
	 * 		The rotation at point cube.
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
	 * 		The percentage along the 3-point curve to get the position.
	 * @return
	 * 		The position on the quadratic Bezier curve that is pos percent along it.
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
	 * Original C code from:
	 * 
	 * Solving the Nearest Point-on-Curve Problem and
	 *	A Bezier Curve-Based Root-Finder
	 *	by Philip J. Schneider
	 *	from "Graphics Gems", Academic Press, 1990
	 */
	
	
	/**
	 * Compute the parameter value of the point on a Bezier curve
	 * segment closest to some arbitrary, user-input point.
	 * 
	 * @param P
	 * 		The user-supplied point.
	 * @return
	 * 		The point on the curve at that parameter value.
	 */
	public double getClosestPointRatio(Position P)	{
		//Ctl pts for 5th-degree eqn
	    Position[] w;
	    
	    //Possible roots
	    double[] t_candidate = new double[W_DEGREE];
	    
	    //Number of roots found
	    int 	n_solutions;		
	    
	    //Parameter value of closest pt
	    double	t;

	    Position[] V = { p0, p1, p2, p3 };
	    
	    //  Convert problem to 5th-degree Bezier form
	    w = ConvertToBezierForm(P, V);

	    // Find all possible roots of 5th-degree equation 
	    n_solutions = FindRoots(w, W_DEGREE, t_candidate, 0);

	    // Compare distances of P to all candidates, and to t=0, and t=1 
	    {
			double 	dist, new_dist;
			Position p;
			int		i;

		
		// Check distance to beginning of curve, where t = 0	
			dist = V2SquaredLength(V2Sub(P, V[0]));
	        t = 0.0;

		// Find distances for candidate points	
	        for (i = 0; i < n_solutions; i++) {
		    	p = Bezier(V, DEGREE, t_candidate[i], null, null);
		    	new_dist = V2SquaredLength(V2Sub(P, p));
		    	if (new_dist < dist) {
	                	dist = new_dist;
		        		t = t_candidate[i];
	    	    }
	        }

		// Finally, look at distance to end point, where t = 1.0 
			new_dist = V2SquaredLength(V2Sub(P, V[DEGREE]));
	        	if (new_dist < dist) {
	            	dist = new_dist;
	            	t = 1.0;
	        }
	    }

	    return t;
	}

	
	/**
	 * Given a point and a Bezier curve, generate a 5th-degree
	 * Bezier-format equation whose solution finds the point on the 
	 * curve nearest the user-defined point.
	 * 
	 * @param P
	 * 		The point to find t for.
	 * @param V
	 * 		The control points.
	 * @return
	 * 		An array of points representing the solution curve.
	 * 	
	 */
	public Position[] ConvertToBezierForm(Position P, Position[] V){

	    int 	i, j, k, m, n, ub, lb;	
	    
	    //Table indices
	    int 	row, column;
	    
	    //V(i)'s - P	
	    Position[] c = new Position[DEGREE+1];
	    
	    //V(i+1) - V(i)
	    Position[] d = new Position[DEGREE];
	    
	    //Ctl pts of 5th-degree curve
	    Position[] 	w;
	    
	    //Dot product of c, d
	    double[][] cdTable = new double[3][4];
	    
	    //Precomputed "z" for cubics
	    double[][] z = {
		{1.0, 0.6, 0.3, 0.1},
		{0.4, 0.6, 0.6, 0.4},
		{0.1, 0.3, 0.6, 1.0},
	    };


	    /* 
	     * Determine the c's -- these are vectors created by subtracting
	     * point P from each of the control points.		
		 */
	    for (i = 0; i <= DEGREE; i++) {
			c[i] = V2Sub(V[i], P);
	    }
	    
	    /* 
	     * Determine the d's -- these are vectors created by subtracting
	     * each control point from the next.
	    */ 				
	    for (i = 0; i <= DEGREE - 1; i++) { 
			d[i] = V2ScaleII(V2Sub(V[i+1], V[i]), 3.0);
	    }

	    /* 
	     * Create the c,d table -- this is a table of dot products of the 
	     * c's and d's	
	     */ 						
	    for (row = 0; row <= DEGREE - 1; row++) {
			for (column = 0; column <= DEGREE; column++) {
		    	cdTable[row][column] = V2Dot(d[row], c[column]);
			}
	    }

	    /* 
	     * Now, apply the z's to the dot products, on the skew diagonal
	     * Also, set up the x-values, making these "points"		
	     */
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

	
	/**
	 * Given a 5th-degree equation in Bernstein-Bezier form, find all
	 * of the roots in the interval [0, 1].
	 * 
	 * @param w
	 * 		The control points
	 * @param degree
	 * 		The degree of the polynomial
	 * @param t
	 * 		RETURN candidate t-values
	 * @param depth
	 * 		The depth of the recursion
	 * @return
	 * 		The number of roots found.
	 */
	public int FindRoots(Position[] w, int degree, double[] t, int depth){  
	    int 	i;
	    //New left and right
	    Position[] Left = new Position[W_DEGREE+1];
	    
	    //Control polygons.
	    Position[] Right = new Position[W_DEGREE+1];
	    
	    //Solution count from children.
	    int 	left_count,	right_count;
	    
	    //Solutions from kids.
	    double[] left_t = new double[W_DEGREE+1];
		double[] right_t = new double[W_DEGREE+1];

	    switch (CrossingCount(w, degree)) {
	    //No solutions here
	       	case 0 : {
		     return 0;	
		}
	    //Unique solution
		case 1 : {
		    /* 
		     * Stop recursion when the tree is deep enough.
		     * If deep enough, return 1 solution at midpoint.
		  	 */
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

	    /*
	     *  Otherwise, solve recursively after
	     *  subdividing control polygon
	     */
	    Bezier(w, degree, 0.5, Left, Right);
	    left_count  = FindRoots(Left,  degree, left_t, depth+1);
	    right_count = FindRoots(Right, degree, right_t, depth+1);


	    // Gather solutions together
	    for (i = 0; i < left_count; i++) {
	        t[i] = left_t[i];
	    }
	    for (i = 0; i < right_count; i++) {
	 		t[i+left_count] = right_t[i];
	    }

	    // Send back total number of solutions	
	    return (left_count+right_count);
	}

	
	/**
	 * Count the number of times a Bezier control polygon
	 * crosses the 0-axis. This number is >= the number of roots.
	 * 
	 * @param V
	 * 		Control points of Bezier curve.
	 * @param degree
	 * 		Degree of the Bezier curve.
	 * @return
	 * 		The number of crossings.
	 */
	public int CrossingCount(Position[] V, int degree){
	    int 	i;	
	    //Number of zero-crossings.
	    int 	n_crossings = 0;
	    
	    //Sign of coefficients
	    double	sign, old_sign;

	    sign = old_sign = Math.signum(V[0].getY());
	    for (i = 1; i <= degree; i++) {
			sign = Math.signum(V[i].getY());
			if (sign != old_sign) n_crossings++;
			old_sign = sign;
	    }
	    return n_crossings;
	}

	
	/**
	 * Check if the control polygon of a Bezier curve is flat enough
	 * for recursive subdivision to bottom out.
	 * 
	 * @param V
	 * 		Control points
	 * @param degree
	 * 		Degree of polynomial
	 * @return
	 * 		true if the curve is flat enough, false otherwise.
	 */
	public boolean ControlPolygonFlatEnough(Position[] V, int degree){
		//Index variable
	    int     i;
	    double  value;
	    double  max_distance_above;
	    double  max_distance_below;
	    
	    //Precision of root
	    double  error;
	    double  intercept_1,
	            intercept_2,
	            left_intercept,
	            right_intercept;
	    
	    //Coefficients of implicit eqn for line from V[0]-V[deg]
	    double  a, b, c; 
	    double  det, dInv;
	    double  a1, b1, c1, a2, b2, c2;

	    /* 
	     * Derive the implicit equation for line connecting first
	     * and last control points.
	     */
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

	    //  Implicit equation for zero line 
	    a1 = 0.0;
	    b1 = 1.0;
	    c1 = 0.0;

	    //  Implicit equation for "above" line 
	    a2 = a;
	    b2 = b;
	    c2 = c - max_distance_above;

	    det = a1 * b2 - a2 * b1;
	    dInv = 1.0/det;

	    intercept_1 = (b1 * c2 - b2 * c1) * dInv;

	    //  Implicit equation for "below" line 
	    a2 = a;
	    b2 = b;
	    c2 = c - max_distance_below;

	    det = a1 * b2 - a2 * b1;
	    dInv = 1.0/det;

	    intercept_2 = (b1 * c2 - b2 * c1) * dInv;

	    // Compute intercepts of bounding box   
	    left_intercept = Math.min(intercept_1, intercept_2);
	    right_intercept = Math.max(intercept_1, intercept_2);

	    error = right_intercept - left_intercept;

	    return (error < EPSILON)? true : false;
	}

	
	/**
	 * Compute intersection of chord from first control point to last
	 * with 0-axis.
	 * 
	 * @param V
	 * 		Control points.
	 * @param degree
	 * 		Degree of curve
	 * @return
	 * 		The X-intercept.
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

	
	/**
	 * Evaluate a Bezier curve at a particular parameter value.
	 * Fill in control points for resulting sub-curves if "Left" and 
	 * "Right" are non-null.
	 * 
	 * @param degree
	 * 		Degree of Bezier curve
	 * @param V
	 * 		Control points
	 * @param t
	 * 		Parameter value
	 * @param Left
	 * 		RETURN left half control points
	 * @param Right
	 * 		RETURN right half control points
	 * @return
	 * 		The position that is t% along the curve represented in V.
	 */
	private Position Bezier(Position[] V, int degree, double t, Position[] Left, Position[] Right)

	{
		//Index variables.
	    int 	i, j;
	    Position[][] Vtemp = new Position[W_DEGREE+1][W_DEGREE+1];


	    // Copy control points	
	    for (j =0; j <= degree; j++) {
			Vtemp[0][j] = V[j];
	    }

	    // Triangle computation	
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
	
	// returns squared length of input vector 
	private double V2SquaredLength(Position a){
		return((a.getX() * a.getX())+(a.getY() * a.getY()));
	}

	// return vector difference c = a-b 
	private Position V2Sub(Position a, Position b){
		double x =  a.getX() - b.getX();
		double y = a.getY() - b.getY();
		Position c = new Position(x, y);
		return c;
	}


	// return the dot product of vectors a and b 
	private double V2Dot(Position a, Position b){
		return((a.getX() * b.getX())+(a.getY() * b.getY()));
	}
	
}
