package com.threed.jpct.skeleton.util;

/**
 * A simple timer implementation, to count the time in seconds
 * from the last reset.
 * 
 * TODO: Use a timing source with a better granularity(like gage?).
 * 
 * @author mcgreevyj
 */

public class Timer
{
	protected double timestamp;
	
	public Timer()
	{
		this.timestamp = (double)System.currentTimeMillis()/1000d;
	}
	/**
	 * Returns the seconds that have elapsed since the last
	 * reset.
	 * 
	 * @return A double containing the seconds.
	 */
	public double getTime()
	{
		return ((double)System.currentTimeMillis()/1000d) - timestamp;
	}
	/**
	 * Resets the timer.
	 */
	public void reset()
	{
		timestamp = (double)System.currentTimeMillis()/1000d;
	}
}