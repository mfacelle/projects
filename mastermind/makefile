# makefile for mastermind game

SRC_DIR = .
INC_DIR = ./include

CFLAGS = -I${INC_DIR} -std=c++11

all: install

install: mastermind.o main.o
	@g++ mastermind.o main.o ${CFLAGS} -o run
	@rm -f *.o

main.o: main.cpp
	@g++ -c main.cpp ${CFLAGS} -o main.o

mastermind.o: mastermind.cpp
	@g++ -c mastermind.cpp ${CFLAGS} -o mastermind.o

clean:
	@rm -f *.o run