//
//  mastermind.cpp
//  mastermind
//


#include "mastermind.h"
#include <iostream>
#include <random>
#include <string>

using std::cout;
using std::endl;
using std::cin;

char colors[] = { 'r', 'g', 'b', 'm', 'y', 'c' };

string CORRECT_COLOR = "cc";
string CORRECT_PLACEMENT = "cp";
string WRONG = "--";


// ---------------------------------------------
void mastermind::play(const int &num_turns)
{
    this->num_turns = num_turns;
    initialize();
    
    // tell user rules and color choices
    cout << "\n----- MASTERMIND -----\n" << endl;
    cout << "In this game, you have " << num_turns << " turns " <<
        "to guess a " << CODE_SIZE << " character code." << endl;
    cout << "After you enter a selection, the game will tell you how many choices are " <<
        "correctly placed, and how many choices are the correct color but wrongly placed." << endl;
    cout << "These will only represent how many fall into these categories; not which choices " <<
        "specifically are correct/incorrect." << endl;
    cout << "\tcc : correct color, wrong placement" << endl;
    cout << "\tcp : correct color and placement" << endl;
    cout << "\t-- : wrong correct color and placement" << endl;
    
    cout << "\nPossible colors are: [ ";
    for (int i = 0; i < NUM_COLORS; i++)
        cout << colors[i] << " ";
    cout << "]\n" << endl;
    
    // main game loop
    char playagain;
    do {
        // generate random code
        random_code();
        cout << "\n--------------------------------" << endl;
        cout << "        Beginning game!" << endl;
        cout << "--------------------------------\n" << endl;
    
        // start recursively playing:
        int n = 0;
        bool victory = play_turn(n);
    
        // end of game:
        if (victory) {
            cout << "Congratulations! You won in " << n+1 << " turns!" << endl;
        }
        else {
            cout << "You lost!" << endl;
            cout << "The code was:\n\t";
            std::string s;
            for (int i = 0; i < CODE_SIZE; i++) {
                cout << code[i] << " ";
            }
            cout << s << endl;
        }
        
        cout << "\nPlay again? (y/n)" << endl;
        cin >> playagain;
    } while (playagain == 'y');
}

// ---------------------------------------------
// initialize
void mastermind::initialize()
{
    // map numbers to color chars
    for (int i = 0; i < NUM_COLORS; i++)
        colormap.put(i, colors[i]);
}

// ---------------------------------------------
// determines the code that the user must figure out
void mastermind::random_code()
{
    // from cplusplus documentation: seeding with current time
    unsigned seed = (unsigned)std::chrono::system_clock::now().time_since_epoch().count();
    std::default_random_engine generator(seed);
    
    std::uniform_int_distribution<int> distribution(0, NUM_COLORS-1);

    for (int i = 0; i < CODE_SIZE; i++)
        colormap.get(distribution(generator), code[i]);

}

// ---------------------------------------------
bool mastermind::play_turn(int &n)
{
    // if turns have been exceeded without a success: you lose
    if (n >= num_turns)
        return false;
    
    cout << "\nTurn " << n+1 << endl;
    cout << "Enter your " << CODE_SIZE << " combination code attempt:\n\t";
    char choices[4];
    for (int i = 0; i < CODE_SIZE; i++)
        cin >> choices[i];
    
    turn t (n, choices);
    
    if (turn_results(&t))
        return true;
    
    print_turn_results(&t);
    
    n++;
    return play_turn(n);
}

// ---------------------------------------------
// using a brute-force method for checking for placements
// since theres only 4 digits in each code, this will be okay
bool mastermind::turn_results(turn* t)
{
    // store index of already-checked code digits
    vector<int> codecorrect;
    // store index of already-checked choice digits
    vector<int> choicecorrect;
    // store number of cc and cp
    int ncp = 0;
    int ncc = 0;
    char c;
    
    // check for ncp matches FIRST (higher priority):
    for (int i = 0; i < CODE_SIZE; i++) {
        if (t->choice[i] == code[i]) {
            ncp++;
            choicecorrect.push_back(i);
            codecorrect.push_back(i);
        }
    }
    
    for (int i = 0; i < CODE_SIZE; i++) {
        if (std::find(choicecorrect.begin(), choicecorrect.end(), i) != choicecorrect.end())
            continue;
        c = t->choice[i];
        for (int j = 0; j < CODE_SIZE; j++) {
            // skip already-classified digits
            if (std::find(codecorrect.begin(), codecorrect.end(), j) != codecorrect.end())
                continue;
            else if (t->choice[i] == code[j]) {
                if (i == j)
                    ncp++;
                else
                    ncc++;
                choicecorrect.push_back(i);
                codecorrect.push_back(j);
                break;
            }
        }
    }
    // fill results string
    int n = 0;
    for (int i = 0; i < ncc && i < CODE_SIZE; i++, n++)
        t->results[i] = CORRECT_COLOR;
    for (int i = n; i < n+ncp && i < CODE_SIZE; i++)
        t->results[i] = CORRECT_PLACEMENT;
    for (int i = ncc+ncp; i < CODE_SIZE; i++)
        t->results[i] = WRONG;
    
    if (ncp >= CODE_SIZE)
        return true;
    else
        return false;
}

// ---------------------------------------------
// print turn results
void mastermind::print_turn_results(const turn* t)
{
    cout << "\t";
    for (int i = 0; i < CODE_SIZE; i++)
        cout << t->results[i] << "   ";
    cout << endl;
}
