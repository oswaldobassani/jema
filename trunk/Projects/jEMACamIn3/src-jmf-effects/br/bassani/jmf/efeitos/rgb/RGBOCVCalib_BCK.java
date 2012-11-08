package br.bassani.jmf.efeitos.rgb;

import itesm.gvision.apps.calibrator.CalibratorOCV;
import itesm.gvision.image.tools.ImageTools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.Effect;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.bassani.jmf.efeitos.IConfiguracao;

import com.sun.media.util.BufferToBufferedImage;

public class RGBOCVCalib_BCK extends RGBEffect implements Effect, IConfiguracao {

	private static final String effectname = "RGBOCVCalib_BCK";
	
	/** OCV Bridge */
	private CalibratorOCV cocv = new CalibratorOCV();
	
	private BufferToBufferedImage conversor = null;

	public RGBOCVCalib_BCK(){
		super();
	}

	private boolean funcaoRmaiorGmaiorBmaiorMin = true;
	private boolean intervaloCor = false;
	private int rMinimo = 119;
	private int rMaximo = 255;
	private int gMinimo = 81;
	private int gMaximo = 211;
	private int bMinimo = 59;
	private int bMaximo = 154;
	
	private int countImagensAdicionadas = 0;
	private Vector<int[]> imagesCalib = new Vector<int[]>();
	private int countImagensProCalculo = -1;
	private boolean calibCalculada = false;
	private double[] matrizCalib;
	
	private long t0, t1;
	private int count0, count1, count2, count3;
	
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
				
						outData[index + 2] = (byte)r;
						outData[index + 1] = (byte)g;
						outData[index + 0] = (byte)b;
			}
		}
		
		long seqNumber = inBuffer.getSequenceNumber();
		
		if(seqNumber%5==0){
			count0 = count1 = count2 = count3 = 0;
			t0 = System.currentTimeMillis();
//		}
		
		int[] imageData = null;
		if(conversor==null){
			try {
				conversor = new BufferToBufferedImage((VideoFormat)inputFormat);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}else{
			try {
				/*
				 * TODO: Dar compatibilidade total.	
				 * java.lang.ClassCastException: [B cannot be cast to [I
				 */
				//System.out.println(inData.length);
				//System.out.println((inData.length/(640*480)));
				int[] inData2;
				/*if(inData.length==iw*ih){*/
					inData2 = new int[inData.length];
					for(int i=0; i<inData.length; i++){
						inData2[i] = inData[i];
					}/*
				}else{
					if(inData.length==iw*ih*3){
						inData2 = new int[inData.length/3];
						for(int i=0; i<inData.length; i+=3){
							inData2[i/3] = inData[i]<<16 & inData[i+1]<<8 & inData[i+2]<<0;
						}
					}else{
						inData2 = new int[0];
					}
				}**/
				inBuffer.setData(inData2);
				BufferedImage img = (BufferedImage)conversor.createImage(inBuffer);
				ImageIO.write(img, "JPEG", new File("temp"+seqNumber+"_"+countImagensAdicionadas+"_pre_.jpg"));
				inBuffer.setData(inData);
				
				
				BufferedImage bi = new BufferedImage(iw, ih, BufferedImage.TYPE_BYTE_GRAY);
				byte imageDataBI[] = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
				for(int i=0; i<imageDataBI.length; i++){
					imageDataBI[i] = (byte)((inData[3*i]+inData[3*i+1]+inData[3*i+2])/3);
				}
				ImageIO.write(bi, "JPEG", new File("temp"+seqNumber+"_"+countImagensAdicionadas+"_pre2_.jpg"));
				
				//System.out.println(c);
				//System.out.println(img.getColorModel().toString());
				
				
				int[] inData3 = new int[inData.length/3];
				for(int i=0; i<inData.length; i+=3){
					inData3[i/3] = inData[i]<<16 & inData[i+1]<<8 & inData[i+2]<<0;
				}
				BufferedImage img2 = ImageTools.getBufferedImage(inData3, iw, ih);
				
				ImageIO.write(img2, "JPEG", new File("temp"+seqNumber+"_"+countImagensAdicionadas+".jpg"));
				
				//double pnts[] = this.cocv.findCorners(ImageTools.getImageDataRGB(img), iw,  ih);
				/*int[]*/ imageData = ImageTools.getImageDataRGB(img2);
				double pnts[] = this.cocv.findCorners(imageData, iw,  ih);
				
				if(pnts==null){
					//System.out.println("Nao achou!!!");
				}else{
					// 96 = 6 * 8 * 2
					if (pnts.length == 96){
						System.out.println("Achou!!!");
						
						countImagensAdicionadas++;
						//this.cocv.addCalibrationImage(imageData, iw,  ih);
						imagesCalib.add(imageData);
						
						for(int i=0; i<96; i+=2){
							//System.out.println("("+pnts[i]+", "+pnts[i+1]+")");
							
							int xPos = (int)Math.round(pnts[i]);
							int yPos = (int)Math.round(pnts[i+1]);
							int indexPos = xPos * 3 + yPos * iw * 3;
							if(indexPos+1<outData.length) outData[indexPos + 1] = (byte)255;
							if(indexPos+1+3<outData.length) outData[indexPos + 1 + 3] = (byte)255;
							if(indexPos+1-3>0) outData[indexPos + 1 - 3] = (byte)255;
							if(indexPos+1+3*iw<outData.length) outData[indexPos + 1 + 3*iw] = (byte)255;
							if(indexPos+1-3*iw>0) outData[indexPos + 1 - 3*iw] = (byte)255;
						}
					}else{
						//System.out.println("Quase achou!!!");
					}
				}
				
			} catch (RuntimeException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		boolean fazerCalib = false;
		if(!calibCalculada){
			if(countImagensAdicionadas>=4){
				fazerCalib = true;
			}
		}else{
			if(countImagensAdicionadas>countImagensProCalculo+3){
				fazerCalib = true;
			}
		}
		if(fazerCalib){
			
			for(int i=0; i<this.imagesCalib.size(); i++){
				System.out.println("matrizCalib: addCalibrationImage ("+i+")");
				this.cocv.addCalibrationImage(this.imagesCalib.get(i), iw, ih);
			}
			
			System.out.println("matrizCalib: calibrate ...");
			if(this.cocv.calibrate()){
				countImagensProCalculo = countImagensAdicionadas;
				matrizCalib = this.cocv.getResults();
				
				System.out.println("matrizCalib: ");
				for(int i=0; i<matrizCalib.length; i++){
					System.out.print(matrizCalib[i]+" ");
				}
				System.out.println("");
				
				calibCalculada = true;
			}else{
				System.out.println("matrizCalib: falhou!!!");
			}
			this.imagesCalib.clear();
			countImagensAdicionadas = 0;
		}else{
			if(calibCalculada){
				if(imageData!=null){
					System.out.println("getUndistortedImage: ...");
					int[] imagemNova = this.cocv.getUndistortedImage(imageData, iw, ih);
					System.out.println("getUndistortedImage: imagemNova array size: "+imagemNova.length);
					for(int i=0; i<imagemNova.length; i++){
						outData[i*3+0] = (byte)(imagemNova[i]>>16 & 255);//16
						outData[i*3+1] = (byte)(imagemNova[i]>>8 & 255);//8
						outData[i*3+2] = (byte)(imagemNova[i]>>0 & 255);
					}
				}
			}
		}
		
//		if(seqNumber%10==0){
			t1 = System.currentTimeMillis();
			System.out.println("Convert: rgb OCV loop \t "+(t1-t0)+" ms.");
			t0 = System.currentTimeMillis();
		}
		
		return BUFFER_PROCESSED_OK;
	}

	public String getName() {
		return effectname;
	}

	//Objetos Graficos da Configuracao
	private JPanel painel;
	
	JLabel valorRMin, valorRMax, valorGMin, valorGMax, valorBMin, valorBMax;
	JSlider labelRMin, labelRMax, labelGMin, labelGMax, labelBMin, labelBMax;
	
	public JPanel openPanelConfiguracao(){
		if(painel==null){
			painel = new JPanel();
			painel.setLayout(new BorderLayout(5,5));

			painel.add(new JLabel(" -- Propriedades -- "), BorderLayout.NORTH);
			
			JRadioButton metodoFuncaoPre, metodoIntervalo;
			metodoFuncaoPre = new JRadioButton("Funcao: r>g && g>b && b>='bMinimo'");
			if(funcaoRmaiorGmaiorBmaiorMin) metodoFuncaoPre.setSelected(true);
			metodoFuncaoPre.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					intervaloCor = false;
					funcaoRmaiorGmaiorBmaiorMin = true;
				}
			});
			
			metodoIntervalo = new JRadioButton("Funcao: r,g,b <> [Min-Max]");
			if(intervaloCor) metodoIntervalo.setSelected(true);
			metodoIntervalo.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					funcaoRmaiorGmaiorBmaiorMin = false;
					intervaloCor = true;
				}
			});
			
			ButtonGroup group = new ButtonGroup();
			group.add(metodoFuncaoPre);
			group.add(metodoIntervalo);
			
			JPanel metodo = new JPanel(new GridLayout(-1,1,5,5));
			metodo.add(metodoFuncaoPre);
			metodo.add(metodoIntervalo);
			painel.add(metodo, BorderLayout.CENTER);
			
			JPanel sliders = new JPanel(new GridLayout(-1,1,5,5));
			
			JPanel barraRMin = new JPanel(new FlowLayout(FlowLayout.LEFT));
			barraRMin.add(new JLabel("rMin: "));
			valorRMin = new JLabel("-----");
			labelRMin = new JSlider(0, 255, 1);
			labelRMin.setValue(rMinimo);
			labelRMin.setPaintLabels(false);
			labelRMin.setPaintTicks(false);
			labelRMin.setPaintTrack(true);
			labelRMin.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent arg0) {
					rMinimo = labelRMin.getValue();
					labelRMin.setToolTipText("rMinimo: "+rMinimo);
					valorRMin.setText(""+rMinimo);
				}
			});
			barraRMin.add(labelRMin);
			barraRMin.add(valorRMin);
			sliders.add(barraRMin);
			// -------------------------------
			JPanel barraRMax = new JPanel(new FlowLayout(FlowLayout.LEFT));
			barraRMax.add(new JLabel("rMax: "));
			valorRMax = new JLabel("-----");
			labelRMax = new JSlider(0, 255, 1);
			labelRMax.setValue(rMaximo);
			labelRMax.setPaintLabels(false);
			labelRMax.setPaintTicks(false);
			labelRMax.setPaintTrack(true);
			labelRMax.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent arg0) {
					rMaximo = labelRMax.getValue();
					labelRMax.setToolTipText("rMaximo: "+rMaximo);
					valorRMax.setText(""+rMaximo);
				}
			});
			barraRMax.add(labelRMax);
			barraRMax.add(valorRMax);
			sliders.add(barraRMax);
			// -------------------------------
			JPanel barraGMin = new JPanel(new FlowLayout(FlowLayout.LEFT));
			barraGMin.add(new JLabel("gMin: "));
			valorGMin = new JLabel("-----");
			labelGMin = new JSlider(0, 255, 1);
			labelGMin.setValue(gMinimo);
			labelGMin.setPaintLabels(false);
			labelGMin.setPaintTicks(false);
			labelGMin.setPaintTrack(true);
			labelGMin.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent arg0) {
					gMinimo = labelGMin.getValue();
					labelGMin.setToolTipText("gMinimo: "+gMinimo);
					valorGMin.setText(""+gMinimo);
				}
			});
			barraGMin.add(labelGMin);
			barraGMin.add(valorGMin);
			sliders.add(barraGMin);
			// -------------------------------
			JPanel barraGMax = new JPanel(new FlowLayout(FlowLayout.LEFT));
			barraGMax.add(new JLabel("gMax: "));
			valorGMax = new JLabel("-----");
			labelGMax = new JSlider(0, 255, 1);
			labelGMax.setValue(gMaximo);
			labelGMax.setPaintLabels(false);
			labelGMax.setPaintTicks(false);
			labelGMax.setPaintTrack(true);
			labelGMax.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent arg0) {
					gMaximo = labelGMax.getValue();
					labelGMax.setToolTipText("gMaximo: "+gMaximo);
					valorGMax.setText(""+gMaximo);
				}
			});
			barraGMax.add(labelGMax);
			barraGMax.add(valorGMax);
			sliders.add(barraGMax);
			// -------------------------------
			JPanel barraBMin = new JPanel(new FlowLayout(FlowLayout.LEFT));
			barraBMin.add(new JLabel("bMin: "));
			valorBMin = new JLabel("-----");
			labelBMin = new JSlider(0, 255, 1);
			labelBMin.setValue(bMinimo);
			labelBMin.setPaintLabels(false);
			labelBMin.setPaintTicks(false);
			labelBMin.setPaintTrack(true);
			labelBMin.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent arg0) {
					bMinimo = labelBMin.getValue();
					labelBMin.setToolTipText("bMinimo: "+bMinimo);
					valorBMin.setText(""+bMinimo);
				}
			});
			barraBMin.add(labelBMin);
			barraBMin.add(valorBMin);
			sliders.add(barraBMin);
			// -------------------------------
			JPanel barraBMax = new JPanel(new FlowLayout(FlowLayout.LEFT));
			barraBMax.add(new JLabel("bMax: "));
			valorBMax = new JLabel("-----");
			labelBMax = new JSlider(0, 255, 1);
			labelBMax.setValue(bMaximo);
			labelBMax.setPaintLabels(false);
			labelBMax.setPaintTicks(false);
			labelBMax.setPaintTrack(true);
			labelBMax.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent arg0) {
					bMaximo = labelBMax.getValue();
					labelBMax.setToolTipText("bMaximo: "+bMaximo);
					valorBMax.setText(""+bMaximo);
				}
			});
			barraBMax.add(labelBMax);
			barraBMax.add(valorBMax);
			sliders.add(barraBMax);
			// -------------------------------
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

	public int getBMaximo() {
		return bMaximo;
	}

	public void setBMaximo(int maximo) {
		bMaximo = maximo;
	}

	public int getBMinimo() {
		return bMinimo;
	}

	public void setBMinimo(int minimo) {
		bMinimo = minimo;
	}

	public int getGMaximo() {
		return gMaximo;
	}

	public void setGMaximo(int maximo) {
		gMaximo = maximo;
	}

	public int getGMinimo() {
		return gMinimo;
	}

	public void setGMinimo(int minimo) {
		gMinimo = minimo;
	}

	public int getRMaximo() {
		return rMaximo;
	}

	public void setRMaximo(int maximo) {
		rMaximo = maximo;
	}

	public int getRMinimo() {
		return rMinimo;
	}

	public void setRMinimo(int minimo) {
		rMinimo = minimo;
	}

	public boolean isFuncaoRmaiorGmaiorBmaiorMin() {
		return funcaoRmaiorGmaiorBmaiorMin;
	}

	public void setFuncaoRmaiorGmaiorBmaiorMin(boolean funcaoRmaiorGmaiorBmaiorMin) {
		this.funcaoRmaiorGmaiorBmaiorMin = funcaoRmaiorGmaiorBmaiorMin;
	}

	public boolean isIntervaloCor() {
		return intervaloCor;
	}

	public void setIntervaloCor(boolean intervaloCor) {
		this.intervaloCor = intervaloCor;
	}

}
