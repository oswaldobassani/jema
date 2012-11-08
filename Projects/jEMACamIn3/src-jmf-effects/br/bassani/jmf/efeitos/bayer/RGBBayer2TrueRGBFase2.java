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

public class RGBBayer2TrueRGBFase2 extends RGBBayerEffect implements Effect, IConfiguracao {

	private static final String effectname = "RGBBayer2TrueRGBFase2";

	public RGBBayer2TrueRGBFase2(){
		super();
	}
	
	private long t0, t1;

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
		
		if(seqNumber%10==0){
			t0 = System.currentTimeMillis();
		}
		
		int index, val;
		int valMissingR, valMissingG, valMissingB;
		
		for(y = 1; y < ih-1; y++) {
			for(x = 1; x < iw-1; x++) {
				index = y * iw + x;
				val = inData[index];
				outData[index] = val;
				if((y&1)==0){
					if((x&1)==0) {
						/* */
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
						/* */
						//outData[index] = val;
					}else{
						/*
						 * Com apenas 1 dos green calculados
						 * eh possivel ter 30fps
						 * custo do processamento 1 ou 2 ms
						 */
						/* */
						// green 1
						// (need red/blue)
						valMissingR = (int2red(inData[index-1]) + int2red(inData[index+1])) >> 1;
						valMissingB = (int2blue(inData[index-iw]) + int2blue(inData[index+iw])) >> 1;
						outData[index] = val | (valMissingR<<16) | (valMissingB);
						/* */
					}
				}else{
					if((x&1)==0) {
						/*
						 * Usando o 'pause', com green1 e green2, 30fps
						 */
						/* */
						// green 2
						// (need red/blue)
						valMissingB = (int2blue(inData[index-1]) + int2blue(inData[index+1])) >> 1;
						valMissingR = (int2red(inData[index-iw]) + int2red(inData[index+iw])) >> 1;
						outData[index] = val | (valMissingR<<16) | (valMissingB);
						/* */
					}else{
						/* */
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
						/* */
						//outData[index] = val;
					}
				}
			}
		}
		
		/*
		 * Com todos os codigos ativos:
		 * Convert: rgb basico loop 	 8 ms. (fase 1)
		 * Convert: rgb fase2 loop 	 	4 ms. (fase 2)
		 * >> 30fps (usando e fazendo um bom 'pause' no inicio!)
		 * (Sem explicacao!!)
		 */
		
		if(seqNumber%10==0){
			t1 = System.currentTimeMillis();
			System.out.println("Convert: rgb fase2 loop \t "+(t1-t0)+" ms.");
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
