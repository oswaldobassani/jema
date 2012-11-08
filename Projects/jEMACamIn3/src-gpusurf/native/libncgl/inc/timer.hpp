#ifndef _NC_TIMER_
#define _NC_TIMER_

#include <ctime>
#include <sys/time.h>

class ncTimer {

public:
    ncTimer();
    ~ncTimer();

    void start();
    void stop();
    void setNrSlots(unsigned int nrs);
    float getTime();
    float getAverageTime();

private:
    struct timeval  start_time;
    struct timeval  end_time;
    float* slots;
    unsigned int nrslots;
    unsigned int currentslot;
    unsigned int usedslots;
};

#endif // _NC_TIMER_
