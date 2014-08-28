package common.shapes;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;


/**	A sphere created by normalizing points on a cube made of triangles.<br>
 * 	This method was illustrated by user Kevin at:
 * <code>http://stackoverflow.com/questions/7687148/drawing-sphere-in-opengl-without-using-glusphere</code>.
 * <br><br>
 * 	The technique used to recursively subdivide triangles was found in a Powerpoint by:<br>
 * 	<code>Barb Ericson, Georgia Insitute of Technology, March 2010</code><br>
 *  And then modified to use a depth parameter, as done in:<br>
 *  <code>http://www.glprogramming.com/red/chapter02.html#name8</code><br>
 * 
 * @author Mike Facelle
 * @reference (1): User <code>Kevin</code> posted at: <code>http://stackoverflow.com/questions/7687148/drawing-sphere-in-opengl-without-using-glusphere</code>
 * @reference (2): OpenGL Programming Guide, Example 2-14 and on <code>http://www.glprogramming.com/red/chapter02.html#name8</code>
 * @reference (3): Powerpoint by <code>Barb Ericson, Georgia Insitute of Technology, March 2010<code>
 */
public class MySphere 
{
	/** Default radius */
	private static float DEFAULT_R = 10.0f;
	/** Default resolution. Should be a low value.  At resolution >= 4, loading
	 * 	and rendering take quite some time */
	private static int DEFAULT_RES = 2;
	
	/** Number of original points found in the cube */
	private static int NUM_POINTS = 8;
	
	/** Depth parameter for recursive subdivision.  It determines the number of
	 *  times each triangle is subdivided */
	private int resolution = DEFAULT_RES;
	/** Radius of the original cube that gets stretched into a sphere*/
	private float r_cube = DEFAULT_R / 2;
	
	private float x = 0;
	private float y = 0;
	private float z = 0;
	private float r = 1.0f;
	private float g = 1.0f;
	private float b = 1.0f;
	private float radius = 10.0f;
	
	private Point[] p;
	private ArrayList<MyTriangle> triangles;	// original triangles (forms a cube)
	private ArrayList<MyTriangle> tri_norms;	// normalize triangles (forms a sphere)
	
// ============================================================	

	private MySphere(Builder builder)
	{
		this(builder.radius, builder.resolution, builder.x, builder.y, builder.z, builder.r, builder.g, builder.b);
	}
	
	public MySphere()
	{
		this(DEFAULT_R, DEFAULT_RES, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}
	// ----
	public MySphere(float rad, int res, float xx, float yy, float zz)
	{
		this(rad, res, xx, yy, zz, 1.0f, 1.0f, 1.0f);
	}
	// ----
	public MySphere(float rad, int res, float xx, float yy, float zz, float rr, float gg, float bb)
	{
		radius = rad;
		r_cube = radius / 2;
		resolution = res;
		x = xx;
		y = yy;
		z = zz;
		r = rr;
		g = gg;
		b = bb;
		initialize();
	}
		
// ============================================================	

	private void initialize()
	{
		generatePoints();
		generateShapes();
		normalizeShapes();
	}
	
// ============================================================	
	
	/** Creates the 8 points of the cube 
	 * 	Order is very important, as they are referenced later
	 * 	and all triangles must be created CCW for their side to face outwards
	 */
	private void generatePoints()
	{
		p = new Point[NUM_POINTS];
		// these are in a specific order, for subdivision later
		p[0] = new Point(new float[]{ -radius, -radius, -radius });
		p[1] = new Point(new float[]{ -radius,  radius, -radius });
		p[2] = new Point(new float[]{  radius,  radius, -radius });
		p[3] = new Point(new float[]{  radius, -radius, -radius });
		
		p[4] = new Point(new float[]{ -radius, -radius,  radius });
		p[5] = new Point(new float[]{  radius, -radius,  radius });
		p[6] = new Point(new float[]{  radius,  radius,  radius });
		p[7] = new Point(new float[]{ -radius,  radius,  radius });
	}
	
	// -------------------------------------------
	
	private void generateShapes()
	{
		triangles = new ArrayList<MyTriangle>();
		// add each group of subdivided triangles, 2 for each face (12 in all)
		// which points to make it counter-clockwise was worked out on paper (hopefully my axes aren't backwards)
		// 012,230 ; 456,674 ; 176,621 ; 035,540 ; 047,710 ; 526,653
		
		triangles.addAll(new MyTriangle(p[0], p[1], p[2], r, g, b).subdivideTriangle(resolution));
		triangles.addAll(new MyTriangle(p[2], p[3], p[0], r, g, b).subdivideTriangle(resolution));
		triangles.addAll(new MyTriangle(p[4], p[5], p[6], r, g, b).subdivideTriangle(resolution));
		triangles.addAll(new MyTriangle(p[6], p[7], p[4], r, g, b).subdivideTriangle(resolution));
		triangles.addAll(new MyTriangle(p[1], p[7], p[6], r, g, b).subdivideTriangle(resolution));
		triangles.addAll(new MyTriangle(p[6], p[2], p[1], r, g, b).subdivideTriangle(resolution));
		triangles.addAll(new MyTriangle(p[0], p[3], p[5], r, g, b).subdivideTriangle(resolution));
		triangles.addAll(new MyTriangle(p[5], p[4], p[0], r, g, b).subdivideTriangle(resolution));
		triangles.addAll(new MyTriangle(p[0], p[4], p[7], r, g, b).subdivideTriangle(resolution));
		triangles.addAll(new MyTriangle(p[7], p[1], p[0], r, g, b).subdivideTriangle(resolution));
		triangles.addAll(new MyTriangle(p[3], p[2], p[6], r, g, b).subdivideTriangle(resolution));
		triangles.addAll(new MyTriangle(p[6], p[5], p[3], r, g, b).subdivideTriangle(resolution));
	}
	
	// -------------------------------------------

	private void normalizeShapes()
	{
		tri_norms = new ArrayList<MyTriangle>();
		for (MyTriangle t : triangles)
			tri_norms.add(t.normalize(new Point(0,0,0), radius));	
	}
	
	// -------------------------------------------

	public void draw()
	{
		draw(false, true);
	}
	/** Draws the sphere with or without lines and faces for each triangle */
	public void draw(boolean withLines, boolean withFaces)
	{
		glPushMatrix();
		glTranslatef(x,y,z);
		for (int i = 0; i < tri_norms.size(); i++) {
			MyTriangle t = tri_norms.get(i);
			t.draw(withLines, withFaces);
		}
		glPopMatrix();
	}
	
	
// =========================================================
	// get and set
		
	public float x()	{ return x; }
	public float y() 	{ return y; }
	public float z()	{ return z; }
	public float r()	{ return r; }
	public float g()	{ return g; }
	public float b()	{ return b; }
	public float resolution()	{ return resolution; }
	public float radius()	{ return radius; }
		
	public void setX(float xx)	{ x = xx; }
	public void setY(float yy)	{ y = yy; }
	public void setZ(float zz)	{ z = zz; }
	public void setPosition(float xx, float yy, float zz)
	{	x = xx; y = yy; z = zz; }
	public void setR(float rr) 	{ r = rr; }
	public void setG(float gg) 	{ g = gg; }
	public void setB(float bb) 	{ b = bb; }
	public void setColor(float rr, float gg, float bb)
	{	r = rr;	g = gg;	b = bb; }	
	public void setResolution(int res)	
	{ 
		resolution = res; 
		initialize();	// to re-create the sphere because it's resolution has changed
	}
	public void setRadius(float r)	
	{ 
		radius = r; 
		initialize();	// to re-create the sphere because it's size has changed
	}


	
	
// ==========================================================================================================
//	BUILDER CLASS
// ==========================================================================================================

	public static class Builder
	{
		private float x = 0;
		private float y = 0;
		private float z = 0;
		private float r = 1.0f;
		private float g = 1.0f;
		private float b = 1.0f;
		private float radius = 10.0f;
		private int resolution = DEFAULT_RES;
		
		// ============================================================	

		public Builder()
		{
			;
		}

		// ============================================================	

		public MySphere build()
		{
			return new MySphere(this);
		}
		
		// ============================================================	

		public Builder setPosition(float xx, float yy, float zz)
		{	
			x = xx; y = yy; z = zz; 
			return this;
		}
		public Builder setPosition(float[] r)
		{
			if (r.length != 3)	// number of points in 3d position vector is 3
				throw new IllegalArgumentException("[MySphere.Builder]Length of position vector " + r.length +
							" is not equal to 3");
			x = r[0];	y = r[1];	z = r[2];
			return this;
		}
		// ---
		public Builder setColor(float rr, float gg, float bb)
		{	
			if (rr < 0 || gg < 0 || bb < 0)
				throw new IllegalArgumentException("[MySphere.Builder]RGB color value { " + rr + ", " + gg + "," + bb + " } contains " +
						"one or more negative values.");
			r = rr;	g = gg;	b = bb; 
			return this;
		}
		public Builder setColor(float[] c)
		{	
			if (c.length != 3)	// number of points in color vector is 3
			if (c[0] < 0 || c[1] < 0 || c[2] < 0)
				throw new IllegalArgumentException("[MySphere.Builder]RGB color value { " + c[0] + ", " + c[1] + "," + c[2] + " } contains " +
						"one or more negative values.");
			r = c[0];	g = c[1];	b = c[2]; 
			return this;
		}
		// ---
		public Builder setResolution(int res)	
		{ 
			resolution = res; 
			return this;
		}
		// ---
		public Builder setRadius(float r)	
		{ 
			if (r <= 0)
				throw new IllegalArgumentException("[MySphere.Builder]Radius " + r + " is less than 0");
			radius = r; 
			return this;
		}
		
// ============================================================	
	}
	
}
