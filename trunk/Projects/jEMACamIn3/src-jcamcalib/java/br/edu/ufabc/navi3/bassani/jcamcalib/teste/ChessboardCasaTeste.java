package br.edu.ufabc.navi3.bassani.jcamcalib.teste;

import itesm.gvision.apps.calibrator.CalibratorOCV;
import itesm.gvision.image.tools.ImageTools;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;

/**
 * Teste de etapas sobre imagens de teste com uso do CalibratorOCV.
 * @author bassani
 */
public class ChessboardCasaTeste {
	
	/** OCV Bridge */
	private CalibratorOCV cocv;
	
	private boolean imgWrite;
	
	private Vector<int[]> imagesCalib = new Vector<int[]>();
	private double[] matrizCalib;
	
	private long t0, t1;
	
	/*
	 * Teste dos dados:
	 * <DataDir>/test-images/chessboard-casa-calib-effect/chessboard-*.jpg
	 */
	public ChessboardCasaTeste() {
		this(new CalibratorOCV());
		imgWrite = true;
		inicializaTeste();
	}
	
	public ChessboardCasaTeste(CalibratorOCV _cocv) {
		super();
		this.cocv = _cocv;
		imgWrite = false;
	}
	
	public double[] getMatrizCalib() {
		return matrizCalib;
	}

	public void inicializaTeste(){
		System.out.println("ChessboardCasaTeste");
		System.out.println("");
		System.out.println("br.edu.ufabc.navi3.bassani.jcamcalib.teste.ChessboardCasaTeste");
		System.out.println("");
		
		File[] arquivosBase = {
				new File("test-images/chessboard-casa-calib-effect/chessboard-0000000548.jpg"),
				new File("test-images/chessboard-casa-calib-effect/chessboard-0000000592.jpg"),
				new File("test-images/chessboard-casa-calib-effect/chessboard-0000000677.jpg"),
				new File("test-images/chessboard-casa-calib-effect/chessboard-0000000697.jpg")
		};
		
		File arquivosTeste = new File("test-images/chessboard-casa-calib-effect/chessboard-0000000000.jpg");
		
		int iw = 640;
		int ih = 480;
		
		for(File arquivo : arquivosBase){
			try {
				t0 = System.currentTimeMillis();
				
				BufferedImage imgReadOriginal = ImageIO.read(arquivo);
				
				if(imgWrite) ImageIO.write(imgReadOriginal, "JPEG", new File(arquivo.getName().replaceFirst(".jpg", "")+"_proc00"+".jpg"));
				if(imgWrite) ImageIO.write(imgReadOriginal, "PNG", new File(arquivo.getName().replaceFirst(".jpg", "")+"_proc00"+".png"));
				
				BufferedImage imgIn = imgReadOriginal;
				
				DataBuffer buff = imgIn.getRaster().getDataBuffer();
				if(buff instanceof DataBufferByte){
					byte[] imageDataBI = ((DataBufferByte)buff).getData();
					if(imageDataBI.length==iw*ih){
						System.out.println("Read - DataBufferByte - length = iw*ih");
					}else if(imageDataBI.length==3*iw*ih){
						System.out.println("Read - DataBufferByte - length = 3*iw*ih");
						
						imgIn = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
						int imageDataIntArray[] = ((DataBufferInt)imgIn.getRaster().getDataBuffer()).getData();
						if(imageDataIntArray.length==iw*ih){
							System.out.println("Read - DataBufferByte >> DataBufferInt - length = iw*ih");
							
							for(int i=0; i<imageDataIntArray.length; i++){
								int r = (int)imageDataBI[3*i];
								if(r<0) r+=256;
								if(r>255) r=255;
								int g = (int)imageDataBI[3*i+1];
								if(g<0) g+=256;
								if(g>255) g=255;
								int b = (int)imageDataBI[3*i+2];
								if(b<0) b+=256;
								if(b>255) b=255;
								imageDataIntArray[i] = (int)((r<<0)+(g<<8)+(b<<16));
							}
						}else if(imageDataIntArray.length==3*iw*ih){
							System.out.println("Read - DataBufferByte >> DataBufferInt - length = 3*iw*ih");
							
							for(int i=0; i<imageDataIntArray.length; i++){
								int val = (int)imageDataBI[i];
								if(val<0) val+=256;
								imageDataIntArray[i] = val;
							}
						}
						
						if(imgWrite) ImageIO.write(imgIn, "JPEG", new File(arquivo.getName().replaceFirst(".jpg", "")+"_proc01"+".jpg"));
						if(imgWrite) ImageIO.write(imgIn, "PNG", new File(arquivo.getName().replaceFirst(".jpg", "")+"_proc01"+".png"));
						
					}else{
						System.out.println("Read - DataBufferByte - length = "+imageDataBI.length);
					}
				}else if(buff instanceof DataBufferInt){
					int[] imageDataBI = ((DataBufferInt)buff).getData();
					if(imageDataBI.length==iw*ih){
						System.out.println("Read - DataBufferInt - length = iw*ih");
					}else if(imageDataBI.length==3*iw*ih){
						System.out.println("Read - DataBufferInt - length = 3*iw*ih");
					}else{
						System.out.println("Read - DataBufferInt - length = "+imageDataBI.length);
					}
				}
				
				/*
				 * ImageTools.getImageDataRGB() requer um image buffer com int[].
				 */
				int[] imageData = ImageTools.getImageDataRGB(imgIn);
				double pnts[] = this.cocv.findCorners(imageData, iw,  ih);
				
				if(pnts==null){
					System.out.println("Nao achou!!!");
				}else{
					// 96 = 6 * 8 * 2
					if (pnts.length == 96){
						System.out.println("Achou!!!");
						imagesCalib.add(imageData);
					}else{
						System.out.println("Quase achou!!! - pnts.length: "+pnts.length);
					}
				}
				
				t1 = System.currentTimeMillis();
				System.out.println("Loop Img in: \t "+(t1-t0)+" ms.");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		t0 = System.currentTimeMillis();
		
		for(int i=0; i<this.imagesCalib.size(); i++){
			System.out.println("matrizCalib: addCalibrationImage ("+i+")");
			this.cocv.addCalibrationImage(this.imagesCalib.get(i), iw, ih);
		}
		
		t1 = System.currentTimeMillis();
		System.out.println("Main end: \t "+(t1-t0)+" ms.");
		t0 = System.currentTimeMillis();
		
		System.out.println("matrizCalib: calibrate ...");
		if(this.cocv.calibrate()){
			
			t1 = System.currentTimeMillis();
			System.out.println("Main end: \t "+(t1-t0)+" ms.");
			t0 = System.currentTimeMillis();
			
			matrizCalib = this.cocv.getResults();
			
			System.out.println("matrizCalib: ");
			for(int i=0; i<matrizCalib.length; i++){
				System.out.print(matrizCalib[i]+" ");
			}
			System.out.println("");
		}else{
			System.out.println("matrizCalib: falhou!!!");
		}
		
		t1 = System.currentTimeMillis();
		System.out.println("Main end: \t "+(t1-t0)+" ms.");
		t0 = System.currentTimeMillis();
		
		try {
			BufferedImage imgReadOriginal = ImageIO.read(arquivosTeste);
			
			if(imgWrite) ImageIO.write(imgReadOriginal, "JPEG", new File(arquivosTeste.getName().replaceFirst(".jpg", "")+"_proc02"+".jpg"));
			if(imgWrite) ImageIO.write(imgReadOriginal, "PNG", new File(arquivosTeste.getName().replaceFirst(".jpg", "")+"_proc02"+".png"));
			
			BufferedImage imgIn = imgReadOriginal;
			
			DataBuffer buff = imgIn.getRaster().getDataBuffer();
			if(buff instanceof DataBufferByte){
				byte[] imageDataBI = ((DataBufferByte)buff).getData();
				if(imageDataBI.length==iw*ih){
					System.out.println("Read - DataBufferByte - length = iw*ih");
				}else if(imageDataBI.length==3*iw*ih){
					System.out.println("Read - DataBufferByte - length = 3*iw*ih");
					
					imgIn = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
					int imageDataIntArray[] = ((DataBufferInt)imgIn.getRaster().getDataBuffer()).getData();
					if(imageDataIntArray.length==iw*ih){
						System.out.println("Read - DataBufferByte >> DataBufferInt - length = iw*ih");
						
						for(int i=0; i<imageDataIntArray.length; i++){
							int r = (int)imageDataBI[3*i];
							if(r<0) r+=256;
							if(r>255) r=255;
							int g = (int)imageDataBI[3*i+1];
							if(g<0) g+=256;
							if(g>255) g=255;
							int b = (int)imageDataBI[3*i+2];
							if(b<0) b+=256;
							if(b>255) b=255;
							imageDataIntArray[i] = (int)((r<<0)+(g<<8)+(b<<16));
						}
					}else if(imageDataIntArray.length==3*iw*ih){
						System.out.println("Read - DataBufferByte >> DataBufferInt - length = 3*iw*ih");
						
						for(int i=0; i<imageDataIntArray.length; i++){
							int val = (int)imageDataBI[i];
							if(val<0) val+=256;
							imageDataIntArray[i] = val;
						}
					}
					
					if(imgWrite) ImageIO.write(imgIn, "JPEG", new File(arquivosTeste.getName().replaceFirst(".jpg", "")+"_proc03"+".jpg"));
					if(imgWrite) ImageIO.write(imgIn, "PNG", new File(arquivosTeste.getName().replaceFirst(".jpg", "")+"_proc03"+".png"));
					
				}else{
					System.out.println("Read - DataBufferByte - length = "+imageDataBI.length);
				}
			}else if(buff instanceof DataBufferInt){
				int[] imageDataBI = ((DataBufferInt)buff).getData();
				if(imageDataBI.length==iw*ih){
					System.out.println("Read - DataBufferInt - length = iw*ih");
				}else if(imageDataBI.length==3*iw*ih){
					System.out.println("Read - DataBufferInt - length = 3*iw*ih");
				}else{
					System.out.println("Read - DataBufferInt - length = "+imageDataBI.length);
				}
			}
			
			t1 = System.currentTimeMillis();
			System.out.println("Teste read/convert: \t "+(t1-t0)+" ms.");
			t0 = System.currentTimeMillis();
			
			/*
			 * ImageTools.getImageDataRGB() requer um image buffer com int[].
			 */
			int[] imageData = ImageTools.getImageDataRGB(imgIn);

			System.out.println("getUndistortedImage: ...");
			int[] imagemNova = this.cocv.getUndistortedImage(imageData, iw, ih);
			System.out.println("getUndistortedImage: imagemNova array size: "+imagemNova.length);
			
			t1 = System.currentTimeMillis();
			System.out.println("Teste undistorted: \t "+(t1-t0)+" ms.");
			t0 = System.currentTimeMillis();
			
			try {
				BufferedImage imgFinal = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
				int imageDataIntArray[] = ((DataBufferInt)imgFinal.getRaster().getDataBuffer()).getData();
				
				for(int i=0; i<imagemNova.length; i++){
					imageDataIntArray[i] = imagemNova[i];
				}
				
				if(imgWrite) ImageIO.write(imgFinal, "JPEG", new File(arquivosTeste.getName().replaceFirst(".jpg", "")+"_proc04"+".jpg"));
				if(imgWrite) ImageIO.write(imgFinal, "PNG", new File(arquivosTeste.getName().replaceFirst(".jpg", "")+"_proc04"+".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			t1 = System.currentTimeMillis();
			System.out.println("Teste convert/write: \t "+(t1-t0)+" ms.");
			t0 = System.currentTimeMillis();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ChessboardCasaTeste();
	}

}
