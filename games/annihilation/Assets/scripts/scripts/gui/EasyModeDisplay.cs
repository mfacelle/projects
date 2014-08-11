using UnityEngine;
using System.Collections;

public class EasyModeDisplay : MonoBehaviour 
{

	void Start () 
	{
		if (PlayerPrefs.GetInt(ScoreManager.GAMEMODE_KEY) == ScoreManager.EASYMODE)
			GetComponent<GUIText>().text = "(Easy mode)";
	}
}
