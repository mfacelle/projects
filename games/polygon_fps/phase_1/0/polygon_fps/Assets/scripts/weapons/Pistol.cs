using UnityEngine;
using System.Collections;

public class Pistol : Weapon 
{	
	void Start() 
	{
		fireRate = 0.2f;
		launchForce = DEFAULT_LAUNCHFORCE;
		damage = 1.0f;
		automatic = false;
		weaponName = WeaponNames.PISTOL;
		init();
	}
}
