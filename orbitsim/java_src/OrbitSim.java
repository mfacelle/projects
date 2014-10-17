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
import static solar_system.Conversions.AU;

/** OrbitSim is a class that displays a simulation of a planetary orbit.<br>
 * <b>SHOULD BE REDONE WITH BETTER PHYSICS / UI STUFF!!!!</b><br>
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
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 700;
	private static final String TITLE = "Orbit Simulator";
	private static final int ZNEAR = -1024;
	private static final int ZFAR = 1024;
	private static final int FPS = 40;
	/** Pixels per meter.  For scaling to the screen display */
	private float PPM = 1E-11f;
	
	
	// eclipse and real filepaths (eclipse paths are res/data/..., real paths are ./data/...)
	//public static final String BACKUPDATA_FILEPATH = "res/data/earth.dat";
	public static final String BACKUPDATA_FILEPATH = "./data/earth.dat";
	//public static final String SIMDATA_FILEPATH = "res/data/orbitsim.dat";
	public static final String SIMDATA_FILEPATH = "./data/orbitsim.dat";
	/** Number of parameters read in on the first line of the orbitsim.dat file */
	public static final int NUM_PARAMS = 7;
	
	// parameters and data (defaults given will not be used)
	private float[] time;
	private float[] x;
	private float[] y;
	/** planet name (either one from our solar system, preset, or "custom") */
	private String name = "";
	/** semi-major axis (in m)*/
	private float a = 1;
	/** eccentricity */
	private float ecc = 0;
	/** planet mass (in kg) */
	private float planetmass = 1;
	/** host mass (in kg)*/
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
	/** Default timescale (for when user hits reset timescale button */
	private static final float DEFAULT_TIMESCALE = 1.0E6f;
	/** Multiplied by dt to determine how fast simulation advances */
	private float timescale = DEFAULT_TIMESCALE;
	/** Multiplied by timescale when user inputs a speedup/slowdown */
	private static final float TIMESCALE_CHANGE_FACTOR = 0.1f;
	/** Whether or not the simulation is paused. */
	private boolean isPaused;
	/** The pause menu to display when paused */
	private OrbitSimPauseMenu pauseMenu;

	// lwjgl stuff
	/** Fraction of the screen that the host star will take up */
	private static final float STAR_SCALE = 5.0E-3f;
	/** Fraction of the screen that the planet will take up */
	private static final float PLANET_SCALE = 2.0E-3f;
	private FloatBuffer textOrthoMatrix;
	private FloatBuffer orthoMatrix;
	private Viewpoint camera;
	private LWJGLTimer timer;
	/** Font size to be used */
	private static final int FONT_SIZE = 14;
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
// INITIALIZE
	
	public void initialize()
	{
		System.out.println("\nBeginning simulation display.");
		System.out.println("\nLoading simulation data... (this may take some time)");
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
		System.out.println("Done loading.  Beginning animation.");
		
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
		
// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
		// SHOULD STILL DO THIS!! (but not exactly the same, since user will have JUST entered params themself)
		// also print Rmin, Rmax (in AU and m) on the display to give users a sense of scale
		
//		System.out.println("SIMULATION PARAMETERS:");
//		System.out.println("a="+a + "\tecc="+ecc + "\tm="+hostmass + "\tT="+period);
//		System.out.println("d="+d + "\tPPM="+PPM);
//		System.out.println("sun.r="+(R_SUN*PPM*SUN_SCALE)+"\tplanet.r="+(R_EARTH*PPM*PLANET_SCALE));
//		System.out.println("sun.loc="+star.x()+", "+star.y() + "\tplanet.loc="+planet.x()+","+planet.y());
// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

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
		mytext = new MyText_Bitmap(FONT_SIZE);
		// setting planet and star to a constant display size, to guarantee they aren't smaller than 1 px

		star = new MySphere.Builder().setPosition(d * PPM, 0, 0)
										.setRadius(STAR_SCALE * Display.getWidth())
										.setColor(1, 1, 0)
										.build();
		planet = new MySphere.Builder().setPosition(x[0]*PPM, y[0]*PPM, 0)
										.setRadius(PLANET_SCALE * Display.getWidth())
										.setColor(0, 0, 1)
										.build();
		x_axis = new MyLine2D(-Display.getWidth()/2.0f, 0, Display.getWidth()/2.0f, 0,		0.2f, 0.2f, 0.3f);
		y_axis = new MyLine2D(0, -Display.getHeight()/2.0f, 0, Display.getHeight()/2.0f, 	0.2f, 0.2f, 0.3f);
		//orbit2 = new MyCurve(x, y);
		createOrbit();
		
		pauseMenu = new OrbitSimPauseMenu();
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
// RUN

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
// INPUT

	public void input(float dt)
	{
		if (isKeyDown(KEY_Q))
			close(0);
		
		// single-press inputs:
		while (Keyboard.next() && Keyboard.getEventKeyState()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				isPaused = !isPaused;		// toggle pause menu
			else if (Keyboard.isKeyDown(Keyboard.KEY_T))
				timescale = DEFAULT_TIMESCALE;
		}
		
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
		// divide by timescale to make rotational speed constant, regardless of timescale
		//	(dt is multiplied by timescale during main loop)
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
// DRAW

	@Override
	public void draw()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	
		glLoadIdentity();
		
		// rotate based on user input
		glRotatef(pitch, 1, 0, 0);
		glRotatef(yaw, 0, 1, 0);
		glRotatef(roll, 0, 0, 1);
		
		if (isPaused) {
			pauseMenu.draw(textOrthoMatrix, orthoMatrix);
		}
		else {
			x_axis.draw();
			y_axis.draw();
			star.draw();
			planet.draw();
			//orbit2.draw(PPM);
			orbit.draw(PPM);
			
			// draw text:
			MyText.enterTextDrawMode(textOrthoMatrix);
			
			// on left side:
			// display planet's name
			mytext.write("planet: " + name, 0, Display.getHeight()-4.0f*mytext.fontSize());
			// display elapsed time in earth years
			String timeDisplay = String.format("t = %.2f earth yr", time[frameIndex] / ONE_YEAR);
			mytext.write(timeDisplay, 0, Display.getHeight()-3.0f*mytext.fontSize());
			// display elapsed time in this planet's years
			String timeDisplayRelative = String.format("  = %.2f planet yr", time[frameIndex] / period);
			mytext.write(timeDisplayRelative, 0, Display.getHeight()-2.0f*mytext.fontSize());
			// display timescale
			String timescaleDisplay = String.format("timescale = %1.2e", timescale);
			mytext.write(timescaleDisplay, 0,  Display.getHeight()-mytext.fontSize());
			
			// on right side:
			// display perihelion distance (AU and meters)
			String perihelionDisplay = String.format("Rmin = %.3f AU", (x[0] / AU));
			mytext.write(perihelionDisplay, 
							Display.getWidth()-perihelionDisplay.length()*mytext.fontSize(), 
							Display.getHeight()-mytext.fontSize());
			String aphelionDisplay = String.format("Rmax = %.3f AU", (2*a - x[0]) / AU);
			mytext.write(aphelionDisplay, 
							Display.getWidth()-aphelionDisplay.length()*mytext.fontSize(), 
							Display.getHeight()-2.0f*mytext.fontSize());
			
			// top-left: display "ESC : menu" so users know how to access the pause menu
			mytext.write("ESC : menu", 0, 0);
			
			MyText.exitTextDrawMode(orthoMatrix);
		}
	}
	
// ===============================================================
// UPDATE / LOGIC

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
	}
	
	// --------------------------------------------------------------

	@Override
	public void logic(float dt)
	{
		// nothing to do - simulation data already loaded
	}
	
	// --------------------------------------------------------------


// ===============================================================
// GET / SET

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
// CLOSE

	public void close(int iscrash)
	{
		System.out.println("\nExiting program" + (iscrash != 0 ? " as crash" : "") + ".");
		Display.destroy();
		System.exit(iscrash);
	}
	
	
	
// ================================================================================================================
//	DATA LOADER CLASS
// ================================================================================================================
//	put in this class so it can directly access parameter fields and data arrays.  
//	Probably better than copying between functions and classes
	
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
			name = filein.next();
			a = filein.nextFloat();
			ecc = filein.nextFloat();
			planetmass = filein.nextFloat();
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
			long elapsed_t = System.currentTimeMillis();	// keep track of time to display percentage completed
			for (int i = 0; filein.hasNextFloat(); i++) {
				// assumes all rows will be a complete data set of 3 entries
				// load data and convert from [time radius theta] to [x y time] for drawing to the screen
				//	(nextFloat() will throw errors if it cannot be read in)
				t = filein.nextFloat();
				r = filein.nextFloat();
				theta = filein.nextFloat();
				time[i] = t;
				x[i] = r * (float)Math.cos(theta);	// must shift by d (???)
				// HOW!????? WHENEVER I SHIFT, IT DOESNT DO SHIT! (unless another orbit is drawn, and THAT one shows up correctly....
				y[i] = r * (float)Math.sin(theta);
//				System.out.println(time + ", " + radius + ", " + theta);
//				Point p = new Point(radius*(float)Math.cos(theta), radius*(float)Math.sin(theta), time);
//				System.out.println(p);		
				if (System.currentTimeMillis() - elapsed_t >= 1000) {
					System.out.println((int)((float)i/n * 100) + "%");
					elapsed_t = System.currentTimeMillis();
				}
			}
			return 0;
		}
		
		// ===============================================================


	}
	
}
