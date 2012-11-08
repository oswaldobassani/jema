package br.bassani.jmf.efeitos.bayer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.bassani.jmf.efeitos.IConfiguracao;

public class RGBBayer2TrueRGBFase3 extends RGBBayerEffect implements Effect, IConfiguracao {

	private static final String effectname = "RGBBayer2TrueRGBFase3";

	public RGBBayer2TrueRGBFase3(){
		super();
	}

	public int process(Buffer inBuffer, Buffer outBuffer) {
		int outputDataLength = ((VideoFormat)outputFormat).getMaxDataLength();
		validateIntArraySize(outBuffer, outputDataLength);
		
		outBuffer.setLength(outputDataLength);
		outBuffer.setFormat(outputFormat);
		outBuffer.setFlags(inBuffer.getFlags());
		
		int [] inData = (int[]) inBuffer.getData();
		int [] outData = (int[]) outBuffer.getData();
		RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
		Dimension sizeIn = vfIn.getSize();

		if ( inData.length == 0 ) {
			return BUFFER_PROCESSED_FAILED;
		}
		
		int iw = sizeIn.width;
		int ih = sizeIn.height;
		
		int x, y;
		
		if ( outData.length < iw*ih ) {
			System.out.println("the buffer is not full");
			return BUFFER_PROCESSED_FAILED;
		}
		
		int index, val;
		int valMissingR, valMissingG, valMissingB;
		
		for(y = 1; y < ih-1; y++) {
			for(x = 1; x < iw-1; x++) {
				index = y * iw + x;
				val = inData[index];
				if((y&1)==0){
					if((x&1)==0) {
						/*
						// red
						// Fill in missing green pixels
						valMissingG = ( int2green(inData[index-iw])
										+ int2green(inData[index-1])
										+ int2green(inData[index+1])
										+ int2green(inData[index+iw]) ) >> 2;
						// (need blue)
						valMissingB = ( int2blue(inData[index-iw+1])
										+ int2blue(inData[index-iw-1])
										+ int2blue(inData[index+iw+1])
										+ int2blue(inData[index+iw-1]) ) >> 2;
						outData[index] = val | (valMissingG<<8) | valMissingB;
						*/
						
						
						//int2green(inData[index-1]);
						
						//valMissingG = ( int2green(inData[index-1]) + int2green(inData[index+1])) >> 2;
						//outData[index] = val | (valMissingG<<8);
						
						outData[index] = val;
						
					}else{
						/*
						// green 1
						// (need red/blue)
						valMissingR = (int2red(inData[index-1]) + int2red(inData[index+1])) >> 1;
						valMissingB = (int2blue(inData[index-iw]) + int2blue(inData[index+iw])) >> 1;
						outData[index] = val | (valMissingR<<16) | (valMissingB);
						*/
						outData[index] = val;
					}
				}else{
					if((x&1)==0) {
						/*
						// green 2
						// (need red/blue)
						valMissingB = (int2blue(inData[index-1]) + int2blue(inData[index+1])) >> 1;
						valMissingR = (int2red(inData[index-iw]) + int2red(inData[index+iw])) >> 1;
						outData[index] = val | (valMissingR<<16) | (valMissingB);
						*/
						outData[index] = val;
					}else{
						/*
						// blue
						// Fill in missing green pixels
						valMissingG = ( int2green(inData[index-iw])
										+ int2green(inData[index-1])
										+ int2green(inData[index+1])
										+ int2green(inData[index+iw]) ) >> 2;
						// (need red)
						valMissingR = ( int2red(inData[index-iw+1])
										+ int2red(inData[index-iw-1])
										+ int2red(inData[index+iw+1])
										+ int2red(inData[index+iw-1]) ) >> 2;
						outData[index] = val | (valMissingR<<16) | (valMissingG<<8);
						*/
						outData[index] = val;
					}
				}
			}
		}
		
		return BUFFER_PROCESSED_OK;
	}

	public String getName() {
		return effectname;
	}

	//Objetos Graficos da Configuracao
	private JPanel painel;
	
	public JPanel openPanelConfiguracao(){
		if(painel==null){
			painel = new JPanel();
			painel.setLayout(new BorderLayout(5,5));

			painel.add(new JLabel(" -- Propriedades -- "), BorderLayout.NORTH);

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
