package com.threed.jpct.skeleton.util;

import com.threed.jpct.Matrix;
import com.threed.jpct.SimpleVector;

/**
 * Provides some useful utility methods.
 * 
 * @author mcgreevyj
 */

public class SkeletalUtils
{
	/**
	 * Extracts an angle vector from a rotation matrix.
	 * 
	 * @param mat	matrix to extract from
	 * @return		SimpleVector containing angles.
	 */
	public static SimpleVector deriveAngles(Matrix mat)
	{
	    SimpleVector s = new SimpleVector();
	    float[]      m = mat.getDump();
	    
	    s.x = (float)Math.atan(m[9]/m[10]);
	    s.y = (float)Math.asin(-m[2]);
	    s.z = (float)Math.atan(m[4]/m[0]);
	    
	    return s;
	} 
}
