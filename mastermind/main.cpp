//
//  main.cpp
//  mastermind
//

#include "mastermind.h"
#include <iostream>

using std::cout;
using std::endl;

int main(int argc, const char * argv[])
{
    int num_turns = 10;
    mastermind game;
    game.play(num_turns);
    
    return 0;
}
