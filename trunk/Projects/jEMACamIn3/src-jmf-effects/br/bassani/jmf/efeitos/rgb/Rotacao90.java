package br.bassani.jmf.efeitos.rgb;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

public class Rotacao90 extends RGBEffect {

	private static final String effectname = "Rotacao90";
	
	private int videoInW, videoInH;
	private int videoOutW, videoOutH;
	
	public Rotacao90(){
		super();
	}
	
	@Override
	public Format setInputFormat(Format input) {
		inputFormat = super.setInputFormat(input);
		VideoFormat vFormat = (VideoFormat)inputFormat;
		Dimension d = vFormat.getSize();
		videoInW = videoOutH = (int)d.getWidth();
		videoInH = videoOutW = (int)d.getHeight();
		return inputFormat;
	}
	
	@Override
	public Format setOutputFormat(Format output) {
		RGBFormat defaultOutputFormat = (RGBFormat)super.setOutputFormat(output);
		RGBFormat incoming = (RGBFormat) defaultOutputFormat;
		
		Dimension size = incoming.getSize();
		int maxDataLength = incoming.getMaxDataLength();
		int lineStride = incoming.getLineStride();
		float frameRate = incoming.getFrameRate();
		int flipped = incoming.getFlipped();
		
		if (size == null)
			return null;
		if (maxDataLength < videoOutW * videoOutH * 3)
			maxDataLength = videoOutW * videoOutH * 3;
		if (lineStride < videoOutW * 3)
			lineStride = videoOutW * 3;
		if (flipped != Format.FALSE) flipped = Format.FALSE;
		
		//Modificacoes:
		size = new Dimension(videoOutW, videoOutH);
		lineStride = videoOutW * 3;

		if(videoOutH == size.height && videoInW == size.width){
			System.out.println("Output height/Input width - OK");
		}
		if(videoOutW == size.width && videoInH == size.height){
			System.out.println("Output width/Input height - OK");
		}
		
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
	
	@Override
	public int process(Buffer inBuffer, Buffer outBuffer) {
		int outputDataLength = ((VideoFormat)outputFormat).getMaxDataLength();
		validateByteArraySize(outBuffer, outputDataLength);
		
		outBuffer.setLength(outputDataLength);
		outBuffer.setFormat(outputFormat);
		outBuffer.setFlags(inBuffer.getFlags());
		
		byte [] inData = (byte[]) inBuffer.getData();
		byte [] outData = (byte[]) outBuffer.getData();
		
		/*
		RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
		Dimension sizeIn = vfIn.getSize();
		*/
		/*
		int iw = sizeIn.width;
		int ih = sizeIn.height;
		*/
		
		if ( inData.length == 0 ) {
			System.out.println("inData length: "+inData.length);
			return BUFFER_PROCESSED_FAILED;
		}
		
		int iw = videoInW;
		int ih = videoInH;
		
		if ( outData.length < iw*ih*3 ) {
			System.out.println("the buffer is not full");
			return BUFFER_PROCESSED_FAILED;
		}

		int x, y;
		int r,g,b;
		int xOut, yOut;
		for (  x = 0; x < iw; x++ ){
			for (  y = 0; y < ih; y++ ) {
				int indexIn = x * 3 + y * iw * 3;
				yOut = x;
				xOut = ih - y -1;
				int indexOut = xOut * 3 + yOut * ih * 3;
				r = byte2int(inData[indexIn + 2]);
				g = byte2int(inData[indexIn + 1]);
				b = byte2int(inData[indexIn + 0]);

				outData[indexOut + 2] = (byte)r;
				outData[indexOut + 1] = (byte)g;
				outData[indexOut + 0] = (byte)b;
			}
		}
		
		return BUFFER_PROCESSED_OK;
	}

	@Override
	public String getName() {
		return effectname;
	}

}
