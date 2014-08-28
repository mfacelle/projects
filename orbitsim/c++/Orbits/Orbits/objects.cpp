////
////  objects.cpp
////  Orbits
////
////  Objects for a primitive 2D-planet orbit-display
//
//#include "objects.h"
//
//// ------------------- FUNCTIONS ---------------------
////  Uses Newton's Fc = m v^2/r to determine the overall velocity of and object
////  Requires A and R, since only acceleration is really necessary in this class, 
////  uses v^2 = a*r instead. (circular motion)
//double compute_velocity(double accel, double radius)
//{
//    return sqrt(accel * radius);
//}
//
////  Returns the gravitational force, at distance r, acting on masses m1 and m2
////  Parameters m1, m2, and r must be in SI units (kg, km), or an incorrect result will
////  be returned.  This value can then be converted back to AU, :E, :S
//double gravitational_force(double m1, double m2, double r)
//{
//    return (G * m1 * m2) / (r * r);
//}
//
////  Used when creating a Planet object; to find the radius based on the mass.
////  Relationship found via a yahoo answers post by "Dr. Bob".  Derived from a
////  log-log plot of the radius v mass of the 8 planets in our solar system.
//double planet_radius(double mass)
//{
//    return	PLANET_MR_FACTOR * pow(mass, PLANET_MR_POWER);
//}
//
////  Used when creating a Star object; to find the radius based on the mass.<br>
////  Mass is randomly generated, and then radius is calculated based off that.<br>
////  Relationship found via a yahoo answers post by "Dr. Bob".  No author of the
////  paper he posted was listed, but based on the URL, it is from Penn State.
//double star_radius(double mass)
//{
//    return	mass >= 1 ? pow(mass, STAR_MR_HI) : pow(mass, STAR_MR_LO);
//}
//
//
//
//// ------------------- CONSTRUCTORS ---------------------
//
//// ---- star ----
//Star::Star()
//{
//    mass = 0;
//    radius = 0;
//    //planets = NULL;   // null by default
//    loc_x = 0;
//    loc_y = 0;
//    loc_z = 0;
//}
//Star::Star(double init_m, double init_x, double init_y, double init_z)
//{
//    mass = init_m;
//    radius = star_radius(mass);
//    //planets = NULL;   // null by default
//    loc_x = init_x;
//    loc_y = init_y;
//    loc_z = init_z;
//}
//Star::Star(double init_m, vector<Planet> init_planets, double init_x, double init_y, double init_z)
//{
//    mass = init_m;
//    radius = star_radius(mass);
//    planets = init_planets;
//    loc_x = init_x;
//    loc_y = init_y;
//    loc_z = init_z;
//}
//
//// ---- planet ----
//Planet::Planet()
//{
//    mass = 0;
//    radius = 0;
//    phi_0 = 0;
//    i_0 = 0;
//    theta = 0;
//    incline = 0;
//    r = 0;
//    parent = NULL;
//    // calculated values:
//    // must convert to SI units for calculation (due to G), then back to relative units
//    accel = 0;
//    v_mag = 0;
//}
//Planet::Planet(double init_m, double phi, double inc, double init_r, Star * init_p)
//{
//    mass = init_m;
//    radius = planet_radius(mass);
//    phi_0 = phi;
//    i_0 = inc;
//    theta = phi_0;
//    incline = i_0;
//    r = init_r;
//    parent = init_p;
//    // calculated values:
//    // must convert to SI units for calculation (due to G), then back to relative units
//    accel = gravitational_force(EtoKG(mass), 
//                                StoKG(parent->getMass()), 
//                                EtoM(r))	/	EtoKG(mass);
//    accel = MtoAU(accel);
//    v_mag = compute_velocity(accel, radius);
//}
//
//
//// ------------------- PLANET METHODS ---------------------
//void Planet::update(double etime)
//{
//    // get component-wise acceleration
//    double x_accel = accel * cos(theta);
//    double z_accel = accel * sin(theta);
//    double y_accel = accel * sin(incline);
//    // update velocity components
//    double xvel = getXVelocity() + x_accel*etime;
//    double zvel = getZVelocity() + z_accel*etime;
//    double yvel = getYVelocity() + y_accel*etime;
//    // update location components
//    loc_x += xvel * etime;
//    loc_z += zvel * etime;
//    loc_y += yvel * etime;
//    
//    // update radius and acceleration based on new position
//    r = sqrt(loc_x*loc_x + loc_y*loc_y + loc_z*loc_z);
//    accel = gravitational_force(EtoKG(mass), 
//                                StoKG(parent->getMass()), 
//                                EtoM(r))	/	EtoKG(mass);
//    accel = MtoAU(accel);
//    v_mag = compute_velocity(accel, r);
//}
//
//
//// ------------------- STAR METHODS ---------------------
//void Star::update(double etime)
//{
//    
//}


	