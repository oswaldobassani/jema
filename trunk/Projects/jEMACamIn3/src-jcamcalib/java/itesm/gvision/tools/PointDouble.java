/*
 * PointDouble.java
 *
 * Created on March 3, 2004, 12:11 AM
 */

package itesm.gvision.tools;

//----------------------------------------------------------------------------------
/**
 * Un punto de coordenadas doubles.
 * @author  hugo
 */
public class PointDouble 
{
	public double x;
	public double y;
	
	
	//----------------------------------------------------------------------------------
	/** Creates a new instance of PointDouble */
	public PointDouble(double x, double y) 
	{
		this.x = x;
		this.y = y;
	}
	
	//----------------------------------------------------------------------------------
	public PointDouble() 
	{
		this.x = 0;
		this.y = 0;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Obtiene el valor de x
	 */
	public double getX()
	{
		return x;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Obtiene el valor de y
	 */
	public double getY()
	{
		return y;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Redefine el metodo toString
	 */
	public String toString()
	{
		return this.getClass().getName() + " [" + x + ", " + y + "]";
	}
}
