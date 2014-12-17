using UnityEngine;
using System.Collections;

public class TitleScene_GUI : MonoBehaviour 
{
	public GUISkin guiSkin;

	private float xCenter = 0;
	private float yCenter = 0;
	private float width = 0;
	private float height = 0;

	// =================================================

	void Start() 
	{
		// set up sizes
		xCenter = Screen.width/2.0f;
		yCenter = Screen.height/2.0f;
		width = ButtonSizes.BUTTON_WIDTH * Screen.width;
		height = ButtonSizes.BUTTON_HEIGHT * Screen.height;
	}

	// =================================================

	void OnGUI() 
	{
		GUI.skin = guiSkin;
		GUI.BeginGroup(new Rect(xCenter-width/2.0f, yCenter-height*3.0f, width, height*7.5f));
		// all button positions will now be relative to this rect

		// start button - "hard" mode (really just normal mode)
		if (GUI.Button(new Rect(0, 0, width, height), "Normal Mode")) {
			PlayerPrefs.SetInt(ScoreManager.GAMEMODE_KEY, ScoreManager.HARDMODE);
			Application.LoadLevel("maingame");
		}
		// start button - easy mode
		if (GUI.Button(new Rect(0, height*1.25f, width, height), "Easy Mode")) {
			PlayerPrefs.SetInt(ScoreManager.GAMEMODE_KEY, ScoreManager.EASYMODE);
			Application.LoadLevel("maingame");
		}
		
		// to title button
		if (GUI.Button(new Rect(0, height*2.75f, width, height), "Instructions"))
			Application.LoadLevel("instructions");

		// to options button
		if (GUI.Button(new Rect(0, height*4.00f, width, height), "Options"))
			Application.LoadLevel("options");

		// quit button
		if (GUI.Button(new Rect(0, height*5.25f, width, height), "Quit"))
			Application.Quit();

		GUI.EndGroup();
	}

	// =================================================

//	// confirmation dialog (yes/no popup)
//	void popupYesNoWindow(int windowID)
//	{
//		Debug.Log ("popup dialog");
//		if (GUI.Button(new Rect(xCenter-width/2.0f, yCenter-height*2.0f, width/2.0f, height/2.0f), "Yes")) {
//			PlayerPrefs.SetInt(ScoreManager.EASYHIGHSCORE_KEY, 0);	
//			PlayerPrefs.SetInt(ScoreManager.HARDHIGHSCORE_KEY, 0);		
//		}
//		if (GUI.Button(new Rect(0, height*4.00f, width, height), "Yes"))
//		    return;	// unnecessary line
//	}
}
