//
//  constants.cpp
//  Orbits
//
//  Defines constants, such as real-life planet masses


#include "constants.h"

// relationship between radius and mass 
// derived from log-log plot of the 8 planets in our system (yahoo answers: "Dr.Bob")
// R = 1.196 M^0.412
// R,M in terms of earth radii/mass
const float PLANET_MR_FACTOR = 1.196;
const float PLANET_MR_POWER = 0.412;

// for calculating radius, based on mass:
// R = M^0.57 for M>1	and		R = M^0.8 for M<1
// (yahoo answers: Dr.Bob's post. No author of the paper listed, but it is from Penn State)
const float STAR_MR_LO = 0.57;
const float STAR_MR_HI = 0.8;


// --------------- real-life values ----------------------

// gravitational constant
const float G = 6.67E-11; // in m^3 kg^-1 s^-2

// EARTH orbital parameters
const float EARTH_SEMIMAJOR_AXIS = 149.60E+9;
const float EARTH_ECCENTRICITY = 0.0167;
const float EARTH_PERIHELION = 1.47098E+08;
const float EARTH_APHELION = 1.52098E+08;

// stats (for conversion): - MAY NOT BE NECESSARY (except AU-LY compare)
const float AU = 1.49597890E+08;		// 1 AU, in km
const float LY = 6.32397263E+04;		// 1 LY, in AU
// sun
const float MASS_SOL = 1.98892E+30;  	// in kg
const float RADIUS_SOL = 6.960E+05;  	// in km
// earth
const float MASS_EARTH = 5.9742E+24;	// in kg
const float RADIUS_EARTH = 6.3781E+03;	// in km