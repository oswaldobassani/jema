#ifndef _GAUSSIANS_
#define _GAUSSIANS_

struct Gaussians {

    Gaussians() {
    }

    ~Gaussians() {
        if (data) {
            delete[] data;
            data = 0;
        }
        if (data_) {
            delete[] data_;
            data_ = 0;
        }
        if (sigma) {
            delete[] sigma;
            sigma=0;
        }
        if (normval) {
            delete[] normval;
            normval=0;
        }
    }

    void init(int nr_, int range_) {
        nr=nr_;
        range=range_;
        size=2*range+1;
        data_= new float[size*nr];
        data = new float*[nr];
        sigma=new float[nr];
        normval=new float[nr];
        for (int i=0;i<nr;++i)
            data[i]= &data_[size*i];
    }

    void set(unsigned int index,float sigma_) {
        sigma[index]=sigma_;

        normval[index]=0.0f;
        for (int i=0;i<size;++i)
            normval[index]+=data[index][i]=expf(-(range-i)*(range-i)/(2*sigma[index]*sigma[index]));

		normval[index]=1.0/normval[index];
	}

    void print() {
        for (int i=0;i<nr;++i)
            printf("\nsigma %d = %.5f -> %.5f",i,sigma[i],2*sigma[i]);
        printf("\n");
        for (int j=0;j<size;++j) {
            for (int i=0;i<nr;++i)
                printf("%.5f ",data[i][j]);
            printf("\n");
        }
    }

    float*  data_;
    float** data;
    float* sigma;
    float* normval;
    int range;
    int size;
    int nr;
};

#endif // _GAUSSIANS_
