using UnityEngine;
using System.Collections;

public class Credits_GUI : MonoBehaviour 
{

	public GUISkin guiSkin;
	
	private float width = 0;
	private float height = 0;
	private float buttonWidth = 0;
	private float buttonHeight = 0;
	private float totalHeight = 0;

	private float textAreaStart = 0;
	private float yPos = 0;	// keep track of y position for scrolling
	private const float scrollSpeed = 1.25f;
	
	// =================================================
	
	void Start() 
	{
		// set up sizes
		width = Screen.width * 0.9f;	
		height = Screen.height * 0.40f;	// individual text area height
		buttonWidth = ButtonSizes.BUTTON_WIDTH * Screen.width;
		buttonHeight = ButtonSizes.BUTTON_HEIGHT * Screen.height;
		yPos = Screen.height*0.7f;	// start a bit on screen
		totalHeight = height*5.0f;

		textAreaStart = Screen.height*0.2f;
	}

	// =================================================

	void Update()
	{
		yPos -= scrollSpeed;
		if (yPos + totalHeight < textAreaStart)	// if end of credits go offscreen, restart
			yPos += Screen.height*0.7f + totalHeight;
	}
	
	// =================================================
	
	void OnGUI() 
	{
		GUI.skin = guiSkin;

		GUI.BeginGroup(new Rect(Screen.width*0.05f, textAreaStart, width, Screen.height*0.7f));
		GUI.TextArea(new Rect(0, yPos, width, height), "Full game, including graphics, music, and sounds by\n\nMike Facelle");
		GUI.TextArea(new Rect(0, yPos+height, width, height), "Font used:\n\nOrbitron Black, by\nMatt McInerney");
		GUI.TextArea(new Rect(0, yPos+height*2.0f, width, height), "This game was built with the\nUnity Engine");
		GUI.TextArea(new Rect(0, yPos+height*3.0f, width, height*2), "Special Thanks to:\n\n" + 
		             "Michael J. Facelle\nErin Facelle\nKathryn Facelle\nPatrick Kelly\n\n...and the Internet");
		GUI.EndGroup();

		// back button in bottom-left corner (same pos as instructions back button)
		if (GUI.Button(new Rect(ButtonSizes.EDGE_BUFFER, Screen.height-ButtonSizes.EDGE_BUFFER-buttonHeight*0.75f, buttonWidth/2.0f, buttonHeight*0.75f), "Back"))
			Application.LoadLevel("options");
	}
}
