package br.ufabc.bassani.opensurf.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import javax.imageio.ImageIO;

import br.ufabc.bassani.opensurf.OpenSurf;

public class JavaOpenSurfTest {

	OpenSurf surf;
	
	public JavaOpenSurfTest() {
		surf = new OpenSurf();
		
		String img1Name, img2Name;
		
		try {
			
			//img1Name = "./test-images/cs766/mars3-l.jpg";
			//img2Name = "./test-images/cs766/mars3-r.jpg";
			
			//img1Name = "./test-images/cs766/scene1.row3.col1.png";
			//img2Name = "./test-images/cs766/scene1.row3.col5.png";
			
			//testeDisparity(img1Name, img2Name, "javaDisparity_img1.jpg");

			for(int i=0; i<=3123/*3123*/; i+=1){
				if(true) break;
				String temp = Integer.toString(i);
				while(temp.length()<10) temp = "0"+temp;
				img2Name = "./test-images/camera-ufacb-firewire-2009-12-21/camera1/camera_1_-"+temp+".jpg";
				img1Name = "./test-images/camera-ufacb-firewire-2009-12-21/camera2/camera_2_-"+temp+".jpg";
				System.out.println("Disparity camera 2-1 ... "+temp);
				testeDisparity(img1Name, img2Name, 0, 0, "camera-ufacb-firewire-2009-12-21_m0M0_disparity/camera_1-2_-"+temp+".jpg");
				// if(i%20==0) System.gc();
			}
			
			
			for(int i=0; i<=2670; i+=1){
				if(true) break;
				String temp = Integer.toString(i);
				while(temp.length()<10) temp = "0"+temp;
				img2Name = "./test-images/chessboard-ufabc-artags/camera1/camera_1_-"+temp+".jpg";
				img1Name = "./test-images/chessboard-ufabc-artags/camera2/camera_2_-"+temp+".jpg";
				System.out.println("Disparity camera 2-1 ... "+temp);
				testeDisparity(img1Name, img2Name, -14, 13, "camera_2-1_m14M13_Disparity/camera_1-2_-"+temp+".jpg");
				// if(i%20==0) System.gc();
			}

			if(true) return;
			
			//img1Name = "./test-images/chessboard-ufabc-artags/camera1/camera_1_-0000002002.jpg";
			//img2Name = "./test-images/chessboard-ufabc-artags/camera2/camera_2_-0000002002.jpg";
			
			testeDuasImgs1(img1Name, img2Name);
			
			//img1Name = "./test-images/chessboard-ufabc-artags/camera1/camera_1_-0000002002.jpg";
			//img2Name = "./test-images/chessboard-ufabc-artags/camera2/camera_2_-0000002002.jpg";
			
			boolean upright = true;
			int octaves = 8;
			int intervals = 4;
			int init_sample = 2;
			float thres = 0.00002f;
			testeDuasImgs2(img1Name, img2Name, upright, octaves, intervals, init_sample, thres);
			
			File dir = new File("test-images/chessboard-ufabc-artags/camera2");
			if(dir.exists()){
				File[] fotos = dir.listFiles(new FilenameFilter(){
					public boolean accept(File dir, String fileName) {
						System.out.println("Verificando ... "+fileName);
						if(fileName.endsWith(".jpg")){
							System.out.println("Aceitando ... "+fileName);
							return true;
						}
						return false;
					}
				});
				Vector<String> inputFiles = new Vector<String>();
				Arrays.sort(fotos, new Comparator<File>(){
					public int compare(File arg0, File arg1) {
						return arg0.getName().compareTo(arg1.getName());
					}
				});
				for(File foto : fotos){
					String path = "test-images/chessboard-ufabc-artags/camera2"+"/"+foto.getName();
					inputFiles.add(path);
					System.out.println("Adicionando ... "+path);
				}
				int count = 0;
				img1Name = null;
				img2Name = null;
				for(String path : inputFiles){
					if(img1Name==null){
						img1Name = path;
					}else{
						img2Name = path;
						
						if(count<5){
							String temp = Integer.toString(count);
							while(temp.length()<10) temp = "0"+temp;
							
							String output = "camera_2_surf/camera_2_surf_-" + temp + ".jpg";
							
							testeDuasImgs2(img1Name, img2Name, upright, octaves, intervals, init_sample, thres, output);
						}
						
						img1Name = img2Name;
						count++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int[] getIntRGBArray(BufferedImage img){
		int w = img.getWidth();
		int h = img.getHeight();
		
		int[] imgBytes = null;
		
		if(img.getRaster().getDataBuffer() instanceof DataBufferInt){
			imgBytes = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
		}else if(img.getRaster().getDataBuffer() instanceof DataBufferByte){
			byte[] imgByteArray = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
			
			imgBytes = new int[w*h];
			
			//Convertendo
			int r, g, b;
			for(int i=0,j=0; i<w*h; i++,j+=3){
				r = imgByteArray[j+0];
				g = imgByteArray[j+1];
				b = imgByteArray[j+2];

				//if(Math.abs(r1-r2)+Math.abs(g1-g2)+Math.abs(b1-b2)>50){
				imgBytes[i] = (r<<16 | g<<8 | b);
			}
		}
		return imgBytes;
	}
	
	private int[] getIntRGBArray_V02(BufferedImage imgIn){
		System.out.println(" == getIntRGBArray_V02 == ");
		
		long t0, t1;
		t0 = System.currentTimeMillis();
		
		int iw = imgIn.getWidth();
		int ih = imgIn.getHeight();
		
		int[] imageDataIntArray = null;
		
		DataBuffer buff = imgIn.getRaster().getDataBuffer();
		if(buff instanceof DataBufferByte){
			byte[] imageDataBI = ((DataBufferByte)buff).getData();
			if(imageDataBI.length==iw*ih){
				System.out.println("Read - DataBufferByte - length = iw*ih");
			}else if(imageDataBI.length==3*iw*ih){
				System.out.println("Read - DataBufferByte - length = 3*iw*ih");
				
				imgIn = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
				imageDataIntArray = ((DataBufferInt)imgIn.getRaster().getDataBuffer()).getData();
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
			}else{
				System.out.println("Read - DataBufferByte - length = "+imageDataBI.length);
			}
		}else if(buff instanceof DataBufferInt){
			imageDataIntArray = ((DataBufferInt)buff).getData();
			if(imageDataIntArray.length==iw*ih){
				System.out.println("Read - DataBufferInt - length = iw*ih");
			}else if(imageDataIntArray.length==3*iw*ih){
				System.out.println("Read - DataBufferInt - length = 3*iw*ih");
			}else{
				System.out.println("Read - DataBufferInt - length = "+imageDataIntArray.length);
			}
		}
		
		t1 = System.currentTimeMillis();
		System.out.println("Teste read/convert: \t "+(t1-t0)+" ms.");
		return imageDataIntArray;
	}
	
	private int[] getIntRGBArray_V03(BufferedImage imgIn, int delta){
		System.out.println(" == getIntRGBArray_V03 == ");
		
		long t0, t1;
		t0 = System.currentTimeMillis();
		
		int iw = imgIn.getWidth();
		int ih = imgIn.getHeight();
		
		int[] imageDataIntArray = null;
		
		DataBuffer buff = imgIn.getRaster().getDataBuffer();
		if(buff instanceof DataBufferByte){
			byte[] imageDataBI = ((DataBufferByte)buff).getData();
			if(imageDataBI.length==iw*ih){
				System.out.println("Read - DataBufferByte - length = iw*ih");
			}else if(imageDataBI.length==3*iw*ih){
				System.out.println("Read - DataBufferByte - length = 3*iw*ih");
				
				imgIn = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
				imageDataIntArray = ((DataBufferInt)imgIn.getRaster().getDataBuffer()).getData();
				if(imageDataIntArray.length==iw*ih){
					System.out.println("Read - DataBufferByte >> DataBufferInt - length = iw*ih");
					
					for(int i=0; i<imageDataIntArray.length; i++){
						int r = 0;
						int g = 0;
						int b = 0;
						imageDataIntArray[i] = (int)((r<<0)+(g<<8)+(b<<16));
					}
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
						int indexNovo = i + delta*iw;
						if(indexNovo<0 || indexNovo>=imageDataIntArray.length){
							
						}else{
							imageDataIntArray[indexNovo] = (int)((r<<0)+(g<<8)+(b<<16));
						}
					}
				}else if(imageDataIntArray.length==3*iw*ih){
					System.out.println("Read - DataBufferByte >> DataBufferInt - length = 3*iw*ih");
					
					for(int i=0; i<imageDataIntArray.length; i++){
						int val = (int)imageDataBI[i];
						if(val<0) val+=256;
						imageDataIntArray[i] = val;
					}
				}
			}else{
				System.out.println("Read - DataBufferByte - length = "+imageDataBI.length);
			}
		}else if(buff instanceof DataBufferInt){
			imageDataIntArray = ((DataBufferInt)buff).getData();
			if(imageDataIntArray.length==iw*ih){
				System.out.println("Read - DataBufferInt - length = iw*ih");
			}else if(imageDataIntArray.length==3*iw*ih){
				System.out.println("Read - DataBufferInt - length = 3*iw*ih");
			}else{
				System.out.println("Read - DataBufferInt - length = "+imageDataIntArray.length);
			}
		}
		
		t1 = System.currentTimeMillis();
		System.out.println("Teste read/convert: \t "+(t1-t0)+" ms.");
		return imageDataIntArray;
	}
	
	private void testeDisparity(String img1Name, String img2Name, String output) throws IOException {
		testeDisparity(img1Name, img2Name, 0, 0, output);
	}
	
	private void testeDisparity(String img1Name, String img2Name, int delta1, int delta2, String output) throws IOException {
		System.out.println(" == Disparity == ");
		
		long t0, t1;

		int[] img1Bytes;
		int[] img2Bytes;
		int w;
		int h;
		
		t0 = System.currentTimeMillis();
		BufferedImage img1 = ImageIO.read(new File(img1Name));
		BufferedImage img2 = ImageIO.read(new File(img2Name));
		
		w = img1.getWidth();
		h = img1.getHeight();
		
		//img1Bytes = getIntRGBArray_V02(img1);
		//img2Bytes = getIntRGBArray_V02(img2);
		
		img1Bytes = getIntRGBArray_V03(img1, delta1);
		img2Bytes = getIntRGBArray_V03(img2, delta2);
		
		if(img1Bytes==null || img2Bytes==null) return;
		t1 = System.currentTimeMillis();
		System.out.println("Tempo de load/conversion: "+(t1-t0)+" ms.");
		
		t0 = System.currentTimeMillis();
		int[] disparity = surf.getDisparityImageFromColorImages(img1Bytes, img2Bytes, w, h);
		t1 = System.currentTimeMillis();
		
		System.out.println("disparity.length : "+disparity.length+" - (length/(w*h)) : "+(disparity.length/(w*h)));
		
		Graphics g1 = img1.getGraphics();
		
		System.out.println(" -- Pontos -- ");
		Color color;
		/* - OK -
		for(int i=0; i<disparity.length; i++){
			int gray = (disparity[i] & 0xFF);
			if(gray<0) gray+=256;
			if(gray>255) gray = 255;
			//System.out.print(" "+gray);
			color = new Color(gray, gray, gray);
			g1.setColor(color);
			g1.fillRect(i%w, (int)(i/(double)w), 1, 1);
		}
		*/
		int index = 0;
		for(int y=0; y<h; y++){
			for(int x=0; x<w; x++){
				int gray = (disparity[index] & 0xFF);
				if(gray<0) gray+=256;
				if(gray>255) gray = 255;
				//System.out.print(" "+gray);
				color = new Color(gray, gray, gray);
				g1.setColor(color);
				g1.fillRect(x, y, 1, 1);
				index++;
			}
		}
		System.out.println(" -- -- -- -- ");
		
		img1.flush();
		
		ImageIO.write(img1, "JPEG", new File(output));
		
		System.out.println("Tempo de Disparity: "+(t1-t0)+" ms.");
	}
	
	private void testeDuasImgs1(String img1Name, String img2Name) throws IOException {
		System.out.println(" == Teste 1 == ");
		
		long t0, t1;

		int[] img1Bytes;
		int[] img2Bytes;
		int w;
		int h;
		
		t0 = System.currentTimeMillis();
		BufferedImage img1 = ImageIO.read(new File(img1Name));
		BufferedImage img2 = ImageIO.read(new File(img2Name));
		
		w = img1.getWidth();
		h = img1.getHeight();
		
		img1Bytes = getIntRGBArray_V02(img1);
		img2Bytes = getIntRGBArray_V02(img2);
		
		if(img1Bytes==null || img2Bytes==null) return;
		t1 = System.currentTimeMillis();
		System.out.println("Tempo de load/conversion: "+(t1-t0)+" ms.");
		
		t0 = System.currentTimeMillis();
		float[] posicoes = surf.getCorrespondencePoints(img1Bytes, img2Bytes, w, h);
		t1 = System.currentTimeMillis();
		
		Graphics g1 = img1.getGraphics();
		Graphics g2 = img2.getGraphics();
		
		//g1.setColor(Color.red);
		//g2.setColor(Color.red);
		
		System.out.println(" -- Pontos -- ");
		Color color;
		for(int i=0; i<posicoes.length; i+=4){
			//System.out.println("("+posicoes[i+0]+","+posicoes[i+1]+") ("+posicoes[i+2]+","+posicoes[i+3]+")");
			
			color = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
			
			g1.setColor(color);
			g2.setColor(color);
			
			g1.drawOval((int)posicoes[i+0]-1, (int)posicoes[i+1]-1, 2, 2);
			g2.drawOval((int)posicoes[i+2]-1, (int)posicoes[i+3]-1, 2, 2);
		}
		System.out.println(" -- -- -- -- ");
		
		img1.flush();
		img2.flush();
		
		ImageIO.write(img1, "PNG", new File("javasurf_teste1_img1.png"));
		ImageIO.write(img2, "PNG", new File("javasurf_teste1_img2.png"));
		
		System.out.println("Java OpenSurf aplicado em: "+img1Name+" / "+img2Name);
		System.out.println("Total de pares de pontos: "+(posicoes.length/4)+".");
		System.out.println("Tempo de SURF: "+(t1-t0)+" ms.");
	}

	private void testeDuasImgs2(String img1Name, String img2Name, boolean upright,  int octaves, int intervals, int init_sample, float thres) throws IOException {
		testeDuasImgs2(img1Name, img2Name, upright, octaves, intervals, init_sample, thres, "javasurf_teste2_img1-2.jpg");
	}
	
	private void testeDuasImgs2(String img1Name, String img2Name, boolean upright,  int octaves, int intervals, int init_sample, float thres, String output) throws IOException {
		System.out.println(" == Teste 2 == ");
		
		long t0, t1;

		int[] img1Bytes;
		int[] img2Bytes;
		int w;
		int h;
		
		t0 = System.currentTimeMillis();
		BufferedImage img1 = ImageIO.read(new File(img1Name));
		BufferedImage img2 = ImageIO.read(new File(img2Name));
		
		w = img1.getWidth();
		h = img1.getHeight();
		
		img1Bytes = getIntRGBArray(img1);
		img2Bytes = getIntRGBArray(img2);
		
		if(img1Bytes==null || img2Bytes==null) return;
		t1 = System.currentTimeMillis();
		System.out.println("Tempo de load/conversion: "+(t1-t0)+" ms.");
		
		surf.reset(w, h);
		surf.configure(upright, octaves, intervals, init_sample, thres);
		
		float[] posicoes;
		
		posicoes = surf.getCorrespondencePointsFromLoop(img1Bytes);
		System.out.println("Total de pares de pontos (Loop 0): "+(posicoes.length/4)+".");
		
		t0 = System.currentTimeMillis();
		posicoes = surf.getCorrespondencePointsFromLoop(img2Bytes);
		t1 = System.currentTimeMillis();
		
		Graphics g1 = img1.getGraphics();
		Graphics g2 = img2.getGraphics();
		
		//g1.setColor(Color.red);
		//g2.setColor(Color.red);
		
		System.out.println(" -- Pontos -- ");
		Color color;
		for(int i=0; i<posicoes.length; i+=4){
			//System.out.println("("+posicoes[i+0]+","+posicoes[i+1]+") ("+posicoes[i+2]+","+posicoes[i+3]+")");
			
			color = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
			
			g1.setColor(color);
			g2.setColor(color);
			
			g1.drawOval((int)posicoes[i+0]-1, (int)posicoes[i+1]-1, 2, 2);
			g2.drawOval((int)posicoes[i+2]-1, (int)posicoes[i+3]-1, 2, 2);
			
			g2.drawLine((int)posicoes[i+0], (int)posicoes[i+1], (int)posicoes[i+2], (int)posicoes[i+3]);
		}
		System.out.println(" -- -- -- -- ");
		
		img1.flush();
		img2.flush();
		
		//ImageIO.write(img1, "PNG", new File("javasurf_teste2_img1.png"));
		//ImageIO.write(img2, "PNG", new File("javasurf_teste2_img2.png"));
		
		ImageIO.write(img2, "JPEG", new File(output));
		
		System.out.println("Java OpenSurf aplicado em: "+img1Name+" / "+img2Name);
		System.out.println("Total de pares de pontos: "+(posicoes.length/4)+".");
		System.out.println("Tempo de SURF: "+(t1-t0)+" ms.");
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new JavaOpenSurfTest();
	}

}
