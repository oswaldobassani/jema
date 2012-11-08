#ifndef _NCGL_STRING_
#define _NCGL_STRING_

#include <stdarg.h>
#include <string>

using namespace std;

class ncglString {

public:
    ncglString();
    ~ncglString();

    void print();
    void clear();
    int add(const char *cp_format,...);
    void write(const char *filename);
    char* getBuffer();

private:
    char* buffer;
    unsigned int cpos;
};

#endif // _CGSTRING_
