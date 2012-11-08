#ifndef _NCMATH_
#define _NCMATH_
#include <iostream>

//class templates
template <class T>
class nc_v3;
template <class T>
class nc_v4;
template <class T>
class nc_m3x3;

//binary operator templates
template <class T>
nc_v3<T> 	operator*(T in, nc_v3<T> vin);
template <class T>
nc_v4<T> 	operator*(T in, nc_v4<T> vin);
template <class T>
nc_m3x3<T> 	operator*(T in, nc_m3x3<T> min);

//nc_v3
template <class T>
class nc_v3 {
    friend nc_v3 operator*<>(T, nc_v3 <T>);

protected:
    T data[3];

public:
    nc_v3(T x=0.0, T y=0.0, T z=0.0) {

        data[0]=x;
        data[1]=y;
        data[2]=z;
    };
    nc_v3(const nc_v3<T>& in) {
        for (int i=0;i<3;++i)
            data[i]=in.data[i];
    };
    nc_v3(nc_v4<T> in) {

        if (in.w()==0) {
            data[0]=in.x();
            data[1]=in.y();
            data[2]=in.z();
        } else {
            data[0]=in.x()/in.w();
            data[1]=in.y()/in.w();
            data[2]=in.z()/in.w();
        }
    };
    T * data_block() {
        return data;
    };
    T & operator[](int index) {
        return data[index];
    };
    T operator[](int index) const {
        return data[index];
    };
    T & operator()(int index) {
        return data[index];
    };
    T x() const {
        return data[0];
    };
    T y() const {
        return data[1];
    };
    T z() const {
        return data[2];
    };
    nc_v3<T> operator*(T in) {
        nc_v3<T> out;
        out.data[0]=data[0]*in;
        out.data[1]=data[1]*in;
        out.data[2]=data[2]*in;
        return out;
    };
    nc_v3<T> operator+(nc_v3<T> in) {
        nc_v3<T> out;
        out.data[0]=data[0]+in.data[0];
        out.data[1]=data[1]+in.data[1];
        out.data[2]=data[2]+in.data[2];
        return out;
    };
    nc_v3<T> operator+=(nc_v3<T> in) {
        nc_v3<T> out;
        out=*this+in;
        return out;
    };
    nc_v3<T> operator-(nc_v3<T> in) {
        nc_v3<T> out;
        out.data[0]=data[0]-in.data[0];
        out.data[1]=data[1]-in.data[1];
        out.data[2]=data[2]-in.data[2];
        return out;
    };
    nc_v3<T> operator-=(nc_v3<T> in) {
        nc_v3<T> out;
        out=*this-in;
        return out;
    };
    void operator=(nc_v4<T> const in) {
        if (in.w()==0) {
            data[0]=in.x();
            data[1]=in.y();
            data[2]=in.z();
        } else {
            data[0]=in.x()/in.w();
            data[1]=in.y()/in.w();
            data[2]=in.z()/in.w();
        }
    };
    T dot(nc_v3<T> in) {
        return data[0]*in.data[0]+data[1]*in.data[1]+data[2]*in.data[2];
    };
    T norm() {
        return sqrt(data[0]*data[0]+data[1]*data[1]+data[2]*data[2]);
    };
    void normalize() {
        T inorm=((T)1.0)/this->norm();
        data[0]*=inorm;
        data[1]*=inorm;
        data[2]*=inorm;
    };

};
template <class T>
nc_v3<T> operator*(T in, nc_v3<T> vin) {
    nc_v3<T> vout;
    for (int i=0;i<3;++i)
        vout.data[i]=vin.data[i]*in;

    return vout;
}

//nc_v4
template <class T>
class nc_v4 {
    friend nc_v4 operator*<>(T, nc_v4 <T>);

protected:
    T data[4];

public:
    nc_v4(T x=0.0, T y=0.0, T z=0.0,T w=1.0) {

        data[0]=x;
        data[1]=y;
        data[2]=z;
        data[3]=w;

    };
    nc_v4(const nc_v4<T>& in) {
        for (int i=0;i<4;++i)
            data[i]=in.data[i];
    };
    nc_v4(nc_v3<T> in) {

        data[0]=in.x();
        data[1]=in.y();
        data[2]=in.z();
        data[3]=1.0;
    };
    T * data_block() {
        return data;
    };
    T & operator[](int index) {
        return data[index];
    };
    T operator[](int index) const {
        return data[index];
    };
    T & operator()(int index) {
        return data[index];
    };
    T x() const {
        return data[0];
    };
    T y() const {
        return data[1];
    };
    T z() const {
        return data[2];
    };
    T w() const {
        return data[3];
    };
    nc_v4<T> operator*(T in) {
        nc_v4<T> out;
        out.data[0]=data[0]*in;
        out.data[1]=data[1]*in;
        out.data[2]=data[2]*in;
        out.data[3]=data[3]*in;
        return out;
    };
    nc_v4<T> operator+(nc_v4<T> in) {
        nc_v4<T> out;
        out.data[0]=data[0]+in.data[0];
        out.data[1]=data[1]+in.data[1];
        out.data[2]=data[2]+in.data[2];
        out.data[3]=data[3]+in.data[3];
        return out;
    };
    nc_v4<T> operator+=(nc_v4<T> in) {
        nc_v4<T> out;
        out=*this+in;
        return out;
    };
    nc_v4<T> operator-(nc_v4<T> in) {
        nc_v4<T> out;
        out.data[0]=data[0]-in.data[0];
        out.data[1]=data[1]-in.data[1];
        out.data[2]=data[2]-in.data[2];
        out.data[3]=data[3]-in.data[3];
        return out;
    };
    nc_v4<T> operator-=(nc_v4<T> in) {
        nc_v4<T> out;
        out=*this-in;
        return out;
    };
    void operator=(nc_v3<T> in) {
        data[0]=in.x();
        data[1]=in.y();
        data[2]=in.z();
        data[3]=1.0;
    };
    T dot(nc_v4<T> in) {
        return data[0]*in.data[0]+data[1]*in.data[1]+data[2]*in.data[2]+data[3]*in.data[3];
    };
    T norm() {
        nc_v3<T> tmp(*this);
        return tmp.norm();
    }
    void normalize() {
        nc_v3<T> tmp(*this);
        tmp.normalize();
        *this=tmp;
    };
};
template <class T>
nc_v4<T> operator*(T in, nc_v4<T> vin) {
    nc_v4<T> vout;
    for (int i=0;i<4;++i)
        vout.data[i]=vin.data[i]*in;

    return vout;
}

//nc_m3x3
template <class T>
class nc_m3x3 {
    friend nc_m3x3 operator*<>(T, nc_m3x3 <T>);
protected:
    T data[9];

public:

    nc_m3x3() {
    };
    nc_m3x3(const nc_m3x3<T>& in) {
        for (int i=0;i<9;++i)
            data[i]=in.data[i];
    };
    T* data_block() {
        return data;
    };
    T* operator[](int row) {
        return &data[row*3];
    };
    T& operator()(int row,int col) {
        return data[row*3+col];
    };
    T  operator()(int row,int col) const {
        return data[row*3+col];
    };
    T& operator()(int index) {
        return data[index];
    };
    nc_v4<T> 	operator*(nc_v4<T> vin) {
        nc_v4<T> vout;
        vout[0]=data[0]*vin[0]+data[1]*vin[1]+data[2]*vin[2];
        vout[1]=data[3]*vin[0]+data[4]*vin[1]+data[5]*vin[2];
        vout[2]=data[6]*vin[0]+data[7]*vin[1]+data[8]*vin[2];
        vout[3]=vin[3];

        return vout;
    };
    nc_v3<T> 	operator*(nc_v3<T> vin) {
        nc_v3<T> vout;
        vout[0]=data[0]*vin[0]+data[1]*vin[1]+data[2]*vin[2];
        vout[1]=data[3]*vin[0]+data[4]*vin[1]+data[5]*vin[2];
        vout[2]=data[6]*vin[0]+data[7]*vin[1]+data[8]*vin[2];

        return vout;
    };
    nc_m3x3<T> 	operator*(nc_m3x3<T> min) {

        nc_m3x3<T> mout;
        for (int i=0;i<3;++i)
            for (int j=0;j<3;++j)
                mout.data[3*i+j]=data[i*3]*min.data[j]+data[i*3+1]*min.data[j+3]+data[i*3+2]*min.data[j+6];
        return mout;
    };
    nc_m3x3<T> 	operator*(T in) {
        nc_m3x3<T> mout;
        for (int i=0;i<9;++i)
            mout.data[i]=data[i]*in;

        return mout;
    };
    void fill(T value) {
        for (int i=0;i<9;++i)
            data[i]=value;
    };
    void diag(T value) {
        for (int i=0;i<3;++i)
            data[i*3+i]=value;
    };
    void setIdentity() {
        this->fill(0.0);
        this->diag(1.0);
    };
    nc_m3x3<T> inverse() {
        nc_m3x3<T> result;

        T det= data[0]*data[4]*data[8]+data[1]*data[5]*data[6]+data[2]*data[3]*data[7]-data[0]*data[5]*data[7]-data[1]*data[3]*data[8]-data[2]*data[4]*data[6];
        T idet=1./det;

        result(0,0)=idet*(data[4]*data[8]-data[7]*data[5]);
        result(0,1)=idet*(data[2]*data[7]-data[8]*data[1]);
        result(0,2)=idet*(data[1]*data[5]-data[4]*data[2]);
        result(1,0)=idet*(data[5]*data[6]-data[8]*data[3]);
        result(1,1)=idet*(data[0]*data[8]-data[6]*data[2]);
        result(1,2)=idet*(data[2]*data[3]-data[5]*data[0]);
        result(2,0)=idet*(data[3]*data[7]-data[6]*data[4]);
        result(2,1)=idet*(data[1]*data[6]-data[7]*data[0]);
        result(2,2)=idet*(data[0]*data[4]-data[3]*data[1]);

        return result;
    };

};
template <class T>
nc_m3x3<T> operator*(T in, nc_m3x3<T> min) {
    nc_m3x3<T> mout;
    for (int i=0;i<9;++i)
        mout.data[i]=min.data[i]*in;

    return mout;
}

//nc_m4x4
template <class T>
class nc_m4x4 {
protected:
    T data[16];

public:

    nc_m4x4() {
    };
    nc_m4x4(const nc_m4x4<T> & in) {
        for (int i=0;i<16;++i)
            data[i]=in.data[i];
    };
    T* data_block() {
        return data;
    };
    T* operator[](int row) {
        return &data[row*4];
    };
    T& operator()(int row,int col) {
        return data[row*4+col];
    };
    T  operator()(int row,int col) const {
        return data[row*4+col];
    };
    T& operator()(int index) {
        return data[index];
    };
    nc_v4<T> 	operator*(nc_v4<T> vin) {
        nc_v4<T> vout;
        vout[0]=data[0]*vin[0]+data[1]*vin[1]+data[2]*vin[2]+data[3]*vin[3];
        vout[1]=data[4]*vin[0]+data[5]*vin[1]+data[6]*vin[2]+data[7]*vin[3];
        vout[2]=data[8]*vin[0]+data[9]*vin[1]+data[10]*vin[2]+data[11]*vin[3];
        vout[3]=data[12]*vin[0]+data[13]*vin[1]+data[14]*vin[2]+data[15]*vin[3];

        return vout;
    };
    nc_m4x4<T> 	operator*(nc_m4x4<T> min) {
        nc_m4x4<T> mout;
        for (int i=0;i<4;++i)
            for (int j=0;j<4;++j)
                mout.data[4*i+j]=data[i*4]*min.data[j]+data[i*4+1]*min.data[j+4]+data[i*4+2]*min.data[j+8]+data[i*4+3]*min.data[j+12];

        return mout;
    };
    nc_m4x4<T> 	operator*(T in) {
        nc_m4x4<T> mout;
        for (int i=0;i<16;++i)
            mout.data[i]=data[i]*in;

        return mout;
    };
    void fill(T value) {
        for (int i=0;i<16;++i)
            data[i]=value;
    };
    void diag(T value) {
        for (int i=0;i<4;++i)
            data[i*4+i]=value;
    };
    void setIdentity() {
        this->fill(0.0);
        this->diag(1.0);
    };
    nc_m4x4<T> inverse() {
        nc_m4x4<T> result;

        T* mat=data;
        T* dst=result[0];


        T tmp[12];
        /* temp array for pairs */
        T src[16];
        /* array of transpose source matrix */
        T det;
        /* determinant */
        /* transpose matrix */
        for ( int i = 0; i < 4; i++) {
            src[i] = mat[i*4];
            src[i + 4] = mat[i*4 + 1];
            src[i + 8] = mat[i*4 + 2];
            src[i + 12] = mat[i*4 + 3];
        }
        /* calculate pairs for first 8 elements (cofactors) */
        tmp[0] = src[10] * src[15];
        tmp[1] = src[11] * src[14];
        tmp[2] = src[9] * src[15];
        tmp[3] = src[11] * src[13];
        tmp[4] = src[9] * src[14];
        tmp[5] = src[10] * src[13];
        tmp[6] = src[8] * src[15];
        tmp[7] = src[11] * src[12];
        tmp[8] = src[8] * src[14];
        tmp[9] = src[10] * src[12];
        tmp[10] = src[8] * src[13];
        tmp[11] = src[9] * src[12];
        /* calculate first 8 elements (cofactors) */
        dst[0] = tmp[0]*src[5] + tmp[3]*src[6] + tmp[4]*src[7];
        dst[0] -= tmp[1]*src[5] + tmp[2]*src[6] + tmp[5]*src[7];
        dst[1] = tmp[1]*src[4] + tmp[6]*src[6] + tmp[9]*src[7];
        dst[1] -= tmp[0]*src[4] + tmp[7]*src[6] + tmp[8]*src[7];
        dst[2] = tmp[2]*src[4] + tmp[7]*src[5] + tmp[10]*src[7];
        dst[2] -= tmp[3]*src[4] + tmp[6]*src[5] + tmp[11]*src[7];
        dst[3] = tmp[5]*src[4] + tmp[8]*src[5] + tmp[11]*src[6];
        dst[3] -= tmp[4]*src[4] + tmp[9]*src[5] + tmp[10]*src[6];
        dst[4] = tmp[1]*src[1] + tmp[2]*src[2] + tmp[5]*src[3];
        dst[4] -= tmp[0]*src[1] + tmp[3]*src[2] + tmp[4]*src[3];
        dst[5] = tmp[0]*src[0] + tmp[7]*src[2] + tmp[8]*src[3];
        dst[5] -= tmp[1]*src[0] + tmp[6]*src[2] + tmp[9]*src[3];
        dst[6] = tmp[3]*src[0] + tmp[6]*src[1] + tmp[11]*src[3];
        dst[6] -= tmp[2]*src[0] + tmp[7]*src[1] + tmp[10]*src[3];
        dst[7] = tmp[4]*src[0] + tmp[9]*src[1] + tmp[10]*src[2];
        dst[7] -= tmp[5]*src[0] + tmp[8]*src[1] + tmp[11]*src[2];
        /* calculate pairs for second 8 elements (cofactors) */
        tmp[0] = src[2]*src[7];
        tmp[1] = src[3]*src[6];
        tmp[2] = src[1]*src[7];
        tmp[3] = src[3]*src[5];
        tmp[4] = src[1]*src[6];
        tmp[5] = src[2]*src[5];
        tmp[6] = src[0]*src[7];
        tmp[7] = src[3]*src[4];
        tmp[8] = src[0]*src[6];
        tmp[9] = src[2]*src[4];
        tmp[10] = src[0]*src[5];
        tmp[11] = src[1]*src[4];
        /* calculate second 8 elements (cofactors) */
        dst[8] = tmp[0]*src[13] + tmp[3]*src[14] + tmp[4]*src[15];
        dst[8] -= tmp[1]*src[13] + tmp[2]*src[14] + tmp[5]*src[15];
        dst[9] = tmp[1]*src[12] + tmp[6]*src[14] + tmp[9]*src[15];
        dst[9] -= tmp[0]*src[12] + tmp[7]*src[14] + tmp[8]*src[15];
        dst[10] = tmp[2]*src[12] + tmp[7]*src[13] + tmp[10]*src[15];
        dst[10]-= tmp[3]*src[12] + tmp[6]*src[13] + tmp[11]*src[15];
        dst[11] = tmp[5]*src[12] + tmp[8]*src[13] + tmp[11]*src[14];
        dst[11]-= tmp[4]*src[12] + tmp[9]*src[13] + tmp[10]*src[14];
        dst[12] = tmp[2]*src[10] + tmp[5]*src[11] + tmp[1]*src[9];
        dst[12]-= tmp[4]*src[11] + tmp[0]*src[9] + tmp[3]*src[10];
        dst[13] = tmp[8]*src[11] + tmp[0]*src[8] + tmp[7]*src[10];
        dst[13]-= tmp[6]*src[10] + tmp[9]*src[11] + tmp[1]*src[8];
        dst[14] = tmp[6]*src[9] + tmp[11]*src[11] + tmp[3]*src[8];
        dst[14]-= tmp[10]*src[11] + tmp[2]*src[8] + tmp[7]*src[9];
        dst[15] = tmp[10]*src[10] + tmp[4]*src[8] + tmp[9]*src[9];
        dst[15]-= tmp[8]*src[9] + tmp[11]*src[10] + tmp[5]*src[8];
        /* calculate determinant */
        det=src[0]*dst[0]+src[1]*dst[1]+src[2]*dst[2]+src[3]*dst[3];
        /* calculate matrix inverse */
        det = 1/det;
        for ( int j = 0; j < 16; j++)
            dst[j] *= det;

        return result;
    };
};
template <class T>
nc_m4x4<T> operator*(T in, nc_m4x4<T> min) {
    nc_m4x4<T> mout;
    for (int i=0;i<16;++i)
        mout.data[i]=min.data[i]*in;

    return mout;
}

//nc_RT
template <class T>
class nc_RT : public nc_m4x4<T> {
protected:
    using nc_m4x4<T>::data;

public:

    nc_RT<T> operator*(nc_RT<T> min) {
        nc_RT<T> mout;
        for (int i=0;i<4;++i)
            for (int j=0;j<4;++j)
                mout.data[4*i+j]=data[i*4]*min.data[j]+data[i*4+1]*min.data[j+4]+data[i*4+2]*min.data[j+8]+data[i*4+3]*min.data[j+12];

        return mout;
    };
    nc_v4<T> operator*(nc_v4<T> vin) {
        nc_v4<T> vout;
        vout[0]=data[0]*vin[0]+data[1]*vin[1]+data[2]*vin[2]+data[3]*vin[3];
        vout[1]=data[4]*vin[0]+data[5]*vin[1]+data[6]*vin[2]+data[7]*vin[3];
        vout[2]=data[8]*vin[0]+data[9]*vin[1]+data[10]*vin[2]+data[11]*vin[3];
        vout[3]=data[12]*vin[0]+data[13]*vin[1]+data[14]*vin[2]+data[15]*vin[3];

        return vout;
    };
    nc_RT<T> inverse() {
        nc_RT<T> result;
        result(0)=data[0];
        result(1)=data[4];
        result(2)=data[8];
        result(3)=-data[0]*data[3]-data[4]*data[7]-data[8]*data[11];
        result(4)=data[1];
        result(5)=data[5];
        result(6)=data[9];
        result(7)=-data[1]*data[3]-data[5]*data[7]-data[9]*data[11];
        result(8)=data[2];
        result(9)=data[6];
        result(10)=data[10];
        result(11)=-data[2]*data[3]-data[6]*data[7]-data[10]*data[11];
        result(12)=0.0;
        result(13)=0.0;
        result(14)=0.0;
        result(15)=1.0;

        return result;
    };
    void setR(nc_m3x3<T> R) {
        T* mp=R.data_block();

        data[0]=*(mp++);
        data[1]=*(mp++);
        data[2]=*(mp++);
        data[4]=*(mp++);
        data[5]=*(mp++);
        data[6]=*(mp++);
        data[8]=*(mp++);
        data[9]=*(mp++);
        data[10]=*mp;
    };
    nc_m3x3<T> getR() {

        nc_m3x3<T> out;

        for (int i=0;i<3;++i)
            for (int j=0;j<3;++j)
                out[i][j]=data[4*i+j];

        return out;
    };
    void setT(nc_v3<T> T_) {
        for (int i=0;i<3;++i)
            data[4*i+3]=T_[i];
    };
    nc_v3<T> getT() {

        nc_v3<T> out;

        for (int i=0;i<3;++i)
            out[i]=data[4*i+3];

        return out;
    };
};

//cross product
template <class T>
nc_v3<T> cross(const nc_v3<T> in1,const nc_v3<T> in2) {
    nc_v3<T> out;
    out[0]=in1[1]*in2[2]-in1[2]*in2[1];
    out[1]=in1[2]*in2[0]-in1[0]*in2[2];
    out[2]=in1[0]*in2[1]-in1[1]*in2[0];

    return out;
}

//print to output stream
template <class t_stream, class T>
t_stream & operator<<(t_stream & first, nc_v3<T> const & v) {
    for (int col=0;col<3;++col)
        first << v[col]<< "\t";
    first << std::endl;
    return first;
};

template <class t_stream, class T>
t_stream & operator<<(t_stream & first, nc_v4<T> const & v) {
    for (int col=0;col<4;++col)
        first << v[col]<< "\t";
    first << std::endl;
    return first;
};

template <class t_stream, class T>
t_stream & operator<<(t_stream & first, nc_m3x3<T> const & m) {
    for (int row=0; row<3; ++row) {
        for (int col=0;col<3;++col) {
            first << m(row, col)<< "\t";
        };
        first << std::endl;
    };
    return first;
};

template <class t_stream, class T>
t_stream & operator<<(t_stream & first, nc_m4x4<T> const & m) {
    for (int row=0; row<4; ++row) {
        for (int col=0;col<4;++col) {
            first << m(row, col)<< "\t";
        };
        first << std::endl;
    };
    return first;
};

#define nc_RT nc_RT<double>
#define nc_m4x4 nc_m4x4<double>
#define nc_m3x3 nc_m3x3<double>
#define nc_v4 nc_v4<double>
#define nc_v3 nc_v3<double>

#endif
