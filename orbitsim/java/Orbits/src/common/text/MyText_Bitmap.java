package common.text;

import static org.lwjgl.opengl.GL11.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.ResourceLoader;

import utility.BufferTools;

import de.matthiasmann.twl.utils.PNGDecoder;

public class MyText_Bitmap extends MyText
{
	public static String DEFAULT_FONT_FILEPATH = "res/images/font.png";
	public static final int DEFAULT_GRID_SIZE = 16;
	public static final int DEFAULT_FONT_SIZE = 16;
	public static final float WIDTH_FACTOR = 1.0f;			// divide fontSize by this when drawing (for width)
	public static final float[] DEFAULT_FONT_COLOR = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	
	private String fontFilepath;
	private int gridSize;		// size of texture grid used for fonts; ie: 16 = 16x16, 8 = 8x8, etc
	private int fontTexture;	// pointer to the texture object
	private float fontSize;		// size of font (as standard units, converted in renderString())
	private StringBuffer text;	// the text stored in this class
	// cannot implement color...
	private float r;
	private float g;
	private float b;
	private float a;
	
// ==================================================================	

	public MyText_Bitmap()
	{
		this(DEFAULT_FONT_SIZE, DEFAULT_FONT_COLOR, DEFAULT_GRID_SIZE, DEFAULT_FONT_FILEPATH);
	}
	public MyText_Bitmap(float font_size)
	{
		this(font_size, DEFAULT_FONT_COLOR, DEFAULT_GRID_SIZE, DEFAULT_FONT_FILEPATH);
	}
	public MyText_Bitmap(float font_size, float[] color, int grid_size, String filePath)
	{
		this.fontFilepath = filePath;
		this.fontSize = font_size;
		this.r = color[0];
		this.g = color[1];
		this.b = color[2];
		this.a = color[3];
		this.gridSize = grid_size;
		this.text = new StringBuffer("");
		load();
	}
	
// ==================================================================	
// loading
	
	public MyText_Bitmap load()
	{
		try {
			loadTextures();
		} 
		catch (IOException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		return this;
	}
	
	// --------------------------------------------------------------
	
	/**
    * 	Code from one of Oskar Veerhoek's tutorials.
    * 
    * @author Oskar Veerhoak
    * 	Copyright (c) 2013, Oskar Veerhoek
    * 	All rights reserved.
    */
    private void loadTextures() throws IOException 
    {
        // Create a new texture for the bitmap font.
        fontTexture = glGenTextures();
        // Bind the texture object to the GL_TEXTURE_2D target, specifying that it will be a 2D texture.
        glBindTexture(GL_TEXTURE_2D, fontTexture);
        // Use TWL's utility classes to load the png file.
        //PNGDecoder decoder = new PNGDecoder(new FileInputStream(fontFilepath));
        PNGDecoder decoder = new PNGDecoder(ResourceLoader.getResourceAsStream(fontFilepath));
        ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buffer.flip();
        // Load the previously loaded texture data into the texture object.
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
                buffer);
        // Unbind the texture.
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
// ==================================================================	
// drawing
    
	public void draw()
	{
		
	}
	
	// --------------------------------------------------------------

	public void write(String s, float x, float y)
	{
		//renderString(s, fontTexture, gridSize, x, y, fontSize);
		renderString(s, fontTexture, gridSize, x, y, fontSize/WIDTH_FACTOR, fontSize);
	}
	
	// ---
	
	public void write(StringBuffer s, float x, float y)
	{
		write(s.toString(), x, y);
	}
	
	// --------------------------------------------------------------

	/**
     * Renders text using a font bitmap.
     * 
     * 	Code from one of Oskar Veerhoek's tutorials.
     * 
     * @author Oskar Veerhoak
     * 	Copyright (c) 2013, Oskar Veerhoek
     * 	All rights reserved.
     * 
     * @modified Mike Facelle, Spring 2014
     *
     * @param string the string to render
     * @param textureObject the texture object containing the font glyphs
     * @param gridSize the dimensions of the bitmap grid (e.g. 16 -> 16x16 grid; 8 -> 8x8 grid)
     * @param x the x-coordinate of the bottom-left corner of where the string starts rendering
     * @param y the y-coordinate of the bottom-left corner of where the string starts rendering
     * @param characterWidth the width of the character
     * @param characterHeight the height of the character
     */
    private void renderString(String string, int textureObject, int gridSize, float x, float y,
                                     float charWidth, float charHeight) 
    {
        glPushAttrib(GL_TEXTURE_BIT | GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureObject);
        // Enable linear texture filtering for smoothed results.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        // Enable additive blending. This means that the colours will be added to already existing colours in the
        // frame buffer. In practice, this makes the black parts of the texture become invisible.
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        // Store the current model-view matrix.
        glPushMatrix();
        // Offset all subsequent (at least up until 'glPopMatrix') vertex coordinates.
        glTranslatef(x, y, 0);
        glBegin(GL_QUADS);
        // Iterate over all the characters in the string.
        for (int i = 0; i < string.length(); i++) {
            // Get the ASCII-code of the character by type-casting to integer.
            int asciiCode = (int) string.charAt(i);
            // There are gridSize cells in a texture, and a texture coordinate ranges from 0.0 to 1.0.
            final float cellSize = 1.0f / gridSize;
            // The cell's x-coordinate is the greatest integer smaller than remainder of the ASCII-code divided by the
            // amount of cells on the x-axis, times the cell size.
            float cellX = ((int) asciiCode % gridSize) * cellSize;
            // The cell's y-coordinate is the greatest integer smaller than the ASCII-code divided by the amount of
            // cells on the y-axis.
            float cellY = ((int) asciiCode / gridSize) * cellSize;
            // set color
            glColor4f(r, g, b, a);
            // texture coordinates and text coordinates on screen (screen goes up-down, texture is down-up)
            glTexCoord2f(cellX, cellY+cellSize);
            glVertex2f(i*charWidth, charHeight);
            glTexCoord2f(cellX+cellSize, cellY+cellSize);
            glVertex2f(i*charWidth+charWidth, charHeight);
            glTexCoord2f(cellX+cellSize, cellY);
            glVertex2f(i*charWidth+charWidth, 0);
            glTexCoord2f(cellX, cellY);
            glVertex2f(i*charWidth, 0);
        }
        glEnd();
        glPopMatrix();
        glPopAttrib();
    }

// ==================================================================	
// get and set

    public float fontSize()		{ return fontSize; }
    public StringBuffer text()	{ return text; }
    public String textString()	{ return text.toString(); }
	public float[] getColor()	{ return new float[] {r, g, b, a}; }
	public float r()		{ return r; }
	public float g()		{ return g; }
	public float b()		{ return b; }
	public float a()		{ return a; }
    // ---
	public void setFontFilepath(String s)	{ this.fontFilepath = s; }
    public void setFontSize(float f)		{ this.fontSize = f; }
    public void setText(String s)		{ this.text = new StringBuffer(s); }
    public void setText(StringBuffer s)	{ this.text = s; }
	public void setColor(float rr, float gg, float bb)
	{	this.r = rr;	this.g = gg;	this.b = bb; this.a = DEFAULT_FONT_COLOR[3]; }
	public void setColor(float rr, float gg, float bb, float aa)
	{	this.r = rr;	this.g = gg;	this.b = bb; this.a = aa; }    
}
