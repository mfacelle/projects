using UnityEngine;
using System.Collections;

public class BackButton : Button 
{
	private Animator anim;
	
	void Start() 
	{
		anim = GameObject.Find("instructions").GetComponentInParent<Animator>();
	}
	
	// =================================================
	
	void OnMouseDown()
	{
		onClick();
		int currentAnimState = anim.GetCurrentAnimatorStateInfo(0).nameHash;
		// if at first instruction, return to title screen
		// otherwise, just go to previous instruction page
		if (currentAnimState == Instructions.INST_0)
			Application.LoadLevel("titlescene");
		else
			anim.SetTrigger("back");
	}
}
