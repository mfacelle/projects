using UnityEngine;
using System.Collections;

public class EasyHighScoreDisplay : MonoBehaviour 
{
	void Start ()
	{
		GetComponent<GUIText>().text = "Easy Mode : " + PlayerPrefs.GetInt(ScoreManager.EASYHIGHSCORE_KEY);

	}
}
