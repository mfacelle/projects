//
//  main.cpp
//  Orbits
//
//  Mike Facelle
//  summer 2014
//
//  Main program to simulate planetary orbits
//

// NOTE: code is probably very inefficient and memory-sucking.
//          may want to avoid making vectors that hold tons of data.
//          instead: just write directly to the file, keeping
//          the handful of variables needed for each loop (and rewriting their data)



#include "calculations.h"
#include "fileio.h"
#include <iostream>
#include <vector>
#include <string>
#include <sstream>
using std::cout;
using std::cin;
using std::endl;
using std::vector;
using std::string;
using std::stringstream;
using std::ios;


// --------------------------------------------
// objects

// default simulation values (passed as const char*, like argv)
const char* DEFAULT_A = "149.6e+9";
const char* DEFAULT_ECC = "0.0167";
const char* DEFAULT_M = "1.9891e+30";
const char* DEFAULT_NUMSTEPS = "100000";
//const char* default_args[] = {" ", "", "fuck you"};
const char* default_args[] = { "", DEFAULT_A, DEFAULT_ECC, DEFAULT_M, DEFAULT_NUMSTEPS };

// path to store data in
const char* FILEPATH = "orbitsim.dat";

float a;
float ecc;
float m;
int numsteps;
float period;
float n;
float dt;

void init(const char**);
void run(vector<float>* tvec, vector<float>* rvec, vector<float>* thvec);
void output_results(vector<float>* tvec, vector<float>* rvec, vector<float>* thvec);
void myclose(int);

// ======================================================================

// --------------------------------------------
// MAIN
int main (int argc, const char** argv)
{   
    bool defaultsim = false;
    
    if (argc < 4) {
    	cout << "Not enough input arguments entered.  The following must be specified, in order:" << endl
        << "semi-major axis" << endl << "eccentricity" << endl << "host-star mass" << endl << "orbital period" << endl;
    	cout << "Run default simulation? (y/n)" << endl;
    	char c; 
    	cin >> c;
    	if (c == 'y')
    		defaultsim = true;
    }
    cout << "Running orbit simulation " << argv[0] << endl << endl;
    
    // create vectors to hold all data and output it later
    // ...should probably just output right to file and not eat up memory with these huge arrays
    vector<float>* time_vec = new vector<float>(numsteps);
    vector<float>* radius_vec = new vector<float>(numsteps);
    vector<float>* theta_vec = new vector<float>(numsteps);
    
    // if default sim, use default values.  NOTE: argv[0] is path, so make first slot of default array empty
    init(!defaultsim ?  argv : default_args);
    run(time_vec, radius_vec, theta_vec);
    output_results(time_vec, radius_vec, theta_vec);
        
    return 0;
}

// ======================================================================

// --------------------------------------------
// INITIALIZE
// grab command-line arguments and convert to float values for simulation
//
void init(const char** args)
{
    // semimajor axis a, eccentricity ecc, host mass m, number of time divisions numsteps, are the input arguments
    a = atof(args[1]);
    ecc = atof(args[2]);
    m = atof(args[3]);
    numsteps = atoi(args[4]);
    
    // period
    period = orbital_period(m, a);
    // mean motion
    n = mean_motion(period);
    // step size (for the t in M=nt)
    dt = period / numsteps;
    
// debug -=-=-=-=-
    cout << "semi-major axis (in m) = " << a << endl 
    << "eccentricity = " << ecc << endl
    << "host star mass (in kg) = " << m << endl 
    << "number of simulation steps = " << numsteps << endl
    << "period (in s) = " << period << endl 
    << "mean motion = " << n << endl 
    << "dt (in s) = " << dt << endl;
// debug -=-=-=-=-
}

// --------------------------------------------
// RUN
//
void run(vector<float>* tvec, vector<float>* rvec, vector<float>* thvec)
{
    // perform "simulation"
    // simulation variables to be used:
    float t = 0, ma = 0, x_0 = 0, ea = 0, ta = 0, r = 0;
    
// debug -=-=-=-=-
    cout << "Beginning simulation" << endl;
    // record time
    clock_t start_time = clock();
// debug -=-=-=-=-
    
    for (int i = 0; i < numsteps; i++) {
        t = i*dt; // get current time
        ma = mean_anomaly(n, t);
        x_0 = determine_x0(ma, ecc);
        ea = eccentric_anomaly(ma, ecc, x_0);
        ta = true_anomaly(ecc, ea);
        r = radius(a, ecc, ta);
        tvec->push_back(t);
        thvec->push_back(ta);
        rvec->push_back(r);
// debug -=-=-=-=-
        if ((float)i/numsteps == 0.25f)
            cout << "25%" << endl;
        if ((float)i/numsteps == 0.5f)
            cout << "50%" << endl;
        if ((float)i/numsteps == 0.75f)
            cout << "75%" << endl;
        //cout << i << "\nt:" << t << "\tr:" << r << "\ttheta:" << ta << endl;
        //cout << "\tma:" << ma << "\tx_0:" << x_0 << "\tea:" << ea << "\tta:" << ta << endl << endl;
    }
    cout << "simulation done." << endl;
    clock_t end_time = clock();
    float delta_t = (float)(end_time - start_time) / CLOCKS_PER_SEC;
    cout << "elapsed time: " << delta_t << endl;
// debug -=-=-=-=-

    
}

// --------------------------------------------
// OUTPUT TO FILE
// first line is the parameters:     a ecc m period
// the rest of the lines are data:   time radius theta
//
void output_results(vector<float>* tvec, vector<float>* rvec, vector<float>* thvec)
{    
    cout << "Outputting data to file" << endl;
    
    ofstream fileout;
// NOTE: fix this to make sure file ends up in the right place
    fileout.open(FILEPATH, ios::out | ios::trunc);
    
    if (!fileout.is_open()) {
        fprintf(stderr, "unable to open file %s\nExiting.", FILEPATH);
        fileout.close();
        myclose(1);
    }
        
    //char* temp;
    stringstream temp;
    // write first line (write_line includes the '\n')
    //sprintf(temp, "%.0f %f %.0f %.0f %.0f", a, ecc, m, period), numsteps;
    temp << a << " " << ecc << " " << m << " " << period << " " << numsteps;
    write_line(fileout, temp.str().c_str());    // probably a bit inefficient (the .str().c_str() thing)
    // write all data
    for (int i = 0; i < numsteps; i++) {
        //sprintf(temp, "%.4f %.0f %f", (*tvec)[i], (*rvec)[i], (*thvec)[i]);
        temp.str(string());
        temp << (*tvec)[i] << " " << (*rvec)[i] << " " << (*thvec)[i];
        write_line(fileout, temp.str().c_str());
// debug -=-=-=-=-
        if ((float)i/numsteps == 0.25f)
            cout << "25%" << endl;
        if ((float)i/numsteps == 0.5f)
            cout << "50%" << endl;
        if ((float)i/numsteps == 0.75f)
            cout << "75%" << endl;
// debug -=-=-=-=-

    }
    cout << "done" << endl;
    fileout.close();
}

// --------------------------------------------
// CLOSE

void myclose(int iscrash)
{
    exit(iscrash);
}