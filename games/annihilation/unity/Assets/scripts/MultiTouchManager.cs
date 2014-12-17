using UnityEngine;
using System.Collections;

// handles multi-touch input
public class MultiTouchManager : MonoBehaviour 
{
	void Start()
	{
		Input.multiTouchEnabled = true;
	}

	void Update() 
	{
		// wont be that many particles on screen (probably no more than 4, gets too tough)
		// so doing this brute-force should be fine
		Touch[] touches = Input.touches;
		for (int i = 0; i < touches.Length; i++) {
			// get the touch coordinates
			Vector2 pos = Camera.main.ScreenToWorldPoint(touches[i].position);

			// get all particles (they will be instantiated as children of this object
			Component[] particles = GetComponentsInChildren<Particle>();
			foreach (Particle p in particles)
				if (p.collider2D.OverlapPoint(pos))
					p.OnTouch(pos.x, pos.y);
		}
	}
}
