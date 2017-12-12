#include <iostream>
#include <fstream>
#include <string>
#include <stdlib.h>
#include <cstring>

#define min(a, b) ((a) < (b) ? (a) : (b))

using namespace std;

int findArg(char **argv, const char *arg);

int main(int argc, char **argv) {
	string s;
	long long bytesToWrite, bytesInLoop;
	char buffer[100];
	if (argc < 3) {
		printf("Usage: FileCutter <FileName> <Bytes> (optional) -o <outFile> -append\n");
		return 1;
	}
	ifstream inFile(argv[1], ios::binary);
	ofstream outFile;

	bytesToWrite = atoll(argv[2]);
	inFile.seekg(0, ios::end);
	if (inFile.tellg() <= bytesToWrite) {
		printf("Warning: The Filesize is only %d bytes. The File won't get smaller.\n", inFile.tellg());
	}
	inFile.seekg(0, ios::beg);

	bool append = false;
	if (findArg(argv, "-append") >= 0) {
		append = true;
	}
	int idx = findArg(argv, "-o");
	if (idx >= 0) {
		if (append)
			outFile.open(argv[idx + 1], ios::binary | ios::app);
		else
			outFile.open(argv[idx + 1], ios::binary | ios::trunc);
	}
	else {
		s.assign(argv[1]);
		s.insert(s.find_last_of("."), " - cutted");
		printf("Writing into File %s...\n", s.c_str());
		if (append)
			outFile.open(s.c_str(), ios::binary | ios::app);
		else
			outFile.open(s.c_str(), ios::binary | ios::trunc);
	}



	do {
		bytesInLoop = min(100ll, bytesToWrite);
		inFile.read(buffer, bytesInLoop);
		outFile.write(buffer, bytesInLoop);
		bytesToWrite -= bytesInLoop;
	} while (bytesToWrite > 0 && !inFile.eof());

	inFile.close();
	outFile.close();
	printf("Writing finished.\n");
	return 0;
}

int findArg(char **argv, const char *arg) {
	for (int i = 0; *(argv + i) != NULL; ++i) {
		if (strcmp(*(argv + i), arg) == 0)
			return i;
	}
	return -1;
}
