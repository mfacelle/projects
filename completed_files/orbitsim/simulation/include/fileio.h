//
//  fileio.h
//  Orbits
//
//  Mike Facelle
//  summer 2014

#ifndef Orbits_fileio_h
#define Orbits_fileio_h

#include <iostream>
#include <fstream>

using std::string;
using std::fstream;
using std::ofstream;
using std::endl;

int write_file(const char* filename, const char* stuff);
void write_line(ofstream &file_out, string line);

#endif
