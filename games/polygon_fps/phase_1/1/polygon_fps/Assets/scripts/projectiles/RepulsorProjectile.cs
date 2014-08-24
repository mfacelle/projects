using UnityEngine;
using System.Collections;

public class RepulsorProjectile : Projectile 
{
	private const float REPULSIVE_FORCE = 25.0f;
	private const float MIN_DISTANCE = 0.25f;

	// repel away from other particles
	void FixedUpdate() 
	{
		GameObject[] projectiles = GameObject.FindGameObjectsWithTag("projectile");

		// use only x and z axes, no movement in y
		float fx = 0;
		float fz = 0;

		foreach (GameObject go in projectiles) {

			float dx = transform.position.x - go.transform.position.x;
			float dz = transform.position.z - go.transform.position.z;
			if (dx == 0 && dz == 0)
				continue;	// same particle, ignore it

			// put a limit on how high the force can get
			if (Mathf.Abs(dx) < MIN_DISTANCE)
				dx = dx < 0 ? -MIN_DISTANCE : MIN_DISTANCE;
			if (Mathf.Abs(dz) < MIN_DISTANCE)
				dz = dz < 0 ? -MIN_DISTANCE : MIN_DISTANCE;

			fx += REPULSIVE_FORCE/dx;
			fz += REPULSIVE_FORCE/dz;
		}

		rigidbody.AddForce(new Vector3(fx, 0, fz));
	}
}
