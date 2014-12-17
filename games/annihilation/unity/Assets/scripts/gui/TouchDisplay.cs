using UnityEngine;
using System.Collections;

public class TouchDisplay : MonoBehaviour 
{
	// Update is called once per frame
	void Update() 
	{
		GetComponent<GUIText>().text = "" + Input.touches.Length;
	}
}
