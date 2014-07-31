using UnityEngine;
using System.Collections;

public class HighScoreDisplay : MonoBehaviour 
{
	void Start() 
	{
		switch(PlayerPrefs.GetInt (ScoreManager.GAMEMODE_KEY)) {
		case ScoreManager.EASYMODE:
			GetComponent<GUIText>().text = "" + PlayerPrefs.GetInt(ScoreManager.EASYHIGHSCORE_KEY);
			break;
		default:
		case ScoreManager.HARDMODE:
			GetComponent<GUIText>().text = "" + PlayerPrefs.GetInt(ScoreManager.HARDHIGHSCORE_KEY);
			break;
		}
	}
}
