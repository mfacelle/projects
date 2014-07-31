using UnityEngine;
using System.Collections;

public class HardHighScoreDisplay : MonoBehaviour 
{
	void Start () 
	{
		GetComponent<GUIText>().text = "Normal Mode : " + PlayerPrefs.GetInt(ScoreManager.HARDHIGHSCORE_KEY);
	}
}
