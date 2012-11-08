/*
 * PointImageWorld.java
 */

package itesm.gvision.apps.calibrator;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;

//----------------------------------------------------------------------------------
/**
 * Join an image point with a real world point
 * @author  hugo
 */
public class PointImageWorld 
{
	private Point2D imagePoint = new Point2D.Double();
	
	private Point2D worldPoint = new Point2D.Double();
	
	private DecimalFormat fmt = new DecimalFormat("0.000");
	
	//----------------------------------------------------------------------------------
	/** Creates a new instance of PointImageWorld */
	public PointImageWorld(double ix, double iy, double wx, double wy) 
	{
		this.imagePoint.setLocation(ix, iy);
		this.worldPoint.setLocation(wx, wy);
	}
	
	//----------------------------------------------------------------------------------
	/**
	 */
	 public void setImagePoint(double x, double y)
	 {
		 this.imagePoint.setLocation(x, y);
	 }
	
 	//----------------------------------------------------------------------------------
	/**
	 */
	public void setWorldPoint(double x, double y)
	{
		this.worldPoint.setLocation(x, y);
	}
	 
	//----------------------------------------------------------------------------------
	/**
	 */
	 public double getImageX()
	 {
		 return this.imagePoint.getX();
	 }
	 
	//----------------------------------------------------------------------------------
	/**
	 */
	 public double getImageY()
	 {
		 return this.imagePoint.getY();
	 }
	
	//----------------------------------------------------------------------------------
	/**
	 */
	 public double getWorldX()
	 {
		 return this.worldPoint.getX();
	 }
	 
	//----------------------------------------------------------------------------------
	/**
	 */
	 public double getWorldY()
	 {
		 return this.worldPoint.getY();
	 }

	//----------------------------------------------------------------------------------
	/**
	 */
	 public Point2D getImagePoint()
	 {
		 return this.imagePoint;
	 }
	 
	//----------------------------------------------------------------------------------
	/**
	 */
	public Point2D getWorldPoint()
	{
		return this.worldPoint;
	}
	 
	 //----------------------------------------------------------------------------------
	/**
	 */
	public String toString() 
	{
		return "(" + fmt.format(this.imagePoint.getX()) + ", " + fmt.format(this.imagePoint.getY()) + ") ==> (" 
		           + fmt.format(this.worldPoint.getX()) + ", " + fmt.format(this.worldPoint.getY()) + ")";
	}
	
	
}
