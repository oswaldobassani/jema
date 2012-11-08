#ifndef _FEATUREDATA_
#define _FEATUREDATA_

#include <fstream>

using namespace std;

struct FeatureData {

    FeatureData() {
        posdata = 0;
        desc_data = 0;
    }

    ~FeatureData() {
        if (posdata) {
            delete[] posdata;
            posdata=0;
        }
        if (desc_data) {
            delete[] desc_data;
            desc_data=0;
        }
    }

    void write(const char* filename,bool verbose) {

        if (verbose)
            fprintf(stderr,"%s -> %d features\n",filename,nrf);

        fstream file(filename,ios::out|ios::binary);

        if (file.is_open()) {
            file.write((char*)&imw,sizeof(unsigned int));
            file.write((char*)&imh,sizeof(unsigned int));
            file.write((char*)&nrf,sizeof(unsigned int));
            file.write((char*)&nrsubregions,sizeof(int));
            file.write((char*)&posdata[0],nrf*4*sizeof(float));
            file.write((char*)&desc_data[0],nrf*nrsubregions*nrsubregions*4*sizeof(float));

            file.close();
        } else
            fprintf(stderr,"ERROR: Could not open file %s\n",filename);

    }

    void writeMiko(const char* filename,bool verbose) {

        if (verbose)
            fprintf(stderr,"%s -> %d features\n",filename,nrf);

        fstream file(filename,ios::out);

        if (file.is_open()) {

        	int desclength = nrsubregions*nrsubregions*4;

        	file << desclength << endl;
        	file << nrf << endl;

        	for (unsigned int i=0;i<nrf;++i)
        	{
        		float sc = posdata[4*i+2]/2.0f;
        		sc*=sc;
        		file   << (posdata[4*i+0]) /* x-location of the interest point */
				<< " " << (posdata[4*i+1]) /* y-location of the interest point */
				<< " " << 1.0/sc /* 1/r^2 */
				<< " " << 0.0     //(*ipts)[n]->strength /* 0.0 */
				<< " " << 1.0/sc; /* 1/r^2 */
				for (int j = 0; j < desclength; j++)
					file << " " << desc_data[i*desclength+j];
				file << endl;
        	}

            file.close();
        } else
            fprintf(stderr,"ERROR: Could not open file %s\n",filename);

    }

    void read(const char* filename) {

        if (posdata) {
            delete[] posdata;
            posdata=0;
        }
        if (desc_data) {
            delete[] desc_data;
            desc_data=0;
        }

        fprintf(stderr,"Reading features from file %s\n",filename);

        fstream file(filename,ios::in|ios::binary);

        if (file.is_open()) {
            file.read((char*)&imw,sizeof(unsigned int));
            file.read((char*)&imh,sizeof(unsigned int));
            file.read((char*)&nrf,sizeof(unsigned int));
            file.read((char*)&nrsubregions,sizeof(int));
            posdata = new float[nrf*4];
            file.read((char*)&posdata[0],nrf*4*sizeof(float));
            desc_data = new float[nrf*nrsubregions*nrsubregions*4];
            file.read((char*)&desc_data[0],nrf*nrsubregions*nrsubregions*4*sizeof(float));

            file.close();
        } else
            fprintf(stderr,"ERROR: Could not open file %s\n",filename);

    }

    unsigned int imw;
    unsigned int imh;
    unsigned int nrf;
    unsigned int nrsubregions;
    float* posdata;
    float* desc_data;
};

#endif
