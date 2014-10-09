//
//  constants.h
//  Orbits
//
//  Defines constants, such as real-life planet masses


#ifndef Orbits_constants_h
#define Orbits_constants_h

#import <string>
using std::string;

// relationship between planet radius and mass 
extern const float PLANET_MR_FACTOR;
extern const float PLANET_MR_POWER;

// for calculating star radius, based on mass
extern const float STAR_MR_LO;
extern const float STAR_MR_HI;


// --------------- real-life values ----------------------

// gravitaional constant
extern const float G;

// default planet simulation parameters:
extern const char* SUN_MASS;
extern const char* DEFAULT_NUMSTEPS;

// arrays in the format: { name, semimajor axis, eccentricity, host star mass, number of steps }
#define NUM_ARGS    6
extern const string mercury_args[NUM_ARGS];
extern const string venus_args[NUM_ARGS];
extern const string earth_args[NUM_ARGS];
extern const string mars_args[NUM_ARGS];
extern const string jupiter_args[NUM_ARGS];
extern const string saturn_args[NUM_ARGS];
extern const string uranus_args[NUM_ARGS];
extern const string neptune_args[NUM_ARGS];
// and pluto isn't a real planet

// planet names (for input-checking)
extern const string MERCURY;
extern const string VENUS;
extern const string EARTH;
extern const string MARS;
extern const string JUPITER;
extern const string SATURN;
extern const string URANUS;
extern const string NEPTUNE;
extern const string CUSTOM;

// stats (for conversion): - MAY NOT BE NECESSARY (except AU-LY compare)
extern const float AU;                  // 1 AU, in km
extern const float LY;                  // 1 LY, in AU
extern const float MASS_SOL;            // in kg
extern const float RADIUS_SOL;          // in m
extern const float MASS_EARTH;          // in kg
extern const float RADIUS_EARTH;        // in m
#endif
