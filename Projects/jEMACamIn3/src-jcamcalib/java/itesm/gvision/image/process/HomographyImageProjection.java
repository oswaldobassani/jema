/*
 * HomographyImageProjection.java
 *
 * Created on March 23, 2005, 8:24 PM
 */

package itesm.gvision.image.process;

import itesm.gvision.tools.PointDouble;

import java.awt.Point;
import java.awt.image.BufferedImage;

//----------------------------------------------------------------------------------
/**
 * Performs an image tranform using the homography projection
 * @author  hugo
 */
public class HomographyImageProjection 
{
	/** The homography operations */
	private HomographyTransform ht;

	/** Fisrt point in real world */
	private Point.Double p1;
	
	/** Second point in real world */
	private Point.Double p2;
	
	/** Scale 1 px = ? units in real world */
	private double scale;
	
	/** Original image */
	private BufferedImage imgOriginal;
		
	
	//----------------------------------------------------------------------------------
	/** 
	 * Creates a new instance of HomographyImageProjection 
	 * @param i2w Image to world homography matrix
	 * @param w2i World to image homography matrix
	 * @param original Image to project.
	 */
	public HomographyImageProjection(double i2w[], double w2i[], BufferedImage original)
	{
		this.imgOriginal = original;
		this.ht          = new HomographyTransform(w2i, i2w);
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Performs the homography image projection
	 * @param scale The scale 1px = n units in real world.
	 * @param x1, y1 Coordinates of the first point in real world.
	 * @param x2, y2 Coordinates of the second point in real world.
	 * @param origin The origin position 1=Left-Top 2=Rigth-Top 3=Rigth-Bottom 4=Left-Bottom
	 */
	public BufferedImage process(double scale, double x1, double y1, double x2, double y2, int origin)
	{
		this.p1 = new Point.Double(x1, y1);
		this.p2 = new Point.Double(x2, y2);
		this.scale = scale;
		
		int resultWidth = (int)Math.round(Math.abs((this.p1.x - this.p2.x) / this.scale));
		int resultHeight = (int)Math.round(Math.abs((this.p1.y - this.p2.y) / this.scale));
		BufferedImage res = null;

		try{
			res = new BufferedImage(resultWidth, resultHeight, BufferedImage.TYPE_INT_RGB);
		}
		catch (Exception e){
			return null;
		}
		
		int xxi = (int)Math.round(p1.x/this.scale);
		int yyi = (int)Math.round(p1.y/this.scale);
		for (int x=0, xx = xxi; x<resultWidth; x++, xx++){
			for (int y=0, yy = yyi; y<resultHeight; y++, yy++){
				PointDouble inimage = this.ht.world2Image(xx*this.scale,  yy*this.scale);
				if (inimage.x >=0 && inimage.x < this.imgOriginal.getWidth() && inimage.y >= 0 && inimage.y < this.imgOriginal.getHeight()){
					int rx = x, ry = y;
					switch (origin){
						case 1: rx = x; ry = y; break;
						case 2: rx = resultWidth-1-x; ry = y; break;
						case 3: rx = resultWidth-1-x; ry = resultHeight-1-y; break;
						case 4: rx = x; ry = resultHeight-1-y; break;
					}
					res.setRGB(rx, ry, this.imgOriginal.getRGB((int)inimage.x, (int)inimage.y));
				}
			}
		}
		return res;
	}
	
}
