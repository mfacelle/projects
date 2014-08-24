using UnityEngine;
using System.Collections;

public class EnvironmentRenderer : MonoBehaviour 
{

	void Start() 
	{
		setVisible(false);
	}

	// ====================================================
	
	void OnTriggerEnter(Collider col)
	{
		if (col.tag == "renderer")
			setVisible(true);
	}

	void OnTriggerExit(Collider col)
	{
		if (col.tag == "renderer")
			setVisible(false);
	}

	// ====================================================

	// set visibility of an environment game object
	// will turn off mesh renderer or light component
	private void setVisible(bool isVisible)
	{
		if (GetComponent<MeshRenderer>())
			GetComponent<MeshRenderer>().enabled = isVisible;
		if (GetComponent<Light>())
			GetComponent<Light>().enabled = isVisible;
	}
}
