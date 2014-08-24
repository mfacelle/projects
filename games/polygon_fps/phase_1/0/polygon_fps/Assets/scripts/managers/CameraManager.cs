using UnityEngine;
using System.Collections;

public class CameraManager : MonoBehaviour 
{
	void Start() 
	{	
		// force screen to be landscape
		Screen.orientation = ScreenOrientation.Landscape;
		Screen.autorotateToLandscapeLeft = true;
		Screen.autorotateToLandscapeRight = true;
	}
}
