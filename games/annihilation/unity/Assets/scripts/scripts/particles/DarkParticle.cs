using UnityEngine;
using System.Collections;

public class DarkParticle : Particle 
{
	// =================================================
	
	void Start()
	{
		oppositeTag = TAG_LIGHT;
		points = POINTS_DARKLIGHT;
	}
	
	// =================================================

}
