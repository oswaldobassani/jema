package com.threed.jpct.skeleton;

import java.util.Hashtable;

/**
 * Provides a simple implementation of a bone.
 * 
 * A bone has two unique identifiers - id and name.
 * The local rotations and translations are stored, along
 * with the bones keyframes, and parent/child references.
 * 
 * @author mcgreevyj
 */

public class SimpleBone
{
	//Some unique identifiers.
	protected int    id;
	protected String name;
	//Matrices to store relative/absolute translation/rotation.
	public Matrix matrixAbsolute = new Matrix(),matrixRelative = new Matrix(),matrixFinal = new Matrix();
	public Matrix rotationAbsolute = new Matrix();
	public Matrix rotationFinal    = new Matrix();
	//The local translations/rotations from model file.
	protected float[] localTranslation  = new float[3];
	protected Quaternion localRotation;
	//Array of keyframes for this bone.
	protected RotationKeyframe[][]    rotationKeyframes;
	protected TranslationKeyframe[][] translationKeyframes;
	//Reference to parent bone, and child bones, if any.
	protected SimpleBone parent;
	protected Hashtable<Integer,SimpleBone> children = new Hashtable<Integer,SimpleBone>(5);
	//Keyframe counters.
	protected int currentRotationKeyframe = 0,currentTranslationKeyframe = 0;
		
	public SimpleBone(int id,String name)
	{
		this.id   = id;
		this.name = name;
	}
	/**
	 * Returns the final transformation vector of the bone.
	 * 
	 * @return SimpleVector containing the final transformation
	 */
//	public SimpleVector getFinalTransform()
//	{
//		return matrixFinal;
//	}
	/**
	 * Returns the absolute transformation of the bone.
	 * This is the sum of all parent bones, and the relative
	 * transformation of the bone.
	 * 
	 * @return SimpleVector containing the absolute translation.
	 */
//	public SimpleVector getAbsoluteTransform()
//	{
//		return matrixAbsolute;
//	}
	/**
	 * Sets the local translation vector for this bone.
	 * 
	 * @param x	the x translation
	 * @param y	the y translation
	 * @param z	the z translation
	 */
	public void setLocalTranslation(float x,float y,float z)
	{
		this.localTranslation[0] = x;
		this.localTranslation[1] = y;
		this.localTranslation[2]= z;
	}
	/**
	 * Sets the local rotation axis of the bone.
	 * This axis is used in conjunction with the rotation angle,
	 * to get a local rotation matrix.
	 * 
	 * @param x	the x axis rotation
	 * @param y	the y axis rotation
	 * @param z	the z axis rotation
	 */
	public void setLocalRotation(float x,float y,float z,float angle)
	{
		this.localRotation = new Quaternion(x,y,z,angle);
	}
	/**
	 * Sets the local rotation angle of the bone.
	 * This angle is used in conjunction with the local rotation
	 * axis to create a local rotation matrix.
	 * @param angle
	 */
//	public void setLocalRotationAngle(float angle)
//	{
//		this.localRotationAngle = angle;
//	}
	/**
	 * Sets the translation, and rotation keyframes for this bone.
	 * 
	 * @param i	the animation index to add keyframes to
	 * @param rotationKeyframes	the rotation keyframes
	 * @param translationKeyframes	the translation keyframes
	 */
	public void setKeyframes(int i,RotationKeyframe[] rotationKeyframes,
			                 TranslationKeyframe[] translationKeyframes)
	{
		this.rotationKeyframes[i]    = rotationKeyframes;
		this.translationKeyframes[i] = translationKeyframes;
	}
	/**
	 * Initializes the keyframe arrays, to support the maximum
	 * animation size.
	 * 
	 * @param max	the number of animations to cater for.
	 */
	public void setMaxAnimations(int max)
	{
		rotationKeyframes    = new RotationKeyframe[max][];
		translationKeyframes = new TranslationKeyframe[max][];
	}
	/**
	 * Sets the parent bone of this bone.
	 * The bone will inherit all transformations from
	 * the parent bone.
	 * 
	 * @param parent	The parent bone
	 */
	public void setParent(SimpleBone parent)
	{
		this.parent = parent;
	}
	/**
	 * Adds a child reference to the bone.
	 * This should not be called directly - it is called
	 * by setParent on the child bone.
	 * 
	 * @param child	The child bone to add a reference to
	 */
	public void addChild(SimpleBone child)
	{
		if(!children.containsKey((Integer)child.id))
		{
			children.put((Integer)child.id,child);
			child.parent = this;
		}
	}
	/**
	 * Returns the parent bone.
	 * 
	 * @return The parent bone.
	 */
	public SimpleBone getParent()
	{
		return parent;
	}
	/**
	 * Returns a hashtable containing all of the bone's children.
	 * The hashtable uses the bone ids as a key.
	 * 
	 * @return	hashtable containing children.
	 */
	public Hashtable<Integer,SimpleBone> getChildren()
	{
		return children;
	}
	
	/*
	 * Extra
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * Extra
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id+"-"+name;
	}
}