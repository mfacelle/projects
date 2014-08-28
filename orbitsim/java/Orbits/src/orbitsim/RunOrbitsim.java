package physics.orbitsim;

import common.engine.Game;

/** A program that will display a simulation of a planet orbiting a star.<br>
 *  It reads data in from a while with the format:
 * 	<code>time radius theta<code> on each line, with the first line containing parameters like semimajor axis<br>
 * 	This data is generated by a simulation written in C++
 * 
 * @author Mike Facelle
 *
 */
public class RunOrbitsim 
{
	public static final String FILEPATH = "./data/Orbits.dat";	// should be able to do this (outside of .jar file)
	
	public static void main(String[] args)
	{
		Game orbitsim = new OrbitSim();
		orbitsim.run();
	}
	
	
}
