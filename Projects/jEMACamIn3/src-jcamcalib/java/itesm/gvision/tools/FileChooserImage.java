/*---------------------------------------------------------------------------------------

ITESM - Campus Monterrey
Maestria en Sistemas Inteligentes

Proyecto de Tesis : Seguimiento Visual de Multiples Objetos con Camara Movil en Ambientes Dinamicos
Creado por        : Hugo Ortega Hernandez - hugorteg@yahoo.com
Fecha             : 15/02/2004

-----------------------------------------------------------------------------------------*/

package itesm.gvision.tools;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

//----------------------------------------------------------------------------------
/**
 * Cuadro de dialogo para seleccionar imagenes. Proporciona filtros adecuados y
 * un preview de los archivos.
 * @author  hugo
 */
public class FileChooserImage extends javax.swing.JFileChooser
{
	
	/** Creates a new instance of FileChooserImage */
	public FileChooserImage()
	{
            this.addChoosableFileFilter(new ImageFilter());
            this.setAcceptAllFileFilterUsed(false);
            this.setAccessory(new ImagePreview(this));
	}
	
}

//----------------------------------------------------------------------------------
/** 
 * ImagePreview is a 1.4 example used by FileChooserDemo2.java. 
 */
class ImagePreview extends JComponent implements PropertyChangeListener 
{
	ImageIcon thumbnail = null;
	File file = null;
	
	public ImagePreview(JFileChooser fc) 
	{
		setPreferredSize(new Dimension(110, 60));
		fc.addPropertyChangeListener(this);
		this.setBorder(new javax.swing.border.TitledBorder("Image preview"));
	}
	
	public void loadImage() 
	{
		if (file == null) {
			thumbnail = null;
			return;
		}	
		//Don't use createImageIcon (which is a wrapper for getResource)
		//because the image we're trying to load is probably not one
		//of this program's own resources.
		ImageIcon tmpIcon = new ImageIcon(file.getPath());
		if (tmpIcon != null){
			if (tmpIcon.getIconWidth() > 90){
				thumbnail = new ImageIcon(tmpIcon.getImage().
				getScaledInstance(100, -1, Image.SCALE_DEFAULT));
			}
			else{ //no need to miniaturize
				thumbnail = tmpIcon;
			}
		}
	}
	
	public void propertyChange(PropertyChangeEvent e) 
	{
		boolean update = false;
		String prop = e.getPropertyName();
		
		//If the directory changed, don't show an image.
		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)){
			file = null;
			update = true;
			
			//If a file became selected, find out which one.
		}
		else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)){
			file = (File) e.getNewValue();
			update = true;
		}
		
		//Update the preview accordingly.
		if (update){
			thumbnail = null;
			if (isShowing()){
				loadImage();
				repaint();
			}
		}
	}
	
	public void paintComponent(Graphics g) 
	{
		if (thumbnail == null){
			loadImage();
		}
		if (thumbnail != null){
			int x = getWidth()/2 - thumbnail.getIconWidth()/2;
			int y = getHeight()/2 - thumbnail.getIconHeight()/2;
			if (y < 0) {
				y = 0;
			}
			if (x < 5) {
				x = 5;
			}
			thumbnail.paintIcon(this, g, x, y);
		}
	}
}

//----------------------------------------------------------------------------------
/** 
 * ImageFilter.java is a 1.4 example used by FileChooserDemo2.java. 
 */
class ImageFilter extends FileFilter 
{
	/** Accept all directories and all gif, jpg, tiff, or png files. */
	public boolean accept(File f) 
	{
		if (f.isDirectory()){
			return true;
		}	
		String extension = Utils.getExtension(f);
		if (extension != null){
			/*if (extension.equals(Utils.tiff) ||
			extension.equals(Utils.tif) ||
			extension.equals(Utils.gif) ||
			extension.equals(Utils.jpeg) ||
			extension.equals(Utils.jpg) ||
			extension.equals(Utils.png)) {*/
			if (extension.toLowerCase().equals(Utils.jpeg) || extension.toLowerCase().equals(Utils.jpg)){
				return true;
			} 
			else{
				return false;
			}
		}
		return false;
	}
	
	/** The description of this filter */
	public String getDescription() 
	{
		return "JPEG Images";
	}
}
