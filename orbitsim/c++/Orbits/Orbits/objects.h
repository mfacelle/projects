////
////  Objects.h
////  Orbits
////
////  Objects for a primitive 2D-planet orbit-display
//
//#ifndef Orbits_Objects_h
//#define Orbits_Objects_h
//
//#include "conversions.h"
//#include <vector>
//
//using namespace std;
//
//
////  Uses Newton's Fc = m v^2/r to determine the overall velocity of and Object
//double compute_velocity(double acc, double radius);
////  Returns the gravitational force, at distance r, acting on masses m1 and m2
//double gravitational_force(double m1, double m2, double r);
////  Used when creating a Planet Object; to find the radius based on the mass.
//double planet_radius(double mass);
//// Used when creating a Star Object; to find the radius based on the mass.
//double star_radius(double mass);
//
//
//class Body;
//class Star;
//class Planet;
//class Orbit;
//
//// ----------------------- Body class ------------------------
//class Body
//{
//protected:
//    double mass;				// star's mass
//    double radius;				// star's radius
//    double loc_x;				// x-location (in AU)
//    double loc_y;				// y-location (in AU)
//    double loc_z;               // z-location (in AU)
//public:
//    Body();
//    
//    // GET AND SET METHODS
//    void setMass(double new_mass)	{ mass = new_mass; };		
//    void setRadius(double new_rad)	{ radius = new_rad; };
//    void setX(double new_x)         { loc_x = new_x; }
//    void setY(double new_y)         { loc_y = new_y; }
//    void setZ(double new_z)         { loc_z = new_z; }
//    void setLocation(double x, double y, double z)  { loc_x = x; loc_y = y; loc_z = z; };
//    // ------------------------
//    double getRadius()          {	return radius; }
//    double getMass()            {	return mass; }
//    double getX()               {	return loc_x; }
//    double getY()               {	return loc_y; }
//    double getZ()               {   return loc_z; }
//};
//
//
//// ----------------------- Star class ------------------------
//// Star is assumed stationary, even though in reality they are not
//class Star: public Body
//{
//private:
//    vector<Planet> planets;     // star's orbiting planets
//    
//public:
//    // CONSTRUCTORS
//    Star();
//    Star(double init_m, double init_x, double init_y, double init_z);
//    Star(double init_m, vector<Planet> init_planets, double init_x, double init_y, double init_z);
//    
//    void update(double etime);
//
//    // GET AND SET METHODS
//    void setPlanets(vector<Planet> new_planets)	{	planets = new_planets; }
//    // ------------------------
//    vector<Planet> getPlanets()	{	return planets; }
//};
//
//
//// ----------------------- Planet class ------------------------
//class Planet: public Body
//{
//private:
//    double phi_0;		// X-Z: original angle (from 0) from parent star, in rads
//    double i_0;         // Y:   original angle (from 0) from parent star, in rads
//    double theta;		// X-Z: current angle (from 0) from parent star, in rads
//    double incline;     // Y:   current angle (from 0) from parent star, in rads
//    double r;           // distance from parent star, in AU
//    double accel;		// acceleration towards star, in AU/s^2
//    double v_mag;		// magnitude of velocity, in AU/s
//    Star * parent;		// parent star
//    
//public:
//    // CONSTRUCTOR
//    Planet();
//    Planet(double init_m, double phi, double inc, double init_r, Star * init_p);
//    
//    void update(double etime);
//    
//    // GET AND SET METHODS
//    void setPhi(double new_phi)		{ phi_0 = new_phi; };
//    void setI_0(double new_i0)      { i_0 = new_i0; };
//    void setTheta(double new_theta)	{ theta = new_theta; };
//    void setIncline(double new_i)   { incline = new_i; };
//    void setDistance(double new_r)  { r = new_r; };
//    void setAccel(double new_acc)	{ accel = new_acc; };
//    void setVelocity(double new_vel){ v_mag = new_vel; };
//    void setParent(Star * new_star)	{ parent = new_star; };
//    // --------------------------
//    double getPhi()			{ return phi_0; };
//    double getI_0()         { return i_0; };
//    double getTheta()		{ return theta; };
//    double getInclince()    { return incline; };
//    double getOrbitR()		{ return r; };
//    double getAccel()		{ return accel; };
//    double getVelocity()	{ return v_mag; };
//    double getXVelocity()	{ return v_mag * cos(theta); };
//    double getZVelocity()   { return v_mag * sin(theta); };
//    double getYVelocity()	{ return v_mag * sin(incline); };
//    double getRelativeX()	{ return r * cos(theta); };
//    double getRelativeZ()   { return r * sin(theta); };
//    double getRelativeY()	{ return r * sin(incline); };
//    Star * getParent()		{ return parent; };
//};
//
//
//// PROBABLY WILL NOT USE THIS:
//// ----------------------- Orbit class ------------------------
//// contains information on the orbit of a planet
//class Orbit
//{
//    double semimajor_axis;
//    double semilatus_rect;
//    double ecc;
//    // not totally necessary:
//    double perihelion;
//    double aphelion;
//};
//
//
//#endif
