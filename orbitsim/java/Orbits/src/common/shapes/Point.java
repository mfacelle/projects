package common.shapes;

public class Point 
{
	public final float x, y, z;
	
	public Point(float xx, float yy)
	{
		this.x = xx;
		this.y = yy;
		this.z = 0.0f;
	}
	public Point(float xx, float yy, float zz)
	{
		this.x = xx;
		this.y = yy;
		this.z = zz;
	}
	
	public Point(float[] xyz)
	{
		if (xyz.length != 3)
			throw new IllegalArgumentException("[Point] Array length " + xyz.length + " is not equal to 3. (x,y,z)");
		this.x = xyz[0];
		this.y = xyz[1];
		this.z = xyz[2];
	}
	
	// ------------------------
	
	public String toString()
	{
		return "(" + x + "," + y + "," + z + ")";
	}
}
