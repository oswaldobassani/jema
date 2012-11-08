package br.bassani.jmf.gui.info;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Vector;

public class UrlInfo {

	private static UrlInfo[] urls = {};
	
	public String nome;
	public String url;

	public UrlInfo(String nome, String url) {
		super();
		this.nome = nome;
		this.url = url;
	}

	@Override
	public String toString() {
		return nome;
	}
	
	public static UrlInfo[] getUrls() {
		if(urls.length==0) initVideosURL();
		return urls;
	}

	private static void initVideosURL(){
		Vector<UrlInfo> array = new Vector<UrlInfo>();
		
		// Windows
		array.add(new UrlInfo("Video4Windows (Win)", "vfw://0"));
		// Video4Linux
		array.add(new UrlInfo("Video4Linux (Linux)", "v4l://0"));
		// Java Firewire Camera JNI
		array.add(new UrlInfo("Java Firewire Camera JNI (Linux)", "fwc://"));
		
		array.add(new UrlInfo("Java Stereo Firewire Camera JNI (Linux)", "sfwc://"));
		array.add(new UrlInfo("Java Real Stereo Firewire Camera JNI (Linux)", "rsfwc://"));

		/*
		 * Videos:
		 * $ ls DataDir/videos/casa/firewire/
		 * Corpo_MJPEG_Bayer.mov  Corpo_MJPEG_RGB.mov
		 * $ ls DataDir/videos/casa/usb/
		 * Log_Home_JPEG_T0.mov
		 * $ ls DataDir/videos/ufabc/firewire/
		 * TesteUFABC_MJPG.mov
		 */
		initVideosURL("videos/ufabc/firewire/TesteUFABC_MJPG.mov", array);
		initVideosURL("videos/casa/firewire/Corpo_MJPEG_Bayer.mov", array);
		initVideosURL("videos/casa/firewire/Corpo_MJPEG_RGB.mov", array);
		initVideosURL("videos/casa/usb/Log_Home_JPEG_T0.mov", array);
		initVideosURL("videos/casa/firewire/chessboard-casa-jpeg.mov", array);
		
		initVideosURL("videos/ufabc/firewire/chessboard-artag-estereo/camera1.mov", array);
		initVideosURL("videos/ufabc/firewire/chessboard-artag-estereo/camera2.mov", array);
		initVideosURL("videos/ufabc/firewire/chessboard-artag-estereo/stereo.mov", array);
		
		urls = array.toArray(urls);
	}
	
	private static void initVideosURL(String filePath, Vector<UrlInfo> array){
		File f = null;
		f = new File(filePath);
		if(f!=null && f.exists() && f.isFile()){
			try {
				String url = f.toURI().toURL().toString();
				array.add(new UrlInfo(filePath, url));
			} catch (MalformedURLException e) {}
		}
	}

}
