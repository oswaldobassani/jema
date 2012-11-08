package br.ufabc.bassani.opensurf;

public class OpenSurf {

	static {
		System.loadLibrary ("javaopensurf");
	}

	public OpenSurf () {
		System.out.println ("OpenSurf: constructor (Java-OpenSURF binding)");
		//configure();
	}

	/* 
	 * img1Bytes byte[] bytes da imagem 1
	 * img2Bytes byte[]
	 * int w
	 * int h
	 * return float[] Conjuntos {[x,y,x,y]} (primeira/segunda imagem)
	 */
	public native float[] getCorrespondencePoints(int[] img1Bytes, int[] img2Bytes, int w, int h);

	
	public native float[] getCorrespondencePointsWithConfig(int[] img1Bytes, int[] img2Bytes, int w, int h, boolean upright,  int octaves, int intervals, int init_sample, float thres);

	public native void reset(int w, int h);
	
	/*
	 * upright - run in rotation invariant mode?
	 * octaves - number of octaves to calculate
	 * intervals - number of intervals per octave
	 * init_sample - initial sampling step
	 * thres - blob response threshold
	 */
	public native void configure(boolean upright,  int octaves, int intervals, int init_sample, float thres);
	
	public native float[] getCorrespondencePointsFromLoop(int[] img1Bytes);
	
	public native int[] getDisparityImageFromColorImages(int[] img1, int[] img2, int w, int h);
	
	/*
	public native int[] getDisparityImageFromGrayScaleImages(int[] img1, int[] img2, int w, int h);
	*/
	
	//! Library function builds vector of described interest points
//	public native void surfDetDes(byte[] imgBytes,  /* image to find Ipoints in */
//		                       float[] ipts, /* reference to vector of Ipoints */
//                		       boolean upright, /* run in rotation invariant mode? */
//                		       int octaves, /* number of octaves to calculate */
//		                       int intervals, /* number of intervals per octave */
//                		       int init_sample, /* initial sampling step */
//                		       float thres /* blob response threshold */)

	/* Matches points between image 1,2	*/
//	public native void getMatches(float[] ipts1, /* vector points 1 */
//					float[] ipts2, /* vector points 2 */
//					float[] matches /* vector matches */);

/*
class Ipoint; // Pre-declaration
typedef std::vector<Ipoint> IpVec;
typedef std::vector<std::pair<Ipoint, Ipoint> > IpPairVec;

//-------------------------------------------------------

//! Ipoint operations
void getMatches(IpVec &ipts1, IpVec &ipts2, IpPairVec &matches);
int translateCorners(IpPairVec &matches, const CvPoint src_corners[4], CvPoint dst_corners[4]);

//-------------------------------------------------------

class Ipoint {

public:

  //! Destructor
  ~Ipoint() {};

  //! Constructor
  Ipoint() : orientation(0) {};

  //! Gets the distance in descriptor space between Ipoints
  float operator-(const Ipoint &rhs)
  {
    float sum=0.f;
    for(int i=0; i < 64; ++i)
      sum += (this->descriptor[i] - rhs.descriptor[i])*(this->descriptor[i] - rhs.descriptor[i]);
    return sqrt(sum);
  };

  //! Coordinates of the detected interest point
  float x, y;

  //! Detected scale
  float scale;

  //! Orientation measured anti-clockwise from +ve x-axis
  float orientation;

  //! Sign of laplacian for fast matching purposes
  int laplacian;

  //! Vector of descriptor components
  float descriptor[64];

  //! Placeholds for point motion (can be used for frame to frame motion analysis)
  float dx, dy;

  //! Used to store cluster index
  int clusterIndex;
};
*/

}
