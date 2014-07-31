using UnityEngine;
using System.Collections;

public class NextButton : Button 
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
		// if at last instruction, return to title screen
		// otherwise, just go to next instruction page
		if (currentAnimState == Instructions.INST_2)
			Application.LoadLevel("titlescene");
		else
			anim.SetTrigger("next");
	}
}
