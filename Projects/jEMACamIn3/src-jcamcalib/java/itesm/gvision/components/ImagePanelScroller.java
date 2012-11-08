/*
 * ImagePanelScroller.java
 *
 * Created on March 24, 2005, 3:02 PM
 */

//----------------------------------------------------------------------------------
package itesm.gvision.components;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

//----------------------------------------------------------------------------------
/**
 * A scrollable image display control.
 * @author  hugo
 */
public class ImagePanelScroller extends JScrollPane
{
	private BufferedImage image;
	
	//----------------------------------------------------------------------------------
	/** Creates a new instance of ImagePanelScroller */
	public ImagePanelScroller()
	{
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Sets the image to display into the control.
	 * @param img The buffered image
	 */
	public void setImage(BufferedImage img)
	{
		this.image = img;
		this.getViewport().removeAll();
		ImageIcon icon = new ImageIcon(img);
		JPanel p = new JPanel(new BorderLayout());
		p.add(new JLabel(icon), BorderLayout.CENTER);
		this.getViewport().add(p);
	}
	
	//----------------------------------------------------------------------------------
	/** 
	 * Gets the current display image.
	 */
	public BufferedImage getImage()
	{
		return this.image;
	}
}

