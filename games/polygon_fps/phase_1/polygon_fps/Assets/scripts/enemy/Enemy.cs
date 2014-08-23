using UnityEngine;
using System.Collections;

public class Enemy : MonoBehaviour 
{
	private const float DEFAULT_HP = 2.0f;

	private float hp;

	// ====================================================

	void Start()
	{
		hp = DEFAULT_HP;
	}

	// ====================================================

	// projectiles have been made triggers so they don't send enemies flying like ragdolls
	void OnCollisionEnter(Collision col)
	{
		if (col.collider.tag == "projectile") {
			damage(col.gameObject.GetComponent<Projectile>().Damage);
			Destroy(col.collider.gameObject);
		}
	}

	// ====================================================

	public void kill()
	{
		Destroy(gameObject);
	}

	// ====================================================

	public void damage(float dmg)
	{
		hp -= dmg;
		if (hp <= 0)
			kill();
	}
	public void setHP(float hpp)	{ hp = hpp; }

	public float getHP()	{ return hp; }
}
