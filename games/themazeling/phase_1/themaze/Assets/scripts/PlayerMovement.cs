using UnityEngine;
using System.Collections;

public class PlayerMovement : MonoBehaviour 
{
	// direction codes for heading
	private const int E = 0;
	private const int NE = 1;
	private const int N = 2;
	private const int NW = 3;
	private const int W = 4;
	private const int SW = 5;
	private const int S = 6;
	private const int SE = 7;

	// maximum movement speed
	private const float V_MAX = 2.0f;
	// force applied when moving
	private const float MOVE_FORCE = 20.0f;
	// velocity to consider 0 (since sin,cos may not return exactly 0)
	private const float ZERO_VELOCITY = 0.5f;

	[HideInInspector]
	public float theta = 0;
	[HideInInspector]
	public int heading = 2;	// initially north
	
	// =============================================
	
	void Start () 
	{
		Input.multiTouchEnabled = true;	// enable multi touch - though it won't be used
	}
	
	// -------------------------------------
	
	void Update () 
	{

	}
	
	// -------------------------------------
	
	void FixedUpdate()
	{
		// get position of touch[0] (use mouse for testing) and determine angle from center of screen
		// set position to center, so that dx,dy = 0 when there's no input
		float x = Screen.width/2.0f;
		float y = Screen.height/2.0f;

		Touch[] touches = Input.touches;
		if (touches.Length > 0) {
			Vector2 touchpos = touches[0].position;
			x = touchpos.x;
			y = touchpos.y;
		}
// ---------------------------------------
		// COMMENT THIS OUT FOR ON-HARDWARE BUILD (since unity requires mouse and can't use touches):
		if (Input.GetMouseButton(0)) {
			x = Input.mousePosition.x;
			y = Input.mousePosition.y;
		}
// ---------------------------------------

		// get offset from center of the screen
		float dx = x - Screen.width/2.0f;
		float dy = y - Screen.height/2.0f;
	
		// calculate angle of heading and move player accordingly:
		float th_temp = getAngle(dx, dy);	// temp theta variable (only changes theta if there's user input)
		// apply force and limit player velocity to V_MAX
		// if no user input, apply force in opposite direction and limit player velocity to 0

		float vx = rigidbody2D.velocity.x;
		float vy = rigidbody2D.velocity.y;
		float vmag = Mathf.Sqrt(vx*vx + vy*vy);
		float fx = MOVE_FORCE*Mathf.Cos(th_temp);
		float fy = MOVE_FORCE*Mathf.Sin(th_temp);

		// deal with directions separately (makes it a bit easier and clearer):
		// --- x ---
		if (dx != 0) {
			if (Mathf.Abs(vx) >= V_MAX)		// if moving and at max velocity, stop applying force
				fx = fx*vx < 0 ? fx : 0;	// if f and v are opposite signs, don't negate f! (means player is turning around)
			vx = vmag*Mathf.Cos(th_temp);	// correct velocity based on current angle
		}
		else if (dx == 0) {	// if not moving, decellerate to 0 velocity
			if (Mathf.Abs(vx) <= ZERO_VELOCITY) {
				fx = 0;
				vx = 0;
			}
			else
				fx = vx > 0 ? -MOVE_FORCE : MOVE_FORCE;	// apply force opposite to velocity
		}
		// --- y ---
		if (dy != 0) {
			if (Mathf.Abs(vy) >= V_MAX)		// if moving and at max velocity, stop applying force
				fy = fy*vy < 0 ? fy : 0;	// if f and v are opposite signs, don't negate f! (means player is turning around)
			vy = vmag*Mathf.Sin(th_temp);	// correct velocity based on current angle
		}
		else if (dy == 0) {	// if not moving, decellerate to 0 velocity
			if (Mathf.Abs(vy) <= ZERO_VELOCITY) {
				fy = 0;
				vy = 0;
			}
			else
				fy = vy > 0 ? -MOVE_FORCE : MOVE_FORCE;	// apply force opposite to velocity
		}

		// apply the force and limit velocity
		rigidbody2D.AddForce(new Vector2(fx, fy));
		rigidbody2D.velocity = new Vector2(vx, vy);	// set velocity, in case a direction is changed or v is set to 0

	// ----- animation / player heading / etc -----
		//determine if player is moving, and set animation accordingly
//		if (dx != 0 || dy != 0)
//			anim.SetBool("moving", true);
//		else
//			anim.SetBool("moving", false);

		// only change angle if there is input from user
		if (dx != 0 || dy != 0) {	
			th_temp = toDegrees(th_temp);
			theta = th_temp;
			int dir = determineHeading(th_temp);
			heading = dir;
			// if left/right swap, flip animation accordingly (if not using 8 separate sprites)
		}
	}
	
	// =============================================
	
	// determines the heading based on angle th
	// divide into 8 directions (of 45 deg each), offset by 45/2 deg (half of section)
	private int determineHeading(float th)
	{
		return (int)Mathf.Floor((th + 22.5f) / 45.0f);
	}
	
	// -------------------------------------
	
	private float toDegrees(float rads)
	{
		return rads * 180f / Mathf.PI;
	}

	private float toRadians(float degs)
	{
		return degs * Mathf.PI / 180f;
	}
	
	// -------------------------------------
	
	// returns the angle associated with the x,y values
	// only returns one of eight angles, to make movement simpler
	private float getAngle(float x, float y)
	{
		float th;
		// first, get exact radian value of theta on [0, 2pi):
		// if no x motion, use either 90 or 270 based on direction in y
		if (x == 0)	// if no horizontal, avoid division by 0 (by determining angle from y-direction)
			th = y >= 0 ? Mathf.PI/2.0f : Mathf.PI*3.0f/2.0f;
		else {
			th = Mathf.Atan(y/x);
			// correct angle since Atan returns on [-45, 45] for [-1, 1]
			if (x < 0)
				th += Mathf.PI;
			else if (y < 0)
				th += Mathf.PI*2.0f;
		}
		// return one of 8 cardinal directions: theta = heading*pi/4 (determined in notes)
		return determineHeading(toDegrees(th))*Mathf.PI / 4.0f;
	}
}
