package common.engine;

public interface Game 
{
	/** Create OpenGL context, physics, viewpoints, etc... */
	public abstract void initialize();
//	/** Initializes GL and Display */
//	public abstract void initializeGL();
//	/** Initializes Viewpoint object */
//	public abstract void initializeViewpoint();
//	/** Initializes drawn objects, physics definitions, etc */
//	public abstract void initializeObjects();
//	/** Initialize physics world */
//	public abstract void initializePhysics();
	// ---
	/** Enter and runs the main loop */
	public abstract void run();
//	/** Check for system input, such as ESC or force-quit type commands */
//	public abstract void checkSystemInput();
	/** Check input */
	public abstract void input(float dt);
	/** Draw the scene */
	public abstract void draw();
	/** Update physics and other game mechanics */
	public abstract void logic(float dt);
	/** Update Display, objects, etc */
	public abstract void update(float dt);	
}
