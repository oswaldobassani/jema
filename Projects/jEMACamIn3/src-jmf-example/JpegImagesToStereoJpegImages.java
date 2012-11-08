import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;


public class JpegImagesToStereoJpegImages {

	public static Vector<String> mainChessboardUFABCImageFind(String basePath, String cameraFolder) throws Exception {
		File dir = new File(basePath+"/"+cameraFolder);
		if(!dir.exists()){
			throw new Exception("Diretorio padrao nao existe.");
		}
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
			String path = basePath+"/"+cameraFolder+"/"+foto.getName();
			inputFiles.add(path);
			System.out.println("Adicionando ... "+path);
		}
		return inputFiles;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int width = 640;
		int height = 480*2;
		
		//String pastaBasica = "test-images/chessboard-ufabc-artags";
		String pastaBasica = "test-images/camera-ufacb-firewire-2009-12-21";
		String camera1Folder = "camera1";
		String camera2Folder = "camera2";
		String outputFolder = "stereo";
		
		Vector<String> input_1 = mainChessboardUFABCImageFind(pastaBasica, camera1Folder);
		Vector<String> input_2 = mainChessboardUFABCImageFind(pastaBasica, camera2Folder);
		
		Iterator<String> it1 = input_1.iterator();
		Iterator<String> it2 = input_2.iterator();
		
		int count = 0;
		while(it1.hasNext() && it2.hasNext()){
			/*
			// Open a random access file for the next image. 
			RandomAccessFile raFile1 = new RandomAccessFile(it1.next(), "r");
			byte data1[] = new byte[(int)raFile1.length()];
			// Read the entire JPEG image from the file.
			raFile1.readFully(data1, 0, (int)raFile1.length());
			raFile1.close();
			*/
			
			BufferedImage img1 = ImageIO.read(new File(it1.next()));
			
			/*
			// Open a random access file for the next image. 
			RandomAccessFile raFile2 = new RandomAccessFile(it2.next(), "r");
			byte data2[] = new byte[(int)raFile2.length()];
			// Read the entire JPEG image from the file.
			raFile2.readFully(data2, 0, (int)raFile2.length());
			raFile2.close();
			*/
			
			BufferedImage img2 = ImageIO.read(new File(it2.next()));
			
			// Create buffered image that does not support transparency
		    BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		    // Create a graphics context on the buffered image
		    Graphics2D g2d = bimage.createGraphics();
		    // Draw on the image
		    /*
		    g2d.drawBytes(data1, 0, data1.length, 0, 0);
		    g2d.drawBytes(data2, 0, data2.length, 0, 480);
		    */
		    g2d.drawImage(img1, 0, 0, null);
		    g2d.drawImage(img2, 0, 480, null);
		    g2d.dispose();
		    
		    String name = "stereo-";
		    String tmp = ""+count;
		    while(tmp.length()<10){
		    	tmp = "0"+tmp;
		    }
		    name = name.concat(tmp);
		    
		    System.out.println("Gerando ... "+name);
		    
		    ImageIO.write(bimage, "JPEG", new File(pastaBasica+"/"+outputFolder+"/"+name+".jpg"));
		    count++;
		}
		

	}

}
