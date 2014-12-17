using UnityEngine;
using System.Collections;

public class Spawner : MonoBehaviour 
{
	// "radius" that particles spawn at in x-direction (in px)
	public const int SPAWN_BUFFER = 25;	// edge buffer so particles don't spawn outside screen (in px)
	
	// spawn times at points = 0 and points = infinity  (in s)
	public const float SPAWN_0 = 10.0f;
	public const float SPAWN_INF = 2.0f;
	public const float SPAWN_INIT = 0.9f;
	// "time" constant for spawnTime function (technically "point" constant)
	public const float TAU = 120.0f;			// 30 min to SPAWN_INF (after 2min: spawnTime~=(1/e)SPAWN_0 )
	
	// how many points to turn on enable signals for other particle types
	public const float PTS_DARKLIGHT = 90.0f;	// 1.5 min (w/o collisions)
	public const float PTS_MINI = 180.0f;		// 3 min (w/o collisions)


	// spawn probabilities
	public const float PROB_NORM = 0.375f;
	public const float PROB_ANTI = 0.375f;
	public const float PROB_DARK = 0.10f;
	public const float PROB_LIGHT = 0.10f;
	public const float PROB_MINI = 0.05f;

	// ---

	// how often particles spawn (in s) - changes during gameplay - based on player points
	private float spawnTime = 10.0f;
	private float sinceLastSpawn = 0;
	private bool spawnedParticles = false;	// if there are no particles, so player doesn't have to wait a while

	// enable signals for particles (norm and anti are always on, others are unlocked at higher scores)
	private bool en_darklight = false;
	private bool en_mini = false;

	// ---

	// prefabs used for instantiation
	public Rigidbody2D normparticle;
	public Rigidbody2D antiparticle;
	public Rigidbody2D darkparticle;
	public Rigidbody2D lightparticle;
	public Rigidbody2D miniparticle;

	// =================================================

	void Start() 
	{
		spawnTime = SPAWN_0;
		sinceLastSpawn = SPAWN_0 * SPAWN_INIT;	// so player doesn't have to wait the whole time
		en_darklight = false;
		en_mini = false;
	}

	// =================================================

	// updates spawn time, spawning particles if appropriate
	// also spawns if there are no more particles on screen (so player doesn't have to wait a while)
	void Update() 
	{
		sinceLastSpawn += Time.deltaTime;
		if (sinceLastSpawn >= spawnTime) {
			spawnParticle();
			sinceLastSpawn -= spawnTime;
			spawnedParticles = true;
		}
		// if there are no particles on screen, wait a bit then spawn one
		else if (spawnedParticles && transform.parent.GetComponentsInChildren<Rigidbody2D>().Length == 0) {
			sinceLastSpawn = spawnTime * SPAWN_INIT;
			spawnedParticles = false;
		}
	}

	// =================================================

	// spawns a particle at spawner's y-location and a random x-location within the screen bounds
	void spawnParticle()
	{
		// choose a random particle type:
		float total = PROB_NORM + PROB_ANTI;		// initial range, supplement with 
		total += en_mini ? PROB_MINI+PROB_DARK+PROB_LIGHT : en_darklight ? PROB_DARK+PROB_LIGHT : 0;
		float type = Random.Range(0.0f, total);

		// spawn particle at a random location (at spawner's y-position)
		Vector2 pos = randomSpawnPosition();

		Rigidbody2D particle;
		if (type >= PROB_DARK+PROB_LIGHT+PROB_ANTI+PROB_NORM && type <= PROB_MINI+PROB_DARK+PROB_LIGHT+PROB_ANTI+PROB_NORM)
			particle = Instantiate(miniparticle, pos, transform.rotation) as Rigidbody2D;
		else if (type >= PROB_LIGHT+PROB_ANTI+PROB_NORM && type < PROB_DARK+PROB_LIGHT+PROB_ANTI+PROB_NORM)
			particle = Instantiate(darkparticle, pos, transform.rotation) as Rigidbody2D;
		else if (type >= PROB_ANTI+PROB_NORM && type < PROB_LIGHT+PROB_ANTI+PROB_NORM)
			particle = Instantiate(lightparticle, pos, transform.rotation) as Rigidbody2D;
		else if (type >= PROB_NORM && type < PROB_ANTI+PROB_NORM)
			particle = Instantiate(antiparticle, pos, transform.rotation) as Rigidbody2D;
		else 	//if (type >= 0 && type < PROB_NORM)
			particle = Instantiate(normparticle, pos, transform.rotation) as Rigidbody2D;

		// make "foreground" (this object's parent), the parent for particles
		particle.transform.parent = gameObject.transform.parent;

		// modify some stuff for easy mode
		if (PlayerPrefs.GetInt(ScoreManager.GAMEMODE_KEY) == ScoreManager.EASYMODE) {
			particle.gravityScale /= 2.0f;	// cut gravity in half
			particle.transform.localScale = new Vector2(particle.transform.localScale.x+0.25f, particle.transform.localScale.y+0.25f);
		}
	}

	// ------------------------------------------

	// updates the spawn time and which particles can be spawned, based on amount of points
	public void updateSpawner(float points)
	{
		// [f(0)-f(inf)]*e^(-t/tau) + f(inf), but t = points, not time
		spawnTime = (SPAWN_0-SPAWN_INF)*Mathf.Exp(-points/TAU) + SPAWN_INF;
		// enable other particles if enough points have been accumulated
		if (points >= PTS_DARKLIGHT)
			en_darklight = true;
		if (points >= PTS_MINI)
			en_mini = true;
	}

	// =================================================

	// returns a random spawn position at the spawner's y-position,
	// and a random x-position on the screen (based on SPAWN_RADIUS)
	Vector2 randomSpawnPosition()
	{
		int x = Random.Range(SPAWN_BUFFER, (int)Camera.main.pixelWidth-SPAWN_BUFFER);
		Vector2 pos = new Vector2(x, 0);	// will ignore y.  need vector2 for ScreentoWorldPosition
		pos = Camera.main.ScreenToWorldPoint(pos);
		return new Vector2(pos.x, transform.position.y);
	}
}
