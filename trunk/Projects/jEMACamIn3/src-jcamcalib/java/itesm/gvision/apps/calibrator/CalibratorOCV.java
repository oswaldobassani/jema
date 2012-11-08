/*
 * CalibratorOCV.java
 */

package itesm.gvision.apps.calibrator;

//----------------------------------------------------------------------------------
/**
 *
 * @author  hugo
 */
public class CalibratorOCV 
{
	
	/** Creates a new instance of CalibratorOCV */
	public CalibratorOCV() 
	{
		System.loadLibrary("ocvcalib");
		this.initOCVCalibrator();
	}

	//----------------------------------------------------------------------------------
	public native void initOCVCalibrator();
	public native double[] findCorners(int img[], int w, int h);
	public native void addCalibrationImage(int img[], int w, int h);
	public native boolean calibrate();
	public native double[] getResults();
	public native int[] getUndistortedImage(int img[], int w, int h);
	public native double[] getHomographyMatrix(double img[], double rw[], int np, int w, int h);
}
