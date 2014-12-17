using UnityEngine;
using System.Collections;

public class Instructions_GUI : MonoBehaviour 
{
	public GUISkin guiSkin;

	private float width = 0;
	private float height = 0;

	private Animator anim;

	// =================================================

	void Start()
	{
		anim = GameObject.Find("instructions").GetComponentInParent<Animator>();

		width = ButtonSizes.BUTTON_WIDTH/2.0f * Screen.width;
		height = ButtonSizes.BUTTON_HEIGHT*0.75f * Screen.height;
	}

	// =================================================

	void OnGUI()
	{
		// determine current animation state and act accordingly
		int currentAnimState = anim.GetCurrentAnimatorStateInfo(0).nameHash;
		// store data to prevent multiple calls outside this class (though now there AREN'T due to GUI group...)
		float buffer = ButtonSizes.EDGE_BUFFER;
		float screenWidth = Screen.width;
		float screenHeight = Screen.height;

		GUI.skin = guiSkin;
		GUI.BeginGroup(new Rect(buffer, screenHeight-buffer-height, screenWidth-buffer, height));
		// all button positions will now be relative to this rect

		// back button
		if (GUI.Button (new Rect(0, 0, width, height), "Back")) {
			// if at first instruction, return to title screen
			// otherwise, just go to previous instruction page
			if (currentAnimState == Instructions.INST_0)
				Application.LoadLevel("titlescene");
			else
				anim.SetTrigger("back");
		}

		// next button
		string nextButtonText = currentAnimState == Instructions.INST_2 ? "Done" : "Next";
		if (GUI.Button (new Rect(screenWidth-buffer*2.0f-width, 0, width, height), nextButtonText)) {
			// if at last instruction, return to title screen
			// otherwise, just go to next instruction page
			if (currentAnimState == Instructions.INST_2)
				Application.LoadLevel("titlescene");
			else
				anim.SetTrigger("next");
		}

		GUI.EndGroup();
	}
}
