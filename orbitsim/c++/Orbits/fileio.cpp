//
//  fileio.cpp
//  Orbits
//
//  Mike Facelle
//  summer 2014

#include "fileio.h"

// -----
// writes the char* to the file specified
// returns 0 for success, 1 if otherwise
int write_file(const char* filename, const char* data)
{
    ofstream fileout(filename);
    fileout.open(filename);
    
    if (fileout.is_open()) {
        fprintf(stderr, "unable to open file %s", filename);
        return 1;
    }
    else {
        fileout << data << endl;
        fileout.close();
    }
    return 0;
}

// -----
// writes a line to the file, ending with a newline
// ASSUMES THAT FILEOUT IS OPEN ALREADY!
void write_line(ofstream &fileout, const char* line)
{
    fileout << line << endl;
}


