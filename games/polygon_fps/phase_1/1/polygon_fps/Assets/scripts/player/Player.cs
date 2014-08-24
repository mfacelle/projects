using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Linq;	// for Dictionary.ElementAt()

public class Player : MonoBehaviour 
{
	// used by PlayerLook and PlayerMovement
	public enum ControlMode { CPU_DEBUG = 0, DEVICE = 1 };

	public ControlMode control = ControlMode.CPU_DEBUG;	// debug by default

	public Weapon[] allWeapons;		// filled in inspector

	[HideInInspector]
	public static Player Instance { get; private set; }	// make globally accessible
	[HideInInspector]
	public Weapon CurrentWeapon { get; private set; }	// for MultiToughManager access
	[HideInInspector]
	public PlayerMovement Movement { get; private set; }	// for MultiTouchManager access

	private Dictionary<string, Weapon> weapons;	// map of weaponname, weapon

	private int weaponIndex = 0;
	private Transform weaponHolder;		// transform to place weapons at for viewing, etc

	// ====================================================

	void Start()
	{
		Instance = this;
		Movement = GetComponent<PlayerMovement>();

		weaponHolder = GameObject.FindWithTag("weaponHolder").transform;
		// create weapon dictionary, and give the player the first weapon (must Instantiate, not just use the prefab)
		weapons = new Dictionary<string, Weapon>();
		weapons.Add(WeaponNames.PISTOL, Instantiate(allWeapons[0], weaponHolder.position, Quaternion.identity) as Weapon);
		CurrentWeapon = weapons.ElementAt(weaponIndex).Value;
		CurrentWeapon.transform.parent = weaponHolder;	// child of main camera, at position to display weapon

// -=-=-=-=- DEBUG -=-=-=-=-
		giveAllWeapons();
// -=-=-=-=- DEBUG -=-=-=-=-

		CurrentWeapon.setAmmo(24);	// start off with some ammo
	}

	// ====================================================

	void Update()
	{
		if (Input.GetButtonDown("nextWeapon")) {
			nextWeapon();
		}
		else if (Input.GetButtonDown("previousWeapon")) {
			previousWeapon();
		}
	}

	// ====================================================
	
	void OnTriggerEnter(Collider col)
	{
		if (col.tag == "weaponSpawn")
			;	// acquire weapon if not already acquired, then add ammo for that weapon
	}

	// ====================================================

	// for when "next weapon" button is pressed
	public void nextWeapon()
	{
		weaponIndex++;	// can't do the ++ inside the if statement because for some reason it makes it always false
		if (weaponIndex >= weapons.Count)
			weaponIndex = 0;
		changeCurrentWeapon();
	}

	// ---

	// for when "previous weapon" button is pressed
	public void previousWeapon()
	{
		weaponIndex--;	// can't do the -- inside the if statement because for some reason it makes it always false
		if (weaponIndex < 0)
			weaponIndex = weapons.Count-1;
		changeCurrentWeapon();
	}

	// ---

	// changes current weapon to weaponIndex
	private void changeCurrentWeapon()
	{
		// store weapon, disable, and get next one, and enable
		CurrentWeapon.gameObject.SetActive(false);					// disable current weapon
		CurrentWeapon = weapons.ElementAt(weaponIndex).Value;		// set next weapon to active
		CurrentWeapon.transform.parent = weaponHolder;				// make sure parent is weaponHolder
		CurrentWeapon.transform.position = weaponHolder.position;	// update position to current
		CurrentWeapon.transform.rotation = weaponHolder.rotation;	// update rotation to current
		CurrentWeapon.gameObject.SetActive(true);					// enable current weapon
	}

	// ====================================================

	public int currentAmmo()
	{
		return CurrentWeapon.getAmmo();
	}

	// ====================================================
	// DEBUG STUFF

	// gives the player all weapons with some ammo
	void giveAllWeapons()
	{
		// disable all, then enable the current (don't assume pistol is current)
		if (!weapons.ContainsKey(WeaponNames.PISTOL)) {
			weapons.Add(WeaponNames.PISTOL, Instantiate(allWeapons[0], weaponHolder.position, Quaternion.identity) as Weapon);
			weapons[WeaponNames.HEAVYBLASTER].gameObject.SetActive(false);
			weapons[WeaponNames.HEAVYBLASTER].addAmmo(24);
		}
		if (!weapons.ContainsKey(WeaponNames.HEAVYBLASTER)) {
		    weapons.Add(WeaponNames.HEAVYBLASTER, Instantiate(allWeapons[1], weaponHolder.position, Quaternion.identity) as Weapon);
			weapons[WeaponNames.HEAVYBLASTER].gameObject.SetActive(false);
			weapons[WeaponNames.HEAVYBLASTER].addAmmo(24);
		}
		if (!weapons.ContainsKey(WeaponNames.MACHINEGUN)) {
			weapons.Add(WeaponNames.MACHINEGUN, Instantiate(allWeapons[2], weaponHolder.position, Quaternion.identity) as Weapon);
			weapons[WeaponNames.MACHINEGUN].gameObject.SetActive(false);
			weapons[WeaponNames.MACHINEGUN].addAmmo(24);
		}
		if (!weapons.ContainsKey(WeaponNames.WIDEBLASTER)) {
			weapons.Add(WeaponNames.WIDEBLASTER, Instantiate(allWeapons[3], weaponHolder.position, Quaternion.identity) as Weapon);
			weapons[WeaponNames.WIDEBLASTER].gameObject.SetActive(false);
			weapons[WeaponNames.WIDEBLASTER].addAmmo(24);
		}
		// enable current weapon
		changeCurrentWeapon();
	}
}
