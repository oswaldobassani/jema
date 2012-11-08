package br.bassani.jmf.efeitos.argb.conjuntos.linha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.Iterator;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.bassani.jmf.efeitos.IConfiguracao;
import br.bassani.jmf.efeitos.argb.ARGB_IN_Effect;
import br.bassani.jmf.efeitos.rgb.conjuntos.linha.objetos.ConjuntoLinha;
import br.bassani.jmf.efeitos.rgb.conjuntos.linha.objetos.ConjuntoPixeis;
import br.bassani.jmf.efeitos.rgb.conjuntos.linha.util.LinhaMedia;
import br.bassani.tetris.swing.PainelTetris;

public class ARGBConjuntosObjMemoComLinhas extends ARGB_IN_Effect implements IConfiguracao {
	
	private static final String effectname = "ARGBConjuntosObjMemoComLinhas";
	
	private boolean jogoTetris = false;
	
	public ARGBConjuntosObjMemoComLinhas(JFrame parent, boolean jogoTetris){
		super();
		
		this.jogoTetris = jogoTetris;
		
		if(jogoTetris) openDialogJogoTetris(parent);
	}
	
	//Parametros de Configuracao
	
	boolean limitarTamanhoConjuntoLinha = true;
	int tamanhoMinimoConjuntoLinha = 10;
	int tamanhoMaximoConjuntoLinha = 35;
	
	boolean limitarAreaConjuntoPixeis = true;
	int tamanhoMinimoConjuntoPixeis = 40;
	
	boolean limitarMinimoLinhasConjuntoPixeis = true;
	int numeroMinimoLinhasConjuntoPixeis = 6;
	
	boolean criarLinhaMediaConjuntoPixeis = true;
	
	//Variaveis de Execucao
	
	int proximoIdConjunto = 1;
	Rectangle regiaoAnalise;
	Vector<ConjuntoPixeis> todosConjuntos;
	Vector<ConjuntoPixeis> todosConjuntosUltimoFrame;
	
	private BufferToImage buffer2image;
	
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

		if ( outData.length < iw * ih * 3 ) {
			System.out.println("the buffer is not full");
			return BUFFER_PROCESSED_FAILED;
		}
		
		if(regiaoAnalise==null){
			regiaoAnalise = new Rectangle(0,0,iw,ih);
		}

		int x, y;
		int a,r,g,b;
		todosConjuntosUltimoFrame = todosConjuntos;
		todosConjuntos = new Vector<ConjuntoPixeis>();
		
		for (  y = 0; y < ih; y++ ) {
			for (  x = 0; x < iw; x++ ){
				int index = x * 4 + y * iw * 4;
				int indexOut = x * 3 + y * iw * 3;
				a = byte2int(inData[index + 3]);
				r = byte2int(inData[index + 2]);
				g = byte2int(inData[index + 1]);
				b = byte2int(inData[index + 0]);
				
				if(regiaoAnalise.contains(x,y)){
					if ( aceitarCor(a, r, g, b) ) {
						
						outData[indexOut + 2] = (byte)r;
						outData[indexOut + 1] = (byte)g;
						outData[indexOut + 0] = (byte)b;
						
						int numLinha = y;
						int numColIn = x;
						int indexDeslocamento = 4;
						int indexDeslocamentoOut = 3;
						x++;
						if(x < iw){
							a = byte2int(inData[index + indexDeslocamento + 3]);
							r = byte2int(inData[index + indexDeslocamento + 2]);
							g = byte2int(inData[index + indexDeslocamento + 1]);
							b = byte2int(inData[index + indexDeslocamento + 0]);
						}
						while (x < iw && /*aceitarCor(inData, index + indexDeslocamento)*/
								aceitarCor(a, r, g, b)){
							
							outData[indexOut + indexDeslocamentoOut + 2] = (byte)r;
							outData[indexOut + indexDeslocamentoOut + 1] = (byte)g;
							outData[indexOut + indexDeslocamentoOut + 0] = (byte)b;
							
							indexDeslocamento += 4;
							indexDeslocamentoOut += 3;
							x++;
							
							if(x < iw){
								a = byte2int(inData[index + indexDeslocamento + 3]);
								r = byte2int(inData[index + indexDeslocamento + 2]);
								g = byte2int(inData[index + indexDeslocamento + 1]);
								b = byte2int(inData[index + indexDeslocamento + 0]);
							}
						}
						x--;
						int numColFim = x;
						
						if(!limitarTamanhoConjuntoLinha || ((numColFim - numColIn <= tamanhoMaximoConjuntoLinha) && (numColFim - numColIn >= tamanhoMinimoConjuntoLinha))){
							
							int estadoInfo = proximoIdConjunto;
							proximoIdConjunto++;
							ConjuntoLinha novoCl = new ConjuntoLinha(numLinha, numColIn, numColFim, estadoInfo);
							
							ConjuntoPixeis clInserido = null;
							for (int idC = 0; idC < todosConjuntos.size(); idC++) {
								if (todosConjuntos.get(idC).verificaSePertenceAoConjuntoTipoIndiferente(novoCl)) {
									if (clInserido == null) {
										clInserido = todosConjuntos.get(idC);
										
										novoCl.setTipo(clInserido.getTipo());
										clInserido.adicionaAoConjunto(novoCl);
										
										proximoIdConjunto--;
									} else {
										ConjuntoPixeis temp = todosConjuntos.get(idC);
										temp.setTipo(clInserido.getTipo());
										clInserido.adicionaConjunto(temp);
										todosConjuntos.remove(idC);
										idC--;
									}
								}
							}
							if (clInserido == null) {
								todosConjuntos.add(new ConjuntoPixeis(novoCl, getRandomColor()));
							}
							
						}else{
							//Color corFundo
							if(true){
								//outData[indexOut + 3] = (byte)a;
								outData[indexOut + 2] = (byte)r;
								outData[indexOut + 1] = (byte)g;
								outData[indexOut + 0] = (byte)b;
							}else{
								//outData[indexOut + 3] = (byte)255;
								outData[indexOut + 2] = 25;
								outData[indexOut + 1] = 100;
								outData[indexOut + 0] = 25;
							}
						}
					}else{
						//Color corFundo
						if(true){
							//outData[indexOut + 3] = (byte)a;
							outData[indexOut + 2] = (byte)r;
							outData[indexOut + 1] = (byte)g;
							outData[indexOut + 0] = (byte)b;
						}else{
							//outData[indexOut + 3] = (byte)255;
							outData[indexOut + 2] = 25;
							outData[indexOut + 1] = 25;
							outData[indexOut + 0] = 100;
						}
					}
				}else{
					//Color corFundo
					if(true){
						//outData[indexOut + 3] = (byte)a;
						outData[indexOut + 2] = (byte)r;
						outData[indexOut + 1] = (byte)g;
						outData[indexOut + 0] = (byte)b;
					}else{
						//outData[indexOut + 3] = (byte)255;
						outData[indexOut + 2] = 0;
						outData[indexOut + 1] = 0;
						outData[indexOut + 0] = 0;
					}
				}
			}
		}
		
		//Modos de Comparacao
		
		/*
		if(todosConjuntosUltimoFrame!=null){
			for(int iCnjUltimoFrame=0; iCnjUltimoFrame<todosConjuntosUltimoFrame.size(); iCnjUltimoFrame++){
				int maiorInterseccao = 0;
				int indexCnj = -1;
				ConjuntoPixeis tempUltimoFrame = todosConjuntosUltimoFrame.get(iCnjUltimoFrame);
				for(int iCnjFrameAtual=0; iCnjFrameAtual<todosConjuntos.size(); iCnjFrameAtual++){
					ConjuntoPixeis tempFrameAtual = todosConjuntos.get(iCnjFrameAtual);
					int interseccao = tempUltimoFrame.getAreaCobertaPor(tempFrameAtual);
					if(interseccao>maiorInterseccao){
						indexCnj = iCnjFrameAtual;
						maiorInterseccao = interseccao;
					}
				}
				if(indexCnj!=-1){
					ConjuntoPixeis tempFrameAtual = todosConjuntos.get(indexCnj);
					tempFrameAtual.setTipo(tempUltimoFrame.getTipo());
					tempFrameAtual.setCor(tempUltimoFrame.getCor());
				}
			}
		}*/
		
		if(todosConjuntosUltimoFrame!=null){
			for(int iCnjFrameAtual=0; iCnjFrameAtual<todosConjuntos.size(); iCnjFrameAtual++){
				ConjuntoPixeis tempFrameAtual = todosConjuntos.get(iCnjFrameAtual);
				if(limitarAreaConjuntoPixeis && tempFrameAtual.getArea()<tamanhoMinimoConjuntoPixeis){
					todosConjuntos.remove(tempFrameAtual);
					iCnjFrameAtual--;
				}else if(limitarMinimoLinhasConjuntoPixeis && tempFrameAtual.getNumeroLinhas()<numeroMinimoLinhasConjuntoPixeis){
					todosConjuntos.remove(tempFrameAtual);
					iCnjFrameAtual--;
				}else{
					int indexCnj = -1;
					for(int iCnjUltimoFrame=0; iCnjUltimoFrame<todosConjuntosUltimoFrame.size(); iCnjUltimoFrame++){
						int maiorInterseccao = 0;
						ConjuntoPixeis tempUltimoFrame = todosConjuntosUltimoFrame.get(iCnjUltimoFrame);
						//int interseccao = tempUltimoFrame.getAreaCobertaPor(tempFrameAtual);
						int interseccao = tempFrameAtual.getAreaCobertaPor(tempUltimoFrame);
						if(interseccao>maiorInterseccao){
							indexCnj = iCnjUltimoFrame;
							maiorInterseccao = interseccao;
						}
					}
					if(indexCnj!=-1){
						ConjuntoPixeis tempUltimoFrame = todosConjuntosUltimoFrame.get(indexCnj);
						tempFrameAtual.setTipo(tempUltimoFrame.getTipo());
						tempFrameAtual.setCor(tempUltimoFrame.getCor());
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
					//outData[indexCL + indexDeslocamento + 3] = (byte)255;
					outData[indexCL + indexDeslocamento + 2] = (byte)r;
					outData[indexCL + indexDeslocamento + 1] = (byte)g;
					outData[indexCL + indexDeslocamento + 0] = (byte)b;
				}
			}
		}
		
		if(criarLinhaMediaConjuntoPixeis){
			if(buffer2image==null && outputFormat!=null){
				buffer2image = new BufferToImage(((VideoFormat)outputFormat));
			}
			if(buffer2image!=null){
				Image semLinhasMedias = buffer2image.createImage(outBuffer);
				geraImagemRGB(iw, ih, outData, semLinhasMedias, todosConjuntos);
			}
		}
		
		return BUFFER_PROCESSED_OK;
	}
	
	private BufferedImage buffer;
	private BufferedImage bufferOut;
	
	private void geraImagemRGB(int width, int height, byte[] outData, Image base, Vector<ConjuntoPixeis> todosConjuntos) {
		if(buffer==null){
			buffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			bufferOut = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			if(tetris!=null){
				tetris.setVideoSize(width, height);
			}
		}
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
			
			if(tetris!=null){
				tetris.addMarcaDedo(c.getTipo(), c.getArea(), l);
			}
		}
		if(tetris!=null){
			tetris.fimQuadro();
			/*
			Image base_tetris = buffer.getScaledInstance(tetris.getWidth(), tetris.getHeight(), BufferedImage.SCALE_FAST);
			tetris.setImagemFundo(base_tetris);
			*/
		}
		
		int indexBuffer;
		int indexVideo;
		DataBuffer data = buffer.getData().getDataBuffer();
		//DataBuffer dataOut = bufferOut.getData().getDataBuffer();
		if(data.getDataType()==DataBuffer.TYPE_BYTE){
			
			boolean flipImage = true;
			int xFlip;
			
			for(int x=0; x<width; x++){
				for(int y=0; y<height; y++){
					xFlip = x;
					if(flipImage){
						xFlip = width - x -1;
					}
					
					indexBuffer = xFlip * 3 + y * width * 3;
					indexVideo = x * 3 + (height - y - 1) * width * 3;
					//A
					//outData[indexVideo+3] = (byte)data.getElem(indexBuffer+3);
					//R
					outData[indexVideo+2] = (byte)data.getElem(indexBuffer+2);
					//dataOut.setElem(indexVideo+2, data.getElem(indexBuffer+2));
					//G
					outData[indexVideo+1] = (byte)data.getElem(indexBuffer+1);
					//dataOut.setElem(indexVideo+1, data.getElem(indexBuffer+1));
					//B
					outData[indexVideo] = (byte)data.getElem(indexBuffer);
					//dataOut.setElem(indexVideo, data.getElem(indexBuffer));
					
					//bufferOut.setRGB(x,y, (outData[indexVideo+2]<<16 & outData[indexVideo+1]<<8 & outData[indexVideo+0]) );
					
					int r,g,b;
					r = byte2int(outData[indexVideo+2]);
					g = byte2int(outData[indexVideo+1]);
					b = byte2int(outData[indexVideo+0]);
					
					//bufferOut.setRGB(x,y, (r<<16 & g<<8 & b) );

					//bufferOut.setRGB(x,y, ((r&0xFF)<<16 & (g&0xFF)<<8 & (b&0xFF)) );
					
					Color color = new Color(r,g,b);
					bufferOut.setRGB(x,y,color.getRGB());
							
				}
			}
			/*
			 * Processo normal - Imagem 'Flipada'
			for(int index=0; index<data.getSize(); index++){
				outData[index] = (byte)data.getElem(index);
			}
			*/
		}
		
		if(tetris!=null){
			Image base_tetris = bufferOut.getScaledInstance(tetris.getWidth(), tetris.getHeight(), BufferedImage.SCALE_FAST);
			tetris.setImagemFundo(base_tetris);
		}
		
	}
	
	/*
	private boolean aceitarCor(byte[] inData, int index){
		return aceitarCor(byte2int(inData[index + 3]), byte2int(inData[index + 2]), byte2int(inData[index + 1]), byte2int(inData[index + 0]));
	}
	*/
	
	private boolean aceitarCor(int a, int r, int g, int b){
		//return ( (r > g) && (g > b) && ( b > 64 ) ) ;
		//return ( (r >= 64) && (g <= 64) && ( b <= 64 ) );
		//return ( (r == 100) && (g == 1) && ( b == 1 ) );
		return ( (a == 255) );
	}
	
	private static Color getRandomColor() {
		return new Color((int) (10 + Math.random() * 235), (int) (10 + Math.random() * 235), (int) (10 + Math.random() * 235));
	}
	
	public String getName() {
		return effectname;
	}

	//Objetos Graficos da Configuracao
	private JPanel painel;
	
	JButton limeteConjLinha, limiteAreaConjunto, limiteLinhasConjunto, ativarLinhaMedia;
	JLabel valorConjLinhaMin, valorConjLinhaMax, valorAraeConjMin, valorLinhasConjMin;
	JSlider sliderConjLinhaMin, sliderConjLinhaMax, sliderAraeConjMin, sliderLinhasConjMin;
	
	/*
	
	boolean limitarAreaConjuntoPixeis = true;
	int tamanhoMinimoConjuntoPixeis = 40;

	*/
	
	
	public JPanel openPanelConfiguracao() {
		if(painel==null){
			painel = new JPanel();
			painel.setLayout(new BorderLayout(5,5));
			painel.add(new JLabel("Comandos"), BorderLayout.NORTH);
			
			
			JPanel sliders = new JPanel(new GridLayout(-1,1,5,5));
			
			limeteConjLinha = new JButton("limeteConjLinha: ON");
			limeteConjLinha.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					if(limeteConjLinha.getText().equals("limeteConjLinha: OFF")){
						limeteConjLinha.setText("limeteConjLinha: ON");
						limitarTamanhoConjuntoLinha = true;
						sliderConjLinhaMin.setEnabled(true);
						sliderConjLinhaMax.setEnabled(true);
					}else{
						limeteConjLinha.setText("limeteConjLinha: OFF");
						limitarTamanhoConjuntoLinha = false;
						sliderConjLinhaMin.setEnabled(false);
						sliderConjLinhaMax.setEnabled(false);
					}
				}
			});
			sliders.add(limeteConjLinha);

			JPanel barraConjLinhaMin = new JPanel(new FlowLayout(FlowLayout.LEFT));
			barraConjLinhaMin.add(new JLabel("tamanhoMinimoConjuntoLinha: "));
			valorConjLinhaMin = new JLabel("-----");
			sliderConjLinhaMin = new JSlider(0, 100, 1);
			sliderConjLinhaMin.setValue(tamanhoMinimoConjuntoLinha);
			sliderConjLinhaMin.setPaintLabels(false);
			sliderConjLinhaMin.setPaintTicks(false);
			sliderConjLinhaMin.setPaintTrack(true);
			sliderConjLinhaMin.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent event) {
					tamanhoMinimoConjuntoLinha = sliderConjLinhaMin.getValue();
					sliderConjLinhaMin.setToolTipText("tamanhoMinimoConjuntoLinha: "+tamanhoMinimoConjuntoLinha);
					valorConjLinhaMin.setText(""+tamanhoMinimoConjuntoLinha);
				}
			});
			barraConjLinhaMin.add(sliderConjLinhaMin);
			barraConjLinhaMin.add(valorConjLinhaMin);
			sliders.add(barraConjLinhaMin);
			
			JPanel barraConjLinhaMax = new JPanel(new FlowLayout(FlowLayout.LEFT));
			barraConjLinhaMax.add(new JLabel("tamanhoMaximoConjuntoLinha: "));
			valorConjLinhaMax = new JLabel("-----");
			sliderConjLinhaMax = new JSlider(0, 100, 1);
			sliderConjLinhaMax.setValue(tamanhoMaximoConjuntoLinha);
			sliderConjLinhaMax.setPaintLabels(false);
			sliderConjLinhaMax.setPaintTicks(false);
			sliderConjLinhaMax.setPaintTrack(true);
			sliderConjLinhaMax.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent event) {
					tamanhoMaximoConjuntoLinha = sliderConjLinhaMax.getValue();
					sliderConjLinhaMax.setToolTipText("tamanhoMaximoConjuntoLinha: "+tamanhoMaximoConjuntoLinha);
					valorConjLinhaMax.setText(""+tamanhoMaximoConjuntoLinha);
				}
			});
			barraConjLinhaMax.add(sliderConjLinhaMax);
			barraConjLinhaMax.add(valorConjLinhaMax);
			sliders.add(barraConjLinhaMax);
			
			limiteAreaConjunto = new JButton("limiteAreaConjunto: ON");
			limiteAreaConjunto.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					if(limiteAreaConjunto.getText().equals("limiteAreaConjunto: OFF")){
						limiteAreaConjunto.setText("limiteAreaConjunto: ON");
						limitarAreaConjuntoPixeis = true;
						sliderAraeConjMin.setEnabled(true);
					}else{
						limiteAreaConjunto.setText("limiteAreaConjunto: OFF");
						limitarAreaConjuntoPixeis = false;
						sliderAraeConjMin.setEnabled(false);
					}
				}
			});
			sliders.add(limiteAreaConjunto);

			JPanel barraAraeConjMin = new JPanel(new FlowLayout(FlowLayout.LEFT));
			barraAraeConjMin.add(new JLabel("tamanhoMinimoConjuntoPixeis: "));
			valorAraeConjMin = new JLabel("-----");
			sliderAraeConjMin = new JSlider(0, 400, 1);
			sliderAraeConjMin.setValue(tamanhoMinimoConjuntoPixeis);
			sliderAraeConjMin.setPaintLabels(false);
			sliderAraeConjMin.setPaintTicks(false);
			sliderAraeConjMin.setPaintTrack(true);
			sliderAraeConjMin.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent event) {
					tamanhoMinimoConjuntoPixeis = sliderAraeConjMin.getValue();
					sliderAraeConjMin.setToolTipText("tamanhoMinimoConjuntoPixeis: "+tamanhoMinimoConjuntoPixeis);
					valorAraeConjMin.setText(""+tamanhoMinimoConjuntoPixeis);
				}
			});
			barraAraeConjMin.add(sliderAraeConjMin);
			barraAraeConjMin.add(valorAraeConjMin);
			sliders.add(barraAraeConjMin);
			
			limiteLinhasConjunto = new JButton("limiteLinhasConjunto: ON");
			limiteLinhasConjunto.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					if(limiteLinhasConjunto.getText().equals("limiteLinhasConjunto: OFF")){
						limiteLinhasConjunto.setText("limiteLinhasConjunto: ON");
						limitarMinimoLinhasConjuntoPixeis = true;
						sliderLinhasConjMin.setEnabled(true);
					}else{
						limiteLinhasConjunto.setText("limiteLinhasConjunto: OFF");
						limitarMinimoLinhasConjuntoPixeis = false;
						sliderLinhasConjMin.setEnabled(false);
					}
				}
			});
			sliders.add(limiteLinhasConjunto);

			JPanel barraLinhasConjMin = new JPanel(new FlowLayout(FlowLayout.LEFT));
			barraLinhasConjMin.add(new JLabel("numeroMinimoLinhasConjuntoPixeis: "));
			valorLinhasConjMin = new JLabel("-----");
			sliderLinhasConjMin = new JSlider(0, 100, 1);
			sliderLinhasConjMin.setValue(numeroMinimoLinhasConjuntoPixeis);
			sliderLinhasConjMin.setPaintLabels(false);
			sliderLinhasConjMin.setPaintTicks(false);
			sliderLinhasConjMin.setPaintTrack(true);
			sliderLinhasConjMin.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent event) {
					numeroMinimoLinhasConjuntoPixeis = sliderLinhasConjMin.getValue();
					sliderLinhasConjMin.setToolTipText("numeroMinimoLinhasConjuntoPixeis: "+numeroMinimoLinhasConjuntoPixeis);
					valorLinhasConjMin.setText(""+numeroMinimoLinhasConjuntoPixeis);
				}
			});
			barraLinhasConjMin.add(sliderLinhasConjMin);
			barraLinhasConjMin.add(valorLinhasConjMin);
			sliders.add(barraLinhasConjMin);
			
			ativarLinhaMedia = new JButton("ativarLinhaMedia: ON");
			ativarLinhaMedia.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					if(ativarLinhaMedia.getText().equals("ativarLinhaMedia: OFF")){
						ativarLinhaMedia.setText("ativarLinhaMedia: ON");
						criarLinhaMediaConjuntoPixeis = true;
					}else{
						ativarLinhaMedia.setText("ativarLinhaMedia: OFF");
						criarLinhaMediaConjuntoPixeis = false;
					}
				}
			});
			sliders.add(ativarLinhaMedia);
			
			painel.add(sliders, BorderLayout.SOUTH);
		}
		return painel;
	}
	
	private JDialog d;
	public void openDialogConfiguracao(JFrame parent) {
		if(d==null){
			d = new JDialog(parent);
			d.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			d.setTitle(getName());
			d.setLayout(new BorderLayout(0,0));
			d.add(openPanelConfiguracao(), BorderLayout.CENTER);
		}
		d.setVisible(true);
		d.pack();
	}
	
	//--------------------------
	
	private JDialog dTetris;
	private PainelTetris tetris;
	public void openDialogJogoTetris(JFrame parent) {
		if(jogoTetris){
			//OK
		}else{
			jogoTetris = true;
		}
		if(dTetris==null){
			dTetris = new JDialog(parent);
			dTetris.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			dTetris.setTitle(getName());
			tetris = new PainelTetris();
			dTetris.add(tetris);
			tetris.setFocusable(true);
			tetris.setRequestFocusEnabled(true);
		}
		dTetris.setVisible(true);
		dTetris.pack();
		tetris.iniciaJogo();
	}
	
	public void close() {
		if(tetris!=null) tetris.terminaJogo();
	}
	
}
