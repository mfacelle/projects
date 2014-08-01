using UnityEngine;
using System.Collections;

// similar to TileRenderer, but for background tiles, since they require a much larger trigger radius
// also doesn't work on any child objects
public class BGRenderer : MonoBehaviour 
{
	void Start () 
	{
		// initially disable rendering and colliders for tile and all children (the walls)
		setEnable(false);
	}
	
	// =============================================
	
	void Update() 
	{
		// get the player's trigger collider for rendering
		Collider2D playerTrigger = GameObject.Find("bg_trigger").GetComponentInChildren<Collider2D>();
		if (playerTrigger.OverlapPoint(new Vector2(transform.position.x, transform.position.y)))
			setEnable(true);
		else 	// disable if not near the player
			setEnable(false);
	}
	
	// =============================================
	
	// enables or disabled all renderers and colliders in this object and its children
	private void setEnable(bool en)
	{
		renderer.enabled = en;
	}
}
