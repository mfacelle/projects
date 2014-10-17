package physics.satellites;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

import common.shapes.MyCurve;
import common.text.MyText;
import common.text.MyText_Bitmap;

public class SatelliteLaunchPauseMenu 
{
	// menu dimensions (3/4 of the screen) (can change if screen size altered)
	private static float WIDTH = SatelliteLaunch.WIDTH * (3/4);	
	private static float HEIGHT = SatelliteLaunch.HEIGHT * (3/4);
	// non-static location variables (can change if screen size is altered)
	// top-left corner of menu
	private static float MENU_X = SatelliteLaunch.WIDTH/2 - WIDTH/2;
	private static float MENU_Y = SatelliteLaunch.HEIGHT/2 - HEIGHT/2;
	private static final int MARGIN = 5;	// pixel margin for inside the menu box
	
	private static final float[] KEYNAME_COLOR = new float[] { 0.0f, 1.0f, 0.1f, 1.0f };
	private static final float[] DESCRIPTION_COLOR = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	private static final float[] BG_COLOR = new float[] { 0.25f, 0.25f, 0.25f, 1.0f };
	
	private static final int FONT_SIZE = 16;
	private static int NUM_LINES = 13;	// initialized at creation
	/** Strings displayed in the menu */
	private static final String[] STRINGS = new String[] {
												"ESC : toggle pause menu",
												"Q   : quit",
												"",
												"Input:",
												"T   : enter timescale",
												"V   : enter velocity",
												"A   : enter angle",
												"M   : enter mass",
												"P   : reset view",
												"",
												"Camera:",
												"[,] : -/+ scale",
												"I,K : -/+ pitch",
												"J,L : -/+ yaw",
												"U,O : ccw/cw roll",
												"",
												"Satellites:",
												"-,+ : neg/pos thrust",
												"<,> : change satellite"
												};

	private static float TEXT_X0 = MENU_X + MARGIN;		// x-location for text
	private static float TEXT_Y0 = MENU_Y + MARGIN; 	// y0-location for text
	private float[] TEXT_Y;								// y-locations for text
	
	private MyText_Bitmap mytext;
	private MyCurve border;
	
	// ===============================================================

	public SatelliteLaunchPauseMenu()
	{
		this(new MyText_Bitmap(FONT_SIZE), SatelliteLaunch.WIDTH, SatelliteLaunch.HEIGHT);
	}
	public SatelliteLaunchPauseMenu(float displayWidth, float displayHeight)
	{
		this(new MyText_Bitmap(FONT_SIZE), displayWidth, displayHeight);
	}
	public SatelliteLaunchPauseMenu(MyText_Bitmap text, float displayWidth, float displayHeight)
	{

		WIDTH = displayWidth * (3.0f/4.0f);
		HEIGHT = displayHeight * (3.0f/4.0f);
		MENU_X = displayWidth/2.0f - WIDTH/2.0f;
		MENU_Y = displayHeight/2.0f - HEIGHT/2.0f;
		
		this.mytext = text;
		NUM_LINES = STRINGS.length;	
		TEXT_X0 = MENU_X + MARGIN;
		TEXT_Y = new float[NUM_LINES];
		for (int i = 0; i < NUM_LINES; i++)
			TEXT_Y[i] = MENU_Y + MARGIN + i*mytext.fontSize();
		float[] borderPoints_x = new float[] { MENU_X, MENU_X+WIDTH,	MENU_X+WIDTH, 	MENU_X, 		MENU_X };
		float[] borderPoints_y = new float[] { MENU_Y, MENU_Y,			MENU_Y+HEIGHT, 	MENU_Y+HEIGHT,	MENU_Y};
		border = new MyCurve(borderPoints_x, borderPoints_y);
		border.setColor(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	// ===============================================================

	public void draw(FloatBuffer textOrthoMatrix, FloatBuffer orthoMatrix)
	{	
		// write all text
		MyText.enterTextDrawMode(textOrthoMatrix);
			
		// draw background square (using text matrix)
		glColor4f(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], BG_COLOR[3]);
		glRectf(MENU_X, MENU_Y, MENU_X+WIDTH, MENU_Y+HEIGHT);
		
		// draw a border
		border.draw();
		
		mytext.setFontSize(FONT_SIZE);
		// draw strings to be displayed - always color of texture used
		glColor4f(DESCRIPTION_COLOR[0], DESCRIPTION_COLOR[1], DESCRIPTION_COLOR[2], DESCRIPTION_COLOR[3]);
		for (int i = 0; i < STRINGS.length; i++) {
			mytext.write(STRINGS[i], TEXT_X0, TEXT_Y[i]);
		}
		MyText.exitTextDrawMode(orthoMatrix);
		
	}
}
