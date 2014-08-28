package common;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.glu.GLU;

import utility.BufferTools;
import common.shapes.MySphere.Builder;

public class Viewpoint 
{
	// ortho types
	public static final int CENTER = 0;		// (0,0) at center (standard xy-plane)
	public static final int TOPLEFT = 1;	// (0,0) in top-left (y-axis reversed)
	public static final int BOTTOMLEFT = 2;	// (0,0) in bottom-left (standard xy-plane)
	
	private float x = 0;
    private float y = 0;
    private float z = 0;
    private float pitch = 0;
    private float yaw = 0;
    private float roll = 0;
    private float fov = 90;
    private float aspectRatio = 1;
    private float zNear = 0;
    private float zFar = 100;
    
    private FloatBuffer perspectiveProjectionMatrix;
    private FloatBuffer orthographicProjectionMatrix;
    
	// =======================================================
    
    public Viewpoint()
    {
    	x = 0;		
    	y = 0;			
    	z = 0;
    	pitch = 0;	
    	yaw = 0;		
    	roll = 0;
    	fov = 90;	
    	aspectRatio = 1;
    	zNear = 0;	
    	zFar = 100;
    	perspectiveProjectionMatrix = BufferTools.reserveData(16);
    	orthographicProjectionMatrix = BufferTools.reserveData(16);
    }
    
    private Viewpoint(Builder b)
    {
    	this.x = b.x;		
    	this.y = b.y;			
    	this.z = b.z;
    	this.pitch = b.pitch;	
    	this.yaw = b.yaw;		
    	this.roll = b.roll;
    	this.fov = b.fov;	
    	this.aspectRatio = b.aspectRatio;
    	this.zNear = b.zNear;	
    	this.zFar = b.zFar;
    }

    // =======================================================

    
	public void move(float dx, float dy, float dz)
	{
		// note: Math.cos, Math.sin, Math.toRadians are static imported
		this.z += dx * (float) cos(toRadians(yaw - 90)) + dz * cos(toRadians(yaw));
        this.x -= dx * (float) sin(toRadians(yaw - 90)) + dz * sin(toRadians(yaw));
        this.y += dy * (float) sin(toRadians(pitch - 90)) + dz * sin(toRadians(pitch));
	}
	
	
// =======================================================
// The following methods are used by Oskar Veerhoek in his Camera and EulerCamera class
// (No point in re-writing them when mine would be almost identical to his)
	
	/** Applies camera position and heading to modelview matrix<br>
	 * @author Oskar Veerhoek
	 */
	public void applyTranslations()
	{
		//glPushAttrib(GL_TRANSFORM_BIT);
        glMatrixMode(GL_MODELVIEW);
        glRotatef(pitch, 1, 0, 0);
        glRotatef(yaw, 0, 1, 0);
        glRotatef(roll, 0, 0, 1);
        glTranslatef(-x, -y, -z);
        //glPopAttrib();
	}
	
	/**
     * 	Sets GL_PROJECTION to an orthographic projection matrix, [with (0,0) at the center]. 
     * 	The matrix mode will be returned it its previous value after execution.
     * @author Oskar Veerhoek
     */
    public void applyOrthographicMatrix(float width, float height) {
        applyOrthographicMatrix(width, height, CENTER);	// using center configuration as default
    }
    /**
     * 	Sets GL_PROJECTION to an orthographic projection matrix [with a certain configuration]. 
     * 	The matrix mode will be returned it its previous value after execution.
     * @author Oskar Veerhoek
     */
    public void applyOrthographicMatrix(float width, float height, int type) {
        glPushAttrib(GL_TRANSFORM_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        switch(type) {
        default:
        case CENTER:
            glOrtho(-width/2, width/2, -height/2, height/2, zNear, zFar);
        	break;
        case TOPLEFT:
            glOrtho(0, width, height, 0, zNear, zFar);
        	break;
        case BOTTOMLEFT:
            glOrtho(0, width, 0, height, zNear, zFar);
        	break;       
        }
        glGetFloat(GL_PROJECTION_MATRIX, orthographicProjectionMatrix);
        glLoadMatrix(orthographicProjectionMatrix);
        glMatrixMode(GL_MODELVIEW);
        glPopAttrib();
    }
    
	/**
     * 	Sets GL_PROJECTION to an perspective projection matrix. The matrix mode will be returned it its previous value
     * 	after execution.<br>
     * @author Oskar Veerhoek
     */
    public void applyPerspectiveMatrix() {
        glPushAttrib(GL_TRANSFORM_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(fov, aspectRatio, zNear, zFar);
        glGetFloat(GL_PROJECTION_MATRIX, perspectiveProjectionMatrix);
        glPopAttrib();
    }
    
// =======================================================
// get and set

    public String toString()
    {
    	return "Camera: [ pos(" + x + ", " + y + ", " + z + ")\trot(" + pitch + ", " + yaw + ", " + roll + ") ]"; 
    }	
	
	public float x()		{ return x; }
	public float y()		{ return y; }
	public float z()		{ return z; }
	public float[] position()	{ return new float[]{ x, y, z }; }
	public float pitch()	{ return pitch; }
	public float yaw()		{ return yaw; }
	public float roll()		{ return roll; }
	public float[] heading()	{ return new float[]{ pitch, yaw, roll }; }
	public float zNear()	{ return zNear; }
	public float zFar()		{ return zFar; }
	public float fov()		{ return fov; }
	public float aspect()	{ return aspectRatio; }
	public FloatBuffer getPerspectiveMatrix()	{ return perspectiveProjectionMatrix; }
	public FloatBuffer getOrthoMatrix()	{ return orthographicProjectionMatrix; }
	// ---
	public void setPosition(float xx, float yy, float zz)
	{	this.x = xx;	this.y = yy;	this.z = zz; }
	public void setPosition(float[] r)
	{	this.x = r[0];	this.y = r[1];	this.z = r[2]; }
	
	public void setHeading(float p, float y, float r)
	{	this.pitch = p;		this.yaw = y;		this.roll = r; }
	public void setHeading(float[] h)
	{	this.pitch = h[0];	this.yaw = h[1];	this.roll = h[2]; }
	
	public void setFOV(float f)		{ this.fov = f; }
	
	public void setZNear(float zn)	
	{ 
		if (zn <= 0) {
            throw new IllegalArgumentException("zNear " + zn + " is 0 or less");
        }
		this.zNear = zn; 
	}
	public void setZFar(float zf)	
	{ 
		if (zf <= 0) {
            throw new IllegalArgumentException("zFar " + zf + " is 0 or less");
        }
		this.zFar = zf; 
	}
	public void setZRange(float zn, float zf)	
	{ 
		if (zf <= zn)
			throw new IllegalArgumentException("[Viewpoint.Builder]Value of zFar " + zf + " is less than or equal to zNear " + zn);
		zNear = zn;
		zFar = zf;
	}
	public void setAspectRatio(float ar)
	{
		if (ar <= 0) {
            throw new IllegalArgumentException("aspectRatio " + aspectRatio + " was 0 or was smaller than 0");
        }
        this.aspectRatio = ar;
	}
	
	
	
	
	
// =====================================================================================================	
// BUILDER CLASS	
// =====================================================================================================
	
	public static class Builder
	{
		
		private float x = 0;
	    private float y = 0;
	    private float z = 0;
	    private float pitch = 0;
	    private float yaw = 0;
	    private float roll = 0;
	    private float fov = 90;
	    private float aspectRatio = 1;
	    private float zNear = 0.01f;
	    private float zFar = 100;
	
		// ============================================================	

	    public Builder()
	    {
	    	;
	    }
	    
		// ============================================================	

	    public Viewpoint build()
	    {
	    	return new Viewpoint(this);
	    }
	    
		// ============================================================	

		public Builder setPosition(float xx, float yy, float zz)
		{	
			x = xx; y = yy; z = zz; 
			return this;
		}
		public Builder setPosition(float[] r)
		{
			if (r.length != 3)	// number of points in 3 position vector is 3
				throw new IllegalArgumentException("[Viewpoint.Builder]Length of position vector " + r.length +
							" is not equal to 3");
			x = r[0];	y = r[1];	z = r[2];
			return this;
		}
		// ---
		public Builder setHeading(float p, float y, float r)
		{	
			pitch = p;	yaw = y;	roll = r; 
			return this;
		}
		public Builder setHeading(float[] h)
		{
			if (h.length != 3)	// number of points in 3 position vector is 3
				throw new IllegalArgumentException("[Viewpoint.Builder]Length of rotation vector " + h.length +
							" is not equal to 3");
			pitch = h[0];	yaw = h[1];		roll = h[2];
			return this;
		}
		// ---
		public Builder setFOV(float f)	
		{ 
			fov = f;
			return this;
		}
		// ---
		public Builder setAspectRatio(float ar)	
		{ 
			if (ar <= 0)
				throw new IllegalArgumentException("[Viewpoint.Builder]Value of aspectRatio " + ar + " is less than or equal to 0");
			aspectRatio = ar;
			return this;
		}
		// ---
		public Builder setZNear(float zn)	
		{ 
			if (zn <= 0)
				throw new IllegalArgumentException("[Viewpoint.Builder]Value of zNear " + zn + " is less than or equal to 0");
			return this;
		}
		public Builder setZFar(float zf)	
		{ 
			if (zf <= zNear)
				throw new IllegalArgumentException("[Viewpoint.Builder]Value of zFar " + zf + " is less than or equal to zNear " + zNear);
			zFar = zf;
			return this;
		}
		public Builder setZRange(float zn, float zf)	
		{ 
			if (zn <= 0)
				throw new IllegalArgumentException("[Viewpoint.Builder]Value of zNear " + zn + " is less than or equal to 0");
			if (zf <= zn)
				throw new IllegalArgumentException("[Viewpoint.Builder]Value of zFar " + zf + " is less than or equal to zNear " + zn);
			zNear = zn;
			zFar = zf;
			return this;
		}
		
// ============================================================	

	}
}

