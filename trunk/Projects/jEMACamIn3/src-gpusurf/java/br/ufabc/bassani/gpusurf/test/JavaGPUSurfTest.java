package br.ufabc.bassani.gpusurf.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import br.ufabc.bassani.gpusurf.JavaGPUSurf;

public class JavaGPUSurfTest {

	boolean init = false;
	JavaGPUSurf surf;
	
	public JavaGPUSurfTest() {
		surf = new JavaGPUSurf();
		
		System.out.println("GPUSurf TEST Starting ... ");
		
		//String pastaBasica = "test-images/chessboard-ufabc-artags";
		// 1 - 2670
		
		String pastaBasica = "test-images/camera-ufacb-firewire-2009-12-21";
		// 1 - 3123
		
		String img1Name;//, img2Name;
		
		float threshold = 0.05f;
		
		try {
			String last = Integer.toString(0);
			while(last.length()<10) last = "0"+last;
			
			int start = 2900;
			int count = 10;
			//20-ok // 30-error (read 2 imagens)
			//50-com erro/ok (read 1 imagem)
			//200-com erro/ok (read 1 imagem)
			for(int i=start; i<=(start+count); i+=1){
				String temp = Integer.toString(i);
				while(temp.length()<10) temp = "0"+temp;
				img1Name = pastaBasica+"/camera1/camera_1_-"+last+".jpg";
				//img2Name = pastaBasica+"/camera1/camera_1_-"+temp+".jpg";
				System.out.println("GPUSurf camera 1-1 ... "+temp);
				testeGPUSurf(img1Name, /*img2Name,*/ 0, 0, pastaBasica+"/camera_1-1_GPUSurf_T0.05f/camera_1-1_-"+last+".jpg", threshold);
				last = temp;
				
				//try {
				//	Thread.sleep(200);
				//} catch (InterruptedException e) {
				//	e.printStackTrace();
				//}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private char[] getCharBGRAArray(BufferedImage imgIn, int delta){
		System.out.println(" == getCharBGRAArray == ");
		
		long t0, t1;
		t0 = System.currentTimeMillis();
		
		int iw = imgIn.getWidth();
		int ih = imgIn.getHeight();
		
		char[] imageDataCharArray = null;
		
		imageDataCharArray = new char[0];
		
		DataBuffer buff = imgIn.getRaster().getDataBuffer();
		if(buff instanceof DataBufferByte){
			byte[] imageDataBI = ((DataBufferByte)buff).getData();
			if(imageDataBI.length==iw*ih){
				System.out.println("Read - DataBufferByte - length = iw*ih");
			}else if(imageDataBI.length==3*iw*ih){
				System.out.println("Read - DataBufferByte - length = 3*iw*ih");
				
				if(false){
					imageDataCharArray = new char[4*iw*ih];
					System.out.println("Read - DataBufferByte >> char[] - length = 4*iw*ih");
					
					for(int i=0; i<imageDataCharArray.length; i+=4){
						int a = 0;
						int r = 0;
						int g = 0;
						int b = 0;
						imageDataCharArray[i+0] = (char)b;
						imageDataCharArray[i+1] = (char)g;
						imageDataCharArray[i+2] = (char)r;
						imageDataCharArray[i+3] = (char)a;
					}
					for(int i=0, j=0; i<imageDataBI.length; i+=3, j+=4){
						int r = (int)imageDataBI[i];
						if(r<0) r+=256;
						if(r>255) r=255;
						int g = (int)imageDataBI[i+1];
						if(g<0) g+=256;
						if(g>255) g=255;
						int b = (int)imageDataBI[i+2];
						if(b<0) b+=256;
						if(b>255) b=255;
						imageDataCharArray[j+0] = (char)b;
						imageDataCharArray[j+1] = (char)g;
						imageDataCharArray[j+2] = (char)r;
						imageDataCharArray[j+3] = (char)0;
					}
				}else if(false){
					imageDataCharArray = new char[3*iw*ih];
					System.out.println("Read - DataBufferByte >> char[] - length = 4*iw*ih");
					
					for(int i=0; i<imageDataCharArray.length; i+=4){
						int a = 0;
						int r = 0;
						int g = 0;
						int b = 0;
						imageDataCharArray[i+0] = (char)b;
						imageDataCharArray[i+1] = (char)g;
						imageDataCharArray[i+2] = (char)r;
						imageDataCharArray[i+3] = (char)a;
					}
					for(int i=0, j=0; i<imageDataBI.length; i+=3, j+=3){
						int r = (int)imageDataBI[i];
						if(r<0) r+=256;
						if(r>255) r=255;
						int g = (int)imageDataBI[i+1];
						if(g<0) g+=256;
						if(g>255) g=255;
						int b = (int)imageDataBI[i+2];
						if(b<0) b+=256;
						if(b>255) b=255;
						imageDataCharArray[j+0] = (char)b;
						imageDataCharArray[j+1] = (char)g;
						imageDataCharArray[j+2] = (char)r;
					}
				}else{
					imageDataCharArray = new char[iw*ih];
					System.out.println("Read - DataBufferByte >> char[] - length = iw*ih");
					
					for(int i=0; i<imageDataCharArray.length; i++){
						imageDataCharArray[i] = (char)0;
					}
					
					//int index = 0;
					//int indexOut = 0;
					/*
					for(int y=0; y<ih; y++){
						if(y<ih/2-10){
							for(int x=0; x<iw; x++){
								int r = (int)imageDataBI[index];
								if(r<0) r+=256;
								if(r>255) r=255;
								int g = (int)imageDataBI[index+1];
								if(g<0) g+=256;
								if(g>255) g=255;
								int b = (int)imageDataBI[index+2];
								if(b<0) b+=256;
								if(b>255) b=255;
								
								//imageDataCharArray[indexOut] = (char)((b+g+r)/3);
								imageDataCharArray[indexOut] = (char)(255*x/iw);
								
								index+=3;
								indexOut+=1;
							}
						}else{
							for(int x=0; x<iw; x++){
								int r = (int)imageDataBI[index];
								if(r<0) r+=256;
								if(r>255) r=255;
								int g = (int)imageDataBI[index+1];
								if(g<0) g+=256;
								if(g>255) g=255;
								int b = (int)imageDataBI[index+2];
								if(b<0) b+=256;
								if(b>255) b=255;
								
								//imageDataCharArray[indexOut] = (char)((b+g+r)/3);
								imageDataCharArray[indexOut] = (char)(255*y/ih);
								
								index+=3;
								indexOut+=1;
							}
						}
					}
					*/
					int index = 0;
					int indexOut = 0;
					for(int y=0; y<ih; y++){
						for(int x=0; x<iw; x++){
							index = x*3 + y*iw*3;
							
							int r = (int)imageDataBI[index];
							if(r<0) r+=256;
							if(r>255) r=255;
							int g = (int)imageDataBI[index+1];
							if(g<0) g+=256;
							if(g>255) g=255;
							int b = (int)imageDataBI[index+2];
							if(b<0) b+=256;
							if(b>255) b=255;
							
							indexOut = x/2 + y * iw/2;
							if(y<ih/2){
								//imageDataCharArray[indexOut] = (char)(255*x/iw);
								if(imageDataCharArray[indexOut]==0){
									imageDataCharArray[indexOut] = (char)((b+g+r)/3);
								}else{
									imageDataCharArray[indexOut] = (char)((imageDataCharArray[indexOut] + (char)((b+g+r)/3))/2);
								}
							}else{
								//imageDataCharArray[indexOut] = (char)(255*2*(y-ih/2)/ih);
								if(imageDataCharArray[indexOut]==0){
									imageDataCharArray[indexOut] = (char)((b+g+r)/3);
								}else{
									imageDataCharArray[indexOut] = (char)((imageDataCharArray[indexOut] + (char)((b+g+r)/3))/2);
								}
							}
						}
					}
					/*
					for(int i=0, j=0; i<imageDataBI.length; i+=3, j++){
						int r = (int)imageDataBI[i];
						if(r<0) r+=256;
						if(r>255) r=255;
						int g = (int)imageDataBI[i+1];
						if(g<0) g+=256;
						if(g>255) g=255;
						int b = (int)imageDataBI[i+2];
						if(b<0) b+=256;
						if(b>255) b=255;
						imageDataCharArray[j] = (char)((b+g+r)/3);
					}
					*/
				}
			}else{
				System.out.println("Read - DataBufferByte - length = "+imageDataBI.length);
			}
		}else if(buff instanceof DataBufferInt){
			int[] imageDataIntArrayTemp = ((DataBufferInt)buff).getData();
			if(imageDataIntArrayTemp.length==iw*ih){
				System.out.println("Read - DataBufferInt - length = iw*ih");
			}else if(imageDataIntArrayTemp.length==3*iw*ih){
				System.out.println("Read - DataBufferInt - length = 3*iw*ih");
			}else{
				System.out.println("Read - DataBufferInt - length = "+imageDataIntArrayTemp.length);
			}
		}
		
		t1 = System.currentTimeMillis();
		System.out.println("Teste read/convert: \t "+(t1-t0)+" ms. length = "+imageDataCharArray.length);
		return imageDataCharArray;
	}
	
	private void testeGPUSurf(String img1Name, /*String img2Name,*/ int delta1, int delta2, String output, float threshold) throws IOException {
		System.out.println(" == testeGPUSurf == ");
		
		long t0, t1;

		char[] img1Bytes;
		//char[] img2Bytes;
		int w;
		int h;
		
		t0 = System.currentTimeMillis();
		BufferedImage img1 = ImageIO.read(new File(img1Name));
		//BufferedImage img2 = ImageIO.read(new File(img2Name));
		
		w = img1.getWidth();
		h = img1.getHeight();
		
		img1Bytes = getCharBGRAArray(img1, delta1);
		//img2Bytes = getCharBGRAArray(img2, delta2);
		
		if(img1Bytes==null /*|| img2Bytes==null*/) return;
		t1 = System.currentTimeMillis();
		System.out.println("Tempo de load/conversion: "+(t1-t0)+" ms.");
		
		if(!init){
			surf.init(w, h);
			init = true;
		}
		
		t0 = System.currentTimeMillis();
		//float[] descritores1 = surf.getFeatures(JavaGPUSurf.BGRA, img1Bytes, w, h);
		//float[] descritores1 = surf.getFeatures(JavaGPUSurf.BGR, img1Bytes, w, h);
		float[] descritores1 = surf.getFeatures(JavaGPUSurf.I, img1Bytes, threshold);
		t1 = System.currentTimeMillis();
		
		System.out.println("descritores1.length : "+descritores1.length);
		
		Graphics g1 = img1.getGraphics();
		
		float minR=Float.MAX_VALUE, maxR=Float.MIN_VALUE;
		float minA=Float.MAX_VALUE, maxA=Float.MIN_VALUE;
		
		//System.out.println(" -- Pontos -- ");
		Color color = Color.red;
		for(int index = 0; index<descritores1.length; index+=4){
			float fx = descritores1[index];
			float fy = descritores1[index+1];
			
			float fraio = descritores1[index+2];
			// Radianos
			float fangulo = descritores1[index+3];
			
			int x = ((int)descritores1[index] & 0xFF);
			if(x<0) x+=256;
			if(x>255) x = 255;
			
			int y = ((int)descritores1[index+1] & 0xFF);
			if(y<0) y+=256;
			if(y>255) y = 255;
			
			g1.setColor(color);
			//g1.fillRect(x, y, 1, 1);
			//g1.fillOval(x-1, y-1, 3, 3);
			
			//g1.fillOval((int)(fx-2), (int)(fy-2), 4, 4);
			
			int size = (int)(fraio+1);
			g1.drawOval((int)(fx-size), (int)(fy-size), 2*size, 2*size);
			
			float endX = (float)(fx + (size*2)*Math.sin((double)fangulo));
			float endY = (float)(fy + (size*2)*Math.cos((double)fangulo));
			
			g1.drawLine((int)(fx), (int)(fy), (int)(endX), (int)(endY));
			
			if(fraio<minR) minR = fraio;
			if(fraio>maxR) maxR = fraio;
			
			if(fangulo<minA) minA = fangulo;
			if(fangulo>maxA) maxA = fangulo;
		}
		//System.out.println(" -- -- -- -- ");
		//System.out.println(" -minR- "+minR);
		//System.out.println(" -maxR- "+maxR);
		//System.out.println(" -minA- "+minA);
		//System.out.println(" -maxA- "+maxA);
		//System.out.println(" -- -- -- -- ");
		
		img1.flush();
		
		ImageIO.write(img1, "JPEG", new File(output));
		
		System.out.println("Tempo de GPU SURF: "+(t1-t0)+" ms.");
		tempoTotal += (t1-t0);
		numeroQuadros++;
	}
	
	public static long tempoTotal = 0;
	public static int numeroQuadros = 0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new JavaGPUSurfTest();
		
		System.out.println("Tempo de GPU SURF medio: "+(tempoTotal/numeroQuadros)+" ms por quadro. Total de quadros: "+numeroQuadros);
		
		try {
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}

}
