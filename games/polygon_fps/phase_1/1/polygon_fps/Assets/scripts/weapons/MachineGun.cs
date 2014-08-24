using UnityEngine;
using System.Collections;

public class MachineGun : Weapon
{
	void Start() 
	{
		fireRate = 0.1f;
		launchForce = DEFAULT_LAUNCHFORCE * 1.1f;
		damage = 0.75f;
		automatic = true;
		weaponName = WeaponNames.MACHINEGUN;
		init();
	}
}
