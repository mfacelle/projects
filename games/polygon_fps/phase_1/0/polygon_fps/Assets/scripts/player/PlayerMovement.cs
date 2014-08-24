using UnityEngine;
using System.Collections;

public class PlayerMovement : MonoBehaviour 
{
	[HideInInspector]
	public static PlayerMovement Instance { get; private set; }

	public Player.ControlMode control = Player.ControlMode.CPU_DEBUG;	// debug by default

	public const float MOVE_SPEED = 10.0f;
	public const float JUMP_SPEED = 10.0f;
	public const float GRAVITY = 20.0f;
	public const float IN_AIR = 15.0f;

	private CharacterController controller;

	private Vector3 movement;

	// ====================================================

	void Start() 
	{
		controller = GetComponent<CharacterController>();
		movement = Vector3.zero;
		Instance = this;	// make globally accessible
	}

	// ====================================================

	// code very similar to Unity API code for character controller
	// not allowing jump
	void Update () 
	{
		if (control == Player.ControlMode.CPU_DEBUG) {
			// strafe based on direction camera is facing
			float dx = Input.GetAxis("Horizontal") * MOVE_SPEED;
			float dz = Input.GetAxis("Vertical") * MOVE_SPEED;

			move(dx, dz);
		}
	}

	// ====================================================

	// moves the player in the x-z plane.  called by MultiTouchManager
	public void move(float dx, float dz)
	{
		
		if (controller.isGrounded) {
			movement = new Vector3(dx, 0, dz);
			// use TransformDirection since Move() requires global vector, not relative
			movement = transform.TransformDirection(movement.x, 0, movement.z);	
//			if (Input.GetButton("Jump"))
//				movement.y = JUMP_SPEED;
		}
//		else {	// allow for slower in-air movement, if in opposite direction
//			movement.x += Mathf.Abs(movement.x + dx/IN_AIR) < MOVE_SPEED ? dx/IN_AIR : 0;
//			movement.z += Mathf.Abs(movement.z + dz/IN_AIR) < MOVE_SPEED ? dz/IN_AIR : 0;
//			float tempY = movement.y;	// store to avoid losing it in TransformDirection
//			// use TransformDirection since Move() requires global vector, not relative
//			movement = transform.TransformDirection(movement.x, 0, movement.z);	
//			movement.y = tempY;
//		}

		// keep player grounded
		if (transform.position.y >= 1)
			movement.y -= GRAVITY * Time.deltaTime;
		else 	// keep player from falling through floor, sometimes hitting corners sends player downward
			transform.position = new Vector3(transform.position.x, 1, transform.position.z);
		controller.Move(movement * Time.deltaTime);
	}
}
