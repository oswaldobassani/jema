/*---------------------------------------------------------------------------------------
 
ITESM - Campus Monterrey
Maestria en Sistemas Inteligentes
 
Proyecto de Tesis : Seguimiento Visual de Multiples Objetos con Camara Movil en Ambientes Dinamicos
Creado por        : Hugo Ortega Hernandez - hugorteg@yahoo.com
Fecha             : 15/02/2004
 
-----------------------------------------------------------------------------------------*/

package itesm.gvision.tools;

import java.io.File;

import javax.swing.ImageIcon;

//----------------------------------------------------------------------------------
/**
 * Utils.java is a 1.4 example used by FileChooserDemo2.java.
 */
public class Utils 
{
	public final static String jpeg = "jpeg";
	public final static String jpg  = "jpg";
	public final static String gif  = "gif";
	public final static String tiff = "tiff";
	public final static String tif  = "tif";
	public final static String png  = "png";
	
	//----------------------------------------------------------------------------------
	/**
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) 
	{
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		
		if (i > 0 &&  i < s.length() - 1){
			ext = s.substring(i+1).toLowerCase();
		}
		return ext;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 */
	protected static ImageIcon createImageIcon(String path) 
	{
		java.net.URL imgURL = Utils.class.getResource(path);
		if (imgURL != null){
			return new ImageIcon(imgURL);
		}
		else{
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Obtiene el nombre del archivo de un directorio/archivo completo.
	 */
	public static String getFileFromPath(String p) 
	{
		int last = p.lastIndexOf(File.separatorChar);
		if (last != -1){
			return p.substring(last+1);
		}
		else{
			return p;
		}
	}
	
	//----------------------------------------------------------------------------------
	/**
	 *
	 */
	public static String getDirFromPath(String p) 
	{
		int last = p.lastIndexOf(File.separatorChar);
		if (last != -1){
			return p.substring(0, last);
		}
		else{
			return p;
		}
	}
}
