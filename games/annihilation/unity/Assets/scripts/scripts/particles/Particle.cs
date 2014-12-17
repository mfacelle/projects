using UnityEngine;
using System.Collections;

public abstract class Particle : MonoBehaviour 
{
	// force constants used for functions to determine applied force
	public const float FX_MAX = 500.0f;
	public const float FY_MIN = 10.0f;
	public const float FY_MAX = 500.0f;

	// "magnetic" force magnitude applied between particles that can annihilate
	public const float F_MAGNETIC = 5.0f;
	// max distance for magnetic force to act on (in 1 world pos = 100px)
	public const float FMAG_MAX_R = 2.5f;
	// min distance for magnetic force (limit how strong the applied force can be)
	public const float FMAG_MIN_R = 1E-1f;
	
	// buffer from top of screen to prevent particles from annihilating out of sight of the player (in px)
	public const float TOP_BUFFER = 64.0f;

	// for subclass initialization:
	// tags for various particle types
	protected static string TAG_NORM = "normparticle";
	protected static string TAG_ANTI = "antiparticle";
	protected static string TAG_LIGHT = "lightparticle";
	protected static string TAG_DARK = "darkparticle";
	protected static string TAG_MINI = "miniparticle";
	// point values for various particle types
	protected static int POINTS_NORMANTI = 5;
	protected static int POINTS_DARKLIGHT = 10;
	protected static int POINTS_MINI = 50;

	// to make bounce sfx more interesting: range of pitches
	private const float BOUNCE_SFX_PITCH_LO = 0.80f; 
	private const float BOUNCE_SFX_PITCH_HI = 1.25f;

	// ---
	
	// explosion prefab:
	public Annihilation anni;

	// to be assigned a value by subclass: 
	// the tag for the particle this one annihilates with
	protected string oppositeTag = TAG_NORM;	// norm as default
	// point value
	protected int points = POINTS_NORMANTI;		// 1 as default

	// =================================================
	
	// commented out for the device, or else this gets called WITH MultiTouchManager's OnTouch() call
	// which will double the force applied!
//	protected void OnMouseDown()
//	{
//		// determine angle between mouse click and object position
//		Vector2 mouse = Camera.main.ScreenToWorldPoint(Input.mousePosition);
//		OnTouch(mouse.x, mouse.y);
//	}
	
	// ------------------------------------------

	// for outside calls of OnMouseDown() : from MultiTouchManager
	// if a particle is clicked on, give it an upward force
	// depends on where object was clicked: can send it off to right or left as well
	public void OnTouch(float x, float y)
	{
		// play bounce sfx
		// select random pitch so bounce sfx isn't as boring
		if (ScoreManager.instance.isMusicOn()) {
			audio.pitch = Random.Range(BOUNCE_SFX_PITCH_LO, BOUNCE_SFX_PITCH_HI);
			audio.Play();
		}

		// determine distance between touch point and particle center
		float dy = transform.position.y - y;	// always an upward force
		float dx = transform.position.x - x;
		
		// functions used for determining force
		// derived from looking at desired plots of [fx vs dx] and [fy vs dy]
		float fx = FX_MAX * tanh(dx);					// can be either negative or positive
		float fy = (FY_MAX-FY_MIN) * sech(dy) + FY_MIN;	// always positive

		// adjust force on mini particles - weaken it because they go flying up WAY too much
		if (this is MiniParticle) {
			fx *= 3.0f/4.0f;
			fy *= 3.0f/4.0f;
		}
		
		// adjust v_y if in easy mode (less gravity, so particles will go flying up too much)
		if (PlayerPrefs.GetInt(ScoreManager.GAMEMODE_KEY) == ScoreManager.EASYMODE)
			fy /= 1.25f;
		
		// set velocity to 0, then add force, so mouse click has same effect every time
		rigidbody2D.velocity = new Vector2(0,0);
		rigidbody2D.AddForce(new Vector2(fx, fy));
	}

	// =================================================
	
	protected void OnCollisionEnter2D(Collision2D col)
	{
		// determine screen height in world position
		Vector2 screen = Camera.main.ScreenToWorldPoint(new Vector2(0, Camera.main.pixelHeight+TOP_BUFFER));
		
		// annihilates with antiparticles (only if within the bounds of the screen)
		if (col.collider.tag == oppositeTag && transform.position.y < screen.y) {
			// destroy both colliding particles and spawn explosion
			onAnnihilate(col.collider.transform);
			Destroy(col.collider.gameObject);
			Destroy(gameObject);
			// add points to the player
			GameObject.Find("player").GetComponent<Player>().addPoints(points);
		}
	}

	// =================================================
	
	// compute "magnetic" forces between annihilatable particles
	// there won't be too many particles on screen at once, so iterating over them all won't be an issue
	void FixedUpdate()
	{
		Component[] particles = transform.parent.GetComponentsInChildren<Rigidbody2D>();
		foreach (Rigidbody2D rb in particles) {
			if (rb.name == this.name)	// ignore same object (in the case of mini particles, where they attract each other)
				continue;
			else if (rb.tag == oppositeTag) {
				// doing component-wise, so there's no need to deal with arctan and trig functions
				float dx = transform.position.x - rb.transform.position.x;
				float dy = transform.position.y - rb.transform.position.y;
				// put a limit on how high the force can get (and this avoids division by 0)
				if (Mathf.Abs(dx) < FMAG_MIN_R)
					dx = dx < 0 ? -FMAG_MIN_R : FMAG_MIN_R;
				if (Mathf.Abs(dy) < FMAG_MIN_R)
					dy = dy < 0 ? -FMAG_MIN_R : FMAG_MIN_R;

				// only act if within a certain distance (to avoid weird behavior)
				if (Mathf.Sqrt(dx*dx + dy*dy) < FMAG_MAX_R) {
					// limit downward force, to avoid particles being "thrown" down, causing insta-lose
					if (dy > 0)
						dy *= 4.0f;	// must multiply, since this term goes in the denominator

					// negate forces so they're attractive (due to how dx,dy are calculated)
					rigidbody2D.AddForce(new Vector2(-F_MAGNETIC/dx, -F_MAGNETIC/dy));
				}
			}
		}
	}

	// =================================================
	
	void OnTriggerEnter2D(Collider2D col)
	{
		if (col.tag == "remover")
			onRemove();
	}

	// =================================================

	void onRemove()
	{
		Destroy(gameObject);
		// then the player loses (could implement an hp system later)
		Application.LoadLevel("endGame");
	}

	// ------------------------------------------

	// creates an annihilation animation in between the two colliders
	protected void onAnnihilate(Transform col)
	{
		float x = (transform.position.x + col.transform.position.x) / 2.0f;
		float y = (transform.position.y + col.transform.position.y) / 2.0f;

		Instantiate(anni, new Vector2(x, y), Quaternion.identity);
	}

	// =================================================
	// hyperbolic trig functions (for determining input force)

	public static float tanh(float x)
	{
		return (Mathf.Exp(x) - Mathf.Exp(-x)) / (Mathf.Exp(x) + Mathf.Exp(-x));
	}
	// ---
	public static float sech(float x)
	{
		return 2.0f / (Mathf.Exp(x) + Mathf.Exp(-x));
	}
}
