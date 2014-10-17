//
//  conversions.cpp
//  Orbits
//
//  constants and equations for unit conversions


#include "conversions.h"

// to SI units ---------------------------
double DegtoRad(double deg)	// Degrees to Radians
{	return deg * M_PI / 180; }
double AUtoM(double au)	// AU to meters
{ 	return au * AU * 1000; }
double AUtoKM(double au)	// AU to km
{ 	return au * AU; }
double EtoKG(double em)	// :Emass to kg
{	return em * MASS_EARTH; }
double StoKG(double sm)	// :Smass to kg
{	return sm * MASS_SOL; }
double EtoM(double er)	// :Eradius to meters
{	return er * RADIUS_EARTH; }
double StoM(double sr)	// :Sradius to meters
{	return sr * RADIUS_SOL; }

// to relative units ---------------------
double MtoAU(double m)	// meters to AU
{	return m / 1000 / AU; }
double KMtoAU(double km)	// km to AU
{	return km / AU; }
double KGtoE(double kg)	// kg to :Emass
{	return kg / MASS_EARTH; }
double KGtoS(double kg)	// kg to :Smass
{	return kg / MASS_SOL; }
double MtoE(double m)		// meters to :Eradius
{	return m / RADIUS_EARTH; }
double MtoS(double m)		// meters to :Sradius
{	return m / RADIUS_SOL; }

// between relative units ----------------
double LYtoAU(double ly)	// LY to AU
{ 	return ly * LY; }
double AUtoLY(double au)	// AU to LY
{	return au / LY; }
double ERtoAU(double er)	// :Eradius to AU
{	return er * (RADIUS_EARTH / AU); }
double SRtoAU(double sr)	// :Sradius to AU
{	return sr * (RADIUS_SOL / AU); }
double ERtoSR(double er)	// :Eradius to :Sradius
{	return er * (RADIUS_EARTH / RADIUS_SOL); }
double SRtoER(double sr)	// :Sradius to :Eradius
{	return sr * (RADIUS_SOL / 1); }
double EMtoSM(double em)	// :Emass to :Smass
{	return em * (MASS_EARTH / MASS_SOL); }
double SMtoEM(double sm)	// :Smass to :Emass
{	return sm * (MASS_SOL / MASS_EARTH); }