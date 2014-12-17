using UnityEngine;
using System.Collections;

public class Button : MonoBehaviour 
{
	public Sprite buttonPressed;
	public Sprite button;

	// =================================================

	// for testing before a subclass script is added:
	void OnMouseDown()
	{
		onClick();
	}

	// ---

	// separate method so subclasses can call this
	protected void onClick() 
	{
		// change sprite to "pressed button"
		GetComponent<SpriteRenderer>().sprite = buttonPressed;
	}

	// ------------------------------------------

	void OnMouseUp()
	{
		onRelease();
	}

	// ---

	// separate so subclasses can
	protected void onRelease()
	{
		GetComponent<SpriteRenderer>().sprite = button;
	}
}
