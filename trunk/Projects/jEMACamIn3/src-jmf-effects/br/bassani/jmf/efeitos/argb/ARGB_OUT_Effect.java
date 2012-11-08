package br.bassani.jmf.efeitos.argb;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Effect;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;

public abstract class ARGB_OUT_Effect implements Effect {

	protected Format inputFormat;
	protected Format outputFormat;
	protected Format[] inputFormats;
	protected Format[] outputFormats;
	
	public ARGB_OUT_Effect(){
		/*
		 * RGBFormat(java.awt.Dimension size, int maxDataLength, java.lang.Class dataType, float frameRate,
		 * 		int bitsPerPixel, int red, int green, int blue, 
		 * 		int pixelStride, int lineStride, int flipped, int endian) 
		 */
		
		inputFormats = new Format[] {
				new RGBFormat(null,
						Format.NOT_SPECIFIED,
						Format.byteArray,
						Format.NOT_SPECIFIED,
						24,
						3, 2, 1,
						3, Format.NOT_SPECIFIED,
						Format.TRUE,
						Format.NOT_SPECIFIED)
		};
		
		outputFormats = new Format[] {
				new RGBFormat(null,
						Format.NOT_SPECIFIED,
						Format.byteArray,
						Format.NOT_SPECIFIED,
						32,
						3, 2, 1,
						4, Format.NOT_SPECIFIED,
						Format.TRUE,
						Format.NOT_SPECIFIED)
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
		if (maxDataLength < size.width * size.height * 4)
			maxDataLength = size.width * size.height * 4;
		if (lineStride < size.width * 4)
			lineStride = size.width * 4;
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
	
	
	public byte[] validateByteArraySize(Buffer buffer,int newSize) {
		Object objectArray=buffer.getData();
		byte[] typedArray;
		
		if (objectArray instanceof byte[]) {     // is correct type AND not null
			typedArray=(byte[])objectArray;
			if (typedArray.length >= newSize ) { // is sufficient capacity
				return typedArray;
			}
			
			byte[] tempArray=new byte[newSize];  // re-alloc array
			System.arraycopy(typedArray,0,tempArray,0,typedArray.length);
			typedArray = tempArray;
		} else {
			typedArray = new byte[newSize];
		}
		
		buffer.setData(typedArray);
		return typedArray;
	}
	
	public int byte2int(byte b){
		if(b<0) return 256 + b;
		else return b;
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
