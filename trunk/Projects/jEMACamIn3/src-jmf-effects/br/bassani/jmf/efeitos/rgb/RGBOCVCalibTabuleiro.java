package br.bassani.jmf.efeitos.rgb;

import itesm.gvision.apps.calibrator.CalibratorOCV;
import itesm.gvision.image.tools.ImageTools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.Effect;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.bassani.jmf.efeitos.IConfiguracao;

public class RGBOCVCalibTabuleiro extends RGBEffect implements Effect, IConfiguracao {

	private static final String effectname = "RGBOCVCalibTabuleiro";

	/** OCV Bridge */
	private CalibratorOCV cocv = new CalibratorOCV();

	public RGBOCVCalibTabuleiro(){
		super();
	}

	private long t0, t1;

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

		boolean returnAntes = false;

		if(returnAntes){
			return BUFFER_PROCESSED_OK;
		}

		boolean processa = true;
		boolean geraImagens = false;
		
		int testarEmFrames = 10;

		if(seqNumber%testarEmFrames==0){
			t0 = System.currentTimeMillis();
			if(processa){
				try {
					int[] inData3 = new int[inData.length/3];
					for(int i=0; i<inData.length; i+=3){
						r = (int)inData[i];
						if(r<0) r+=256;
						if(r>255) r=255;
						g = (int)inData[i+1];
						if(g<0) g+=256;
						if(g>255) g=255;
						b = (int)inData[i+2];
						if(b<0) b+=256;
						if(b>255) b=255;
						inData3[i/3] = (int)((r<<0)+(g<<8)+(b<<16));
					}
					BufferedImage img2 = ImageTools.getBufferedImage(inData3, iw, ih);

					if(geraImagens) ImageIO.write(img2, "JPEG", new File("temp"+seqNumber+".jpg"));

					int[] imageData = ImageTools.getImageDataRGB(img2);
					double pnts[] = this.cocv.findCorners(imageData, iw,  ih);

					if(pnts==null){
						//System.out.println("Nao achou!!!");
					}else{
						// 96 = 6 * 8 * 2
						if (pnts.length == 96){
							System.out.println("Achou!!!");

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

			t1 = System.currentTimeMillis();
			System.out.println("Convert: rgb OCV loop \t "+(t1-t0)+" ms.");
			
			/*
			 * Convert: rgb OCV loop 	 90 ms.
			 * Convert: rgb OCV loop 	 120 ms.
			 */
			
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

			painel.add(new JLabel("Nenhuma config."), BorderLayout.CENTER);
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
