using UnityEngine;
using System.Collections;

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
			Vector2 pos = touches[i].position;

			// DIRECTIONAL BUTTONS
			float directionalEdge = DirectionalDisplay.Instance.Edge;
			if (pos.x <= directionalEdge && pos.y <= directionalEdge) {
				Player.Instance.Movement.move(directionalEdge - pos.x, directionalEdge - pos.y);
			}
		}
	}
}
