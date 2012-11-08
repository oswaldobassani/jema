package com.threed.jpct.skeleton;


/**
 * Encapsulates a translation keyframe.
 * The translation is stored in a vector.
 * 
 * @author mcgreevyj
 */

public class TranslationKeyframe
{
	public float time;
	public float[] translation = new float[3];
	
	/**
	 * Sets the translation vector of the keyframe.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setTranslation(float x,float y,float z)
	{
		this.translation[0] = x;
		this.translation[1] = y;
		this.translation[2] = z;
	}
}
