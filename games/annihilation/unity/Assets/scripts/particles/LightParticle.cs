using UnityEngine;
using System.Collections;

public class LightParticle : Particle 
{
	// =================================================
	
	void Start()
	{
		oppositeTag = TAG_DARK;
		points = POINTS_DARKLIGHT;
	}
	
	// =================================================

}
