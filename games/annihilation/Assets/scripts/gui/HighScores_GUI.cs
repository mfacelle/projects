using UnityEngine;
using System.Collections;

public class HighScores_GUI : MonoBehaviour 
{
	public GUISkin guiSkin;
	
	private float width = 0;
	private float height = 0;
	
	// =================================================
	
	void Start() 
	{
		// set up sizes
		width = ButtonSizes.BUTTON_WIDTH * Screen.width;
		height = ButtonSizes.BUTTON_HEIGHT * Screen.height;
	}
	
	// =================================================

	void OnGUI() 
	{
		GUI.skin = guiSkin;

		// back button in bottom-left corner (same pos as instructions back button)
		if (GUI.Button(new Rect(ButtonSizes.EDGE_BUFFER, Screen.height-ButtonSizes.EDGE_BUFFER-height*0.75f, width/2.0f, height*0.75f), "Back"))
			Application.LoadLevel("options");
	}
}
