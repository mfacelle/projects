using UnityEngine;
using System.Collections;

// adjusts camera for iphone 5 vs iphone 4
public class CameraFixer : MonoBehaviour 
{
	// tolerance for aspect ratios to be equal to
	private const float TOLERANCE = 0.10f;

	// the value for width (height for landscape) to keep constant, regardless of aspect ratio
	private const float constantValue = 6.40f;	// iphone width, in unity world units (1 = 100px)

	// aspect ratios to support
	private const float aspect2_3 = 2.0f/3.0f;
	private const float aspect3_4 = 3.0f/4.0f;
	private const float aspect9_16 = 9.0f/16.0f;
	private const float aspect3_5 = 3.0f/5.0f;

	void Start () 
	{
		// camera size based on screen size - should work for ANY device, in theory...
		float width = Screen.width;
		float height = Screen.height;
		float aspectTall = width/height;	// since devices width < height
		float aspectWide = height/width;

		// the magic numbers for orthographic size derived manually and are found in my notes
		if (Screen.orientation == ScreenOrientation.Portrait || Screen.orientation == ScreenOrientation.PortraitUpsideDown) {
			if (inTolerance(aspectTall, aspect2_3)) {			// iphone 4
				Camera.main.orthographicSize = 4.8f;
				Camera.main.aspect = aspect2_3;
			}
			else if (inTolerance(aspectTall, aspect3_4)) {
				Camera.main.orthographicSize = 4.267f;
				Camera.main.aspect = aspect3_4;
			}
			else if (inTolerance(aspectTall, aspect9_16)) {		// iphone 5
				Camera.main.orthographicSize = 5.68f;
				Camera.main.aspect = aspect9_16;
			}
			else if (inTolerance(aspectTall, aspect3_5)) {
				Camera.main.orthographicSize = 5.333f;
				Camera.main.aspect = aspect3_5;
			}
		}
// DO LATER: (phase 4?)
		else if (Screen.orientation == ScreenOrientation.LandscapeLeft || Screen.orientation == ScreenOrientation.LandscapeRight) {
			if (inTolerance(aspectTall, aspect2_3)) {
				
			}
			else if (inTolerance(aspectTall, aspect3_4)) {
				
			}
			else if (inTolerance(aspectTall, aspect9_16)) {
				
			}
			else if (inTolerance(aspectTall, aspect3_5)) {
				
			}
		}
	}

	// ===========================================

	// returns true if value is within aspect +/- TOLERANCE %
	private bool inTolerance(float value, float aspect)
	{
		return value >= aspect-aspect*TOLERANCE && value <= aspect+aspect*TOLERANCE;
	}

}
