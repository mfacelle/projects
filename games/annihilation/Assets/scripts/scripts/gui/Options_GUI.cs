using UnityEngine;
using System.Collections;

public class Options_GUI : MonoBehaviour 
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
		GUI.BeginGroup(new Rect(xCenter-width/2.0f, yCenter-height*2.75f, width, height*7.5f));

		// display high scores button
		if (GUI.Button(new Rect(0, 0, width, height), "View\nHigh Scores"))
			Application.LoadLevel("highscores");	// go to view high scores scene (try and make a modal window?)

		// credits button
		if (GUI.Button(new Rect(0, height*1.50f, width, height), "Credits"))
			Application.LoadLevel("credits");	// go to credit scene (try and make a modal window?)

		// reset high scores button
		if (GUI.Button(new Rect(0, height*3.00f, width, height), "Clear\nHigh Scores")) {
			PlayerPrefs.SetInt(ScoreManager.EASYHIGHSCORE_KEY, 0);	
			PlayerPrefs.SetInt(ScoreManager.HARDHIGHSCORE_KEY, 0);
		}

		// music on/off button
		ScoreManager inst = ScoreManager.instance;
		if (GUI.Button(new Rect(0, height*4.50f, width, height), "Toggle\nMusic/Sounds")) {
			inst.setMusicOn(!inst.isMusicOn());
			if (!inst.isMusicOn())
				inst.audio.Stop();	// stop music if audio off
			else
				inst.audio.Play();	// or start playing it if turned back on
		}

		GUI.EndGroup();

		// back button in bottom-left corner (same pos as instructions back button)
		if (GUI.Button(new Rect(ButtonSizes.EDGE_BUFFER, Screen.height-ButtonSizes.EDGE_BUFFER-height*0.75f, width/2.0f, height*0.75f), "Back"))
			Application.LoadLevel("titlescene");
	}
}
