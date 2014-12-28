//
//  mastermind.h
//  mastermind
//
//  Created by Mike on 12/26/14.
//  Copyright (c) 2014 Mike Facelle. All rights reserved.
//

#ifndef __mastermind__mastermind__
#define __mastermind__mastermind__

#include "hashtable.h"
#include <vector>
#include <string>

using std::vector;
using std::string;

#define NUM_COLORS  6
#define CODE_SIZE   4

extern char colors[];
extern string CORRECT_COLOR;
extern string CORRECT_PLACEMENT;
extern string WRONG;

class mastermind
{
public:
    // map of available colors
    hashtable<int, char> colormap;
    
    void play(const int &num_turns);
    
private:

    // a turn taken: code choice, and results
    class turn
    {
    public:
        turn(const int &n, char c[])
        {
            turn_number = n;
            for (int i = 0; i < CODE_SIZE; i++)
                choice[i] = c[i];
        }
        int turn_number;
        char choice[CODE_SIZE];
        string results[CODE_SIZE];
    };
    
    char code[CODE_SIZE];    // the code to be determined
    int num_turns;          // total number of turns allowed
    int current_turn;       // the number of the current turn
    vector<turn> turns;     // turns taken
    
    void initialize();
    void random_code();     // selects a random starting code
    bool play_turn(int &n);   // plays the n-th turn of the game
    bool turn_results(turn* t);     // calculates result of turn
    void print_turn_results(const turn* t);   // prints cc, cp results
};

#endif /* defined(__mastermind__mastermind__) */
