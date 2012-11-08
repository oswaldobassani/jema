package color.util.mike.perry;

/**
 * RGB/YUV Pixel Conversion
 * 
 * @author Mike Perry
 */
public class MikePerryColorSpaceConversionUtil {

	/*
	 * OBS (*)
	 * Author: Keith Jack
	 * Book: "Video Demystified" (ISBN 1-878707-09-4). 
	 * In both these cases, you have to clamp the output values to keep them in the [0-255] range. Rumour has it that the valid range is actually a subset of [0-255] (I've seen an RGB range of [16-235] mentioned) but clamping the values into [0-255] seems to produce acceptable results to me.
	 */
	
	/*
	 * RGB to YUV Conversion (*)
	 */
	public void rgb2yuv(int R, int G, int B, int Y, int U, int V){
		/*int Cr, Cb;*/
		/**/Y  =      (int) ( (0.257 * R) + (0.504 * G) + (0.098 * B) + 16);
		/*Cr =*/ V =  (int) ( (0.439 * R) - (0.368 * G) - (0.071 * B) + 128);
		/*Cb =*/ U =  (int) (-(0.148 * R) - (0.291 * G) + (0.439 * B) + 128);
	}

	/*
	 * YUV to RGB Conversion (*)
	 */
	public void yuv2rgb(int Y, int U, int V, int R, int G, int B){
		B = (int) (1.164 * (Y - 16) + 0.000 * (V - 000) + 2.018 * (U - 128));
		G = (int) (1.164 * (Y - 16) - 0.813 * (V - 128) - 0.391 * (U - 128));
		R = (int) (1.164 * (Y - 16) + 1.596 * (V - 128) + 0.000 * (U - 000));
	}
	
	/*
	 * OBS (**)
	 * Julien (surname unknown)
	 */
	
	/*
	 * RGB to YUV Conversion (**)
	 */
	public void rgb2yu1v1(int R, int G, int B, int Y, int U1, int V1){
		Y  = (int) (0.299* R + 0.587 * G + 0.114 * B);
		U1 = (int) ((B-Y)* 0.565);
		V1 = (int) ((R-Y)* 0.713);
	}

	/*
	 * YUV to RGB Conversion (**)
	 */
	public void yu1v12rgb(int Y, int U1, int V1, int R, int G, int B){
		R = (int) (Y + 1.403 * V1);
		G = (int) (Y - 0.344 * U1 - 0.714 * V1);
		B = (int) (Y + 1.770 * U1);
	}
	
	/*
	 * OBS (***)
	 * Avery Lee
	 */
	
	/*
	 * (YCbCr) YUV to RGB Conversion (***)
	 */
	public void yCbCr2rgb(int Y, int Cb, int Cr, int R, int G, int B){
		R = (int) (Y + 1.402 * (Cr-128));
		G = (int) (Y - 0.34414 * (Cb-128) - 0.71414 * (Cr-128));
		B = (int) (Y + 1.772 * (Cb-128));
	}

}
