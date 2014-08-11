using UnityEngine;
using System.Collections;

// contains data such as the player score
// also handles updating the score GUI
public class Player : MonoBehaviour 
{
	public const string SCORE_STRBASE = "score: ";
	public static float points = 0;	// made static so endGame scene can access it

	// =================================================

	void Start() 
	{
		points = 0;
	}

	// =================================================

	// update player points based on elapsed time (1s = 1pt)
	void Update() 
	{
		points += Time.deltaTime;
		//GUIText scoreDisplay = GameObject.Find("scoreDisplay").GetComponent<GUIText>();
		//scoreDisplay.text = SCORE_STRBASE + (int)points;
		// update the spawner, updates spawn time and particles available
		GameObject.Find ("spawner").GetComponent<Spawner>().updateSpawner(points);
	}

	// =================================================

	public void addPoints(float pts)
	{
		points += pts;
	}

	// ------------------------------------------

	public float getPoints()	{ return points; }

}
