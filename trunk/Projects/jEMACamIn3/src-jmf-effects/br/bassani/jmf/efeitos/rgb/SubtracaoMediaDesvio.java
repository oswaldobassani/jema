package br.bassani.jmf.efeitos.rgb;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Effect;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

public class SubtracaoMediaDesvio implements Effect {
	
	private static final String effectname = "SubtracaoMediaDesvio";
	
	private Format inputFormat;
	private Format outputFormat;
	private Format[] inputFormats;
	private Format[] outputFormats;
	
	private int numero_quadros_pulados;
	private int numero_quadros_media;
	private int desvio_maximo_soma;
	
	private int count_quadros;
	
	public SubtracaoMediaDesvio(){
		this(5, 10, 1200);
	}
	
	public SubtracaoMediaDesvio(int pularQuadros, int quadrosDaMedia, int somaQuadradoComponentesMaxima){
		numero_quadros_pulados = pularQuadros;
		numero_quadros_media = quadrosDaMedia;
		desvio_maximo_soma = somaQuadradoComponentesMaxima;
		
		count_quadros = 0;
		
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
						24,
						3, 2, 1,
						3, Format.NOT_SPECIFIED,
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
		if (maxDataLength < size.width * size.height * 3)
			maxDataLength = size.width * size.height * 3;
		if (lineStride < size.width * 3)
			lineStride = size.width * 3;
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
	
	
	private byte[] validateByteArraySize(Buffer buffer,int newSize) {
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
	
	private int byte2int(byte b){
		if(b<0) return 256 + b;
		else return b;
	}
	
	int[] mediaInData;
	
	public int process(Buffer inBuffer, Buffer outBuffer) {
		int outputDataLength = ((VideoFormat)outputFormat).getMaxDataLength();
		validateByteArraySize(outBuffer, outputDataLength);
		
		outBuffer.setLength(outputDataLength);
		outBuffer.setFormat(outputFormat);
		outBuffer.setFlags(inBuffer.getFlags());
		
		byte [] inData = (byte[]) inBuffer.getData();
		byte [] outData = (byte[]) outBuffer.getData();
		RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
		Dimension sizeIn = vfIn.getSize();
		
		if(count_quadros==0){
			mediaInData = new int[inData.length];
		}
		
		if ( inData.length == 0 ) {
			System.out.println("inData length: "+inData.length);
			return BUFFER_PROCESSED_FAILED;
		}
		
		int iw = sizeIn.width;
		int ih = sizeIn.height;
		
		int x, y;
		
		if ( outData.length < iw*ih*3 ) {
			System.out.println("the buffer is not full");
			return BUFFER_PROCESSED_FAILED;
		}
		
		int r,g,b;
		
		for (  x = 0; x < iw; x++ ){
			for (  y = 0; y < ih; y++ ) {
				int index = x * 3 + y * iw * 3;
				r = byte2int(inData[index + 2]);
				g = byte2int(inData[index + 1]);
				b = byte2int(inData[index + 0]);
				
				if(count_quadros>numero_quadros_pulados && count_quadros<numero_quadros_pulados+numero_quadros_media){
					mediaInData[index + 2] += r;
					mediaInData[index + 1] += g;
					mediaInData[index + 0] += b;
				}else if(count_quadros==numero_quadros_pulados+numero_quadros_media){
					mediaInData[index + 2] = mediaInData[index + 2]/(numero_quadros_media-1);
					mediaInData[index + 1] = mediaInData[index + 1]/(numero_quadros_media-1);
					mediaInData[index + 0] = mediaInData[index + 0]/(numero_quadros_media-1);
				}else if(count_quadros>numero_quadros_pulados+numero_quadros_media){
					if(desvio(mediaInData[index + 2], mediaInData[index + 1], mediaInData[index + 0], r, b, g)<desvio_maximo_soma){
						r = g = b = 0;
					}
				}
				outData[index + 2] = (byte)r;
				outData[index + 1] = (byte)g;
				outData[index + 0] = (byte)b;
			}
		}
		
		count_quadros++;
		return BUFFER_PROCESSED_OK;
	}
	
	private int desvio(int r, int g, int b, int _r, int _g, int _b){
		return (r-_r)*(r-_r)+(g-_g)*(g-_g)+(b-_b)*(b-_b);
	}
	
	public String getName() {
		return effectname;
	}
	
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
