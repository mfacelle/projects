using UnityEngine;
using System.Collections;

// just contains references to hashed animation state names
// for the back and next button used during instructions
public class Instructions : MonoBehaviour 
{
	public static int INST_0 = Animator.StringToHash("base.instructions_0");
	public static int INST_1 = Animator.StringToHash("base.instructions_1");
	public static int INST_2 = Animator.StringToHash("base.instructions_2");
}
