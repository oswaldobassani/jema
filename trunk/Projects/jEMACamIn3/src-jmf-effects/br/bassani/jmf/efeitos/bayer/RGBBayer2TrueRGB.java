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

public class RGBBayer2TrueRGB extends RGBBayerEffect implements Effect, IConfiguracao {

	private static final String effectname = "RGBBayer2TrueRGB";

	public RGBBayer2TrueRGB(){
		super();
	}
	
	private long t0, t1;
	private int count0, count1, count2, count3;

	private int[] inValues = null;

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

		long seqNumber = inBuffer.getSequenceNumber();
		
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
		
		int r,g,b;
		
		//int alphaVal = 255<<24;
		
		if(inValues==null || inValues.length!=iw*ih){
			inValues = new int[iw*ih];
		}
		
		if(seqNumber%10==0){
			count0 = count1 = count2 = count3 = 0;
			t0 = System.currentTimeMillis();
		}
		
		for(x = 0; x < iw; x++ ){
			for(y = 0; y < ih; y++ ) {
				int index = x + y * iw;
				int val = inData[index];
				//r = int2red(val);
				//g = int2green(val);
				b = int2blue(val);
				inValues[index] = b;
				
				if((y&1)==0){
					if((x&1)==0) {
						// red
						//outData[index] = alphaVal & (val<<16);
						outData[index] = (b<<16);
					}else{
						// green 1
						//outData[index] = alphaVal & (val<<8);
						outData[index] = (b<<8);
					}
				}else{
					if((x&1)==0) {
						 // green 2
						//outData[index] = alphaVal & (val<<8);
						outData[index] = (b<<8);
					}else{
						// blue
						//outData[index] = alphaVal & val;
						outData[index] = b;
					}
				}
			}
		}
		
		if(seqNumber%10==0){
			t1 = System.currentTimeMillis();
			System.out.println("Convert: rgb basico loop \t "+(t1-t0)+" ms.");
			t0 = System.currentTimeMillis();
		}
		
		/*
		 * Com o 'true' abaixo:
		 * 
		 * Convert: rgb basico loop 	 9 ms.
		 * Convert: rgb basico loop 	 8 ms.
		 * Convert: rgb basico loop 	 10 ms.
		 * 
		 *  >> 30 fps
		 * 
		 */
		
		if(true){
			//Interrompe a correcao completa de cores
			return BUFFER_PROCESSED_OK;
		}
		
		int index, val;
		int valMissingR, valMissingG, valMissingB;
		for(y = 1; y < ih-1; y++) {
			for(x = 1; x < iw-1; x++) {
				index = y * iw + x;
				val = outData[index];
				if((y&1)==0){
					if((x&1)==0) {
						if(false){
							// red
							// Fill in missing green pixels
							valMissingG = ( inValues[index-iw]
											+ inValues[index-1]
											+ inValues[index+1]
											+ inValues[index+iw] ) >> 2;
							// (need blue)
							valMissingB = ( inValues[index-iw+1]
											+ inValues[index-iw-1]
											+ inValues[index+iw+1]
											+ inValues[index+iw-1] ) >> 2;
							outData[index] = val | (valMissingG<<8) | valMissingB;
							count0++;
						}
					}else{
						// green 1
						// (need red/blue)
						valMissingR = (inValues[index-1] + inValues[index+1]) >> 1;
						valMissingB = (inValues[index-iw] + inValues[index+iw]) >> 1;
						outData[index] = val | (valMissingR<<16) | (valMissingB);
						count1++;
					}
				}else{
					if((x&1)==0) {
						// green 2
						// (need red/blue)
						valMissingB = (inValues[index-1] + inValues[index+1]) >> 1;
						valMissingR = (inValues[index-iw] + inValues[index+iw]) >> 1;
						outData[index] = val | (valMissingR<<16) | (valMissingB);
						count2++;
					}else{
						if(false){
							// blue
							// Fill in missing green pixels
							valMissingG = ( inValues[index-iw]
											+ inValues[index-1]
											+ inValues[index+1]
											+ inValues[index+iw] ) >> 2;
							// (need red)
							valMissingR = ( inValues[index-iw+1]
											+ inValues[index-iw-1]
											+ inValues[index+iw+1]
											+ inValues[index+iw-1] ) >> 2;
							outData[index] = val | (valMissingR<<16) | (valMissingG<<8);
							count3++;
						}
					}
				}
			}
		}
		
		if(seqNumber%10==0){
			t1 = System.currentTimeMillis();
			System.out.println("Convert: rgb extras loop \t "+(t1-t0)+" ms.");
			
			System.out.println("Convert: rgb extras - count0 \t "+count0);
			System.out.println("Convert: rgb extras - count1 \t "+count1);
			System.out.println("Convert: rgb extras - count2 \t "+count2);
			System.out.println("Convert: rgb extras - count3 \t "+count3);
			
			t0 = System.currentTimeMillis();
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
