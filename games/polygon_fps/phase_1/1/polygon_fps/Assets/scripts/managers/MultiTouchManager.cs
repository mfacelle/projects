using UnityEngine;
using System.Collections;

public class MultiTouchManager : MonoBehaviour 
{
	private const float MOVE_SCALE = 0.10f;	// scale touch displacement so player moves at a reasonable speed

	void Start()
	{
		Input.multiTouchEnabled = true;
	}
	
	void Update() 
	{
//		if (Player.Instance.control == Player.ControlMode.CPU_DEBUG)
//			return;	// ignore multi touch if in cpu debug mode

		// wont be that many particles on screen (probably no more than 4, gets too tough)
		// so doing this brute-force should be fine
		Touch[] touches = Input.touches;
		Debug.Log(touches.Length);
		for (int i = 0; i < touches.Length; i++) {
			// get the touch coordinates
			Vector2 pos = touches[i].position;

			// DIRECTIONAL BUTTONS
			float directionalEdge = DirectionalDisplay.Instance.Edge;
			if (pos.x <= directionalEdge && pos.y <= directionalEdge) {
				// must use pos - center, or player moves in opposite direction!
				Player.Instance.Movement.move((pos.x-DirectionalDisplay.Instance.Center.x)*MOVE_SCALE, (pos.y-DirectionalDisplay.Instance.Center.y)*MOVE_SCALE);
			}
			// ANYWHERE ELSE: FIRE WEAPON
			else {
				TouchPhase phase = touches[i].phase;
				Debug.Log("phase:" + phase);
				// send fire signals to weapon
				if (phase == TouchPhase.Began)
					Player.Instance.CurrentWeapon.SigFire = true;
				Player.Instance.CurrentWeapon.SigHoldFire =  (phase == TouchPhase.Moved || phase == TouchPhase.Stationary);

			}
		}
	}
}
