package physics;

import java.util.ArrayList;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import utility.LWJGLTimer;

import static org.lwjgl.opengl.GL11.*;

import common.MyText;
import common.MyText_Bitmap;
import common.MySphere;
import common.Viewpoint;
import common.engine.Game;
import static common.Common.*;

import static solar_system.Conversions.R_EARTH;
import static solar_system.Conversions.M_EARTH;
import static solar_system.Conversions.G;

/** A simulation where you launch a satellite from earth.<br>
 * 	The user specifies values for initial velocity, angle, and mass.<br>
 *  Satellites are drawn to the screen to show if the values make for a successful orbit.<br>
 * 
 * @author Mike Facelle
 *
 */
public class SatelliteLaunch implements Game
{
	// screen options
	private static final String TITLE = "Satellite Launch Simulator";
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final int FPS = 30;
	/** Number of pixels per meter (ppm).<br>
	 *  10^-4 is used to scale 6.4E6 (R_EARTH) to 640 pixels (screen width).
	 *  The mantissa is used to scale how many earth radii can be displayed on the screen
	 */
	public static float PPM = 8.0E4f;	// not final to allow for mid-simulation changes
	/** The factor PPM will be multiplied/divided by when input requests it */
	private static final float PPM_CHANGE = 2;
	// camera options
	private static final float ZNEAR = -1E+4f;
	private static final float ZFAR = 1E+4f;
	private float pitch = 0;;
	private float yaw = 0;;
	private float roll = 0;
	private static final float OMEGA = 5;	// in degrees - rotational speed
	
	// -----
	
	// simulation information
	public static final float R_ATMOSPHERE = 53000;				// how far out the atmosphere goes (in m)
	public static final float RADIUS = R_EARTH + R_ATMOSPHERE;	// radius projectile launched from: edge of space
	public static final float BOUNDS_RADIUS = R_EARTH * 100.0f;	// how far out particles can go before destroyed
	public static final float MASS = M_EARTH;	// mass of host planet
	public static final float[] LAUNCH_POS = new float[] { 0.0f, RADIUS, 0.0f };
	// initial values
	private static final float INIT_TIMESCALE = 100;		// how much to move dt by in main loop
	private static final float INIT_VELOCITY = 7835.14f;	// in m/s (velocity needed for circular motion at edge-of-space)
	private static final float INIT_MASS = 10.0f;			// in kg
	private static final float INIT_ANGLE = 0.0f;			// in degrees
	private static final float INIT_THRUST = 10.0f;			// in Newtons
	// color of sat[0], the controllable one
	private static final float[] CURRENTSAT_COLOR = new float[] { 0.0f, 1.0f, 0.0f, 1.0f };
	private static final float[] DEFAULT_SAT_COLOR = new float[] { 0.0f, 1.0f, 1.0f, 1.0f };
	private int currentSatIndex = 0;		// index of the current controllable satellite
	
	// variables
	private boolean isPaused = false;	// determines whether or not to draw the pause menu
	private float timeScale = 1;
	private float planetMass = M_EARTH;					// in kg
	private float planetRadius = R_EARTH + R_ATMOSPHERE;// in m
	private float launchMass = INIT_MASS;				// in kg
	private float launchAngle = INIT_ANGLE;				// in degrees
	private float launchVelocity = INIT_VELOCITY;		// in m/s
	private float thrustForce = INIT_THRUST;			// in N
	private float sat0Speed = 0;						// in m/s
	private float sat0Altitude = 0;						// in m above earths surface
	// objects for storing info and drawing:
	private ArrayList<Satellite> satellites;	// the satellite Entity (location, rotation)
	private Sphere earth;						// strictly a graphical representation of the earth
	private static final int NUM_STACKS = 16;	// for sphere rendering
	private static final int NUM_SLICES = 16;	// for sphere rendering
	// game-related objects:
	private Viewpoint camera;
	private LWJGLTimer timer;
	private FloatBuffer textOrthoMatrix;
	private FloatBuffer orthoMatrix;
	
	// -----
	
	// text stuff:
	private static final int FONT_SIZE = 14;
	private MyText_Bitmap mytext;		// MyText object to draw with
	private static final String[] BASE_STRINGS = new String[] {
												"timescale = ",
												"velocity = ",
												"angle = ",
												"mass = ",
												"thrust force = ",
												"",
												"current sat: ",
												"speed = ",
												"altitude = ",
												"pos force = ",
												"neg force = ",
												"total force = ",
												"mass = "
												};
	private static final int NUM_LINES = BASE_STRINGS.length;	// how many lines printed to screen
	private static final String TINPUT_STR = "t = ";
	private static final String VINPUT_STR = "v = ";
	private static final String AINPUT_STR = "a = ";
	private static final String MINPUT_STR = "m = ";
	private static final String FINPUT_STR = "f = ";
	// indices for text strings (ones that change are format ***_STR):
	private static final int TIM_STR = 0;
	private static final int VEL_STR = 1;
	private static final int ANG_STR = 2;
	private static final int MAS_STR = 3;
	private static final int THR_STR = 4;
	private static final int BLANK_STR = 5;
	private static final int SATL_STR = 6;
	private static final int SPD_STR = 7;
	private static final int ALT_STR = 8;
	private static final int POS_STR = 9;	// applied forward thrust force
	private static final int NEG_STR = 10;	// applied backward thrust force
	private static final int TAF_STR = 11;	// total applied force (taf)
	private static final int SMA_STR = 12;	// current satellite mass
	
	// for displaying how to open the menu to the user
	private static final String ESCMENU_STR = "ESC : menu";
	
	private StringBuffer[] strings;				// contains the actual strings to be rendered
	private static final float TEXT_X = 0.0f;						// x-coordinate for all text
	private static float TEXT_Y = HEIGHT-MyText_Bitmap.DEFAULT_FONT_SIZE;	// y-coordinate for input text
	private float[] text_y;	// to hold y-coordinates of all text
	private float escmenutext_x = WIDTH - ESCMENU_STR.length()*FONT_SIZE;	// location of the esc:menu string
	private float escmenutext_y = 0.0f;
	
	// pause menu:
	private SatelliteLaunchPauseMenu pauseMenu;

	// input variables:
	private int inputMode;		// the mode of input from keyboard
	private static final int MODE_NORMAL = 0;
	private static final int MODE_TIMESCALE = 1;
	private static final int MODE_VELOCITY = 2;
	private static final int MODE_ANGLE = 3;
	private static final int MODE_MASS = 4;	
	private static final int MODE_FORCE = 5;
	// temp variables for input:
	private StringBuffer inputString;	// the String to display while typing input
	private float inputVal;				// the inputVal to input, while typing it in
	private boolean isInteger;			// for if a '.' has been entered in the text (switches how numbers are added)
	private int decimalPlaces;			// how many decimal places have been entered so far
			
			
	
// ===============================================================

	public SatelliteLaunch()
	{
		this(MASS, RADIUS);
	}
	
	public SatelliteLaunch(float mPlanet, float rPlanet)
	{
		this.planetMass = mPlanet;
		this.planetRadius = rPlanet;
		timeScale = INIT_TIMESCALE;
		launchMass = INIT_MASS;
		launchAngle = INIT_ANGLE;
		launchVelocity = INIT_VELOCITY;
		timer = new LWJGLTimer();	
		initialize();
		isPaused = false;
	}
	
	
// ===============================================================
	
	private void initialize()
	{
		orthoMatrix = BufferUtils.createFloatBuffer(16);
		textOrthoMatrix = BufferUtils.createFloatBuffer(16);
		initializeGL();
		initializeViewpoint();
		initializeObjects();
		initializePhysics();	// does nothing
		initializeIO();
	}
	
// ===============================================================
	
	@Override
	public void initializeGL() 
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
			Display.destroy();
			System.exit(1);
		}
		
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	// --------------------------------------------------------------

	@Override
	public void initializeViewpoint() 
	{
		textOrthoMatrix = MyText.standardOrthoMatrix();
		
		camera = new Viewpoint();
		camera.setZRange(ZNEAR, ZFAR);
		camera.applyOrthographicMatrix(Display.getWidth(), Display.getHeight(), Viewpoint.CENTER);
        glGetFloat(GL_PROJECTION_MATRIX, orthoMatrix);
        glLoadMatrix(orthoMatrix);
	}

	// --------------------------------------------------------------

	@Override
	public void initializeObjects() 
	{
		satellites = new ArrayList<Satellite>();
		//earth = new MySphere.Builder().setRadius(R_EARTH / PPM).setResolution(3).setPosition(0,0,0).setColor(1.0f,1.0f,1.0f).build();
		earth = new Sphere();
		inputString = new StringBuffer("");
		strings = new StringBuffer[NUM_LINES];
		for(int i = 0; i < NUM_LINES; i++) 
			strings[i] = new StringBuffer(BASE_STRINGS[i]);
		strings[TIM_STR].append("" + INIT_TIMESCALE);
		strings[VEL_STR].append("" + INIT_VELOCITY);
		strings[ANG_STR].append("" + INIT_ANGLE);
		strings[MAS_STR].append("" + INIT_MASS);
		strings[THR_STR].append("" + INIT_THRUST);
		strings[SPD_STR].append("0");
		strings[ALT_STR].append("0");
		strings[POS_STR].append("0");
		strings[NEG_STR].append("0");
		strings[TAF_STR].append("0");
		
		pauseMenu = new SatelliteLaunchPauseMenu();
	}

	// --------------------------------------------------------------

	@Override
	public void initializePhysics() 
	{	
		// not using any physics
	}
	
	// --------------------------------------------------------------

	public void initializeIO()
	{
// -=-=-=-=-=-=-=-=-=-=-=- DEBUGGING -=-=-=-=-=-=-=-=-=-=-=-
System.out.println("loading glyphs");
//-=-=-=-=-=-=-=-=-=-=-=- DEBUGGING -=-=-=-=-=-=-=-=-=-=-=-
		mytext = new MyText_Bitmap(FONT_SIZE).load();
// -=-=-=-=-=-=-=-=-=-=-=- DEBUGGING -=-=-=-=-=-=-=-=-=-=-=-
System.out.println("glyphs done");
//-=-=-=-=-=-=-=-=-=-=-=- DEBUGGING -=-=-=-=-=-=-=-=-=-=-=-

		inputString = new StringBuffer("");
		inputVal = 0;
		isInteger = true;
		decimalPlaces = 0;
		// fix bottom-of-screen text location, if it's not the height use to initialize it
		TEXT_Y = Display.getHeight() - mytext.fontSize();
		text_y = new float[NUM_LINES];
		for (int i = 0; i < NUM_LINES; i++)
			text_y[i] = i*mytext.fontSize();
		escmenutext_x = Display.getWidth() - ESCMENU_STR.length()*mytext.fontSize();
	}


// ===============================================================
	
	@Override
	public void run() 
	{
// -=-=-=-=-=-=-=-=-=-=-=- DEBUGGING -=-=-=-=-=-=-=-=-=-=-=-
System.out.println("program start:");
//-=-=-=-=-=-=-=-=-=-=-=- DEBUGGING -=-=-=-=-=-=-=-=-=-=-=-
		timer.initialize();
		while (!Display.isCloseRequested()) {

			float dt = (float)LWJGLTimer.toSeconds(timer.update()) * timeScale;
			checkSystemInput();
			draw();
			input(dt);
			if (!isPaused) {
				logic(dt);	// logic does nothing
				update(dt);
			}
			
			Display.update();
			Display.sync(FPS);
		}
	}


// ===============================================================
// DRAW
	
	@Override
	/**	Contians a lot of "magic numbers."  Didn't store them in variables because they're
	 * 	only used here, and I just kinda got lazy.<br>
	 * 	Should be storing all: color and position vectors, radius values,
	 *  and rotations for things like orienting the earth 
	 * 
	 */
	public void draw() 
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	
		glLoadIdentity();
		
		// rotate based on user input
		glRotatef(pitch, 1, 0, 0);
		glRotatef(yaw, 0, 1, 0);
		glRotatef(roll, 0, 0, 1);
		
		// render the earth: (at 0,0,0; no translate needed)
		glPushMatrix();
		glColor4f(0.0f, 0.0f, 1.0f, 1.0f);	// blue
		// rotate earth so you're viewing it from celestial plane
		glRotatef(90.0f, 1.0f, 0.0f, 0.0f);	// so NS runs with y-axis
		glRotatef(23.4f, 0, 1, 0);			// axial tilt (in y-axis because of previous x-rotate)
		earth.setDrawStyle(GLU.GLU_SILHOUETTE);
		earth.draw(R_EARTH/PPM, NUM_STACKS, NUM_SLICES);
		// draw earth's axis through it (along z-axis cuz that's how Sphere is drawn)
		glColor4f(0.0f, 0.0f, 0.9f, 1.0f);
		glBegin(GL_LINE_STRIP);
		glVertex3f(0, 0, -R_EARTH*2/PPM);
		glVertex3f(0, 0, R_EARTH*2/PPM);
		glEnd();
		glPopMatrix();
		
		// draw normal to orbital plane through earth
		glColor4f(0.0f, 1.0f, 0.5f, 1.0f);
		glBegin(GL_LINE_STRIP);
		glVertex3f(0, -BOUNDS_RADIUS/PPM, 0);
		glVertex3f(0, BOUNDS_RADIUS/PPM, 0);
		glEnd();
		// draw small sphere to indicate up
		glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		glPushMatrix();
		glTranslatef(0, R_EARTH*2/PPM, 0);
		earth.draw(4.0f, NUM_STACKS, NUM_SLICES);
		glPopMatrix();
		
		// draw satellites:
		for (int i = 0; i < satellites.size(); i++) {
			Satellite sat = satellites.get(i);
			// draw line to the satellite (cuz why not)
			glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
			glBegin(GL_LINE_STRIP);
			glVertex3f(0, 0, 0);
			glVertex3f(sat.x()/PPM, sat.y()/PPM, sat.z()/PPM);
			glEnd();
			// actually draw the satellite:
			sat.draw(1/PPM, true);
		}		
		
		// if paused, display pause menu (translate to 0,0:up-left axes)
		if (isPaused)
			pauseMenu.draw(textOrthoMatrix, orthoMatrix);
		else {	
			// draw info text
			MyText.enterTextDrawMode(textOrthoMatrix);
			mytext.write(inputString, TEXT_X, TEXT_Y);
			for (int i = 0; i < NUM_LINES; i++) {
				mytext.write(strings[i], TEXT_X, text_y[i]);
			}
			// draw ESC : menu text in upper-right corner
			mytext.write(ESCMENU_STR, escmenutext_x, escmenutext_y);
			MyText.exitTextDrawMode(orthoMatrix);
		}
	}

// ===============================================================
// INPUT	

	@Override
	public void checkSystemInput() 
	{
		// escape, if ESC is pressed
		if (Keyboard.isKeyDown(Keyboard.KEY_Q))
			cleanUp(false);			
	}
	
	// --------------------------------------------------------------

	@Override
	public void input(float dt) 
	{	
		// switch to appropriate input mode method
		if (inputMode != MODE_NORMAL && !isPaused)
			inputMode(inputMode);

		// read input and rotate scene accordingly
		if (!isPaused)
			cameraControlMode();
		// control sat[0]
		currentSatControlMode();
		
		// get normal input (no values to store: select input mode or fire)
		while (Keyboard.next() && Keyboard.getEventKeyState()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				isPaused = !isPaused;		// toggle pause menu
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				fire();						// fire a new satellite
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_LBRACKET)) {
				PPM *= PPM_CHANGE;			// shrink scale by PPM_CHANGE
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_RBRACKET)) {
				PPM /= PPM_CHANGE;			// increase scale by PPM_CHANGE
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
				if (satellites.size() > 0)
					satellites.remove(0);	// delete first satellite in list
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD)) {		// actually '>' key (with shift)
				// move to next controllable satellite
				if (satellites.size() > 0)
					currentSatIndex = currentSatIndex+1 > satellites.size()-1 ? 0 : currentSatIndex+1;
				else
					currentSatIndex = 0;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_COMMA)) { 		// actually '<' key (with shift)
				// move to previous controllable satellite
				if (satellites.size() > 0)
					currentSatIndex = currentSatIndex-1 < 0 ? satellites.size()-1 : currentSatIndex-1;
				else
					currentSatIndex = 0;
			}
			// if paused, do not allow variable-entry mode
			if (!isPaused) {
				if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
					inputMode = MODE_TIMESCALE;	// enter timescale-input mode
					inputString = new StringBuffer(TINPUT_STR);
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
					inputMode = MODE_VELOCITY;	// enter velocity-input mode
					inputString = new StringBuffer(VINPUT_STR);
				}
				else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
					inputMode = MODE_ANGLE;		// enter angle-input mode
					inputString = new StringBuffer(AINPUT_STR);
				}
				else if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
					inputMode = MODE_MASS;		// enter mass-input mode
					inputString = new StringBuffer(MINPUT_STR);
				}
				else if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
					inputMode = MODE_FORCE;		// enter thrust-force-input mode
					inputString = new StringBuffer(FINPUT_STR);
				}
			}
		}
	}

	// --------------------------------------------------------------

	/** Receives a value to update if RETURN is pressed.  If it isn't, then
	 *  this method updates the inputVal and inputString variables.
	 * 
	 */
	public void inputMode(int mode)
	{
		// do when key is depressed only, not raised
		while (Keyboard.next() && Keyboard.getEventKeyState()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
				// SHOULD have done these using arrays with indices, instead of individual parameters...
				switch(mode) {
				case MODE_TIMESCALE:
					timeScale = inputVal;
					break;
				case MODE_VELOCITY:
					launchVelocity = inputVal;
					break;
				case MODE_ANGLE:
					launchAngle = inputVal;
					break;
				case MODE_MASS:
					launchMass = inputVal;
					break;
				case MODE_FORCE:
					thrustForce = inputVal;
					break;
				default:		
					break;
				}
				inputVal = 0;
				isInteger = true;
				decimalPlaces = 0;
				inputString = new StringBuffer("");
				inputMode = MODE_NORMAL;	// go back to normal mode now
				break;
			}
			
			int length = inputString.length();
			if (Keyboard.isKeyDown(Keyboard.KEY_BACK) && length > 0) {
				char last = inputString.charAt(length-1);
				inputString.delete(length-1, length);
				// only digits or '.' can be entered, assume it will be one of those:
				if (last == '.')		// switch back to integer domain
					isInteger = true;	
				else if (isInteger) {	// shift right
					inputVal = (inputVal - (int)(last - '0')) / 10;
				}
				else if (!isInteger) {	// reduce number of recorded decimal places
					inputVal -= (float)Math.pow(10,-(decimalPlaces--)) * (int)(last - '0');
					System.out.println(inputVal + ",\t" + decimalPlaces);
				}
			}
			
			// interpret keyboard entry into string and number values:
			char c = Keyboard.getEventCharacter();
			if ('0' <= c && c <= '9' && isInteger) {
				inputString.append(c);
				// shift left, then add new digit
				inputVal = inputVal*10 + (int)(c - '0');
			}
			else if ('0' <= c && c <= '9' && !isInteger) {
				inputString.append(c);
				// multiply digit by negative power of 10, then add to inputVal. increment decimalPlaces
				inputVal += (float)Math.pow(10,-(++decimalPlaces)) * (int)(c - '0');
			}
			else if (c == '.' && isInteger) {
				inputString.append(c);
				isInteger = false;
			}
			// else do nothing		
		}
	}

	// --------------------------------------------------------------

	/**	Rotates the scene based on user input.
	 * 
	 */
	private void cameraControlMode()
	{
		if (inputMode == MODE_NORMAL && Keyboard.isKeyDown(Keyboard.KEY_P)) {
			pitch = 0;					// reset orientation (only if NOT entering values)
			yaw = 0;
			roll = 0;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_I) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			pitch -= OMEGA;			// rotate up (pitch)
			if (pitch < 0)
				pitch += 360;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_K)  || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			pitch += OMEGA;			// rotate down (pitch)
			if (pitch >= 360)
				pitch -= 360;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_J)  || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			yaw -= OMEGA;				// rotate left (yaw)
			if (yaw < 0)
				yaw += 360;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_L)  || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			yaw += OMEGA;				// rotate right (yaw)
			if (yaw >= 360)
				yaw -= 360;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_U)) {
			roll += OMEGA;				// rotate CW (roll)
			if (roll < 0)
				roll += 360;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
			roll -= OMEGA;				// rotate CCW (roll)
			if (roll >= 360)
				roll -= 360;
		}
	}
	
	// --------------------------------------------------------------

	/** Applies thrust
	 * 
	 */
	private void currentSatControlMode() 
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) {	// actually '+' key (due to Shift)
			if (satellites.size() > 0) {			// apply forward thrust
				Satellite sat = satellites.get(currentSatIndex);
				// motion all in xy-plane. determine angle to have thrust act on
				float theta = (float)Math.atan(Math.abs(sat.dy() / sat.dx()));
				float fx = INIT_THRUST*(float)Math.cos(theta);
				fx = sat.dx() < 0 ? -fx : fx;
				float fy = INIT_THRUST*(float)Math.sin(theta);
				fy = sat.dy() < 0 ? -fy : fy;
				sat.applyForcePos(fx, fy, 0.0f);
			}
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_MINUS)) {
			if (satellites.size() > 0) {			// apply retrograde thrust
				Satellite sat = satellites.get(currentSatIndex);
				// motion all in xy-plane. determine angle to have thrust act on
				float theta = (float)Math.atan(Math.abs(sat.dy() / sat.dx()));
				float fx = -INIT_THRUST*(float)Math.cos(theta);
				fx = sat.dx() < 0 ? -fx : fx;
				float fy = -INIT_THRUST*(float)Math.sin(theta);
				fy = sat.dy() < 0 ? -fy : fy;
				sat.applyForceNeg(fx, fy, 0.0f);
			}
		}
	}
	
// ===============================================================
// LOGIC/UPDATE	
	
	@Override
	public void logic(float dt) 
	{
		// basically all handled during update() - this one is simple
		// why iterate through all the satellites twice, once for bound-check and once for update
		// just do it all in udpate()
	}

	// --------------------------------------------------------------

	@Override
	public void update(float dt) 
	{
		// update satellites and remove out-of-bounds ones
		for (int i = 0; i < satellites.size(); i++) {
			Satellite s = satellites.get(i);
			s.update(dt);	// update first, to include any forces applies during input()
			if (i == currentSatIndex)		// if controllable satellite, set different color. player can control its thrust
				s.setColor(CURRENTSAT_COLOR);
			else
				s.setColor(DEFAULT_SAT_COLOR);
			float[] fg = s.gravitationalForce();
			s.setForce(fg[0], fg[1], fg[2]);
			if (outOfBounds(s.x(), s.y(), s.z()) || insideEarth(s.x(), s.y(), s.z())) {
				System.out.println("removing");
				satellites.remove(i--);
			}
		}
		
		updateStrings();	// update displayed Strings
		
	}
	
	// --------------------------------------------------------------

	/** Updates the StringBuffer objects displayed on the screen */
	public void updateStrings()
	{
		// update variables for satellite[0], if it exists (data used in strings)
		if (satellites.size() > 0) {
			Satellite sat = satellites.get(0);
			sat0Speed = magnitude(sat.dx(), sat.dy(), sat.dz());
			sat0Altitude = magnitude(sat.x(), sat.y(), sat.z()) - R_EARTH;
		}
		else {
			sat0Speed = 0;
			sat0Altitude = 0;
		}
		// now update strings (rewrite them using current values)
		// there's definitely a more efficient way to do this (using arrays for everything, then just one loop)
		// but it works and doesn't really slow things down, so I'll leave it.
		// must copy+paste and modify a line for each string added... should have done arrays...
		(strings[TIM_STR] = new StringBuffer(BASE_STRINGS[TIM_STR])).append("" + timeScale);
		(strings[VEL_STR] = new StringBuffer(BASE_STRINGS[VEL_STR])).append("" + launchVelocity);
		(strings[ANG_STR] = new StringBuffer(BASE_STRINGS[ANG_STR])).append("" + launchAngle);
		(strings[MAS_STR] = new StringBuffer(BASE_STRINGS[MAS_STR])).append("" + launchMass);
		(strings[THR_STR] = new StringBuffer(BASE_STRINGS[THR_STR])).append("" + thrustForce);

		if (satellites.size() > 0) {
			(strings[SATL_STR] = new StringBuffer(BASE_STRINGS[SATL_STR])).append("" + currentSatIndex);
			(strings[SPD_STR] = new StringBuffer(BASE_STRINGS[SPD_STR])).append("" + satellites.get(currentSatIndex).speed());
			(strings[ALT_STR] = new StringBuffer(BASE_STRINGS[ALT_STR])).append("" + (satellites.get(currentSatIndex).positionMag() - R_EARTH));
			(strings[POS_STR] = new StringBuffer(BASE_STRINGS[POS_STR])).append("" + satellites.get(currentSatIndex).appliedForcePos());
			(strings[NEG_STR] = new StringBuffer(BASE_STRINGS[NEG_STR])).append("" + satellites.get(currentSatIndex).appliedForceNeg());
			(strings[TAF_STR] = new StringBuffer(BASE_STRINGS[TAF_STR])).append("" + satellites.get(currentSatIndex).totalAppliedForce());
			(strings[SMA_STR] = new StringBuffer(BASE_STRINGS[SMA_STR])).append("" + satellites.get(currentSatIndex).mass());
		}
		else {
			(strings[SATL_STR] = new StringBuffer(BASE_STRINGS[SATL_STR])).append("none");
			(strings[SPD_STR] = new StringBuffer(BASE_STRINGS[SPD_STR])).append("");
			(strings[ALT_STR] = new StringBuffer(BASE_STRINGS[ALT_STR])).append("");
			(strings[POS_STR] = new StringBuffer(BASE_STRINGS[POS_STR])).append("");
			(strings[NEG_STR] = new StringBuffer(BASE_STRINGS[NEG_STR])).append("");
			(strings[TAF_STR] = new StringBuffer(BASE_STRINGS[TAF_STR])).append("");
			(strings[SMA_STR] = new StringBuffer(BASE_STRINGS[SMA_STR])).append("");
		}

	}
	
	
// ===============================================================
// ENGINE
	
	/**	"Fires" the satellite with a given initial velocity and launch angle given by
	 * 	the variables in this class.<br>
	 * 	Fires in the xy-plane, for simplicity.<br>
	 * 	Launch point is along the y-axis at the surface of the planet and z=0.
	 * 
	 */
	private void fire()
	{
		// fire new Satellite originating at planet's surface
		// fire only in the x-y plane, for simplicity
		Satellite s = new Satellite(launchMass, planetMass);
		s.setVelocity(launchVelocity*(float)Math.cos(Math.toRadians(launchAngle)), 
					launchVelocity*(float)Math.sin(Math.toRadians(launchAngle)), 
					0.0f);
		s.setPosition(LAUNCH_POS);
		float[] fg = s.gravitationalForce();
		s.setForce(fg[0], fg[1], fg[2]);
		satellites.add(s);
	}
	
	// --------------------------------------------------------------
	
	/** Determines if the xyz position is "inside" the earth. (R_COM < R_EARTH).<br>
	 *  Compares the magnitude of the satellite's position to the length of earth's radius.
	 * 
	 * @return True if the r vector to the Satellite's center is less than the magnitude of Earth's radius, R_EARTH.
	 */
	private boolean insideEarth(float x, float y, float z)
	{
		return magnitude(x,y,z) < R_EARTH ? true : false;
	}
	
	// ---
	
	/** Determines if the xyz position falls outside of the allowed distance
	 * 
	 * @return True if the magnitude of the position vector (x,y,z) is greater than the BOUNDS_RADIUS parameter
	 */
	private boolean outOfBounds(float x, float y, float z)
	{
		return magnitude(x,y,z) < BOUNDS_RADIUS ? false : true;
	}
	

	
// ===============================================================
// CLEAN-UP
	
	public void cleanUp(boolean isCrash)
	{
		Display.destroy();
		System.exit(isCrash ? 1 : 0);
	}
	
// ===============================================================

}
