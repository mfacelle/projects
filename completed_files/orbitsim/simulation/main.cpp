//
//  main.cpp
//  Orbits
//
//  Mike Facelle
//  summer 2014
//
//  Main program to simulate planetary orbits
//  Gets user input to either simulate a predefined planet (from our solar system)
//      or a custom planet, with user-specified values
//      (either in earth-relative units, or SI units)
//
//
// NOTE: code is probably very inefficient and memory-sucking.
//          may want to avoid making vectors that hold tons of data.
//          instead: just write directly to the file, keeping
//          the handful of variables needed for each loop (and rewriting their data)


#include "calculations.h"
#include "fileio.h"
#include <iostream>
#include <vector>
#include <sstream>

using std::to_string;
using std::stof;
using std::cout;
using std::cin;
using std::endl;
using std::vector;
using std::stringstream;
using std::ios;


// --------------------------------------------
// objects



// path to store data in
const char* FILEPATH = "../data/orbitsim.dat";

// simulation parameters
string name;    // name of the planet ("custom" if not a predefined planet)
float a;        // semimajor axis
float ecc;      // eccentricity
float m1;       // planet mass
float m2;       // host star mass
int numsteps;   // number of simualtion steps
float period;   // orbital period
float n;        // mean motion
float dt;       // time step

// functions
void customArgs(const string* &);
void init(const string* args);
void run(vector<float>* tvec, vector<float>* rvec, vector<float>* thvec);
void output_results(vector<float>* tvec, vector<float>* rvec, vector<float>* thvec);
void myclose(int);
bool equalsIgnoreCase(const string&, const string&);

// ======================================================================

// --------------------------------------------
// MAIN
int main (int argc, const char** argv)
{

    
    const string* args;
    bool noInput = true;
    
    // get planet name from user, and set simulation args to those specified values
    do {
        // prompt user for a planet name to simulate (or "custom", which then prompts them for each variable
        cout << "Enter a planet to simulate (or 'custom' for user-specified parameters)" << endl;
        cout << "\t[ mercury, venus, earth, mars, jupiter, saturn, uranus, neptune, custom ]" << endl;
        string planetName;
        cin >> planetName;
        
        if (planetName.compare(MERCURY) == 0) {
            noInput = false;
            args = mercury_args;
        }
        else if (planetName.compare(VENUS) == 0) {
            noInput = false;
            args = venus_args;
        }
        else if (planetName.compare(EARTH) == 0) {
            noInput = false;
            args = earth_args;
        }
        else if (planetName.compare(MARS) == 0) {
            noInput = false;
            args = mars_args;
        }
        else if (planetName.compare(JUPITER) == 0) {
            noInput = false;
            args = jupiter_args;
        }
        else if (planetName.compare(SATURN) == 0) {
            noInput = false;
            args = saturn_args;
        }
        else if (planetName.compare(URANUS) == 0) {
            noInput = false;
            args = uranus_args;
        }
        else if (planetName.compare(NEPTUNE) == 0) {
            noInput = false;
            args = neptune_args;
        }
        else if (planetName.compare(CUSTOM) == 0) {
            noInput = false;
            customArgs(args);
        }
        else {
            cout << "Invalid name! Please re-enter.\n" << endl;
        }
    } while (noInput);
    
    cout << "\nRunning orbit simulation for planet " << args[0] << endl << endl;
    
    // create vectors to hold all data and output it later
    // ...should probably just output right to file and not eat up memory with these huge arrays
    vector<float>* time_vec = new vector<float>(numsteps);
    vector<float>* radius_vec = new vector<float>(numsteps);
    vector<float>* theta_vec = new vector<float>(numsteps);
    
    init(args);
    run(time_vec, radius_vec, theta_vec);
    output_results(time_vec, radius_vec, theta_vec);
        
    return 0;
}

// ======================================================================

// --------------------------------------------
// GET CUSTOM ARGS

// retrieves custom simulation parameters from the user
// either as earth-relative units, or absolute SI units
// args array in the format: { name, semimajor axis, eccentricity, planet mass, host star mass, number of steps }
void customArgs(const string* &params)
{
    bool relativeUnits = false;
    
    cout << "\nCustom-orbit parameter input mode." << endl;
    cout << "Input values in earth-relative units? [y/n]" << endl
        << "\tie: a = 1.0 AU, instead of a = 1.496e11" << endl;
    char c;
    cin >> c;
    relativeUnits = c == 'y';
    
    // parameters to enter
    float semimajorAxis;
    float eccentricity;
    float planetMass;
    float hostMass;
    float numberOfSteps;
    
    cout << "\nPlease enter the following parameters:\n" << endl;
    
    cout << "Semi-major axis (in " << (relativeUnits ? "AU" : "m") << ") = ";
    cin >> semimajorAxis;
    cout << endl;
    cout << "Eccentricity (absolute only) = ";
    cin >> eccentricity;
    cout << endl;
    cout << "Planet mass (in " << (relativeUnits ? "earth-masses" : "kg") << ") = ";
    cin >> planetMass;
    cout << endl;
    cout << "Host-star mass (in " << (relativeUnits ? "solar-masses" : "kg") << ") = ";
    cin >> hostMass;
    cout << endl;
    cout << "Number of simulation steps = ";
    cin >> numberOfSteps;
    cout << endl;

    if (relativeUnits) {
        semimajorAxis *= stof(earth_args[1]);
        planetMass *= stof(earth_args[3]);
        hostMass *= stof(earth_args[4]);
    }
    
    // limit eccentricity to 0 < ecc <= 1, to prevent division by 0 (only plotting bound orbits)
    if (eccentricity >= 1)
        eccentricity = 1 - 1.0E-5;
    else if (eccentricity < 0)
        eccentricity = 0;

    // convert to strings and put into args[]
    string args1 = to_string(semimajorAxis);
    string args2 = to_string(eccentricity);
    string args3 = to_string(planetMass);
    string args4 = to_string(hostMass);
    string args5 = to_string(numberOfSteps);
    
    // this might be assigning stack memory locations??
    params = new string[6] { CUSTOM, to_string(semimajorAxis), args2, args3, args4, args5 };
}


// --------------------------------------------
// INITIALIZE
// convert args to float or int values
//
void init(const string* args)
{
    // semimajor axis a, eccentricity ecc, host mass m, number of time divisions numsteps, are the input arguments
    name = args[0];
    a = stof(args[1]);
    ecc = stof(args[2]);
    m1 = stof(args[3]);
    m2 = stof(args[4]);
    numsteps = stoi(args[5]);
    
    // period
    period = orbital_period(m2, a);
    // mean motion
    n = mean_motion(period);
    // step size (for the t in M=nt)
    dt = period / numsteps;
    
    cout << "PARAMETERS:" << endl
    << "semi-major axis (in m) = " << a << endl
    << "eccentricity = " << ecc << endl
    << "planet mass (in kg) = " << m1 << endl
    << "host star mass (in kg) = " << m2 << endl
    << "number of simulation steps = " << numsteps << endl
    << "period (in s) = " << period << endl 
    << "mean motion = " << n << endl 
    << "dt (in s) = " << dt << endl;
}

// --------------------------------------------
// RUN
//
void run(vector<float>* tvec, vector<float>* rvec, vector<float>* thvec)
{
    // perform "simulation"
    // simulation variables to be used:
    float t = 0, ma = 0, x_0 = 0, ea = 0, ta = 0, r = 0;
    
    cout << "\nBeginning simulation" << endl;
    // record time
    clock_t start_time = clock();
    
    clock_t time = clock(); // record time as is passes to output percent done every 1s
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
        
        if ((clock() - time) / CLOCKS_PER_SEC >= 1) {
            time = clock();
            cout << (int)((float)i/numsteps * 100) << "%" << endl;
        }
    }
    cout << "simulation done." << endl;
    clock_t end_time = clock();
    float delta_t = (float)(end_time - start_time) / CLOCKS_PER_SEC;
    cout << "elapsed time: " << delta_t << endl;

    
}

// --------------------------------------------
// OUTPUT TO FILE
// first line is the parameters:     a ecc m period
// the rest of the lines are data:   time radius theta
//
void output_results(vector<float>* tvec, vector<float>* rvec, vector<float>* thvec)
{    
    cout << "\nOutputting data to file" << endl;
    
    ofstream fileout;
// NOTE: fix this to make sure file ends up in the right place
    fileout.open(FILEPATH, ios::out | ios::trunc);
    
    if (!fileout.is_open()) {
        fprintf(stderr, "unable to open file %s\nExiting.", FILEPATH);
        fileout.close();
        myclose(1);
    }
    
    clock_t time = clock(); // keep track of elapsed time to print percentage every 1s
    stringstream temp;
    // write first line (write_line includes the '\n')
    // args[0] is the planet name
    temp << name << " " << a << " " << ecc << " " << m1 << " " << m2 << " " << period << " " << numsteps;
    write_line(fileout, temp.str());
    // write all data
    for (int i = 0; i < numsteps; i++) {
        temp.str(string());
        temp << (*tvec)[i] << " " << (*rvec)[i] << " " << (*thvec)[i];
        write_line(fileout, temp.str().c_str());
        // keep track of time and output percentage done every 1 second
        if ((clock() - time) / CLOCKS_PER_SEC >= 1) {
            time = clock();
            cout << (int)((float)i/numsteps*100) << "%" << endl;
        }

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

// --------------------------------------------
// EQUALS IGNORE CASE (for input checking)

// returns true if the strings are equal, case-insensitive.
// tolower() returns lower case version of char, or the char itself if it is NOT upper case
bool equalsIgnoreCase(const string& a, const string& b)
{
    unsigned int size = a.size();
    if (size != b.size())
        return false;
    
    for (unsigned int i = 0; i < size; i++)
        if (tolower(a[i]) != tolower(b[i]))
            return false;
    
    return true;
}