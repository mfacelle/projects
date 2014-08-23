using UnityEngine;
using System.Collections;

public class DirectionalDisplay : MonoBehaviour 
{
	private const float SCREEN_FRACTION = 0.10f;	// how much of the screen's width it will take up

	public static DirectionalDisplay Instance { get; private set; }	// make globally accessible
	public float Edge { get; private set; }
	public Vector2 Center { get; private set; }

	void Start() 
	{
		// set size of directional gui based on screen size
		Edge = Screen.width * SCREEN_FRACTION;
		Rect inset = new Rect(0, 0, Edge, Edge);	// at bottom-left corner
		GetComponent<GUITexture>().pixelInset = inset;
		Center = new Vector2(Edge/2.0f, Edge/2.0f);	// since at bottom-left, center is just edge/2
		Instance = this;
	}
}
