using UnityEngine;
using System.Collections;

public class PlayerLook : MonoBehaviour 
{
	public enum AxesMode { XandY = 0, X = 1, Y = 2 };

	public AxesMode axes = AxesMode.XandY;				// x and y by default

	private const float sensitivityX_DEBUG = 5.0f;
	private const float sensitivityX = 15.0f;
	private const float sensitivityY = 5.0f;
	private const float LOOK_THRESHOLD = 0.10f;		// how much the accelerometer must move before the player turns

	private const float maximumY = 60.0f;

	private float rotationX = 0;
	private float rotationY = 0;

	// ====================================================

	void Update() 
	{
		// only in pitch and yaw, no roll

		// YAW
		if (axes == AxesMode.XandY || axes == AxesMode.X) {
			// get current x angle and add axis value to it
			rotationX = transform.localEulerAngles.y;	// x-look is YAW (y-axis rotation)

			if (Player.Instance.control == Player.ControlMode.CPU_DEBUG)
				rotationX += Input.GetAxis("Mouse X") * sensitivityX_DEBUG;
			else
				rotationX += Mathf.Abs(Input.acceleration.x) >= LOOK_THRESHOLD ? -Input.acceleration.x*sensitivityX : 0;	// need to negate direction
		}

		// PITCH
		if (axes == AxesMode.XandY || axes == AxesMode.Y) {
			// keep y starting at 0 and just add axis look to it, but between min and max
			if (Player.Instance.control == Player.ControlMode.CPU_DEBUG)
				rotationY += Input.GetAxis("Mouse Y") * sensitivityY;
			else
				rotationY += Input.acceleration.z * sensitivityY;

			rotationY = Mathf.Clamp(rotationY, -maximumY, maximumY);
		}

		transform.localEulerAngles = new Vector3(-rotationY, rotationX, 0);
	}
}
