using UnityEngine;
using System.Collections;

public class QuitButton : Button 
{
	// quits the application
	void OnMouseDown()
	{
		onClick();
		Application.Quit();
	}

	

}
