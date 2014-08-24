using UnityEngine;
using System.Collections;

public class HeavyBlaster : Weapon 
{
	void Start() 
	{
		fireRate = 0.75f;
		launchForce = DEFAULT_LAUNCHFORCE;
		damage = 2.0f;
		automatic = false;
		weaponName = WeaponNames.HEAVYBLASTER;
		init();
	}

}
