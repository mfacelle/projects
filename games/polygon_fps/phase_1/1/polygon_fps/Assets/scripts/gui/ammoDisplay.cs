using UnityEngine;
using System.Collections;

public class ammoDisplay : MonoBehaviour 
{
	private Player player;

	void Start()
	{
		player = GameObject.FindWithTag("player").GetComponent<Player>();
	}

	void Update() 
	{
		GetComponent<GUIText>().text = "" + player.currentAmmo();
	}
}
