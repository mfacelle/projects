using UnityEngine;
using System.Collections;

public class InstructionButton : Button 
{
	void OnMouseDown()
	{
		onClick();	// call parent method for clicking "animation" (not really an anim)
		// load the actual game
		Application.LoadLevel("instructions");
	}
}
