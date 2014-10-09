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

// default planet simulation parameters:
const char* SUN_MASS = "1.9891e+30";
const char* DEFAULT_NUMSTEPS = "1000000";

// arrays in the format: { name, semimajor axis, eccentricity, planet mass, host star mass, number of steps }
const string mercury_args[] =    { "mercury",    "5.789e10", "0.206",    "3.302e23", SUN_MASS, DEFAULT_NUMSTEPS };
const string venus_args[] =      { "venus",      "1.082e11", "0.007",    "4.869e24", SUN_MASS, DEFAULT_NUMSTEPS };
const string earth_args[] =      { "earth",      "1.496e11", "0.017",    "5.974e24", SUN_MASS, DEFAULT_NUMSTEPS };
const string mars_args[] =       { "mars",       "2.280e11", "0.093",    "6.419e23", SUN_MASS, DEFAULT_NUMSTEPS };
const string jupiter_args[] =    { "jupiter",    "7.784e11", "0.048",    "1.899e27", SUN_MASS, DEFAULT_NUMSTEPS };
const string saturn_args[] =     { "saturn",     "1.427e12", "0.054",    "5.685e26", SUN_MASS, DEFAULT_NUMSTEPS };
const string uranus_args[] =     { "uranus",     "2.871e12", "0.047",    "8.685e25", SUN_MASS, DEFAULT_NUMSTEPS };
const string neptune_args[] =    { "neptune",    "4.498e12", "0.009",    "1.024e26", SUN_MASS, DEFAULT_NUMSTEPS };
// and pluto isn't a real planet

// planet names (for input-checking)
const string MERCURY = "mercury";
const string VENUS = "venus";
const string EARTH = "earth";
const string MARS = "mars";
const string JUPITER = "jupiter";
const string SATURN = "saturn";
const string URANUS = "uranus";
const string NEPTUNE = "neptune";
const string CUSTOM = "custom";

// -----
// stats (for conversions): - MAY NOT BE NECESSARY (except AU-LY compare)
const float AU = 1.49597890E+11;		// 1 AU, in m
const float LY = 6.32397263E+04;		// 1 LY, in AU

extern const float MASS_SOL = 1.989e30;     // in kg
extern const float RADIUS_SOL = 6.958e8;    // in m
extern const float MASS_EARTH = 5.974e24;   // in kg
extern const float RADIUS_EARTH = 6.371e6;  // in m