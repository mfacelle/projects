using UnityEngine;
using System.Collections;

public class ToTitleButton : Button 
{
	// return to title screen
	void OnMouseDown()
	{
		onClick();
		Application.LoadLevel("titlescene");
	}

}
