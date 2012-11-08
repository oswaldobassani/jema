/*
 * CalibratorImagePanel.java
 */

package itesm.gvision.apps.calibrator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Vector;

//----------------------------------------------------------------------------------
/**
 * Shows an image and calibration points.
 * @author  hugo
 */
public class CalibratorImagePanel extends javax.swing.JComponent 
{
	/** Imagen actual */
	BufferedImage image;
	
	/** Puntos localizados */
	Vector points = new Vector();
	
	
	/** Creates a new instance of CalibratorImagePanel */
	public CalibratorImagePanel() {
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Set the image to paint
	 */
	public void setImage(BufferedImage img)
	{
		this.image = img; // as a copy
		this.setSize(img.getWidth(),  img.getHeight());
		this.repaint();
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Add a new calibration point
	 */
	public void addPoint(Point p)
	{
		this.points.add(p);
	}

	//----------------------------------------------------------------------------------
	/**
	 * Add a new calibration point
	 */
	public void addPoint(int x, int y)
	{
		this.points.add(new Point(x,y));
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Clear all points
	 */
	public void clearPoints()
	{
		this.points.clear();
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Redefine paint method
	 */
	public void paint(Graphics g)
	{
		int sz = 5;
		if (this.image != null){
			g.drawImage(this.image, 0, 0, this);
			g.setColor(Color.RED);
			for(int i=0; i<this.points.size(); i++) {
				Point p = (Point)this.points.get(i);
				g.fillOval(p.x-sz/2, p.y-sz/2, sz, sz);
			}
		}
	}
	
}
