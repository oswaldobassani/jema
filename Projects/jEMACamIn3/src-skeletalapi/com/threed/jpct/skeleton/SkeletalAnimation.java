package com.threed.jpct.skeleton;

/**
 * Stores some information about a particular animation sequence.
 * This includes it's name, the length, and whether or not it should
 * be looped.
 * 
 * @author mcgreevyj
 */

public class SkeletalAnimation
{
	protected String name;
	protected float  length;
	protected boolean looping = true;

	/**
	 * Sets the name of the animation.
	 * 
	 * @param name	The name of the animation sequence.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the length, in seconds, of the animation
	 * sequence.
	 * 
	 * @param length	Length of the animation.
	 */
	public void setLength(float length)
	{
		this.length = length;
	}
	
	/*
	 * Extras
	 */

	public String getName() {
		return name;
	}

	public float getLength() {
		return length;
	}

	public boolean isLooping() {
		return looping;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}
	
}