using UnityEngine;
using System.Collections;

public class EndGame_GUI : MonoBehaviour 
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
		GUI.BeginGroup(new Rect(xCenter-width/2.0f, yCenter-height/2.0f, width, height*5.0f));
		// all button positions will now be relative to this rect

		// restart button
		if (GUI.Button(new Rect(0, 0, width, height), "Restart"))
			Application.LoadLevel("maingame");

		// to title button
		if (GUI.Button(new Rect(0, height*1.25f, width, height), "To Title"))
			Application.LoadLevel("titlescene");

		// quit button
		if (GUI.Button(new Rect(0, height*2.50f, width, height), "Quit"))
			Application.Quit();

		GUI.EndGroup();
	}
}
