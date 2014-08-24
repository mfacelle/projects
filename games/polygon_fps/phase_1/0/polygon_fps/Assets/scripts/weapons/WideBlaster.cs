using UnityEngine;
using System.Collections;

public class WideBlaster : Weapon 
{
	private const float SHOT_ANGLE = 5.0f;

	void Start() 
	{
		fireRate = 0.6f;
		launchForce = DEFAULT_LAUNCHFORCE * 0.9f;
		damage = 1.5f;
		automatic = false;
		weaponName = WeaponNames.WIDEBLASTER;
		init();
	}

	// ====================================================

	// fire 3 projecitles in a wide pattern (center and +/- 15 degrees)
	protected override void fireProjectile()
	{
		ammo -= 3;	// use 3 ammo, since 3 projectiles are fired

		// seems shitty, but apparently need to ignore collisions BEFORE they happen,
		// 	or the first collision WILL affect physics THEN be ignored afterwards

		Rigidbody projectileInst_0 = Instantiate(projectile, transform.parent.position, transform.parent.rotation) as Rigidbody;
		projectileInst_0.AddForce(projectileInst_0.transform.forward * launchForce);
		projectileInst_0.GetComponent<Projectile>().Damage = damage;

		float rotation_y = transform.eulerAngles.y + SHOT_ANGLE;
		Rigidbody projectileInst_1 = Instantiate(projectile, transform.parent.position, Quaternion.Euler(new Vector3(0, rotation_y, 0))) as Rigidbody;
		projectileInst_1.AddForce(projectileInst_1.transform.forward * launchForce);
		projectileInst_1.GetComponent<Projectile>().Damage = damage;

		rotation_y = transform.eulerAngles.y - SHOT_ANGLE;
		Rigidbody projectileInst_2 = Instantiate(projectile, transform.parent.position, Quaternion.Euler(new Vector3(0, rotation_y, 0))) as Rigidbody;
		projectileInst_2.AddForce(projectileInst_2.transform.forward * launchForce);
		projectileInst_2.GetComponent<Projectile>().Damage = damage;
	}
}
