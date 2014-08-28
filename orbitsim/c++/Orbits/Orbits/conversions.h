//
//  conversions.h
//  Orbits
//
//  constants and equations for unit conversions

#ifndef Orbits_conversions_h
#define Orbits_conversions_h

#include <math.h>
#include "constants.h"

// to SI units ---------------------------
double DegtoRad(double deg);	// Degrees to Radians
double AUtoM(double au);	// AU to meters
double AUtoKM(double au);	// AU to km
double EtoKG(double em);	// :Emass to kg
double StoKG(double sm);	// :Smass to kg
double EtoM(double er);	// :Eradius to meters
double StoM(double sr);	// :Sradius to meters
// to relative units ---------------------
double MtoAU(double m);	// meters to AU
double KMtoAU(double km);	// km to AU
double KGtoE(double kg);	// kg to :Emass
double KGtoS(double kg);	// kg to :Smass
double MtoE(double m);		// meters to :Eradius
double MtoS(double m);		// meters to :Sradius
// between relative units ----------------
double LYtoAU(double ly);	// LY to AU
double AUtoLY(double au);	// AU to LY
double ERtoAU(double er);	// :Eradius to AU
double SRtoAU(double sr);	// :Sradius to AU
double ERtoSR(double er);	// :Eradius to :Sradius
double SRtoER(double sr);	// :Sradius to :Eradius
double EMtoSM(double em);	// :Emass to :Smass
double SMtoEM(double sm);	// :Smass to :Emass

#endif
