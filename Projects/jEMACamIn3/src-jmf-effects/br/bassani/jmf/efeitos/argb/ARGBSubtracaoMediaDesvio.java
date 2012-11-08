package br.bassani.jmf.efeitos.argb;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.bassani.jmf.efeitos.IConfiguracao;

public class ARGBSubtracaoMediaDesvio extends ARGB_OUT_Effect implements IConfiguracao{
	
	private static final String effectname = "ARGBSubtracaoMediaDesvio";
	
	private int numero_quadros_pulados;
	private int numero_quadros_media;
//	private int desvio_maximo_soma;
	
	private boolean mudarCorFundo;
	private int count_quadros;
	
	private float fatorDesvio = 1.0f;
	
	public ARGBSubtracaoMediaDesvio(){
		this(5, 10/*, 1200*/);
	}
	
	public ARGBSubtracaoMediaDesvio(int pularQuadros, int quadrosDaMedia/*, int somaQuadradoComponentesMaxima*/){
		super();

		mudarCorFundo = false;
		count_quadros = 0;
		
		numero_quadros_pulados = pularQuadros;
		numero_quadros_media = quadrosDaMedia;
		/*desvio_maximo_soma = somaQuadradoComponentesMaxima;*/
	}
	
	int[] mediaInData;
	//(a-b)^2
	int[] desvioComponenteA2;
	int[] desvioComponenteMenos2A;
	int[] desvioInData;
	
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
			mediaInData = new int[outData.length];
			desvioComponenteA2 = new int[outData.length];
			desvioComponenteMenos2A = new int[outData.length];
			desvioInData = new int[outData.length];
		}
		
		if ( inData.length == 0 ) {
			System.out.println("inData length: "+inData.length);
			return BUFFER_PROCESSED_FAILED;
		}
		
		int iw = sizeIn.width;
		int ih = sizeIn.height;
		
		int x, y;
		
		if ( outData.length < iw*ih*4 ) {
			System.out.println("the buffer is not full");
			return BUFFER_PROCESSED_FAILED;
		}
		
		int r,g,b;
		
		for (  x = 0; x < iw; x++ ){
			for (  y = 0; y < ih; y++ ) {
				int index = x * 4 + y * iw * 4;
				int indexRGB = x * 3 + y * iw * 3;
				r = byte2int(inData[indexRGB + 2]);
				g = byte2int(inData[indexRGB + 1]);
				b = byte2int(inData[indexRGB + 0]);
				
				outData[index + 3] = (byte)0;
				
				if(count_quadros>numero_quadros_pulados && count_quadros<numero_quadros_pulados+numero_quadros_media){
					mediaInData[index + 2] += r;
					mediaInData[index + 1] += g;
					mediaInData[index + 0] += b;
					
					desvioComponenteA2[index + 2] += r*r;
					desvioComponenteMenos2A[index + 2] += -2*r;
					desvioComponenteA2[index + 1] += g*g;
					desvioComponenteMenos2A[index + 1] += -2*g;
					desvioComponenteA2[index + 0] += b*b;
					desvioComponenteMenos2A[index + 0] += -2*b;
					
				}else if(count_quadros==numero_quadros_pulados+numero_quadros_media){
					mediaInData[index + 2] = mediaInData[index + 2]/(numero_quadros_media-1);
					mediaInData[index + 1] = mediaInData[index + 1]/(numero_quadros_media-1);
					mediaInData[index + 0] = mediaInData[index + 0]/(numero_quadros_media-1);
					
					desvioInData[index + 2] = (int)Math.pow( ( desvioComponenteA2[index + 2] + desvioComponenteMenos2A[index + 2] * mediaInData[index + 2] + numero_quadros_media * Math.pow(mediaInData[index + 2], 2) ) / (numero_quadros_media-1) , 0.5);
					desvioInData[index + 1] = (int)Math.pow( ( desvioComponenteA2[index + 1] + desvioComponenteMenos2A[index + 1] * mediaInData[index + 1] + numero_quadros_media * Math.pow(mediaInData[index + 1], 2) ) / (numero_quadros_media-1) , 0.5);
					desvioInData[index + 0] = (int)Math.pow( ( desvioComponenteA2[index + 0] + desvioComponenteMenos2A[index + 0] * mediaInData[index + 0] + numero_quadros_media * Math.pow(mediaInData[index + 0], 2) ) / (numero_quadros_media-1) , 0.5);
				}else if(count_quadros>numero_quadros_pulados+numero_quadros_media){
					//if(desvio(mediaInData[index + 2], mediaInData[index + 1], mediaInData[index + 0], r, b, g)<desvio_maximo_soma){
					if(Math.abs(r - mediaInData[index + 2])<=fatorDesvio*desvioInData[index + 2] &&
							Math.abs(g - mediaInData[index + 1])<=fatorDesvio*desvioInData[index + 1] &&
							Math.abs(b - mediaInData[index + 0])<=fatorDesvio*desvioInData[index + 0]){
						if(mudarCorFundo){
							r = g = b = 0;
						}
						//r = g = b = 0;
						//outData[index + 3] = (byte)0;
					}else{
						//r = 255;
						//g = b = 0;
						outData[index + 3] = (byte)255;
					}
				}
				outData[index + 2] = (byte)r;
				outData[index + 1] = (byte)g;
				outData[index + 0] = (byte)b;
			}
		}
		
		if(count_quadros==numero_quadros_pulados+numero_quadros_media){
			int[] desvioInDataImg = new int[desvioInData.length];
			int maxVal = 0;
			for(int i=0; i<desvioInData.length; i++){
				desvioInDataImg[i] = desvioInData[i];
				if(desvioInData[i]>maxVal) maxVal = desvioInData[i];
			}
			if(maxVal>=255){
				for(int i=0; i<desvioInData.length; i++){
					desvioInDataImg[i] = desvioInDataImg[i]*255/maxVal;
				}
			}
			BufferedImage bi = new BufferedImage(iw, ih, BufferedImage.TYPE_3BYTE_BGR);
			BufferedImage biDes = new BufferedImage(iw, ih, BufferedImage.TYPE_3BYTE_BGR);
			byte imageDataBI[] = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
			byte imageDataBIDes[] = ((DataBufferByte)biDes.getRaster().getDataBuffer()).getData();
			for (  x = 0; x < iw; x++ ){
				for (  y = 0; y < ih; y++ ) {
					int index = x * 4 + y * iw * 4;
					int indexRGB = x * 3 + y * iw * 3;
					
					int img = x * 3 + (ih-1-y) * iw * 3;
					
					imageDataBI[img+0] = (byte)(mediaInData[index+0]);
					imageDataBI[img+1] = (byte)(mediaInData[index+1]);
					imageDataBI[img+2] = (byte)(mediaInData[index+2]);
					
					imageDataBIDes[img+0] = (byte)(desvioInDataImg[index+0]);
					imageDataBIDes[img+1] = (byte)(desvioInDataImg[index+1]);
					imageDataBIDes[img+2] = (byte)(desvioInDataImg[index+2]);
				}
			}
			try {
				ImageIO.write(bi, "PNG", new File("media.png"));
				ImageIO.write(biDes, "PNG", new File("desvio.png"));
			} catch (IOException e) {
				e.printStackTrace();
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

	//Objetos Graficos da Configuracao
	private JPanel painel;
	private JButton colorMark;
	
	JLabel labelFatorDesvio;
	JSlider valorFatorDesvio;
	
	public JPanel openPanelConfiguracao() {
		if(painel==null){
			painel = new JPanel();
			painel.setLayout(new BorderLayout(5,5));
			
			JPanel painelBotoes = new JPanel();
			painelBotoes.setLayout(new BorderLayout(5,5));
			painelBotoes.add(new JLabel("Comandos"), BorderLayout.NORTH);
			
			JButton b = new JButton("RESET");
			b.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					count_quadros = 0;
				}
			});
			painelBotoes.add(b, BorderLayout.CENTER);
			
			colorMark = new JButton("colorMark: OFF");
			colorMark.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					if(colorMark.getText().equals("colorMark: OFF")){
						colorMark.setText("colorMark: ON");
						mudarCorFundo = true;
					}else{
						colorMark.setText("colorMark: OFF");
						mudarCorFundo = false;
					}
				}
			});
			painelBotoes.add(colorMark, BorderLayout.SOUTH);
			
			JPanel sliders = new JPanel(new GridLayout(-1,1,5,5));
			
			JPanel barraRMin = new JPanel(new FlowLayout(FlowLayout.LEFT));
			barraRMin.add(new JLabel("Fator Desvio: "));
			labelFatorDesvio = new JLabel("-----");
			valorFatorDesvio = new JSlider(0, 200, 1);
			valorFatorDesvio.setValue((int)(fatorDesvio * 100));
			valorFatorDesvio.setPaintLabels(false);
			valorFatorDesvio.setPaintTicks(false);
			valorFatorDesvio.setPaintTrack(true);
			valorFatorDesvio.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent event) {
					fatorDesvio = (float)valorFatorDesvio.getValue()/100.0f;
					valorFatorDesvio.setToolTipText("Fator Desvio: "+fatorDesvio);
					labelFatorDesvio.setText(""+fatorDesvio);
				}
			});
			barraRMin.add(valorFatorDesvio);
			barraRMin.add(labelFatorDesvio);
			sliders.add(barraRMin);
			
			painel.add(painelBotoes, BorderLayout.CENTER);
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
	
}
