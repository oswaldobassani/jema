package br.bassani.jmf.efeitos.rgb;

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

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.NyARCode;
import jp.nyatla.nyartoolkit.core.param.NyARParam;
import jp.nyatla.nyartoolkit.core.transmat.NyARTransMatResult;
import jp.nyatla.nyartoolkit.detector.NyARSingleDetectMarker;
import jp.nyatla.nyartoolkit.jmf.utils.JmfNyARRaster_RGB;
import br.bassani.jmf.efeitos.IConfiguracao;

public class NyARToolkitEffect extends RGBEffect implements Effect, IConfiguracao {

	private static final String effectname = "NyARToolkitEffect";
	
	private final String CARCODE_FILE = "NyARToolkit/Data/patt.hiro";

	private final String PARAM_FILE = "NyARToolkit/Data/camera_para.dat";

	private NyARSingleDetectMarker _nya;

	private JmfNyARRaster_RGB _raster;

	private NyARTransMatResult _trans_mat_result = new NyARTransMatResult();

	/*
	 * Bassani: Config de resolucao externa ao codigo com 640x480 30fps.
	 * Padrao: 320x240 15fps
	 */
	private static int WIDTH = 640;
	private static int HEIGHT = 480;
	private static float FPS = 30.0f;

	public NyARToolkitEffect(){
		super();
	}

	private long t0, t1;

	public int process(Buffer inBuffer, Buffer outBuffer) {

		if(_raster==null){
			try {
				// NyARToolkitの準備
				NyARParam ar_param = new NyARParam();
				NyARCode ar_code = new NyARCode(16, 16);
				ar_param.loadARParamFromFile(PARAM_FILE);
				ar_param.changeScreenSize(WIDTH, HEIGHT);
				
				//キャプチャイメージ用のラスタを準備
				// Preparing for raster ...
				// Alterado para 3.0.0
				this._raster = new JmfNyARRaster_RGB(/*WIDTH, HEIGHT,*/ ((VideoFormat)inBuffer.getFormat()));
				
				this._nya = new NyARSingleDetectMarker(ar_param, ar_code, 80.0, this._raster.getBufferType());
				ar_code.loadARPattFromFile(CARCODE_FILE);
				
			} catch (NyARException e) {
				e.printStackTrace();
			}
		}

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
		boolean checkMemory = false;

		int testarEmFrames = 1;

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

					if(_raster!=null){
						
						this._raster.setBuffer(inBuffer);
						
						System.out.println("findMarker: ...");
	
						boolean is_marker_exist = this._nya.detectMarkerLite(this._raster, 100);
						if (is_marker_exist) {
							//変換行列を取得
							this._nya.getTransmationMatrix(this._trans_mat_result);
							
							System.out.println("Encontrou | Confidence: " + this._nya.getConfidence());
							System.out.println("[m00]" +this._trans_mat_result.m00);
							System.out.println("[m01]" +this._trans_mat_result.m01);
							System.out.println("[m02]" +this._trans_mat_result.m02);
							System.out.println("[m03]" +this._trans_mat_result.m03);
							System.out.println("[m10]" +this._trans_mat_result.m10);
							System.out.println("[m11]" +this._trans_mat_result.m11);
							System.out.println("[m12]" +this._trans_mat_result.m12);
							System.out.println("[m13]" +this._trans_mat_result.m13);
							System.out.println("[m20]" +this._trans_mat_result.m20);
							System.out.println("[m21]" +this._trans_mat_result.m21);
							System.out.println("[m22]" +this._trans_mat_result.m22);
							System.out.println("[m23]" +this._trans_mat_result.m23);
						}else{
							System.out.println("NAO encontrou");
						}
					}else{
						System.out.println("findMarker: _raster=null");
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

			if(checkMemory){
			
				t0 = System.currentTimeMillis();
	
				initMemoryUse();
				if(lastUsedMemory==0){
					lastUsedMemory = usedMemory;
					System.out.println("Memory: totalMemory \t "+(totalMemory)+" .");
					System.out.println("Memory: freeMemory \t "+(freeMemory)+" .");
					System.out.println("Memory: usedMemory \t "+(usedMemory)+" .");
				}else{
					if(lastUsedMemory*1.85<usedMemory){
						System.out.println("Memory: totalMemory \t "+(totalMemory)+" .");
						System.out.println("Memory: freeMemory \t "+(freeMemory)+" .");
						System.out.println("Memory: usedMemory \t "+(usedMemory)+" .");
						collectGarbage();
						lastUsedMemory = 0;
					}
				}
	
				t1 = System.currentTimeMillis();
				System.out.println("Memory: time loop \t "+(t1-t0)+" ms.");
			}
		}

		return BUFFER_PROCESSED_OK;
	}

	private long lastUsedMemory=0;
	private long totalMemory, freeMemory, usedMemory;

	private void initMemoryUse(){
		totalMemory = Runtime.getRuntime().totalMemory();
		freeMemory = Runtime.getRuntime().freeMemory();
		usedMemory = (totalMemory - freeMemory);
	}

	private void collectGarbage() {
		try {
			System.gc();
			System.runFinalization();
		}catch (Exception ex){
		//	ex.printStackTrace();
		}
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
