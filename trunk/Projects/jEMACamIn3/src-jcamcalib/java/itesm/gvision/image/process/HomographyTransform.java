/*
 * HomographyTransform.java
 *
 * Created on June 4, 2004, 5:45 PM
 */

package itesm.gvision.image.process;

import itesm.gvision.tools.PointDouble;

//--------------------------------------------------------------------------------------------
/**
 * Encapsula las operaciones para la transformacino por homografia
 * @author  ortegahe
 */
public class HomographyTransform
{
	/** Matriz de homografia Mundo -> Imagen */
	private double homographyWrd2Img[] = new double[9];
	
	/** Matriz de homografia Imagen -> Mundo */
	private double homographyImg2Wrd[] = new double[9];
	
	//--------------------------------------------------------------------------------------------
	/** 
	 * Creates a new instance of HomographyTransform 
	 * @param hgw2i Matriz de homografia mundo->imagen (3x3) en forma de arreglo
	 * @param hgi2w Matriz de homografia imagen->mundo (3x3) en forma de arreglo
	 */
	public HomographyTransform(double hgw2i[], double hgi2w[]) 
	{
		System.arraycopy(hgw2i, 0, this.homographyWrd2Img, 0, 9);
		System.arraycopy(hgi2w, 0, this.homographyImg2Wrd, 0, 9);
	}
	
	//--------------------------------------------------------------------------------------------
	/**
	 * Multiplica la matriz 3x3 por el vecto 3x1
	 */
	private PointDouble matrixMult(double mtrx[], double vctr[])
	{
		double x = mtrx[0]*vctr[0] + mtrx[1]*vctr[1] + mtrx[2];
		double y = mtrx[3]*vctr[0] + mtrx[4]*vctr[1] + mtrx[5];
		double z = mtrx[6]*vctr[0] + mtrx[7]*vctr[1] + mtrx[8];
		return new PointDouble(x/z, y/z);
	}
	
	//--------------------------------------------------------------------------------------------
	/**
	 * Calcula la coordenada en la imagen dada la coordenada en el mundo real.
	 */
	public PointDouble world2Image(PointDouble pw)
	{
		double v[] = new double[] {pw.x, pw.y, 1};
		return matrixMult(homographyWrd2Img, v);
	}

	//--------------------------------------------------------------------------------------------
	/**
	 * Calcula la coordenada en la imagen dada la coordenada en el mundo real.
	 */
	public PointDouble world2Image(double x, double y)
	{
		return world2Image(new PointDouble(x, y));
	}
	
	//--------------------------------------------------------------------------------------------
	/**
	 * Calcula la coordenada en el mudno real dada la coordenada de la imagen.
	 */	
	public PointDouble image2World(PointDouble pi)
	{
		double v[] = new double[] {pi.x, pi.y, 1};
		return matrixMult(homographyImg2Wrd, v);		
	}

	//--------------------------------------------------------------------------------------------
	/**
	 * Calcula la coordenada en el mudno real dada la coordenada de la imagen.
	 */	
	public PointDouble image2World(double x, double y)
	{
		return image2World(new PointDouble(x, y));
	}
	
}
