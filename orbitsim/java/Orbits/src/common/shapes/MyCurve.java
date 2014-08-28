package common.shapes;

import java.util.ArrayList;

import physics.satellites.Satellite;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glPopMatrix;

public class MyCurve
{
	private ArrayList<Point> points;
	private float r = 1.0f;
	private float g = 1.0f;
	private float b = 1.0f;
	private float a = 1.0f;
	
	// =========================================================

	public MyCurve()
	{
		this(new ArrayList<Point>(), 1.0f, 1.0f, 1.0f, 1.0f);
	}	
	public MyCurve(float[] xpts, float[] ypts)
	{
		if (xpts.length != ypts.length)
			throw new IllegalArgumentException("[MyCurve]: x and y point arrays are of different lengths : " + 
												xpts.length + ", " + ypts.length);
		points = new ArrayList<Point>();
		for (int i = 0; i < xpts.length; i++) {
			points.add(new Point(xpts[i], ypts[i], 0.0f));
		}
		this.r = 1.0f;
		this.g = 1.0f;
		this.b = 1.0f;
		this.a = 1.0f;
	}
	public MyCurve(float[] xpts, float[] ypts, float[] zpts)
	{
		if (xpts.length != ypts.length || xpts.length != zpts.length || ypts.length != zpts.length)
			throw new IllegalArgumentException("[MyCurve]: x,y,z point arrays are of different lengths : "  +
												xpts.length + ", " + ypts.length + ", zpts.length");
		points = new ArrayList<Point>();
		for (int i = 0; i < xpts.length; i++) {
			points.add(new Point(xpts[i], ypts[i], zpts[i]));
		}
		this.r = 1.0f;
		this.g = 1.0f;
		this.b = 1.0f;
		this.a = 1.0f;
	}
	public MyCurve(ArrayList<Point> list)
	{
		this(list, 1.0f, 1.0f, 1.0f, 1.0f);
	}	
	public MyCurve(ArrayList<Point> list, float rr, float gg, float bb, float aa)
	{
		this.points = list;
		this.r = rr;
		this.g = gg;
		this.b = bb;
		this.a = aa;
	}
	// ---
	public MyCurve(Point[] list)
	{
		this(list, 1.0f, 1.0f, 1.0f, 1.0f);
	}
	public MyCurve(Point[] list, float rr, float gg, float bb, float aa)
	{
		points = new ArrayList<Point>();
		for (int i = 0; i < list.length; i++)
			points.add(list[i]);
		initialize(points, rr, gg, bb, aa);
	}
	// ---
	public void initialize(ArrayList<Point> list, float rr, float gg, float bb, float aa)
	{
		this.points = list;
		this.r = rr;
		this.g = gg;
		this.b = bb;
		this.a = aa;
	}
	
	// =========================================================

	public void add(float[] xyz)
	{
		add(xyz[0], xyz[1], xyz[2]);
	}
	public void add(float x, float y, float z)
	{
		add(new Point(x,y,z));
	}
	public void add(Point p)
	{
		points.add(p);
	}
	
	// -------------------------------------------

	public void draw(float scale)
	{
		glColor4f(r, g, b, 1.0f);
		for(int i = 1; i < points.size(); i++) {
			Point p1 = points.get(i-1);
			Point p2 = points.get(i);
			//glPushMatrix();
			glBegin(GL_LINE_STRIP);
			glVertex3f(p1.x*scale, p1.y*scale, p1.z*scale);
			glVertex3f(p2.x*scale, p2.y*scale, p2.z*scale);
			glEnd();
			//glPopMatrix();
		}
	}
	
	public void draw()
	{
		draw(1);
	}
	
	// =========================================================

	public ArrayList<Point> getPoints()	{ return points; }
	public float[] getColor()	{ return new float[] {r, g, b}; }
	public float r()		{ return r; }
	public float g()		{ return g; }
	public float b()		{ return b; }
	
	public void setColor(float[] c)
	{	setColor(c[0], c[1], c[2], c[3]); }
	public void setColor(float rr, float gg, float bb)
	{	setColor(rr, gg, bb, 1.0f); }
	public void setColor(float rr, float gg, float bb, float aa)
	{	this.r = rr; this.g = gg; this.b = bb; this.a = aa; }
	
}
