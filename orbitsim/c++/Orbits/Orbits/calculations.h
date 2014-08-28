//
//  calculations.h
//  Orbits
//
//  Mike Facelle
//  summer 2014
//
//  functions for doing calculations, such as numerically finding
//  the eccentric anomaly

// need to solve: M = E - ecc*sinE, where ecc is the eccentricity (usually epsilon)
// method from:
// http://murison.alpheratz.net/dynamics/twobody/KeplerIterations_summary.pdf
// Marc A. Murison, "A Practical Method for Solving the Kepler Equation", U.S. Naval Observatory, Washington D.C., web. 5/25/14
//
// method for finding initial value when iteratively solving for E from:
//  also used his method for iteratively solving for E
// J.M.A. Danby, T.M. Burkardt, "The Solution of Kepler's Equation, 1", web. 5/25/14. don't have host url anymore
// 


// using const & paremeters because they should be (at least slightly) faster; desirable since this deals with large amounts of data

#ifndef Orbits_calculations_h
#define Orbits_calculations_h

#include "constants.h"
#include <cmath>

float mean_anomaly(const float &mean_motion, const float &t);
float eccentric_anomaly(const float &mean_anomaly, const float &ecc, const float &x_0);
float ecc_recursive(const float &mean_anomaly, const float &ecc, const float &x_n, const float &err_n, const int &depth);
float true_anomaly(const float &ecc, const float &ea);
float radius(const float &a, const float &ecc, const float &theta);

float orbital_period(const float &m, const float &a);
float mean_motion(const float &period);
float determine_x0(const float &mean_anomaly, const float &ecc);
bool in_tolerance(const float &a, const float &b, const float &tol);
void linspace(float* array, const float &maxval, const int &numsteps);

#endif
