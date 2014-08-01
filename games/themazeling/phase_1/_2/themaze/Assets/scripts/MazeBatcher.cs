using UnityEngine;
using System.Collections;

public class MazeBatcher : MonoBehaviour 
{
	void Start () 
	{
		// this requires PRO license... fucking shit.
		//StaticBatchingUtility.Combine(gameObject);

		// via: http://forum.unity3d.com/threads/optimization-using-the-new-2d-tools.211021/
		// (except he used gameObject.active = false, not SetActive()
		//gameObject.SetActive(false);
		//gameObject.SetActive(true);
	}
}
