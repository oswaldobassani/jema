package br.bassani.jmf.efeitos.rgb;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

public class Zoom2x extends RGBEffect {

	private static final String effectname = "Zoom2x";
	
	private int videoInW, videoInH;
	private int videoOutW, videoOutH;
	
	public Zoom2x(){
		super();
	}
	
	@Override
	public Format setInputFormat(Format input) {
		inputFormat = super.setInputFormat(input);
		VideoFormat vFormat = (VideoFormat)inputFormat;
		Dimension d = vFormat.getSize();
		videoInW = (int)d.getWidth();
		videoInH = (int)d.getHeight();
		
		videoOutW = 2*videoInW;
		videoOutH = 2*videoInH;
		
		System.out.println("videoOut WxH: "+videoOutW+"x"+videoOutH);
		System.out.println("videoIn WxH: "+videoInW+"x"+videoInH);
		
		return inputFormat;
	}
	
	@Override
	public Format setOutputFormat(Format output) {
		RGBFormat defaultOutputFormat = (RGBFormat)super.setOutputFormat(output);
		RGBFormat incoming = (RGBFormat) defaultOutputFormat;
		
		Dimension size = incoming.getSize();
		System.out.println("setOutputFormat incoming WxH: "+size.width+"x"+size.height);
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
		
		System.out.println("size Mod WxH: "+size.width+"x"+size.height);
		
		if(videoOutW == size.width && videoInW == size.width/2){
			System.out.println("Output/Input width - OK");
		}
		if(videoOutH == size.height && videoInH == size.height/2){
			System.out.println("Output/Input height - OK");
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
	
	/* FIXME: Baixo Desempenho!!! Diminui o FPS de 30 p/ 20 no processo de 320x240 p/ 640x480
	 * @see javax.media.Codec#process(javax.media.Buffer, javax.media.Buffer)
	 */
	@Override
	public int process(Buffer inBuffer, Buffer outBuffer) {
//		long t0,t1,t2,t3;
		
//		t0 = System.currentTimeMillis();
		
		int outputDataLength = ((VideoFormat)outputFormat).getMaxDataLength();
		validateByteArraySize(outBuffer, outputDataLength);
		
//		t1 = System.currentTimeMillis();
		
		outBuffer.setLength(outputDataLength);
		outBuffer.setFormat(outputFormat);
		outBuffer.setFlags(inBuffer.getFlags());
		
		byte [] inData = (byte[]) inBuffer.getData();
		byte [] outData = (byte[]) outBuffer.getData();
		
		if ( inData.length == 0 ) {
			System.out.println("inData length: "+inData.length);
			return BUFFER_PROCESSED_FAILED;
		}
		
		/*
		RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
		Dimension sizeIn = vfIn.getSize();
		*/
		/*
		int iw = sizeIn.width;
		int ih = sizeIn.height;
		*/
		
		//int iw = videoInW;
		//int ih = videoInH;
		
		if ( outData.length < videoOutW*videoOutH*3 ) {
			System.out.println("the buffer is not full");
			return BUFFER_PROCESSED_FAILED;
		}
		
	//	System.out.println("Out/In: "+outData.length+"/"+inData.length+" "+(outData.length/inData.length));
		
//		t2 = System.currentTimeMillis();
		
		/*
		int r,g,b;
		int xOut, yOut;
		for (  xOut = 0; xOut < videoOutW; xOut++ ){
			for (  yOut = 0; yOut < videoOutH; yOut++ ) {
				int indexOut = xOut * 3 + yOut * videoOutW * 3;
				int indexIn = (xOut/2) * 3 + (yOut/2) * videoInW * 3;

				//System.out.println("Out/In: "+indexOut+"/"+indexIn);
				
				r = byte2int(inData[indexIn + 2]);
				g = byte2int(inData[indexIn + 1]);
				b = byte2int(inData[indexIn + 0]);

				outData[indexOut + 2] = (byte)r;
				outData[indexOut + 1] = (byte)g;
				outData[indexOut + 0] = (byte)b;
			}
		}*/
		
		
	//	int lineInW = videoInW * 3;
		int lineOutW = videoOutW * 3;
		
		byte r,g,b;
	//	int x, y;
		int xOut, yOut;
		
		int indexIn;
		int indexOut0, indexOut1, indexOut2, indexOut3;
		
		indexIn = 0;
		indexOut0 = 0;
		for (  /*y = 0,*/ yOut=0; /*y < videoInH*/ yOut<videoOutH ; /*y++,*/ yOut+=2, indexOut0+=lineOutW ) {
			for (  /*x = 0,*/ xOut=0; /*x < videoInW*/ xOut<videoOutW ; /*x++,*/ xOut+=2, indexIn+=3, indexOut0+=6 ){
				//indexIn = x * 3 + y * lineInW;
				
				/*
				 * { 0, 1 }
				 * { 2, 3 }
				 */
				
				//indexOut0 = xOut * 3 + yOut * lineOutW;
				//int indexOut0 = 2 * indexIn;
				indexOut1 = indexOut0 + 3;
				//int indexOut2 = 2 * x * 3 + ((2 * y)+1) * lineOutW;
				indexOut2 = indexOut0 + lineOutW;
				indexOut3 = indexOut2 + 3;

				//System.out.println("Out/In: "+indexOut+"/"+indexIn);
				
				r = inData[indexIn + 2];
				g = inData[indexIn + 1];
				b = inData[indexIn + 0];

				outData[indexOut0 + 2] = r;
				outData[indexOut0 + 1] = g;
				outData[indexOut0 + 0] = b;
				outData[indexOut1 + 2] = r;
				outData[indexOut1 + 1] = g;
				outData[indexOut1 + 0] = b;
				outData[indexOut2 + 2] = r;
				outData[indexOut2 + 1] = g;
				outData[indexOut2 + 0] = b;
				outData[indexOut3 + 2] = r;
				outData[indexOut3 + 1] = g;
				outData[indexOut3 + 0] = b;
			}
		}
		
//		t3 = System.currentTimeMillis();
		
//		System.out.println("t1-t0: "+(t1-t0));
//		System.out.println("t2-t1: "+(t2-t1));
//		System.out.println("t3-t2: "+(t3-t2));
		
		
		return BUFFER_PROCESSED_OK;
	}

	@Override
	public String getName() {
		return effectname;
	}

}
