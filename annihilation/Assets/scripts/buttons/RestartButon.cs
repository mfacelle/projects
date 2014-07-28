using UnityEngine;
using System.Collections;

public class RestartButon : Button 
{
	// reloads the game
	void OnMouseDown()
	{
		onClick();
		Application.LoadLevel("maingame");
	}
}
