package common.shapes;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.jbox2d.common.Vec2;

/**	A simple 2D line class. Contains two points [(x1,y1), (x2,y2)] that represent the line.
 * 	The points are always ordered such that x1 < x2<br>
 * 	Has constructors and set methods utilizing either 4 floats or 2 Vec2 objects.  This allows
 * 	for ease of use inside some applications that use JBox2D, which makes use of Vec2 objects.<br>
 * 	<br>
 * 	<b>Note:</b> All locations are in pixel-length.
 * 
 * @author Mike Facelle
 *
 */
public class MyLine2D
{
	/** The first point (in pixels) */
	private float x1, y1;	// in px
	/** The second point (in pixels) */
	private float x2, y2;	// in px
	/** The line's slope */
	private float slope;	// the slope of the line
	/** The y-intercept*/
	private float yInt;		// the y-intercept
	/** The color to be used during draw() */
	private float r, g, b;
	
// ============================================================	

	public MyLine2D()
	{
		this(0, 0, 0, 0, 1, 1, 1);
	}
	
	// ---
	
	public MyLine2D(Vec2 p1, Vec2 p2, float rr, float gg, float bb)
	{
		this(p1.x, p1.y, p2.x, p2.y, rr, gg, bb);
	}
	// ---
	public MyLine2D(float xx1, float yy1, float xx2, float yy2, float rr, float gg, float bb)
	{
		if (xx1 <= xx2) {
			x1 = xx1;	y1 = yy1;	x2 = xx2;	y2 = yy2; 
		}
		else {
			x1 = xx2;	y1 = yy2;	x2 = xx1;	y2 = yy1; 
		}
		r = rr;
		g = gg;
		b = bb;
		calculateSlopeAndIntercept();
	}
	
// ============================================================	
// operations

	public void reset()
	{
		setPosition(0,0,0,0);
	}
	
	// ---
		
	/** Determines if the <code>line</code> variable intersects the cell who's
	 * 	location and radius are passed to this method.<br>
	 * 	Uses the equation of the line, <code>y = mx + b, to do this</code>.
	 * 
	 * @return	If <code>line</code> intersects
	 */
	public boolean intersects(Vec2 loc, float r)
	{
		float x = loc.x;			// the x-coordinate to test
		float y	= slope*x + yInt;	// the y-coordinate to test
		// if the test y-point falls inside the circle passed, return true
		return (y >= loc.y-r && y <= loc.y+r) ? true : false;
	}
	
	// ---
	
	/** Calculate both the slope of the line and the y-intercept.<br>
	 * 	Slope is done by performing: <code>slope = (y2-y1) / (x2-x1)</code>.<br>
	 *  Y-intercept is done by performing: <code>yInt = y1 - mx1</code> ( from point-slope form: y0-y1 = m(x0-x1) ).<br>
	 */
	private void calculateSlopeAndIntercept()
	{
		try {
			slope = (y2 - y1) / (x2 - x1);
		} catch (ArithmeticException ex)	{
			// if division by 0, set slope to Infinity
			slope = Float.POSITIVE_INFINITY;
		}
		// calculate the y-intercept, based on point-slope form and setting x=0
		yInt = y1 - slope*x1;
	}
	
// ============================================================	
// draw
	
	public void draw()
	{
		glBegin(GL_LINE_STRIP);
		glColor3f(r, g, b);
		glVertex2f(x1, y1);
		glVertex2f(x2, y2);
		glEnd();
	}
	
// ============================================================	
// get and set
	
	public void setPosition(Vec2 p1, Vec2 p2)	
	{	setPosition(p1.x, p1.y, p2.x, p2.y); }
	public void setPosition(float xx1, float yy1, float xx2, float yy2)	
	{	
		if (xx1 <= xx2) {
			x1 = xx1;	y1 = yy1;	x2 = xx2;	y2 = yy2; 
		}
		else {
			x1 = xx2;	y1 = yy2;	x2 = xx1;	y2 = yy1; 
		}
		calculateSlopeAndIntercept();	// need to re-calculate this
	}
	public void setColor(float rr, float gg, float bb)
	{	r = rr;	g = gg;	b = bb; }
	// ---
	public Vec2[] getPosition()	{ return new Vec2[] {new Vec2(x1,y1), new Vec2(x2,y2) }; }
	public float[] getColor()	{ return new float[] {r, g, b}; }
	public float r()		{ return r; }
	public float g()		{ return g; }
	public float b()		{ return b; }
	public float x1()		{ return x1; }
	public float y1()		{ return y1; }
	public float x2()		{ return x2; }
	public float y2()		{ return y2; }
	public float slope()	{ return slope; }
	public float yInt()		{ return yInt; }
}
