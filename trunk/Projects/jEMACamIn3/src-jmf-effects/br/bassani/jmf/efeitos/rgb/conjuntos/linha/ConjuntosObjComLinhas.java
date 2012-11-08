package br.bassani.jmf.efeitos.rgb.conjuntos.linha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.Iterator;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Effect;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;

import br.bassani.jmf.efeitos.rgb.conjuntos.linha.objetos.ConjuntoLinha;
import br.bassani.jmf.efeitos.rgb.conjuntos.linha.objetos.ConjuntoPixeis;
import br.bassani.jmf.efeitos.rgb.conjuntos.linha.util.LinhaMedia;

public class ConjuntosObjComLinhas implements Effect {
	
	private static final String effectname = "ConjuntosObjComLinhas";
	
	private Format inputFormat;
	private Format outputFormat;
	private Format[] inputFormats;
	private Format[] outputFormats;

	class CoresRGB{
		int r,g,b;
		boolean criarConjuntos;
		public CoresRGB(int _r, int _g, int _b){
			r = _r;
			g = _g;
			b = _b;
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
	
	private Vector<CoresRGB> cores;
	
	public ConjuntosObjComLinhas(){
		cores = new Vector<CoresRGB>();
		
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
		
		int r,g,b;
		
		Rectangle regiaoAnalise = new Rectangle(0+20,0+20,iw-40,ih-40);
		
		Vector<ConjuntoPixeis> todosConjuntos = new Vector<ConjuntoPixeis>();
//		Vector<ConjuntoLinha> todosConjLinha = new Vector<ConjuntoLinha>();
//		ConjuntoLinha tempL;

		for (  y = 0; y < ih; y++ ) {
			for (  x = 0; x < iw; x++ ){
				int index = x * 3 + y * iw * 3;
				r = byte2int(inData[index + 2]);
				g = byte2int(inData[index + 1]);
				b = byte2int(inData[index + 0]);
				
				if(regiaoAnalise.contains(x,y)){
					CoresRGB c;
					int indexCor = cores.indexOf(new CoresRGB(r,g,b));
					if(indexCor==-1){
						c = new CoresRGB(r,g,b);
						if ( (r >= 64) && (g <= 64) && ( b <= 64 ) ) {
							c.criarConjuntos = true;
						}
						cores.add(c);
						indexCor = cores.indexOf(c);
					}else{
						c = cores.get(indexCor);
					}
					
					int numLinha = y;
					int numColIn = x;
					if (c.criarConjuntos) {
						
						
//						outData[index + 2] = (byte)c.r;
//						outData[index + 1] = (byte)c.g;
//						outData[index + 0] = (byte)c.b;
						
						int indexDeslocamento = 3;
						x++;
						while (x < iw && 
								r == byte2int(inData[index + indexDeslocamento + 2]) &&
								g == byte2int(inData[index + indexDeslocamento + 1]) &&
								b == byte2int(inData[index + indexDeslocamento + 0])){
							
//							outData[index + indexDeslocamento + 2] = (byte)c.r;
//							outData[index + indexDeslocamento + 1] = (byte)c.g;
//							outData[index + indexDeslocamento + 0] = (byte)c.b;
							
							indexDeslocamento += 3;
							x++;
						}
						x--;
						int numColFim = x;

//						tempL = new ConjuntoLinha(numLinha, numColIn, numColFim, indexCor);
//						todosConjLinha.add(tempL);
//						ConjuntoLinha novoCl = todosConjLinha.remove(0);
						
						ConjuntoLinha novoCl = new ConjuntoLinha(numLinha, numColIn, numColFim, indexCor);

						ConjuntoPixeis clInserido = null;
						for (int idC = 0; idC < todosConjuntos.size(); idC++) {
							if (todosConjuntos.get(idC).verificaSePertenceAoConjunto(novoCl)) {
								if (clInserido == null) {
									clInserido = todosConjuntos.get(idC);
									clInserido.adicionaAoConjunto(novoCl);
								} else {
									clInserido.adicionaConjunto(todosConjuntos.get(idC));
									todosConjuntos.remove(idC);
									idC--;
								}
							}
						}
						if (clInserido == null) {
							/*
							Color cor;
							if (novoCl.getTipo() != -1) {
								cor = cores.get(novoCl.getTipo()).getAWTColor();
							} else {
								//Color corFundo
								//cor = corFundo;
								cor = Color.black;
							}
							*/
							todosConjuntos.add(new ConjuntoPixeis(novoCl, getRandomColor()));
						}
					}else{
						//Color corFundo
						if(false){
							outData[index + 2] = (byte)r;
							outData[index + 1] = (byte)g;
							outData[index + 0] = (byte)b;
						}else{
							outData[index + 2] = 25;
							outData[index + 1] = 25;
							outData[index + 0] = 100;
						}
					}
				}else{
					//Color corFundo
					if(false){
						outData[index + 2] = (byte)r;
						outData[index + 1] = (byte)g;
						outData[index + 0] = (byte)b;
					}else{
						outData[index + 2] = 0;
						outData[index + 1] = 0;
						outData[index + 0] = 0;
					}
				}
			}
		}
		
		//DEBUGs - Comentados
		//System.out.println("Conjunto Linhas remanescentes : "+ todosConjLinha.size());
		//System.out.println("Total de conjuntos: " + todosConjuntos.size());
		ConjuntoPixeis c;
		//System.out.println("Tamanho dos Conjuntos: ");
		for (int p = 0; p < todosConjuntos.size(); p++) {
			c = todosConjuntos.get(p);
			//System.out.println("Conjunto[" + p + "] : " + c.getNumeroConjuntos());
			Color corConj = c.getCor();
			r = corConj.getRed();
			g = corConj.getGreen();
			b = corConj.getBlue();
			
			Vector<ConjuntoLinha> linhas = c.getConjuntos();
			Iterator<ConjuntoLinha> it = linhas.iterator();
			while(it.hasNext()){
				ConjuntoLinha linha = it.next();
				int xCL = linha.getColunaInicial();
				int yCL = linha.getLinha();
				int indexCL = xCL * 3 + yCL * iw * 3;
				int indexDeslocamento = 0;
				for(; xCL<=linha.getColunaFinal(); xCL++, indexDeslocamento+=3){
					outData[indexCL + indexDeslocamento + 2] = (byte)r;
					outData[indexCL + indexDeslocamento + 1] = (byte)g;
					outData[indexCL + indexDeslocamento + 0] = (byte)b;
				}
			}
		}
		
		
		BufferToImage conv = new BufferToImage(((VideoFormat)outputFormat));
		Image semLinhasMedias = conv.createImage(outBuffer);
		
		geraImagemRGB(iw, ih, outData, semLinhasMedias, todosConjuntos);
		
		
		return BUFFER_PROCESSED_OK;
	}
	
	private void geraImagemRGB(int width, int height, byte[] outData, Image base, Vector<ConjuntoPixeis> todosConjuntos) {
		BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D bufferGraphics = (Graphics2D)buffer.getGraphics();
		
		bufferGraphics.drawImage(base, 0, 0, null);
		
		ConjuntoPixeis c;
		//System.out.println("Tamanho dos Conjuntos: ");
		for (int p = 0; p < todosConjuntos.size(); p++) {
			c = todosConjuntos.get(p);
			LinhaMedia l = new LinhaMedia(c);
			Point p1 = l.getPonto1();
			Point p2 = l.getPonto2();
			bufferGraphics.setColor(Color.black);
			bufferGraphics.drawLine(p1.x, height-p1.y, p2.x, height-p2.y);
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
	}
	
	
	private static Color getRandomColor() {
		return new Color((int) (10 + Math.random() * 235), (int) (10 + Math.random() * 235), (int) (10 + Math.random() * 235));
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
