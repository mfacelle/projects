using UnityEngine;
using System.Collections;

// adjusts camera for iphone 5 vs iphone 4
public class CameraFixer : MonoBehaviour 
{
	void Start () 
	{
		// set camera aspect ratio to correct for device screen resolution
		if (Screen.height <= 980 || iPhone.generation == iPhoneGeneration.iPhone4 || iPhone.generation == iPhoneGeneration.iPhone4S) {
			Camera.main.orthographicSize = 4.8f;	// 640x960
			Camera.main.aspect = 2.0f/3.0f;			// correct the aspect ratio
		}
		else {	// assume iphone 5 (gen 3 can't run iOS 4.3+ anyway)
			Camera.main.orthographicSize = 5.68f;	// 640x1136
			Camera.main.aspect = 640.0f/1136.0f;		// make sure aspect is correct
		}

		// based on screen size - for ANY device!
	}
}
