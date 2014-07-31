using UnityEngine;
using System.Collections;

public class StartButton : Button
{

	// =================================================

	void OnMouseDown()
	{
		onClick();	// call parent method for clicking "animation" (not really an anim)
		// load the actual game
		Application.LoadLevel("maingame");
	}
}
