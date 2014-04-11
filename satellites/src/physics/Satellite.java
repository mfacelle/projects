package physics;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static solar_system.Conversions.G;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import common.Entity;
import common.MyCurve;
import static common.Common.*;

/**	Poorly designed data structure.  MySphere contains coordinates and color values as well, but they aren't used.
 * 
 * @author Mike Facelle
 *
 */
public class Satellite extends Entity
{

	private float mass = 1;			// mass of the satellite
	private float hostMass = 1;		// mass of the host object
	// current velocity:
	private float dx = 0;
	private float dy = 0;
	private float dz = 0;
	// current force:
	private float fx = 0;
	private float fy = 0;
	private float fz = 0;
	// some forces to keep track of
	private float appliedForcePos = 0;		// in N (forward thrust)
	private float appliedForceNeg = 0;		// in N (retrograde thrusters)
	private float totalAppliedForce = 0;	// in N (magnitude of force applied by thrust)
	
	// for drawing:
	private static final float RADIUS = 2.5f;	// in px
	private static final int NUM_STACKS = 8;
	private static final int NUM_SLICES = 8;
	private static final int RESOLUTION = 2;
	private float r = 0.0f;
	private float g = 1.0f;
	private float b = 1.0f;
	private float a = 1.0f;
	private Sphere s;
	private MyCurve path;

	
// ===============================================================

	public Satellite()
	{
		this(0,0, 0,0,0, 0,0,0);
	}
	public Satellite(float mass, float hostmass)
	{
		this(mass, hostmass, 0,0,0, 0,0,0);
	}
	public Satellite(float mass, float hostmass, float x, float y, float z, float dxx, float dyy, float dzz)
	{
		super(x,y,z, 0,0,0);
		this.mass = mass;
		this.hostMass = hostmass;
		this.dx = dxx;
		this.dy = dyy;
		this.dz = dzz;
		this.fx = 0;
		this.fy = 0;
		this.fz = 0;
		this.appliedForcePos = 0.0f;
		this.appliedForceNeg = 0.0f;
		this.totalAppliedForce = 0.0f;	
		r = 0.0f;
		g = 1.0f;
		b = 1.0f;
		s = new Sphere();
		path = new MyCurve();
	}
	
// ===============================================================

	@Override
	public void update(float dt) 
	{
		// update position, then velocity
		x += dx*dt + 0.5f*(fx/mass)*dt*dt;
		y += dy*dt + 0.5f*(fy/mass)*dt*dt;
		z += dz*dt + 0.5f*(fz/mass)*dt*dt;

		dx += (fx/mass)*dt;
		dy += (fy/mass)*dt;
		dz += (fz/mass)*dt;
		
		path.add(x, y, z);
	}
	
	// --------------------------------------------------------------

	public void draw(float scale, boolean withPath)		// scale is 1/pixels-per-meter, ideally
	{
		glColor4f(r, g, b, a);
		glPushMatrix();
		glTranslatef(x*scale, y*scale, z*scale);
		s.draw(RADIUS, NUM_STACKS, NUM_SLICES);
		glPopMatrix();
		
		if (withPath)
			path.draw(scale);
// -=-=-=-=-=-=-=-=-=-=-=- DEBUGGING -=-=-=-=-=-=-=-=-=-=-=-
//System.out.println("drawlocation: " + x*scale + ", " + y*scale + ", " + z*scale);
//-=-=-=-=-=-=-=-=-=-=-=- DEBUGGING -=-=-=-=-=-=-=-=-=-=-=-
	}
	// ---
	@Override
	public void draw() 
	{
		draw(1, false);
	}

	// --------------------------------------------------------------

	@Override
	public void loadGraphics() 
	{
		// do nothing. just drawing a point in draw()
	}
	
// ===============================================================
// engine stuff
	
	/**	Computes the gravitational force, assuming it to be rotating around (0,0,0). 
	 * 
	 * @return	F_G = Gm1m2 / r^2, in [x,y,z] form
	 */
	public float[] gravitationalForce()
	{
		float theta = x != 0 ? (float)Math.atan(y / x) 	:	y >= 0 ? (float)Math.PI/2 : -(float)Math.PI/2;
		float phi = x != 0 ? (float)Math.atan(z / x) 	:	z >= 0 ? (float)Math.PI/2 : -(float)Math.PI/2;
		float fg = gravitationalForce(mass, hostMass, magnitude(x,y,z));
		float r_xy = magnitude(x,y);
		float r_xz = magnitude(x,z);
// -=-=-=-=-=-=-=-=-=-=-=- DEBUGGING -=-=-=-=-=-=-=-=-=-=-=-
//System.out.println("fg:" + fg + ",\tr:" + magnitude(x,y,z) + ",\ttheta:" + Math.toDegrees(theta) + ",\tphi:" + Math.toDegrees(phi));
//-=-=-=-=-=-=-=-=-=-=-=- DEBUGGING -=-=-=-=-=-=-=-=-=-=-=-

		// x=fcostcosp, y=fsint, z=fsinp
		// using ? statements to avoid division by 0.
		return new float[] { (x==0 && (y==0 || z==0)) ? 0 : -(fg*x*Math.abs(x)) / (r_xy*r_xz),
							(x==0 && y==0) ? 0 : -(fg*y) / r_xy,
							(x==0 && z==0) ? 0 : -(fg*z) / r_xz };
	}
	// ---
	/**	Static method.  Computes gravitational force between two masses, m1 and m2.
	 * 
	 * @return	F_G = Gm1m2 / r^2
	 */
	public static float gravitationalForce(float m1, float m2, float r)
	{
		return (G*m1*m2) / (r*r);
	}
	
// ===============================================================
// get and set
	
	public String toString()
	{
		return "Satellite:\n" +
				"\tr=[" + x + ", " + y + ", " + z + "]\n" +
				"\tv=[" + dx + ", " + dy + ", " + dz + "]\n" +
				"\tf=[" + fx + ", " + fy + ", " + fz + "]";
	}
	
	public float mass()		{ return mass; }
	public float dx()		{ return dx; }
	public float dy()		{ return dy; }
	public float dz()		{ return dz; }
	public float[] velocity()	{ return new float[] {dx,dy,dz}; }
	public float speed()	{ return magnitude(dx,dy,dz); }
	public float fx()		{ return fx; }
	public float fy()		{ return fy; }
	public float fz()		{ return fz; }
	public float[] force()	{ return new float[] {fx,fy,fz}; }
	public float appliedForcePos()	{ return appliedForcePos; }
	public float appliedForceNeg()	{ return appliedForceNeg; }
	public float totalAppliedForce()	{ return totalAppliedForce; }
	// -
	public void setMass(int m)	{ this.mass = m; }
	public Satellite setVelocity(float dxx, float dyy, float dzz)
	{	this.dx = dxx;	this.dy = dyy;	this.dz = dzz;	return this; }
	public Satellite setForce(float fxx, float fyy, float fzz)
	{	this.fx = fxx;	this.fy = fyy;	this.fz = fzz;	return this; }
	public Satellite applyForce(float fxx, float fyy, float fzz)
	{	this.fx += fxx;	this.fy += fyy;	this.fz += fzz;	totalAppliedForce += magnitude(fx,fy,fz);	return this; }
	public Satellite applyForcePos(float fxx, float fyy, float fzz)
	{	this.fx += fxx;	this.fy += fyy;	this.fz += fzz;	
		totalAppliedForce += magnitude(fx,fy,fz);	appliedForcePos += magnitude(fx,fy,fz);	return this; }
	public Satellite applyForceNeg(float fxx, float fyy, float fzz)
	{	this.fx += fxx;	this.fy += fyy;	this.fz += fzz;	
		totalAppliedForce += magnitude(fx,fy,fz);	appliedForceNeg += magnitude(fx,fy,fz);	return this; }
	public Satellite setColor(float[] c)
	{	this.r = c[0];	this.g = c[1];	this.b = c[2];	this.a = c[3]; return this; }

}
