package br.bassani.jmf.efeitos.bayer;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Effect;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;

public abstract class RGBBayerEffect implements Effect {

	protected Format inputFormat;
	protected Format outputFormat;
	protected Format[] inputFormats;
	protected Format[] outputFormats;
	
	public RGBBayerEffect(){
		/*
		 * RGBFormat(java.awt.Dimension size, int maxDataLength, java.lang.Class dataType, float frameRate,
		 * 		int bitsPerPixel, int red, int green, int blue, 
		 * 		int pixelStride, int lineStride, int flipped, int endian) 
		 */
		
		inputFormats = new Format[] {
				new RGBFormat(null,
						Format.NOT_SPECIFIED,
						Format.intArray,
						Format.NOT_SPECIFIED,
						32,
						0x00FF0000, 0x0000FF00, 0x000000FF,
						1, Format.NOT_SPECIFIED,
						Format.FALSE,
						RGBFormat.LITTLE_ENDIAN)
		};
		
		outputFormats = new Format[] {
				new RGBFormat(null,
						Format.NOT_SPECIFIED,
						Format.intArray,
						Format.NOT_SPECIFIED,
						32,
						0x00FF0000, 0x0000FF00, 0x000000FF,
						1, Format.NOT_SPECIFIED,
						Format.FALSE,
						RGBFormat.LITTLE_ENDIAN)
		};
	}
	
	//Utility methods.
	private Format matches(Format in, Format outs[]) {
		for (int i = 0; i < outs.length; i++) {
			if (in.matches(outs[i]))
				return outs[i];
		}
		return null;
	}
	
	//Effect methods.
	
	public Format[] getSupportedInputFormats() {
		return inputFormats;
	}
	
	public Format [] getSupportedOutputFormats(Format input) {
		if (input == null) {
			return outputFormats;
		}
		
		if (matches(input, inputFormats) != null) {
			return new Format[] { outputFormats[0].intersects(input) };
		} else {
			return new Format[0];
		}
	}
	
	public Format setInputFormat(Format input) {
		inputFormat = input;
		return inputFormat;
	}
	
	public Format setOutputFormat(Format output) {
		if (output == null || matches(output, outputFormats) == null)
			return null;
		RGBFormat incoming = (RGBFormat) output;
		
		Dimension size = incoming.getSize();
		int maxDataLength = incoming.getMaxDataLength();
		int lineStride = incoming.getLineStride();
		float frameRate = incoming.getFrameRate();
		int flipped = incoming.getFlipped();
		
		if (size == null)
			return null;
		if (maxDataLength < size.width * size.height)
			maxDataLength = size.width * size.height;
		if (lineStride < size.width)
			lineStride = size.width;
		if (flipped != Format.FALSE) flipped = Format.FALSE;
		
		outputFormat = outputFormats[0].intersects(new RGBFormat(size,
				maxDataLength,
				null,
				frameRate,
				Format.NOT_SPECIFIED,
				Format.NOT_SPECIFIED,
				Format.NOT_SPECIFIED,
				Format.NOT_SPECIFIED,
				Format.NOT_SPECIFIED,
				lineStride,
				Format.NOT_SPECIFIED,
				Format.NOT_SPECIFIED));
		
		//System.out.println("final outputformat = " + outputFormat);
		return outputFormat;
	}
	
	
	public int[] validateIntArraySize(Buffer buffer,int newSize) {
		Object objectArray=buffer.getData();
		int[] typedArray;
		
		if (objectArray instanceof int[]) {     // is correct type AND not null
			typedArray=(int[])objectArray;
			if (typedArray.length >= newSize ) { // is sufficient capacity
				return typedArray;
			}
			
			int[] tempArray=new int[newSize];  // re-alloc array
			System.arraycopy(typedArray,0,tempArray,0,typedArray.length);
			typedArray = tempArray;
		} else {
			typedArray = new int[newSize];
		}
		
		buffer.setData(typedArray);
		return typedArray;
	}
	
	public static final int int2red(int val){
		return (val&0x00FF0000)>>16;
	}
	
	public static final int int2green(int val){
		return (val&0x0000FF00)>>8;
	}
	
	public static final int int2blue(int val){
		return (val&0x000000FF)>>0;
	}
	
	public abstract int process(Buffer inBuffer, Buffer outBuffer);

	public abstract String getName();

	public void open() throws ResourceUnavailableException {
		// TODO Auto-generated method stub
	}

	public void close() {
		// TODO Auto-generated method stub
	}

	public void reset() {
		// TODO Auto-generated method stub
	}

	public Object[] getControls() {
		return (Object[]) new Control[0];
	}
	
	public Object getControl(String controlType) {
		try {
			Class cls = Class.forName(controlType);
			Object cs[] = getControls();
			for (int i = 0; i < cs.length; i++) {
				if (cls.isInstance(cs[i]))
					return cs[i];
			}
			return null;
		} catch (Exception e) { // no such controlType or such control
			return null;
		}
	}

}
