using UnityEngine;
using System.Collections;

public class Annihilation : MonoBehaviour {

	[HideInInspector]
	public int doneState = Animator.StringToHash("base.done");

	private Animator anim;

	// =================================================

	void Start()
	{
		anim = GetComponent<Animator>();
		// if music off, dont play audio
		if (!ScoreManager.instance.isMusicOn())
			audio.playOnAwake = false;
	}

	// =================================================

	void Update () 
	{
		// if the animation has completed, destroy the explosion object
		AnimatorStateInfo currentState = anim.GetCurrentAnimatorStateInfo(0);
		if (currentState.nameHash == doneState)
			Destroy(gameObject);
	}
}
