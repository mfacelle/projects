using UnityEngine;
using System.Collections;

public class ScoreDisplay : MonoBehaviour 
{
	void Start() 
	{
		GetComponent<GUIText>().text = "" + (int)Player.points;
	}

	void Update()
	{
		GetComponent<GUIText>().text = "" + (int)Player.points;
	}

}
