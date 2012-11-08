/*
 * Created on 11/06/2005
 * 
 * Copia de F:\MeusJava\SPIX\workspace\SPIXgames01\src\br\com\spixis\games \ util
 * 
 * NAO MODIFICAR !!!!
 * 
 */
package br.com.spixis.games.util;

import java.awt.BufferCapabilities;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

/**
 * @author Oswaldo
 */
public class ScreenUtils {
	
	public static GraphicsEnvironment getLocalGraphicsEnvironment(){
		return GraphicsEnvironment.getLocalGraphicsEnvironment();
	}
	
	public static GraphicsDevice getDefaultScreenDevice(GraphicsEnvironment ge){
		return ge.getDefaultScreenDevice();
	}
	
	public static GraphicsDevice getDefaultScreenDevice(){
		return getLocalGraphicsEnvironment().getDefaultScreenDevice();
	}
	
	public static void printGraphicsConfigurations(GraphicsDevice gs){
    	GraphicsConfiguration[] configs = gs.getConfigurations();
    	System.out.println("GraphicsConfiguration[] [(x,y) WidthxHeight] (isFullScreenRequired)");
    	for(int i=0;i<configs.length; i++){
			Rectangle r = configs[i].getBounds();
			BufferCapabilities cap = configs[i].getBufferCapabilities();
			System.out.println("["+i+"] [("+
					r.x+","+
					r.y+") "+
					r.width+"x"+
					r.height+"] ("+
					cap.isFullScreenRequired()+")");
		}
	}

	public static void printDisplayModes(GraphicsDevice gs){
		DisplayMode[] modes = gs.getDisplayModes();
		System.out.println("DisplayMode[] (WidthxHeight - BitDepth - RefreshRate)");
		for(int i=0;i<modes.length; i++){
			System.out.println("["+i+"] ("+
					modes[i].getWidth()+"x"+
					modes[i].getHeight()+") - "+
					modes[i].getBitDepth()+" - "+
					modes[i].getRefreshRate()+")");
			
		}
	}

	public static void printGraphicsConfiguration(GraphicsDevice gs){
		GraphicsConfiguration config = gs.getDefaultConfiguration();
    	System.out.println("Default GraphicsConfiguration [(x,y) WidthxHeight] (isFullScreenRequired)");
		Rectangle r = config.getBounds();
		BufferCapabilities cap = config.getBufferCapabilities();
		System.out.println("[("+
				r.x+","+
				r.y+") "+
				r.width+"x"+
				r.height+"] ("+
				cap.isFullScreenRequired()+")");
	}
	
	public static void printDisplayMode(GraphicsDevice gs){
		DisplayMode mode = gs.getDisplayMode();
		System.out.println("Default DisplayMode (WidthxHeight - BitDepth - RefreshRate)");
		System.out.println("("+
				mode.getWidth()+"x"+
				mode.getHeight()+") - "+
				mode.getBitDepth()+" - "+
				mode.getRefreshRate()+")");
	}
	
	public static void printFullScreenSupport(GraphicsDevice gs){
	    if (gs.isFullScreenSupported()) {
	    	System.out.println("Full-screen é suportado.");
	    } else {
	    	System.out.println("Full-screen será simulado.");
	    }
	}
	
	public static void printGraphicsConfigurations(){
		printGraphicsConfigurations(getDefaultScreenDevice());
	}
	public static void printDisplayModes(){
		printDisplayModes(getDefaultScreenDevice());
	}
	public static void printGraphicsConfiguration(){
		printGraphicsConfiguration(getDefaultScreenDevice());
	}
	public static void printDisplayMode(){
		printDisplayMode(getDefaultScreenDevice());
	}
	public static void printFullScreenSupport(){
		printFullScreenSupport(getDefaultScreenDevice());
	}
	
	public static boolean isFullScreenSupport(GraphicsDevice gs){
        return gs.isFullScreenSupported();
	}
	
	public static boolean isFullScreenSupport(){
        return isFullScreenSupport(getDefaultScreenDevice());
	}
}
