# makefile for orbitsim program

CPP = g++
SRC_DIR = .
INC_DIR = ./include

CFLAGS = -I${INC_DIR} -std=c++11
LFLAGS = -lm

all: install

install: fileio.o calculations.o constants.o conversions.o main.o
	@${CPP} fileio.o calculations.o constants.o conversions.o main.o ${CFLAGS} -o orbitsim
	@rm -f *.o

fileio.o: fileio.cpp
	@${CPP} -c fileio.cpp ${CFLAGS} -o fileio.o

calculations.o: calculations.cpp
	@${CPP} -c calculations.cpp ${CFLAGS} -o calculations.o
constants.o: constants.cpp
	@${CPP} -c constants.cpp ${CFLAGS} -o constants.o

conversions.o: conversions.cpp
	@${CPP} -c conversions.cpp ${CFLAGS} -o conversions.o

main.o: main.cpp
	@${CPP} -c main.cpp ${CFLAGS} -o main.o

clean:
	@rm -f *.o orbitsim
