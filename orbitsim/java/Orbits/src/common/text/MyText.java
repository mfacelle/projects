package common.text;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.Display;

import utility.BufferTools;

public abstract class MyText 
{
	private static final char FIRST_VISIBLE_ASCII = 0x20;	// ' ' in ascii 
	private static final char DELETE_ASCII = 0x7F;	// backspace is last char, dont display
	
	public abstract MyText load();
	public abstract void draw();
	public abstract void write(String s, float x, float y);

// ==================================================================	
	
	public static void enterTextDrawMode(FloatBuffer orthographicProjectionMatrix)
	{
		glMatrixMode(GL_PROJECTION);
        glLoadMatrix(orthographicProjectionMatrix);
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();
        //glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
	}
	
	// ---
	
	public static void exitTextDrawMode(FloatBuffer projectionMatrix)
	{
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_DEPTH_TEST);
		//glEnable(GL_LIGHTING);
        glPopMatrix();
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(projectionMatrix);	// can be either perspective or orthographic
        glMatrixMode(GL_MODELVIEW);
	}
	
// ==================================================================	
	
	/** Returns a FloatBuffer version of a pretty standard OrthographicProjectMatrix
	 *  for rending text, with (0,0) in the upper-left corner.
	 */
	public static FloatBuffer standardOrthoMatrix()
	{
		FloatBuffer orthoMatrix = BufferTools.reserveData(16);
		glPushMatrix();
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1);
        glGetFloat(GL_PROJECTION_MATRIX, orthoMatrix);
        glLoadMatrix(orthoMatrix);
        glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
		return orthoMatrix;
	}
	
// ==================================================================	

	public static boolean isVisibleChar(char c)
	{
		return c >= FIRST_VISIBLE_ASCII && c < DELETE_ASCII;
	}
}
