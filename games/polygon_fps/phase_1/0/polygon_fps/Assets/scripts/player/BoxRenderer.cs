using UnityEngine;
using System.Collections;

public class BoxRenderer : MonoBehaviour 
{
	private const float BASE_LENGTH = 80;
	private const float CENTER_FACTOR = 0.33f;	// fraction of the length
	private const float LENGTH_BOOST = 25;	// for when ray hits something (add to length)

	// mask lighting so it doesnt cut the ray short (which is what happens)
	public LayerMask raycastMask;

	// shoot a raycast forward and limit the length of the renderer rectangle if something got hit
	void Update() 
	{
		BoxCollider col = GetComponent<BoxCollider>();
		if (!col)
			return;	// but it will exist

		RaycastHit hit;
		Ray renderRay = Camera.main.ViewportPointToRay(new Vector3(0.5f, 0.5f, 0));	// goes to center
		Physics.Raycast(renderRay, out hit, BASE_LENGTH, raycastMask);

		float dist = hit.distance;
		float length = dist < BASE_LENGTH ? (dist == 0 ? BASE_LENGTH : dist + LENGTH_BOOST) : BASE_LENGTH;
		if (length > BASE_LENGTH)
			length = BASE_LENGTH;
		// if ray hits something, shrink renderer length, else use base length
		col.size = new Vector3(col.size.x, col.size.y, length);
		col.center = new Vector3(col.center.x, col.center.y, col.size.z * CENTER_FACTOR);
	}
}
