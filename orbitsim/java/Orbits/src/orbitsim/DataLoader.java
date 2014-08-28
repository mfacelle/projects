package physics.orbitsim;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import common.shapes.Point;

/** A class that will load simulation data (time, radius, theta) from a file. 
 * 
 * @author Mike Facelle
 *
 */
public class DataLoader 
{
	// COMMENTED OUT BECAUSE I'M USING OrbitSim.DataLoader (the inner class) instead
	
//	public static int loadData(String filename, float[] params, float[] time, float[] x, float[] y)
//	{
//		Scanner filein;
//		
//		try {
//			filein = new Scanner(new File(filename));
//		}
//		catch (IOException ex) {
//			System.err.println(ex.getMessage());
//			System.err.println("[DataLoader] Unable to open file " + filename);
//			return -1;
//		}
//		
//		// fill parameters with first row of data:
//		for (int i = 0; i < params.length; i++)
//			params[i] = filein.nextFloat();
//		// the last parameter will be the number of time steps (array size)
//		int n = (int)params[params.length-1];
//		time = new float[n];
//		x = new float[n];
//		y = new float[n];
//		
//		// retrieve the rest of the data in the form: time radius theta
//		float t = 0;
//		float r = 0;
//		float theta = 0;
//		for (int i = 0; filein.hasNextFloat(); i++) {
//			// assumes all rows will be a complete data set of 3 entries
//			// load data and convert from [time radius theta] to [x y time] for drawing to the screen
//			t = filein.nextFloat();
//			r = filein.nextFloat();
//			theta = filein.nextFloat();
//			time[i] = t;
//			x[i] = r * (float)Math.cos(theta);
//			y[i] = r * (float)Math.sin(theta);
////			System.out.println(time + ", " + radius + ", " + theta);
////			Point p = new Point(radius*(float)Math.cos(theta), radius*(float)Math.sin(theta), time);
////			System.out.println(p);		
//		}
//		return 0;
//	}
}
