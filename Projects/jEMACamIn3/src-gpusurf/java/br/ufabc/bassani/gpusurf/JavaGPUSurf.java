package br.ufabc.bassani.gpusurf;

public class JavaGPUSurf {

	static {
		System.loadLibrary ("javagpusurf");
	}
	
	public static final String RGBA = "RGBA";
	public static final String RGB = "RGB";
	public static final String BGRA = "BGRA";
	public static final String BGR = "BGR";
	public static final String I = "I";

	public JavaGPUSurf () {
		System.out.println ("JavaGPUSurf: constructor (Java-GPUSurf binding)");
	}

	/**
	 * 
	 * @param w
	 * @param h
	 * @return
	 */
	public native void init(int w, int h);
	
	/**
	 * 
	 * @param format
	 * @param imgData
	 * @param w
	 * @param h
	 * @param threshold
	 * @return
	 */
	public native float[] getFeatures(String format, char[] imgData,float threshold);
	
}
