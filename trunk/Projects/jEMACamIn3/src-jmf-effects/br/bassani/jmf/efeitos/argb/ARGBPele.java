package br.bassani.jmf.efeitos.argb;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.bassani.jmf.efeitos.IConfiguracao;

public class ARGBPele extends ARGB_OUT_Effect implements Effect, IConfiguracao {

	private static final String effectname = "ARGBPele";

	public ARGBPele(){
		super();
		mudarCorFundo = false;
	}

	private boolean mudarCorFundo;
	
	private boolean funcaoRmaiorGmaiorBmaiorMin = false;
	private boolean intervaloCor = true;
	private int rMinimo = 119;
	private int rMaximo = 255;
	private int gMinimo = 81;
	private int gMaximo = 211;
	private int bMinimo = 59;
	private int bMaximo = 154;
	
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
				
				if(funcaoRmaiorGmaiorBmaiorMin){
					if ( (r > g) && (g > b) && ( b >= bMinimo ) ) {
						outData[index + 3] = (byte)255;
					} else {
						if(mudarCorFundo){
							r = g = b = 0;
						}
					}
				}else if(intervaloCor){
					if ( (r >= rMinimo ) && (r <= rMaximo ) && (g >= gMinimo ) &&
							(g <= gMaximo ) && (b >= bMinimo ) && (b <= bMaximo ) ) {
						outData[index + 3] = (byte)255;
					} else {
						if(mudarCorFundo){
							r = g = b = 0;
						}
					}
				} else {
					if(mudarCorFundo){
						r = g = b = 0;
					}
				}
				
				outData[index + 2] = (byte)r;
				outData[index + 1] = (byte)g;
				outData[index + 0] = (byte)b;
			}
		}
		return BUFFER_PROCESSED_OK;
	}

	public String getName() {
		return effectname;
	}

	//Objetos Graficos da Configuracao
	private JPanel painel;
	
	JButton colorMark;
	JLabel valorRMin, valorRMax, valorGMin, valorGMax, valorBMin, valorBMax;
	JSlider labelRMin, labelRMax, labelGMin, labelGMax, labelBMin, labelBMax;
	
	public JPanel openPanelConfiguracao(){
		if(painel==null){
			painel = new JPanel();
			painel.setLayout(new BorderLayout(5,5));

			//painel.add(new JLabel(" -- Propriedades -- "), BorderLayout.NORTH);
			
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
			painel.add(colorMark, BorderLayout.NORTH);
			
			JRadioButton metodoFuncaoPre, metodoIntervalo;
			metodoFuncaoPre = new JRadioButton("Funcao: r>g && g>b && b>='bMinimo'");
			if(funcaoRmaiorGmaiorBmaiorMin) metodoFuncaoPre.setSelected(true);
			metodoFuncaoPre.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					intervaloCor = false;
					funcaoRmaiorGmaiorBmaiorMin = true;
				}
			});
			
			metodoIntervalo = new JRadioButton("Funcao: r,g,b <> [Min-Max]");
			if(intervaloCor) metodoIntervalo.setSelected(true);
			metodoIntervalo.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event) {
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
				public void stateChanged(ChangeEvent event) {
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
				public void stateChanged(ChangeEvent event) {
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
				public void stateChanged(ChangeEvent event) {
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
				public void stateChanged(ChangeEvent event) {
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
				public void stateChanged(ChangeEvent event) {
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
				public void stateChanged(ChangeEvent event) {
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

}
