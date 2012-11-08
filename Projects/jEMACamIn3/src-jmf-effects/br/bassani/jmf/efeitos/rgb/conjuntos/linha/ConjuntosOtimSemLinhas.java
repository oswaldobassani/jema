package br.bassani.jmf.efeitos.rgb.conjuntos.linha;

import java.awt.Color;
import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

import br.bassani.jmf.efeitos.rgb.RGBEffect;

public class ConjuntosOtimSemLinhas extends RGBEffect {

	private static final String effectname = "ConjuntosOtimSemLinhas";
	
	public ConjuntosOtimSemLinhas(){
		super();
		
		cores = new CoresRGB[100];
		cores[0] = new CoresRGB(0,0,0);
		for(int i=1; i<cores.length; i++){
			cores[i] = new CoresRGB(getRandomColor());
		}
	}

	@Override
	public String getName() {
		return effectname;
	}
	
	class CoresRGB{
		int r,g,b;
		boolean criarConjuntos;
		public CoresRGB(int _r, int _g, int _b){
			r = _r;
			g = _g;
			b = _b;
		}
		public CoresRGB(Color color){
			r = color.getRed();
			g = color.getGreen();
			b = color.getBlue();
		}
		public boolean equals(Object obj){
			if(obj instanceof CoresRGB){
				CoresRGB c = (CoresRGB)obj;
				return ((r==c.r)&&(g==c.g)&&(b==c.b));
			}
			return false;
		}
		public Color getAWTColor(){
			return new Color(r,g,b);
		}
	}
	
	private CoresRGB[] cores;
	private int[][] pixelInfo;
	int proximoIdConjunto = 1;
	
	@Override
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
		
		int iw = sizeIn.width;
		int ih = sizeIn.height;
		
		int x, y;
		
		if ( outData.length < iw*ih*3 ) {
			System.out.println("the buffer is not full");
			return BUFFER_PROCESSED_FAILED;
		}
		
		if(pixelInfo==null){
			pixelInfo = new int[iw][ih];
			for (  x = 0; x < iw; x++ ){
				for (  y = 0; y < ih; y++ ) {
					pixelInfo[x][y] = 0;
				}
			}
		}
		
		if(false){
			for (  x = 0; x < iw; x++ ){
				for (  y = 0; y < ih; y++ ) {
					pixelInfo[x][y] = 0;
				}
			}
			proximoIdConjunto = 1;
		}
		
		int r,g,b;

		for (  y = 0; y < ih; y++ ) {
			for (  x = 0; x < iw; x++ ){
				int index = x * 3 + y * iw * 3;
				r = byte2int(inData[index + 2]);
				g = byte2int(inData[index + 1]);
				b = byte2int(inData[index + 0]);
				if ( aceitarCor(r, g, b) ) {
					int estadoInfo = proximoIdConjunto;
					int possivelEstadoInfo = 0;
					
					//if(pixelInfo[x][y]!=0) estadoInfo = pixelInfo[x][y];
					if(estadoInfo==proximoIdConjunto && y>0 && pixelInfo[x][y-1]!=0) estadoInfo = pixelInfo[x][y-1];
					if(estadoInfo==proximoIdConjunto && y>0 && pixelInfo[x][y-1]!=0 && x>0 && pixelInfo[x-1][y-1]!=0 ) estadoInfo = pixelInfo[x-1][y-1];
					if(estadoInfo==proximoIdConjunto && y<ih-1 && pixelInfo[x][y+1]!=0) estadoInfo = pixelInfo[x][y+1];
					if(estadoInfo==proximoIdConjunto && y<ih-1 && pixelInfo[x][y+1]!=0 && x>0 && pixelInfo[x-1][y+1]!=0) estadoInfo = pixelInfo[x-1][y+1];
					
					int numLinha = y;
					int numColIn = x;
					int indexDeslocamento = 3;
					int xMedio = x;
					x++;
					while ( x < iw && aceitarCor(inData, (index + indexDeslocamento)) ){
						indexDeslocamento += 3;
						//if(estadoInfo==proximoIdConjunto && pixelInfo[x][y]!=0) estadoInfo = pixelInfo[x][y];
						if(estadoInfo==proximoIdConjunto && y>0 && pixelInfo[x][y-1]!=0) estadoInfo = pixelInfo[x][y-1];
						if(estadoInfo==proximoIdConjunto && y<ih-1 && pixelInfo[x][y+1]!=0) estadoInfo = pixelInfo[x][y+1];
						
						xMedio = x + (x-numColIn)/2;
						if(xMedio < iw){
							possivelEstadoInfo = pixelInfo[xMedio][y];
						}
						
						x++;
					}
					x--;
					int numColFim = x;
					
					System.out.println(numLinha+" ["+numColIn+"-"+numColFim+"] "+estadoInfo);
					
					if(estadoInfo==proximoIdConjunto && y>0 && pixelInfo[x][y-1]!=0 && x<iw-1 && pixelInfo[x+1][y-1]!=0 ) estadoInfo = pixelInfo[x+1][y-1];
					if(estadoInfo==proximoIdConjunto && y<ih-1 && pixelInfo[x][y+1]!=0 && x<iw-1 && pixelInfo[x+1][y-1]!=0) estadoInfo = pixelInfo[x+1][y+1];
					
					if(estadoInfo==proximoIdConjunto && possivelEstadoInfo!=0){
						estadoInfo = possivelEstadoInfo;
					}
					
					if(estadoInfo==proximoIdConjunto){
						proximoIdConjunto++;
						if(proximoIdConjunto>=100){
							proximoIdConjunto = 1;
						}
					}
					
					x = numColIn;
					while (x <= numColFim){
						index = x * 3 + y * iw * 3;
						pixelInfo[x][y] = estadoInfo;
						if(true && estadoInfo<cores.length){
							outData[index + 2] = (byte)cores[estadoInfo].r;
							outData[index + 1] = (byte)cores[estadoInfo].g;
							outData[index + 0] = (byte)cores[estadoInfo].b;
						}else{
							outData[index + 2] = (byte)r;
							outData[index + 1] = (byte)g;
							outData[index + 0] = (byte)b;
						}
						x++;
					}
					x--;
					
				} else {
					pixelInfo[x][y] = 0;
					outData[index + 2] = (byte)r;
					outData[index + 1] = (byte)g;
					outData[index + 0] = (byte)b;
				}
			}
		}
		return BUFFER_PROCESSED_OK;
	}
	
	private boolean aceitarCor(byte[] inData, int index){
		return aceitarCor(byte2int(inData[index + 2]), byte2int(inData[index + 1]), byte2int(inData[index + 0]));
	}
	
	private boolean aceitarCor(int r, int g, int b){
		//return ( (r > g) && (g > b) && ( b > 64 ) ) ;
		//return ( (r >= 64) && (g <= 64) && ( b <= 64 ) );
		return ( (r == 100) && (g == 1) && ( b == 1 ) );
	}
	
	private static Color getRandomColor() {
		return new Color((int) (10 + Math.random() * 235), (int) (10 + Math.random() * 235), (int) (10 + Math.random() * 235));
	}
	
}
