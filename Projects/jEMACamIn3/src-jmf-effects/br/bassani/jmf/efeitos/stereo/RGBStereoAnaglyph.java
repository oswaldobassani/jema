package br.bassani.jmf.efeitos.stereo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import br.bassani.jmf.efeitos.IConfiguracao;

/*
 * Creates a 3-D Anaglyph from a left and right image using 2 of the three methods outlined in 
 * 	Sanders, W., McAllister, D.F., "Producing Anaglyphs from Synthetic Images,"
 *		Proc. Electr. Imaging '03, Santa Clara, CA (January 2003)
 *
 * Baseado no código de Frederic V. Hessman do Plugin para ImageJ.
 *
 * @author Oswaldo Bassani
 */
public class RGBStereoAnaglyph extends RGB32Effect implements Effect, IConfiguracao {

	private static final String effectname = "RGBStereoAnaglyph";

	public static final String PHOTOSHOP1 = new String ("Photoshop #1 (R_l,G_r,B_r)");
	public static final String PHOTOSHOP2 = new String ("Photoshop #2 (gray_l,G_r,B_r)");
	public static final String DUBOIS = new String ("Dubois (least-squares projection)");

	private String metodo = PHOTOSHOP1;
	//private String metodo = PHOTOSHOP2;
	//private String metodo = DUBOIS;

	public RGBStereoAnaglyph(){
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

		if ( outData.length < iw*ih/2 ) {
			System.out.println("the buffer is not full");
			return BUFFER_PROCESSED_FAILED;
		}

		int alphaVal = 255<<24;
		int r1,g1,b1;
		int image2_offset = iw * ih / 2;
		int r2,g2,b2;

		if ( metodo.equals (PHOTOSHOP1) ) {
			for(x = 0; x < iw; x++ ){
				for(y = 0; y < ih/2; y++ ) {
					int index1 = x + y * iw;
					int val1 = inData[index1];
					r1 = int2red(val1);
					g1 = int2green(val1);
					b1 = int2blue(val1);

					int index2 = index1 + image2_offset;
					int val2 = inData[index2];
					r2 = int2red(val2);
					g2 = int2green(val2);
					b2 = int2blue(val2);

					outData[index1] = alphaVal | (r1 << 16) + (g2 << 8) + b2;// G,R,B
				}
			}
		}
		// RED=GRAY_LEFT, GREEN=GREEN_RIGHT, BLUE=BLUE_RIGHT
		else if (metodo.equals (PHOTOSHOP2) ) {
			int gray1;
			for(x = 0; x < iw; x++ ){
				for(y = 0; y < ih/2; y++ ) {
					int index1 = x + y * iw;
					int val1 = inData[index1];
					r1 = int2red(val1);
					g1 = int2green(val1);
					b1 = int2blue(val1);

					gray1 = (int)((double)r1*0.299+(double)g1*0.587+(double)b1*0.114); // NTSC GRAY

					int index2 = index1 + image2_offset;
					int val2 = inData[index2];
					r2 = int2red(val2);
					g2 = int2green(val2);
					b2 = int2blue(val2);

					outData[index1] = alphaVal | (gray1 << 16) + (g2 << 8) + b2;
				}
			}
		}
		// ALL COLORS ARE LINEAR COMBINATION OF ALL LEFT- AND RIGHT-COLORS
		else if (metodo.equals (DUBOIS) ) {
			int red, green, blue;
			for(x = 0; x < iw; x++ ){
				for(y = 0; y < ih/2; y++ ) {
					int index1 = x + y * iw;
					int val1 = inData[index1];
					r1 = int2red(val1);
					g1 = int2green(val1);
					b1 = int2blue(val1);

					int index2 = index1 + image2_offset;
					int val2 = inData[index2];
					r2 = int2red(val2);
					g2 = int2green(val2);
					b2 = int2blue(val2);

					red = (int)(
							0.456100*(double)r1
							+0.500484*(double)g1
							+0.176381*(double)b1
							-0.0434706*(double)r2
							-0.0879388*(double)g2
							-0.00155529*(double)b2
					);
					if (red > 255)
						red=255;
					else if (red < 0)
						red=0;

					green = (int)(
							-0.0400822*(double)r1
							-0.0378246*(double)g1
							-0.0157589*(double)b1
							+0.378476*(double)r2
							+0.73364*(double)g2
							-0.0184503*(double)b2
					);
					if (green > 255)
						green=255;
					else if (green < 0)
						green=0;

					blue = (int)(
							-0.0152161*(double)r1
							-0.0205971*(double)g1
							-0.00546856*(double)b1
							-0.0721527*(double)r2
							-0.112961*(double)g2
							+1.2264*(double)b2
					);
					if (blue > 255)
						blue=255;
					else if (blue < 0)
						blue=0;

					outData[index1] = alphaVal | (red << 16) + (green << 8) + blue;
				}
			}
		}else{
			for(x = 0; x < iw; x++ ){
				for(y = 0; y < ih/2; y++ ) {
					int index1 = x + y * iw;
					int val1 = inData[index1];
					r1 = int2red(val1);
					g1 = int2green(val1);
					b1 = int2blue(val1);

					int index2 = index1 + image2_offset;
					int val2 = inData[index2];
					r2 = int2red(val2);
					g2 = int2green(val2);
					b2 = int2blue(val2);

					outData[index1] = alphaVal | (r1<<16) | (g1<<8) | b1;
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
			
			JRadioButton metodoPS1, metodoPS2, metodoDubois;
			metodoPS1 = new JRadioButton(PHOTOSHOP1);
			if(metodo.equals(PHOTOSHOP1)) metodoPS1.setSelected(true);
			metodoPS1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					metodo = PHOTOSHOP1;
				}
			});
			
			metodoPS2 = new JRadioButton(PHOTOSHOP2);
			if(metodo.equals(PHOTOSHOP2)) metodoPS2.setSelected(true);
			metodoPS2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					metodo = PHOTOSHOP2;
				}
			});
			
			metodoDubois = new JRadioButton(DUBOIS);
			if(metodo.equals(DUBOIS)) metodoDubois.setSelected(true);
			metodoDubois.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					metodo = DUBOIS;
				}
			});
			
			ButtonGroup group = new ButtonGroup();
			group.add(metodoPS1);
			group.add(metodoPS2);
			group.add(metodoDubois);
			
			JPanel metodo = new JPanel(new GridLayout(-1,1,5,5));
			metodo.add(metodoPS1);
			metodo.add(metodoPS2);
			metodo.add(metodoDubois);
			painel.add(metodo, BorderLayout.CENTER);

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
