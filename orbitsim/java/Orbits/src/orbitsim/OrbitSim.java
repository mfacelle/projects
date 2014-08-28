package physics.orbitsim;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.input.Keyboard.*;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.Sphere;

import utility.LWJGLTimer;

import common.Viewpoint;
import common.shapes.MyCurve;
import common.shapes.MyLine2D;
import common.shapes.MySphere;
import common.shapes.Point;
import common.text.MyText;
import common.text.MyText_Bitmap;
import common.engine.Game;

import static solar_system.Conversions.R_SUN;
import static solar_system.Conversions.R_EARTH;

/** OrbitSim is a class that displays a simulation of a planetary orbit.<br>
 * 
 * 	It will contain the following functionality:<br>
 * 	-time: speed up / slow down (then reverse)<br>
 * 	-camera: full 3d rotation and zooming<br>
 * 	-toggle to display earth's orbit path<br>
 * 
 * 	<br>
 * 
 * 	The orbit is displayed in the x-y plane, although the data is given as r,theta.
 * 	The data is then converted to cartesian coordinates.<br>
 * 
 * 
 * @author Mike Facelle
 *
 */
public class OrbitSim implements Game
{
	/** For displaying time in units of years (in s) */
	private static final float ONE_YEAR = 3.154E7f;
	
	// screen details
	
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 700;
	private static final String TITLE = "Orbit Simulator";
	private static final int ZNEAR = -1024;
	private static final int ZFAR = 1024;
	private static final int FPS = 40;
	/** Pixels per meter.  For scaling to the screen display */
	private float PPM = 1E-11f;
	
	
	public static final String BACKUPDATA_FILEPATH = "res/data/earthorbitsim.dat";
	public static final String SIMDATA_FILEPATH = "res/data/orbitsim.dat";
	public static final int NUM_PARAMS = 5;
	
	// parameters and data
	private float[] time;
	private float[] x;
	private float[] y;
	/** semi-major axis (in m)*/
	private float a = 1;
	/** eccentricity */
	private float ecc = 0;
	/** host mass (in kg) <br>technically M + m, but since M >> m, hostmass = M */
	private float hostmass = 1;
	/** period of orbit (in s)*/
	private float period = 1;
	/** Length from the center to the focus */
	private float d = 1;
	
	// simulation variables
	/** Current "frame" being displayed.  Incremented when time[i] + elapsedTime >= time[i+1] */
	private int frameIndex = 0;
	/** Time elapsed */
	private float elapsedTime = 0;
	/** Multiplied by dt to determine how fast simulation advances */
	private float timescale = 1.0E6f;
	/** Multiplied by timescale when user inputs a speedup/slowdown */
	private static final float TIMESCALE_CHANGE_FACTOR = 0.1f;
	/** Whether or not the simulation is paused. */
	private boolean isPaused;

	// lwjgl stuff
	/** Scaling the sun, in addition to PPM (because it can be < 1 px */
	private static final float SUN_SCALE = 2.5f;
	/** Scaling the planet, in addition to PPM (because it can be < 1 px */
	private static final float PLANET_SCALE = 2.0E2f;
	private FloatBuffer textOrthoMatrix;
	private FloatBuffer orthoMatrix;
	private Viewpoint camera;
	private LWJGLTimer timer;
	
	private MyText_Bitmap mytext;
	
	private MySphere star;
	private MySphere planet;
	private MyCurve orbit;
	private MyCurve orbit2;
	/** Number of points to divide 2pi by for rendering the orbit */
	private static final int NUM_DIVISIONS = 1000;
	private MyLine2D x_axis;
	private MyLine2D y_axis;
	
	private float pitch;
	private float yaw;
	private float roll;
	private static final float CAMERA_SPEED = 5.0E1f;
	
// ===============================================================

	public OrbitSim()
	{
		initialize();
	}
	
// ===============================================================
	
	public void initialize()
	{
		// load data and parameters
		frameIndex = 0;
		elapsedTime = 0;
		// array to hold the parameters [ a, ecc, hostmass, period ] loaded by DataLoader
		// load simulation data into variables
		if ((new DataLoader()).loadData(SIMDATA_FILEPATH) != 0) {
			System.err.println("Error trying to open " + SIMDATA_FILEPATH + ".\nRunning default simulation.");
			if ((new DataLoader()).loadData(BACKUPDATA_FILEPATH) != 0) {
				System.err.println("Unable to load default simulation.\nExiting program.");
				close(1);
			}
		}
		
		PPM = (float)HEIGHT / a;	// cannot call Display.getWidth() yet, GL hasn't been created
		d = a * ecc;
		
		// begin lwjgl creation and rendering
		orthoMatrix = BufferUtils.createFloatBuffer(16);
		textOrthoMatrix = BufferUtils.createFloatBuffer(16);
		initializeGL();
		// need to set PPM after GL created, but before objects, because they call PPM
		PPM = (float)Display.getHeight() / (2.5f*a);	// using height, because height < width
		initializeViewpoint();
		initializeObjects();
		
		System.out.println("SIMULATION PARAMETERS:");
		System.out.println("a="+a + "\tecc="+ecc + "\tm="+hostmass + "\tT="+period);
		System.out.println("d="+d + "\tPPM="+PPM);
		System.out.println("sun.r="+(R_SUN*PPM*SUN_SCALE)+"\tplanet.r="+(R_EARTH*PPM*PLANET_SCALE));
		System.out.println("sun.loc="+star.x()+", "+star.y() + "\tplanet.loc="+planet.x()+","+planet.y());
	}
	
	// --------------------------------------------------------------

	private void initializeGL()
	{
		// set up window
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle(TITLE);
			Display.setVSyncEnabled(true);
			Display.create();
		} 
		catch(LWJGLException ex) {
			ex.printStackTrace();
			close(1);
		}
				
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	
		
		timer = new LWJGLTimer();
	}
	
	// --------------------------------------------------------------

	private void initializeViewpoint()
	{
		textOrthoMatrix = MyText.standardOrthoMatrix();
		
		camera = new Viewpoint();
		camera.setZRange(ZNEAR, ZFAR);
		camera.applyOrthographicMatrix(Display.getWidth(), Display.getHeight(), Viewpoint.CENTER);
        glGetFloat(GL_PROJECTION_MATRIX, orthoMatrix);
        glLoadMatrix(orthoMatrix);
        
        pitch = 0;
        yaw = 0;
        roll = 0;
	}
	
	// --------------------------------------------------------------

	private void initializeObjects()
	{
		mytext = new MyText_Bitmap(14);
		// need to scale up planet radius because otherwise it's < 1px wide and doesn't show up

		star = new MySphere.Builder().setPosition(d * PPM, 0, 0)
										.setRadius(R_SUN * PPM*SUN_SCALE)
										.setColor(1, 1, 0)
										.build();
		planet = new MySphere.Builder().setPosition(x[0]*PPM, y[0]*PPM, 0)
										.setRadius(R_EARTH * PPM*PLANET_SCALE)	// should add a way to specify planet radius maybe?
										.setColor(0, 0, 1)	// won't render if blue... WHY NOT????
										.build();
		x_axis = new MyLine2D(-Display.getWidth()/2.0f, 0, Display.getWidth()/2.0f, 0,		0.2f, 0.2f, 0.3f);
		y_axis = new MyLine2D(0, -Display.getHeight()/2.0f, 0, Display.getHeight()/2.0f, 	0.2f, 0.2f, 0.3f);
		//orbit2 = new MyCurve(x, y);
		createOrbit();
	}
	
	// -----
	
	/** Generates the elliptical orbit of the planet to be displayed on screen via MyCurve object
	 *  Previously used just the simulation data, but this will show it more accurately.<br>
	 *  <code>r = c / (1-ecc*cos(theta)) = (a*(1-ecc*ecc)) / (1-ecc*cos(theta))</code> 
	 * 
	 */
	public void createOrbit()
	{
		orbit = new MyCurve();
		
		float dtheta = 2*(float)Math.PI / NUM_DIVISIONS;
		float r = 0;
		for (int i = 0; i < NUM_DIVISIONS; i++) {
			r = (a * (1 - ecc*ecc)) / (1 - ecc*(float)Math.cos(i*dtheta));
			orbit.add(r*(float)Math.cos(i*dtheta) - d, r*(float)Math.sin(i*dtheta), 0.0f);
		}
	}
	
// ===============================================================

	@Override
	public void run()
	{
		timer.initialize();

		isPaused = false;
		float dt = 0;
		while(!Display.isCloseRequested()) {
			dt = (float)LWJGLTimer.toSeconds(timer.update()) * timescale;
			dt = isPaused ? 0 : dt;
			input(dt);
			draw();
			input(dt);
			logic(dt);	// logic does nothing
			update(dt);
			
			Display.update();
			Display.sync(FPS);
		}
	}
	
// ===============================================================

	public void input(float dt)
	{
		if (isKeyDown(KEY_Q))
			close(0);
		
		movementInput(dt);
		
		// timescale adjusted +/- by one-tenth its value (unless timescale is 0, in which case it's just +/- 0.1)
		if (isKeyDown(KEY_LBRACKET))	// increase timescale
			timescale -= timescale == 0 ? TIMESCALE_CHANGE_FACTOR : TIMESCALE_CHANGE_FACTOR*Math.abs(timescale);
		if (isKeyDown(KEY_RBRACKET))	// decrease timescale
			timescale += timescale == 0 ? TIMESCALE_CHANGE_FACTOR : TIMESCALE_CHANGE_FACTOR*Math.abs(timescale);
		if (isKeyDown(KEY_BACKSLASH))
			timescale = Math.round(timescale);
	}
	
	// --------------------------------------------------------------
	
	public void movementInput(float dt)
	{
		// divide by timescale to make rotational speed constant
		if (isKeyDown(KEY_I)) {	// +pitch
			pitch += CAMERA_SPEED * (dt/timescale);
		}
		if (isKeyDown(KEY_K)) {	// -pitch
			pitch -= CAMERA_SPEED * (dt/timescale);
		}
		if (isKeyDown(KEY_J)) {	// +yaw
			yaw += CAMERA_SPEED * (dt/timescale);
		}
		if (isKeyDown(KEY_L)) {	// -yaw
			yaw -= CAMERA_SPEED * (dt/timescale);
		}
		if (isKeyDown(KEY_U)) {	// +roll
			roll += CAMERA_SPEED * (dt/timescale);
		}
		if (isKeyDown(KEY_O)) {	// -roll
			roll -= CAMERA_SPEED * (dt/timescale);
		}
		if (isKeyDown(KEY_P)) {	// reset
			pitch = 0;
			yaw = 0;
			roll = 0;
		}
		
	}
	
// ===============================================================

	@Override
	public void draw()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	
		glLoadIdentity();
		
		// rotate based on user input
		glRotatef(pitch, 1, 0, 0);
		glRotatef(yaw, 0, 1, 0);
		glRotatef(roll, 0, 0, 1);
		
		
		x_axis.draw();
		y_axis.draw();
		star.draw();
		planet.draw();
		//orbit2.draw(PPM);
		orbit.draw(PPM);
		MyText.enterTextDrawMode(textOrthoMatrix);
		String timescaleDisplay = String.format("timescale = %.2f", timescale);
		mytext.write(timescaleDisplay, 0, Display.getHeight() - 2.0f*mytext.fontSize());
		String timeDisplay = String.format("t = %.2f earth yr", time[frameIndex] / ONE_YEAR);
		mytext.write(timeDisplay, 0, Display.getHeight()-mytext.fontSize());
		MyText.exitTextDrawMode(orthoMatrix);
	}
	
// ===============================================================

	@Override
	public void update(float dt)
	{
		// advance simulation
		if ((elapsedTime += dt) >= period) 
			elapsedTime -= period;
		// select frame to display
		frameIndex = (int)((elapsedTime/period) * time.length);
		// this shouldn't happen:  (time.length == x.length == y.length from load process)
		if (frameIndex >= time.length)
			frameIndex = 0;
		// set planet's position based on current frame
		// x must be offset by d because the r vector is based off the focus of the ellipse
		planet.setPosition((x[frameIndex] - d) * PPM, y[frameIndex] * PPM, 0);
// debug
//System.out.println(planet.x() + "," + planet.y() + ", " + planet.radius());
// debug

	}
	
	// --------------------------------------------------------------

	@Override
	public void logic(float dt)
	{
		// nothing to do - simulation data already loaded
	}
	
	// --------------------------------------------------------------


// ===============================================================

	public int frameIndex()			{ return frameIndex; }
	public float timescale()	{ return timescale; }
	public float elapsedTime()	{ return elapsedTime; }
	
	// ---
	
	public void setTimescale(float ts)	{ timescale = ts; }
	public void setElapsedTime(float t)	{ elapsedTime = t; }
	public void setIndex(int i)		{ frameIndex = i; }
	
	public void reset()	
	{	frameIndex = 0;	elapsedTime = 0; }
	public void reset(float deltaT)
	{
		elapsedTime = deltaT;
		frameIndex = (int)(elapsedTime/period) * time.length;
	}
	
// ===============================================================

	public void close(int iscrash)
	{
		Display.destroy();
		System.exit(iscrash);
	}
	
	
	
// ================================================================================================================
//	DATA LOADER CLASS
// ================================================================================================================
//	put in this class so it can directly access parameter fields and data arrays.  probably better than copying
//	between functions and classes
	
	
	/** A class that will load simulation data (time, radius, theta) from a file. 
	 * 
	 * @author Mike Facelle
	 *
	 */
	private class DataLoader 
	{
		public int loadData(String filename)
		{
			Scanner filein;
			
			try {
				filein = new Scanner(new File(filename));
			}
			catch (IOException ex) {
				System.err.println(ex.getMessage());
				System.err.println("[DataLoader] Unable to open file " + filename);
				return -1;
			}
			
			// fill parameters with first row of data:
			a = filein.nextFloat();
			ecc = filein.nextFloat();
			hostmass = filein.nextFloat();
			period = filein.nextFloat();
			int n = (int)filein.nextFloat();	// nextFloat just in case of any weird formatting (shouldnt happen though)
			time = new float[n];
			x = new float[n];
			y = new float[n];
			
			// retrieve the rest of the data in the form: time radius theta
			float t = 0;
			float r = 0;
			float theta = 0;
			for (int i = 0; filein.hasNextFloat(); i++) {
				// assumes all rows will be a complete data set of 3 entries
				// load data and convert from [time radius theta] to [x y time] for drawing to the screen
				t = filein.nextFloat();
				r = filein.nextFloat();
				theta = filein.nextFloat();
				time[i] = t;
				x[i] = r * (float)Math.cos(theta);	// must shift by d 
				// HOW!????? WHENEVER I SHIFT, IT DOESNT DO SHIT! (unless another orbit is drawn, and THAT one shows up correctly....
				y[i] = r * (float)Math.sin(theta);
//				System.out.println(time + ", " + radius + ", " + theta);
//				Point p = new Point(radius*(float)Math.cos(theta), radius*(float)Math.sin(theta), time);
//				System.out.println(p);		
			}
			return 0;
		}
		
		// ===============================================================


	}
	
}
