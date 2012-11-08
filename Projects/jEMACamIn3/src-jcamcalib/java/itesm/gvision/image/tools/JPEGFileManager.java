/*
 * JPEGFileManager.java
 *
 * Created on 21 de marzo de 2003, 12:37 AM
 */

package itesm.gvision.image.tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

//----------------------------------------------------------------------------------
/**
 * Utilidades para abrir y guardar jpeg files desde y hacia BufferedImage.
 */
public class JPEGFileManager 
{
	//----------------------------------------------------------------------------------
	/**
	 * Abre un JPEG y regresa un BufferedImage conteniendo la imagen en el formato de color original.
	 * @param filename El nombre del archivo
	 * @return El Buffer con la imagen.
	 */
	public static BufferedImage openFile(String filename) 
	{
		BufferedImage jpegbim;
		FileInputStream ifs;
		JPEGImageDecoder jid;
		
		jpegbim = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
		
		try{
			ifs = new FileInputStream(filename);
		}
		catch(FileNotFoundException e){
			System.out.println("JPEGFileManager :: openFile :: File not found : " + filename + "...");
			return null;
		}
		
		jid = JPEGCodec.createJPEGDecoder(ifs);
		
		try{
			jpegbim = jid.decodeAsBufferedImage();
		}
		catch(java.io.IOException e){
			System.out.println("JPEGFileManager :: openFile :: io exception : " + filename + "...");
			System.exit(0);
			return null;
		}
		
		return jpegbim;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Abre un JPEG y regresa un BufferedImage conteniendo la imagen en escala de grises.
	 * @param filename El nombre del archivo
	 * @return El Buffer con la imagen en escala de grises.
	 */
	public static BufferedImage openFileAsGrayScale(String filename) 
	{
		BufferedImage bim, jpegbim;
		Graphics2D big2d;
		FileInputStream ifs;
		JPEGImageDecoder jid;
		
		jpegbim = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
		
		try{
			ifs = new FileInputStream(filename);
		}
		catch(FileNotFoundException e){
			System.out.println("JPEGFileManager :: openFile :: File not found : " + filename + "...");
			return null;
		}
		
		jid = JPEGCodec.createJPEGDecoder(ifs);
		
		try{
			jpegbim = jid.decodeAsBufferedImage();
		}
		catch(java.io.IOException e){
			System.out.println("JPEGFileManager :: openFile :: io exception : " + filename + "...");
			System.exit(0);
			return null;
		}
		
		int width = jpegbim.getWidth();
		int height = jpegbim.getHeight();
		
		bim = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		big2d = bim.createGraphics();
		
		big2d.drawImage(jpegbim, 0, 0, null);
		
		return bim;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Guarda un BufferedImage en un archivo JPEG
	 * @param img El Buffer con la imagen.
	 * @param file El nombre del archivo.
	 * @param q La calidad (0..100) con que se guardara la imagen.
	 * @return true si se guardo con exito la imagen o false en caso contrario.
	 */
	public static boolean saveFile(BufferedImage img, String file, int q) 
	{
		BufferedOutputStream out;
		JPEGImageEncoder encoder;
		try{
			out = new BufferedOutputStream(new FileOutputStream(file));
		}
		catch(FileNotFoundException e){
			System.err.println("JPEGFileManager :: saveFile :: Imposible crear el archivo " + file);
			e.printStackTrace();
			return false;
		}
		encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
		int quality = q;
		quality = Math.max(0, Math.min(quality, 100));
		param.setQuality((float)quality / 100.0f, false);
		
		// Metodo nao mais visivel.
		//encoder.setJPEGEncodeParam(param);
		
		try{
			encoder.encode(img);
			return true;
		}
		catch(Exception e){
			System.err.println("JPEGFileManager :: saveFile :: Error al escribir el archivo " + file);
			e.printStackTrace();
			return false;
		}
	}
}