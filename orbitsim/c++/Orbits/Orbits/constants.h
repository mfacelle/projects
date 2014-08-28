//
//  constants.h
//  Orbits
//
//  Defines constants, such as real-life planet masses


#ifndef Orbits_constants_h
#define Orbits_constants_h

// relationship between planet radius and mass 
extern const float PLANET_MR_FACTOR;
extern const float PLANET_MR_POWER;

// for calculating star radius, based on mass
extern const float STAR_MR_LO;
extern const float STAR_MR_HI;


// --------------- real-life values ----------------------

// gravitaional constant
extern const float G;

// EARTH orbital parameters
extern const float EARTH_SEMIMAJOR_AXIS;
extern const float EARTH_ECCENTRICITY;
extern const float EARTH_PERIHELION;
extern const float EARTH_APHELION;

// stats (for conversion): - MAY NOT BE NECESSARY (except AU-LY compare)
extern const float AU;                 // 1 AU, in km
extern const float LY;                 // 1 LY, in AU
// sun
extern const float MASS_SOL;           // in kg
extern const float RADIUS_SOL;         // in km
// earth
extern const float MASS_EARTH;         // in kg
extern const float RADIUS_EARTH;       // in km
extern const float ECC_EARTH;          // eccentricity

#endif
