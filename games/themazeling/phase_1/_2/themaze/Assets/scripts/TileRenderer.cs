using UnityEngine;
using System.Collections;

public class TileRenderer : MonoBehaviour 
{
	private bool enable = false;

	void Start () 
	{
		// initially disable rendering and colliders for tile and all children (the walls)
		enable = true;	// prevent initially redundant call
		setEnable(false);
	}

	// =============================================

	void Update() 
	{
		// get the player's trigger collider for rendering
		BoxCollider2D playerTrigger = GameObject.Find("player").GetComponent<BoxCollider2D>();
		if (playerTrigger.OverlapPoint(new Vector2(transform.position.x, transform.position.y)))
			setEnable(true);
		else 	// disable if not near the player
			setEnable(false);
	}

	// =============================================

	// enables or disabled all renderers and colliders in this object and its children
	private void setEnable(bool en)
	{
		if (en == enable)
			return;		// avoid redundant calls to save some memory and time

		renderer.enabled = en;	// this component will definitely exist for tile objects
		Renderer[] renderers = GetComponentsInChildren<Renderer>();
		Collider2D[] colliders = GetComponentsInChildren<Collider2D>();
		foreach (Renderer r in renderers)
			r.enabled = en;
		foreach (Collider2D col in colliders)
			col.enabled = en;

		enable = en; 	// set class variable to prevent redundant calls later
	}
}
