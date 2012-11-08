/*
 * ImageTools.java
 *
 * Created on 21 de marzo de 2003, 12:39 PM
 */

package itesm.gvision.image.tools;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

//--------------------------------------------------------------------------------------------
/**
 * Algunas utilerias para manipulacion de imagenes.
 */
public class ImageTools 
{
	/** Creates new ImageTools */
	public ImageTools() {
	}
	
	//--------------------------------------------------------------------------------------------
	/**
	 * Obtiene un arreglo con los pixeles de una imagen 256 gray level.
	 * @return Arreglo de bytes con el contenido del Buffer
	 */
	public static byte[] getImageDataByte(BufferedImage img) 
	{
		byte imageData[] = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
		byte returnData[] = new byte[imageData.length];
		System.arraycopy(imageData, 0, returnData, 0, imageData.length);
		return returnData;
	}
	
	//--------------------------------------------------------------------------------------------
	/**
	 * Obtiene un arreglo con los pixeles de una imagen RGB
	 * @return Arreglo de int con el contenido del Buffer
	 */
	public static int[] getImageDataRGB(BufferedImage img) 
	{
		int imageData[] = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
		int returnData[] = new int[imageData.length];
		System.arraycopy(imageData, 0, returnData, 0, imageData.length);
		return returnData;
	}
	
	//--------------------------------------------------------------------------------------------
	/**
	 * Obtiene un BufferedImage con los bytes de data, y de tama�o w*h.
	 * @return Buffer contruido con los datos del arreglo en esclaa de grises.
	 */
	public static BufferedImage getBufferedImage(byte[] data, int w, int h) 
	{
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		byte imageData[] = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
		System.arraycopy(data, 0, imageData, 0, data.length);
		return bi;
	}
	
	//--------------------------------------------------------------------------------------------
	/**
	 * Obtiene un BufferedImage con los valores enteros de data, y de tama�o w*h.
	 * @return Buffer contruido con los datos del arreglo en color.
	 */
	public static BufferedImage getBufferedImage(int[] data, int w, int h) 
	{
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		int imageData[] = ((DataBufferInt)bi.getRaster().getDataBuffer()).getData();
		System.arraycopy(data, 0, imageData, 0, data.length);
		return bi;
	}
	
	//--------------------------------------------------------------------------------------------
	/**
	 * Convierte un buffer de RGB a gray scale.
	 */
	public static BufferedImage convertRGBtoGrayScale(BufferedImage imgRGB)
	{
		if (imgRGB.getType() == BufferedImage.TYPE_BYTE_GRAY) return imgRGB;
		BufferedImage bgray = new BufferedImage(imgRGB.getWidth(), imgRGB.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D big2d = bgray.createGraphics();
		big2d.drawImage(imgRGB, 0, 0, null);		
		return bgray;
	}	
	
	//--------------------------------------------------------------------------------------------
	/**
	 * Convierte un buffer de Gray scale a RGB.
	 */
	public static BufferedImage convertGrayScaletoRGB(BufferedImage imggray)
	{
		if (imggray.getType() == BufferedImage.TYPE_INT_RGB) return imggray;
		BufferedImage brgb = new BufferedImage(imggray.getWidth(), imggray.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D big2d = brgb.createGraphics();
		big2d.drawImage(imggray, 0, 0, null);		
		return brgb;
	}
	
	//--------------------------------------------------------------------------------------------
	/**
	 * Obtiene una version ampliada de una imagen (sin interpolaci�n ni nada por el estilo)
	 * Si los valores de subimg sobrepasan los limites de la imagen se ajusta automaticamente.
	 */
	public static BufferedImage getZoomedImage(BufferedImage img, Rectangle subimg, int factor)
	{
		BufferedImage res = new BufferedImage((int)(subimg.getWidth()*factor), (int)(subimg.getHeight()*factor), 
		                                      BufferedImage.TYPE_INT_RGB);
		
		//System.out.println(subimg);
		int xini = subimg.x;
		int xfin = subimg.x + subimg.width; 
		int yini = subimg.y;
		int yfin = subimg.y + subimg.height;
		
		if (subimg.x < 0){
			xini = 0;
			xfin = subimg.width;
		}
		else if (subimg.x + subimg.width > img.getWidth()){
			xini = img.getWidth() - subimg.width;
			xfin = img.getWidth();
		}
		if (subimg.y < 0){
			yini = 0;
			yfin = subimg.height;
		}
		else if (subimg.y + subimg.height > img.getHeight()){
			yini = img.getHeight() - subimg.height;
			yfin = img.getHeight();
		}
		
		//System.out.println(xini + ", " + xfin + ", " + yini + ", " + yfin);
		//int xini = subimg.x < 0 ? 0 : subimg.x;
		//int xfin = subimg.x+subimg.width > img.getWidth() ? img.getWidth() : subimg.x+subimg.width;
		//int yini = subimg.y < 0 ? 0 : subimg.y;
		//int yfin = subimg.y+subimg.height > img.getHeight() ? img.getHeight() : subimg.y+subimg.height;
		for (int x=xini, xf=0; x<xfin; x++, xf++){
			for (int y=yini, yf=0; y<yfin; y++, yf++){
				int color = img.getRGB(x, y);
				for (int i=xf*factor; i<xf*factor+factor; i++){
					for (int j=yf*factor; j<yf*factor+factor; j++){
						//System.out.println("i = " + i + ", j = " + j);
						res.setRGB(i, j, color);
					}
				}
			}
		}
		return res;
	}
}
