package com.threed.jpct.skeleton;


/**
 * Encapsulates a rotation keyframe.
 * A matrix is used to store the rotation.
 * 
 * @author mcgreevyj
 */

public class RotationKeyframe
{
	//public float[] rotation = new float[3];
	public Quaternion quat;
	public float  time;
}