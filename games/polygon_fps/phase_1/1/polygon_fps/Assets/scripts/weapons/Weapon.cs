using UnityEngine;
using System.Collections;

public abstract class Weapon : MonoBehaviour 
{
	public Rigidbody projectile;

	protected const float DEFAULT_FIRERATE = 0.20f;			// in s
	protected const float DEFAULT_LAUNCHFORCE = 5000.0f;	// force applied to launched projectiles
	protected const float DEFAULT_DAMAGE = 1.0f;

	// implemented by subclasses:
	protected float fireRate = DEFAULT_FIRERATE;
	protected float launchForce = DEFAULT_LAUNCHFORCE;
	protected float damage = DEFAULT_DAMAGE;
	protected string weaponName = "Gun";					// for display in-game
	protected int ammo = 0;									// amount of ammo the weapon currently has
	protected bool automatic = false;

	// signals sent by multitouch manager
	public bool SigFire { get; set; }		// sent by touch began
	public bool SigHoldFire { get; set; }	// sent by touch hold

	private float fired = 0;
	private Animator anim;

	// ====================================================

	// called by subclass during initialization
	protected void init()
	{
		SigFire = false;
		SigHoldFire = false;
		fired = fireRate;
		anim = GetComponent<Animator>();
	}

	// -----

	void Update() 
	{
		fired += Time.deltaTime;

		// if in cpu debug mode, use keyboard input
		if (Player.Instance.control == Player.ControlMode.CPU_DEBUG) {
			SigHoldFire = Input.GetButton("Fire");
			SigFire = Input.GetButtonDown("Fire");
		}

		if (fired >= fireRate) {
			if (ammo > 0 && ((automatic && SigHoldFire) || (!automatic && SigFire))) {
				fireProjectile();
				fired = 0;
				anim.SetTrigger("fire");
			}
		}

		// always reset so player must touch again to fire non-automatic weapons
		SigFire = false;

	}

	// ====================================================

	// virtual so subclasses can override if a gun needs to fire a weird projectile or multiple
	protected virtual void fireProjectile()
	{
		ammo--;	// put here so overrides can use different amounts of ammo
		// use transform.parent because weaponHolder object doesn't rotate with gun animation, which affects projectile direction
		Rigidbody projectileInst = Instantiate(projectile, transform.parent.position, transform.parent.rotation) as Rigidbody;
		projectileInst.AddForce(projectileInst.transform.forward * launchForce);
		projectileInst.GetComponent<Projectile>().Damage = damage;
	}

	// ---

	// raycast version.  a bit unrealistic because it's instant
//	private void fire()Box
//	{
//		RaycastHit hit;
//		//Ray fireRay = new Ray(transform.position, transform.forward * SHOT_DISTANCE);	// does NOT go to crosshair
//		Ray fireRay = Camera.main.ViewportPointToRay(new Vector3(0.5f, 0.5f, 0));	// goes to crosshairs
//
//		if (Physics.Raycast(fireRay, out hit, SHOT_DISTANCE)) {
//			if (hit.collider.tag == "enemy") {
//				// destroy enemy
//				Destroy(hit.collider.gameObject);
//			}
//		}
//		//Debug.DrawRay(fireRay.origin, fireRay.direction*SHOT_DISTANCE);
//	}

	// ====================================================

	public void addAmmo(int amount)	{	ammo += amount; }
	public void setAmmo(int amount)	{	ammo = amount; }

	public int getAmmo()	{ return ammo; }
}
