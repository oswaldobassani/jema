#ifndef _KEYPOINTLIST_
#define _KEYPOINTLIST_

struct KeyPointList {

	KeyPointList() {
        nrfeatures = 0;
        positions  = 0;
        angles     = 0;
    }

    ~KeyPointList() {
        nrfeatures = 0;
        if (positions) {
            delete[] positions;
            positions=0;
        }
        if (angles) {
            delete[] angles;
            angles=0;
        }
    }

    void alloc(int nrf) {
		nrfeatures = 0;
        if (positions) {
            delete[] positions;
            positions=0;
        }
        positions = new float[4*nrf];
        if (angles) {
            delete[] angles;
            angles=0;
        }
        angles = new float[nrf];
    }

    unsigned int nrfeatures;
    unsigned int width;
    unsigned int height;
    float* positions;
    float* angles;
};

#endif // _KEYPOINTLIST_
