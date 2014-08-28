//
//  calculations.cpp
//  Orbits
//
//  Mike Facelle
//  summer 2014
//
//  functions for doing calculations, such as numerically finding
//  the eccentric anomaly

#include "calculations.h"

const float ERR_TOLERANCE = 1E-6;   // how small E_n+1 - E_n must be to consider E valid
const float MAX_DEPTH = 64;         // how deep into the recursive loop solving for E can go

// -----------------------------------------------------
// MEAN ANOMALY
// returns the mean anomaly, M = nt, at the specific time, t
//
float mean_anomaly(const float &mean_motion, const float &t)
{
    return mean_motion * t;
}

// -----------------------------------------------------
// ECCENTRIC ANOMALY
// using the formula:
//  [12]    err_n+1 = (x_n - ecc*sin(x_n) - M) / (1 - ecc*cos(x_n) - (1/2)*sin(x_n)*err_n) 
//   where: 
//  [8]     err_n = (x_n - ecc*sin(x_n) - M) / (1 - ecc*cos(x_n))
// then plugging [12] into [14] for err_n:
//  [14]    err_n+1 = (x_n - ecc*sin(x_n) - M) / (1 - ecc*cos(x_n) - (1/2)*(ecc*sin(x_n) - (1/3)*ecc*cos(x_n)*err_n)*err_n)
//
float eccentric_anomaly(const float &mean_anomaly, const float &ecc, const float &x_0)
{
    float err_0 = (x_0 - ecc*sinf(x_0) - mean_anomaly) / (1 - ecc*cosf(x_0) - 0.5*sinf(x_0)*((x_0 - ecc*sinf(x_0)-mean_anomaly)/(1 - ecc*cosf(x_0))));
    //cout << "running eccentric_anomaly. M=" << mean_anomaly << ", ecc=" << ecc << ", x_0=" << x_0 << endl;
    //cout << "err_0=" << err_0 << endl;
    return ecc_recursive(mean_anomaly, ecc, x_0, err_0, 0);
}

// -----
// includes a depth parameter to avoid infinite recursive loop
// chances are by 32 tries, it's close enough.  Seemed like it oscillates around values occasionally, probably due to trig functions in it
float ecc_recursive(const float &mean_anomaly, const float &ecc, const float &x_n, const float &err_n, const int &depth)
{  
    // exit if it has gone past MAX_DEPTH: avoid infinite recursive loop
    if (depth >= MAX_DEPTH)
        return x_n;


    // solve for err_n+1 (referred to as delta in another paper. trying that method, delta_n = d_n)
    float f = x_n - ecc*sin(x_n) - mean_anomaly;
    float d_n = f / (1 - ecc*cos(x_n));    // n1
    d_n = f / (1 - ecc*cos(x_n) + 0.5*d_n*ecc*sin(x_n)); // n2
    d_n = f / (1 - ecc*cos(x_n) + (1.0/2.0)*d_n*ecc*sin(x_n) + (1.0/6.0)*d_n*d_n*ecc*cos(x_n));  // n3
    d_n = f / (1 - ecc*cos(x_n) + (1.0/2.0)*d_n*ecc*sin(x_n) + (1.0/6.0)*d_n*d_n*ecc*cos(x_n) * (1.0/24.0)*d_n*d_n*d_n*ecc*sin(x_n));  // n4
    
    // x_n+1:
    float x_n1 = x_n - d_n;
    // if current value for x (which is approx. E) equals previous, then return current value as E.
    if (in_tolerance(x_n, x_n1, ERR_TOLERANCE))
        return x_n1;
    
    //cout << "next x_n=" << x_n1 << "\tnext err_n=" << err_n1 << endl;
    return ecc_recursive(mean_anomaly, ecc, x_n1, d_n, depth+1);
}

// -----------------------------------------------------
// TRUE ANOMALY
// using the formula:
//  theta = arccos((cos(E) - ecc) / (1 - ecc*cos(E))
// with eccentricity ecc, and eccentric anomaly ea.
// since acos() returns an angle on 0 < theta < pi, and this needs 0 < theta < 2*pi,
// must determine when to modify theta.  This happens when E >= pi.
//
float true_anomaly(const float &ecc, const float &ea)
{
    float ta = acosf( (cosf(ea) - ecc) / (1 - ecc*cosf(ea)) );
    //float ta_sin = asinf( (sqrt(1-ecc*ecc)*sin(ea)) / (1-ecc*cos(ea)) );
    return ea < M_PI ? ta : 2*M_PI - ta;
}

// -----------------------------------------------------
// HELIOCENTRIC DISTANCE (radius)
// using the formula:
//  r = c / (1 - ecc*cos(theta)) = a*(1-ecc^2)/(1 - ecc*cos(theta))
// with semimajor axis a, eccentricity ecc, and true anomaly theta
//
float radius(const float &a, const float &ecc, const float &theta)
{
    return a * (1.0f - ecc*ecc) / (1.0f - ecc*cosf(theta));
}



// ===========================================================================
//  static-ish functions
// ===========================================================================

// -----------------------------------------------------
// ORBITAL PERIOD
// returns the orbital period, T^2 = (4pi^2/(Gm))*a^3 =>  T = 2*pi*sqrt(a^3/(Gm))
// for the orbit with speicified host mass, m, and semi-major axis, a
//
float orbital_period(const float &m, const float &a)
{
    return 2*M_PI*sqrt((a*a*a)/(G*m));
}


// -----------------------------------------------------
// MEAN MOTION
// returns the mean motion for the specified orbital period
//
float mean_motion(const float &period)
{
    return 2*M_PI / period;
}

// -----------------------------------------------------
// STARTING VALUE FOR FINDING E
// there are a number of different methods for finding the start point
// to test.  methods found in J.M.A. Danby, T.M. Burkardt, "The Solution of Kepler's Equation, 1" (PDF).
// chose to use M+ecc, because it seemed to give good, quick results.
// using M ended up in an infinite loop just about every time, but any value slighlty
// off from M seemed to work just fine.  Though not much testing was done
//
float determine_x0(const float &mean_anomaly, const float &ecc)
{
    return mean_anomaly < M_PI ? mean_anomaly + ecc/2 : mean_anomaly - ecc/2;
}

// -----------------------------------------------------
// WITHIN TOLERANCE
// returns whether or not: b-tol < a < b+tol
bool in_tolerance(const float &a, const float &b, const float &tol)
{
    return b-tol <= a && a <= b+tol;
}

// may not actually use this:
// -----------------------------------------------------
// LINSPACE
// divides the value, maxval, into numsteps steps of step size maxval/numsteps.
// each step is stored in the float[] array, which is overwritten completely
// essentially MATLAB's linspace() method. 
//
void linspace(float* array, const float &maxval, const int &numsteps)
{
    array = new float[numsteps];
    float stepsize = maxval / numsteps;
    for (int i = 0; i < numsteps; i++)
        array[i] = i*stepsize;
}
