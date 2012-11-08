package br.bassani.jmf.efeitos.yuv;
import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Effect;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;

public class YUVPele implements Effect {
	
	private static final String effectname = "YUVPele";
	
	private Format inputFormat;
	private Format outputFormat;
	private Format[] inputFormats;
	private Format[] outputFormats;
	
	public YUVPele(){
		
		/*
		 * YUVFormat(java.awt.Dimension size, int maxDataLength, java.lang.Class dataType, 
		 * 		float frameRate, int yuvType, 
		 * 		int strideY, int strideUV, int offsetY, int offsetU, int offsetV) 
		 */
		
		inputFormats = new Format[] {
				new YUVFormat(null,
						Format.NOT_SPECIFIED,
						Format.byteArray,
						Format.NOT_SPECIFIED,
						YUVFormat.YUV_422,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED),
				new YUVFormat(null,
						Format.NOT_SPECIFIED,
						Format.byteArray,
						Format.NOT_SPECIFIED,
						YUVFormat.YUV_420,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED)
		};
		
		outputFormats = new Format[] {
				new YUVFormat(null,
						Format.NOT_SPECIFIED,
						Format.byteArray,
						Format.NOT_SPECIFIED,
						YUVFormat.YUV_422,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED),
				new YUVFormat(null,
						Format.NOT_SPECIFIED,
						Format.byteArray,
						Format.NOT_SPECIFIED,
						YUVFormat.YUV_420,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
						Format.NOT_SPECIFIED,
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
		
		if(input instanceof YUVFormat){
			YUVFormat incoming = (YUVFormat) input;
			
			Dimension size = incoming.getSize();
			System.out.println("setInputFormat: YUVFormat: size: " + size);
			
			int maxDataLength = incoming.getMaxDataLength();
			System.out.println("setInputFormat: YUVFormat: maxDataLength: " + maxDataLength);
			
			float frameRate = incoming.getFrameRate();
			System.out.println("setInputFormat: YUVFormat: frameRate: " + frameRate);
			
			int yuvType = incoming.getYuvType();
			if(yuvType==YUVFormat.YUV_422){
				System.out.println("setInputFormat: YUVFormat: yuvType: YUV_422");
			}else if(yuvType==YUVFormat.YUV_420){
				System.out.println("setInputFormat: YUVFormat: yuvType: YUV_420");
			}else{
				System.out.println("setInputFormat: YUVFormat: yuvType: " + yuvType);
			}
		}
		
		inputFormat = input;
		return inputFormat;
	}
	
	public Format setOutputFormat(Format output) {
		if (output == null || matches(output, outputFormats) == null)
			return null;
		YUVFormat incoming = (YUVFormat) output;
		
		Dimension size = incoming.getSize();
		int maxDataLength = incoming.getMaxDataLength();
		float frameRate = incoming.getFrameRate();
		int yuvType = incoming.getYuvType();
		
		if (size == null) return null;
		
		int indexFormato = 0;
		if(yuvType==YUVFormat.YUV_422){
			indexFormato = 0;
			System.out.println("setOutputFormat: YUVFormat: yuvType: YUV_422");
			if (maxDataLength < size.width * size.height * 2) maxDataLength = size.width * size.height * 2;
		}else if(yuvType==YUVFormat.YUV_420){
			indexFormato = 1;
			System.out.println("setOutputFormat: YUVFormat: yuvType: YUV_420");
			if (maxDataLength < size.width * size.height * 1.5) maxDataLength = (int)(size.width * size.height * 1.5);
		}else{
			System.out.println("setOutputFormat: YUVFormat: yuvType: " + yuvType);
			if (maxDataLength < size.width * size.height * 3) maxDataLength = size.width * size.height * 3;
		}
		outputFormat = outputFormats[indexFormato].intersects(
				new YUVFormat(size,
					maxDataLength,
					Format.byteArray,
					frameRate,
					yuvType,
					incoming.getStrideY(),
					incoming.getStrideUV(),
					incoming.getOffsetY(),
					incoming.getOffsetU(),
					incoming.getOffsetV())
				);

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
	
	public int process(Buffer inBuffer, Buffer outBuffer) {
		int outputDataLength = ((VideoFormat)outputFormat).getMaxDataLength();
		int yuvType = ((YUVFormat)outputFormat).getYuvType();
		validateByteArraySize(outBuffer, outputDataLength);
		
		outBuffer.setLength(outputDataLength);
		outBuffer.setFormat(outputFormat);
		outBuffer.setFlags(inBuffer.getFlags());
		
		byte [] inData = (byte[]) inBuffer.getData();
		byte [] outData = (byte[]) outBuffer.getData();
		YUVFormat vfIn = (YUVFormat) inBuffer.getFormat();
		Dimension sizeIn = vfIn.getSize();
		
		int iw = sizeIn.width;
		int ih = sizeIn.height;
		
		
		if(yuvType==YUVFormat.YUV_422){
			if (outData.length < iw * ih * 2) {
				System.out.println("the buffer is not full");
				return BUFFER_PROCESSED_FAILED;
			}
		}else if(yuvType==YUVFormat.YUV_420){
			if (outData.length < iw * ih * 1.5) {
				System.out.println("the buffer is not full");
				return BUFFER_PROCESSED_FAILED;
			}
		}else{
			if (outData.length < iw * ih * 3) {
				System.out.println("the buffer is not full");
				return BUFFER_PROCESSED_FAILED;
			}
		}

		int x, y;
		
		if(vfIn.getYuvType()==YUVFormat.YUV_422){
			System.out.print("YUV_422 ");
			int cy,u,v;
			for (  x = 0; x < iw; x++ ){
				for (  y = 0; y < ih; y++ ) {
					cy = byte2int(inData[x + y * iw]);
					u = byte2int(inData[(iw*ih) + (x/2 + y/2 * iw/2)]);
					v = byte2int(inData[(iw*ih) + (iw*ih/4) + (x/2 + y/2 * iw/2)]);
					if ( cy > 100 && v > 30 ) {
						outData[x + y * iw] = 0;
						outData[(iw*ih) + (x/2 + y/2 * iw/2)] = 0;
						outData[(iw*ih) + (iw*ih/4) + (x/2 + y/2 * iw/2)] = 0;
					} else {
						outData[x + y * iw] = (byte)cy;
						outData[(iw*ih) + (x/2 + y/2 * iw/2)] = (byte)u;
						outData[(iw*ih) + (iw*ih/4) + (x/2 + y/2 * iw/2)] = (byte)v;
					}
				}
			}
		}else if(vfIn.getYuvType()==YUVFormat.YUV_420){
			System.out.print("YUV_420 IN("+inData.length+") OUT("+outData.length+") ");
			
			/*
			 * Dados
			 * Sample.mov - 176x144 (25344)
			 *  - YUV_420
			 *  - IN(38016 = 25344 * 1.5)
			 *  - OUT(76032 = 25344 * 3)
			 */
			if(false){
				if(inData.length<=outData.length){
					for(int i = 0; i < inData.length; i++){
						outData[i] = inData[i];
					}
				}else{
					for(int i = 0; i < outData.length; i++){
						outData[i] = inData[i];
					}
				}
			}else{
				int cy,u,v;
				for (  x = 0; x < iw; x++ ){
					for (  y = 0; y < ih; y++ ) {
						cy = byte2int(inData[x + y * iw]);
						u = byte2int(inData[(iw*ih) + (x/2 + y/2 * iw/2)]);
						v = byte2int(inData[(iw*ih) + (iw*ih/4) + (x/2 + y/2 * iw/2)]);
						
						/*
						 * Conhecimento previo:
						 * Metodo de Deteccao de Pele em RGB
						 *  : (r > g) && (g > b) && ( b > 64 )
						 *  
						 *  Mapeando para YUV:
						 *  RESUMO
						 *  YUV [66-254][174-255][0-116]
						 */
						if ( true && cy > 66 && cy < 254 && u > 174 && u < 255 && v < 116 ) {
							outData[x + y * iw] = (byte)76;
							if(yuvType==YUVFormat.YUV_420){
								outData[(iw*ih) + (x/2 + y/2 * iw/2)] = (byte)219;
								outData[(iw*ih) + (iw*ih/4) + (x/2 + y/2 * iw/2)] = (byte)156;
							}else if(yuvType==YUVFormat.YUV_422){
								outData[(iw*ih) + (x/2 + y * iw/2)] = (byte)219;
								outData[(iw*ih) + (iw*ih/2) + (x/2 + y * iw/2)] = (byte)156;
							}
						} else {
							outData[x + y * iw] = (byte)cy;
							if(yuvType==YUVFormat.YUV_420){
								outData[(iw*ih) + (x/2 + y/2 * iw/2)] = (byte)u;
								outData[(iw*ih) + (iw*ih/4) + (x/2 + y/2 * iw/2)] = (byte)v;
							}else if(yuvType==YUVFormat.YUV_422){
								outData[(iw*ih) + (x/2 + y * iw/2)] = (byte)u;
								outData[(iw*ih) + (iw*ih/2) + (x/2 + y * iw/2)] = (byte)v;
							}
						}
					}
				}
			}
		}
		return BUFFER_PROCESSED_OK;
	}
	
	/*
	 public int process(Buffer inputBuffer, Buffer outputBuffer) {
	 // TODO Auto-generated method stub
	  
	  System.out.println("[YUVPele (Effect)]"+"process");
	  int inLength = inputBuffer.getLength();//152064
	  System.out.println("[YUVPele (Effect)] inLength: "+inLength);
	  //352 X 288 = 101376 = 152064 / 1.5
	   //
	    /---*
	     byte[] inData = (byte[])inputBuffer.getData();
	     int inLength = inputBuffer.getLength();
	     int inOffset = inputBuffer.getOffset();
	     int outOffset = outputBuffer.getOffset();
	     *---/
	     
	     byte[] inData = (byte[])inputBuffer.getData();
	     byte[] outData = new byte[inData.length];
	     for(int x=0; x<352; x++){
	     for(int y=0; y<288; y++){
	     //outData[x + y * 352] = inData[x + y * 352]; //Y
	      //outData[(352*288) + (x/2 + y/2 * 352/2)] = inData[(352*288) + (x/2 + y/2 * 352/2)]; //U
	       //outData[(352*288) + (352*288/4) + (x/2 + y/2 * 352/2)] = inData[(352*288) + (352*288/4) + (x/2 + y/2 * 352/2)]; //V
	        outData[x + y * 352] = 127;
	        //161 - V +- Pele
	         //91 - U +- Pele
	          if(inData[(352*288) + (x/2 + y/2 * 352/2)] > 80 && inData[(352*288) + (x/2 + y/2 * 352/2)]<100){
	          if(inData[(352*288) + (352*288/4) + (x/2 + y/2 * 352/2)]>150-255 && inData[(352*288) + (352*288/4) + (x/2 + y/2 * 352/2)]<170-255){
	          //outData[(352*288) + (x/2 + y/2 * 352/2)] = inData[(352*288) + (x/2 + y/2 * 352/2)]; //U
	           //outData[(352*288) + (352*288/4) + (x/2 + y/2 * 352/2)] = inData[(352*288) + (352*288/4) + (x/2 + y/2 * 352/2)]; //V
	            outData[(352*288) + (x/2 + y/2 * 352/2)] = -50;
	            outData[(352*288) + (352*288/4) + (x/2 + y/2 * 352/2)] = -50;
	            }else{
	            outData[(352*288) + (x/2 + y/2 * 352/2)] = 50;
	            outData[(352*288) + (352*288/4) + (x/2 + y/2 * 352/2)] = 50;
	            }
	            }else{
	            outData[(352*288) + (x/2 + y/2 * 352/2)] = 127;
	            outData[(352*288) + (352*288/4) + (x/2 + y/2 * 352/2)] = 127;
	            }
	            }
	            }
	            outputBuffer.setData(outData);
	            
	            
	            outputBuffer.setOffset(inputBuffer.getOffset());
	            outputBuffer.setLength(inputBuffer.getLength());
	            //outputBuffer.setData(inputBuffer.getData());
	             
	             //return BUFFER_PROCESSED_FAILED;
	              
	              return BUFFER_PROCESSED_OK;
	              }
	              */
	
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
