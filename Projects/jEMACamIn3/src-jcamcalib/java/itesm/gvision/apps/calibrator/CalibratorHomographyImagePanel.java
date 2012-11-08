/*
 * CalibratorHomographyImagePanel.java
 */

package itesm.gvision.apps.calibrator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Vector;

//----------------------------------------------------------------------------------
/**
 * Component for drawing homography points.
 * @author  hugo
 */
public class CalibratorHomographyImagePanel extends javax.swing.JComponent 
{
	/** Current image */
	BufferedImage image;
	
	/** Mark points */
	Vector points = new Vector();
	
	/** Selected point */
	int selectedPoint;
	
	//----------------------------------------------------------------------------------
	/** Creates a new instance of CalibratorHomographyImagePanel */
	public CalibratorHomographyImagePanel() 
	{
		this.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
		//this.setSize(this.getParent().getWidth(), this.getParent().getHeight());
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Sets the index of selected point
	 */
	public void setSelectedPoint(int id)
	{
		this.selectedPoint = id;
		this.repaint();
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Gets the id of selected point
	 */
	public int getSelectedPoint()
	{
		return this.selectedPoint;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Set the image to paint
	 */
	public void setImage(BufferedImage img)
	{
		this.image = img;
		this.setSize(img.getWidth(),  img.getHeight());
		this.repaint();
	}

	//----------------------------------------------------------------------------------
	/**
	 * Get the current image
	 */
	public BufferedImage getImage()
	{
		return this.image;
	}
	
	//----------------------------------------------------------------------------------
	/** Set the points to paint
	 */
	public void setPoints(Vector p)
	{
		this.points = p;
		this.repaint();
	}

	//----------------------------------------------------------------------------------
	/** Get the points 
	 */
	public Vector getPoints()
	{
		return this.points;
	}
	
	//----------------------------------------------------------------------------------
	/** Add a new point
	 */
	public void addPoint(Point p)
	{
		this.points.add(p);
		this.selectedPoint = this.points.size()-1;
		this.repaint();
	}
	
	//----------------------------------------------------------------------------------
	/** Detele the specified point
	 */
	public void deletePoint(int id)
	{
		this.points.remove(id);
		this.repaint();
	}
	
	//----------------------------------------------------------------------------------
	/** Clear all points
	 */
	public void clearPoints()
	{
		this.points.clear();
	}
	
	//----------------------------------------------------------------------------------
	/** Search for the nearest point to p
	 */
	public void selectNearestPoint(Point p)
	{
		int idx = 0;
		double min = 10000;
		for(int i=0; i<this.points.size(); i++){
			Point current = (Point)this.points.get(i);
			if (current.distance(p) < min){
				min = current.distance(p);
				idx = i;
			}
		}
		this.selectedPoint = idx;
		this.repaint();
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Redefine paint method
	 */
	public void paint(Graphics g)
	{
		int sz = 3;
		if (this.image != null){
			g.drawImage(this.image, 0, 0, this);
			for(int i=0; i<this.points.size(); i++){
				g.setColor(this.selectedPoint == i ? Color.GREEN : Color.RED);
				Point p = (Point)this.points.get(i);
				sz = 3;
				g.drawOval(p.x-sz/2, p.y-sz/2, sz, sz);
				sz = 7;
				g.drawOval(p.x-sz/2, p.y-sz/2, sz, sz);
			}
		}
		else{
			g.drawString("You need to load an undistorted reference image first...", 50, 50);
		}
	}

}
