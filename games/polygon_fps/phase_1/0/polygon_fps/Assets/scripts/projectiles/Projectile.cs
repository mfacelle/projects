using UnityEngine;
using System.Collections;
using System;

public class Projectile : MonoBehaviour 
{
	private const float DEFAULT_DMG = 1.0f;

	public float Damage	{ get; set; }

	// ====================================================

	void Awake()
	{
		Damage = DEFAULT_DMG;
	}

	// -----

	void OnCollisionEnter(Collision col)
	{
		if (col.collider.tag == "projectile") {
			Physics.IgnoreCollision(col.collider, collider);
			return;	// ignore rest of stuff because particle will be destroyed
		}
		// dont do anything if collision with enemy, Enemy class will handle that
		// if damage() is called here, it will ALSO be called inside Enemy, doubling the effect

		// destroy projectile regardless
		Destroy(gameObject);
	}

	// ====================================================

}
