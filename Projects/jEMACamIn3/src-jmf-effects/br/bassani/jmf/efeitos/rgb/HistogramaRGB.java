package br.bassani.jmf.efeitos.rgb;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Effect;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;

public class HistogramaRGB implements Effect {
	
	private static final String effectname = "HistogramaRGB";
	
	private Format inputFormat;
	private Format outputFormat;
	private Format[] inputFormats;
	private Format[] outputFormats;
	
	public HistogramaRGB(){
		
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
	
	private int[] red = new int[256];
	private int[] green = new int[256];
	private int[] blue = new int[256];
	int max_r, max_g, max_b;
	
	public int process(Buffer inBuffer, Buffer outBuffer) {
		int outputDataLength = ((VideoFormat)outputFormat).getMaxDataLength();
		validateByteArraySize(outBuffer, outputDataLength);
		
		outBuffer.setLength(outputDataLength);
		outBuffer.setFormat(outputFormat);
		//outBuffer.setFlags(inBuffer.getFlags());
		
		byte [] inData = (byte[]) inBuffer.getData();
		byte [] outData = (byte[]) outBuffer.getData();
		RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
		Dimension sizeIn = vfIn.getSize();
		
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

		for(int i=0; i<256; i++){
			red[i]=0;
			green[i]=0;
			blue[i]=0;
		}
		
		int r,g,b;
		for (  x = 0; x < iw; x++ ){
			for (  y = 0; y < ih; y++ ) {
				int index = x * 3 + y * iw * 3;
				r = byte2int(inData[index + 2]);
				g = byte2int(inData[index + 1]);
				b = byte2int(inData[index + 0]);
				
				red[r]++;
				if(red[r]>max_r) max_r = red[r];
				green[g]++;
				if(green[g]>max_g) max_g = green[g];
				blue[b]++;
				if(blue[b]>max_b) max_b = blue[b];
			}
		}
		
		BufferToImage conv = new BufferToImage(((VideoFormat)inputFormat));
		Image original = conv.createImage(inBuffer);
		
		geraImagemRGB(iw, ih, outData, original);
		
		return BUFFER_PROCESSED_OK;
	}
	
	private void geraImagemRGB(int width, int height, byte[] outData, Image original) {
		BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D bufferGraphics = (Graphics2D)buffer.getGraphics();
		
		//-- Opcoes para Rotate - Falta FLIP --
		// bufferGraphics.rotate(Math.PI, width/2, height/2);
		// - OU -
		// AffineTransform transf = AffineTransform.getRotateInstance(Math.PI, width/2, height/2);
		// transf.getType()
		// AffineTransform.TYPE_FLIP
		// bufferGraphics.setTransform(transf);
		//-- -- -- -- -- -- -- --
		
		bufferGraphics.drawImage(original, 0, 0, null);
		
		/*
		bufferGraphics.setColor(new Color(0, 255, 0));
		bufferGraphics.fillRect(0, 0, 200, 200);
		
		bufferGraphics.setColor(new Color(0, 0, 255));
		bufferGraphics.fillRect(128, 128, 100, 100);
		
		bufferGraphics.setColor(new Color(0, 255, 255));
		bufferGraphics.fillRect((width/2)-1, (height/2)-1, 3, 3);
		*/
		
		int espacoH = (height-60) / 3;
		int tamanhoH = (height) / 3;
		
		for (int r = 0; r < 256 && r < width; r++) {
			bufferGraphics.setColor(new Color(r, 0, 0));
			int tamanho = (red[r]*espacoH)/max_r;
			bufferGraphics.fillRect(r, espacoH-tamanho, 1, tamanho);
			bufferGraphics.fillRect(r, espacoH+5, 1, 10);
			/*
			 * Borda
			bufferGraphics.setColor(new Color(0, 0, 0));
			bufferGraphics.fillRect(r, espacoH-tamanho-1, 1, 1);
			bufferGraphics.fillRect(r, espacoH, 1, 1);
			*/
		}
		for (int g = 0; g < 256 && g < width; g++) {
			bufferGraphics.setColor(new Color(0, g, 0));
			int tamanho = (green[g]*espacoH)/max_g;
			bufferGraphics.fillRect(g, tamanhoH+espacoH-tamanho, 1, tamanho);
			bufferGraphics.fillRect(g, tamanhoH+espacoH+5, 1, 10);
		}
		for (int b = 0; b < 256 && b < width; b++) {
			bufferGraphics.setColor(new Color(0, 0, b));
			int tamanho = (blue[b]*espacoH)/max_b;
			bufferGraphics.fillRect(b, 2*tamanhoH+espacoH-tamanho, 1, tamanho);
			bufferGraphics.fillRect(b, 2*tamanhoH+espacoH+5, 1, 10);
		}
		
		int indexBuffer;
		int indexVideo;
		DataBuffer data = buffer.getData().getDataBuffer();
		if(data.getDataType()==DataBuffer.TYPE_BYTE){
			for(int x=0; x<width; x++){
				for(int y=0; y<height; y++){
					indexBuffer = x * 3 + y * width * 3;
					indexVideo = x * 3 + (height - y - 1) * width * 3;
					//R
					outData[indexVideo+2] = (byte)data.getElem(indexBuffer+2);
					//G
					outData[indexVideo+1] = (byte)data.getElem(indexBuffer+1);
					//B
					outData[indexVideo] = (byte)data.getElem(indexBuffer);
				}
			}
			/*
			 * Processo normal - Imagem 'Flipada'
			for(int index=0; index<data.getSize(); index++){
				outData[index] = (byte)data.getElem(index);
			}
			*/
		}
		//outData = (byte[]);
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
