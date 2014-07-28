using UnityEngine;
using System.Collections;

public class MusicOnOffText : MonoBehaviour 
{

	void Update() 
	{
		GetComponent<GUIText>().text = "Music/Sounds : " + (ScoreManager.instance.isMusicOn() ? "ON" : "OFF");
	}
}
