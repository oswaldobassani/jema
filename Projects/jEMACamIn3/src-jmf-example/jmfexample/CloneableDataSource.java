package jmfexample;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.SourceCloneable;
import javax.swing.JOptionPane;

import br.bassani.jmf.gui.AFrame;

public class CloneableDataSource {
	
	private AFrame parent;
	
	private DataSource mainDataSource;
	private MediaLocator mediaLocator;
	private Processor processor;
	private boolean processing;
	
	public CloneableDataSource(AFrame parent) {
		this.parent = parent;
		setProcessing(false);
	}
	
	private String formatoVideo;
	
	public String getFormatoVideo(){
		return formatoVideo;
	}
	
	public void setMainSource(){
		setProcessing(false);
		
		//ml == null
		
		boolean webcam = false;
		
		boolean ml_ok = false;
		if(webcam){
			ml_ok = openFromWebcam();//(ml == webcam)
		}else{
			ml_ok = openFromFile("Log_Home_RGB24_T0.avi");//(ml == arquivo)
		}
		
		if(ml_ok){
			try {
				setMainDataSource(Manager.createDataSource(mediaLocator));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(parent, 
						"Exception locating media: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
				return;
			}
		}
	}
	
	private boolean openFromWebcam(){
		formatoVideo = VideoFormat.YUV;
		
		VideoFormat vidformat = new VideoFormat(formatoVideo);
		Vector devices = CaptureDeviceManager.getDeviceList(vidformat);
		CaptureDeviceInfo di = null;
		if (devices.size() > 0) di = (CaptureDeviceInfo) devices.elementAt(0);
		
		if(di==null){
			formatoVideo = VideoFormat.RGB;
			vidformat = new VideoFormat(formatoVideo);
			
			devices = CaptureDeviceManager.getDeviceList(vidformat);
			if (devices.size() > 0) di = (CaptureDeviceInfo) devices.elementAt(0);
			else {
				JOptionPane.showMessageDialog(parent, 
						"Your camera is not connected (RGB or YUV)", "No webcam found", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		
		mediaLocator = di.getLocator();
		return true;
	}
	
	private boolean openFromFile(String arquivo){
		//formatoVideoDisponivel = VideoFormat.YUV;
		formatoVideo = VideoFormat.RGB;
		
		try {
			mediaLocator = new MediaLocator(new File(arquivo).toURL());
			
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(parent, 
					"Exception locating media: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}
	
	public void makeDataSourceCloneable(){
		// turn our data source to a cloneable data source
		setMainDataSource(Manager.createCloneableDataSource(getMainDataSource()));
		/*
		if(processor==null || processor.getDataOutput()==null){
			setMainDataSource(Manager.createCloneableDataSource(getMainDataSource()));
		}else{
			setMainDataSource(Manager.createCloneableDataSource(processor.getDataOutput()));
		}
		*/
	}
	
	public void startProcessing(){
		
		try{
			processor = Manager.createProcessor(getMainDataSource());
		}catch (IOException e) {
			JOptionPane.showMessageDialog(parent, 
					"IO Exception creating processor: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}catch (NoProcessorException e) {
			JOptionPane.showMessageDialog(parent, 
					"Exception creating processor: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		PlayerStateHelper playhelper = new PlayerStateHelper(processor);
		if(!playhelper.configure(10000)){
			JOptionPane.showMessageDialog(parent, 
					"cannot configure processor", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		processor.setContentDescriptor(null);
		
		/*
		//- EXTTRA -
		TrackControl videoTrack = null;
		//Obtain the track controls.
		TrackControl tc[] = processor.getTrackControls();
		
		if (tc == null) {
			System.err.println("Failed to obtain track controls from the processor.");
		}else{
			
			// Search for the track control for the video track.
			for (int i = 0; i < tc.length; i++) {
				System.out.println("TrackControl["+i+"]: "+tc[i].getClass().toString()+" ("+tc[i].getClass().getSuperclass().toString()+")");
				if (tc[i] instanceof BasicTrackControl){
					//((BasicTrackControl)tc[i]).stopTrack();
				}
				if (tc[i].getFormat() instanceof VideoFormat) {
					videoTrack = tc[i];
					//break;
				}
			}
			if (videoTrack == null) {
				System.err.println("The input media does not contain a video track.");
			}else{
				System.out.println("Video format: " + videoTrack.getFormat());
				
				adicionaCodecs(videoTrack);
			}
		}           
		//--
		 */
		
		if(!playhelper.realize(10000)){
			JOptionPane.showMessageDialog(parent, 
					"cannot realize processor", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		// In order for or your clones to start, you must start the original source
		processor.start();
		setProcessing(true);
	}
	
	/*
	private void adicionaCodecs(TrackControl videoTrack){
		//Instantiate and set the frame access codec to the data flow path.
		try {
			Codec codec[] = { new ARGBSubtracaoMediaDesvio(), new ARGBConjuntosObjMemoComLinhas() };
			videoTrack.setCodecChain(codec);
		} catch (UnsupportedPlugInException e) {
			System.err.println("The processor does not support effects.");
		}
	}
	*/
	
	public DataSource cloneMainDataSource(){
		if(!getProcessing()) setMainSource();
		return ((SourceCloneable)getMainDataSource()).createClone();
	}
	
	public DataSource getMainDataSource(){
		return mainDataSource;
	}
	
	public void setMainDataSource(DataSource mainDataSource){
		this.mainDataSource = mainDataSource;
	}
	
	public void setMediaLocator(MediaLocator mediaLocator){
		this.mediaLocator = mediaLocator;
	}
	
	public MediaLocator getMediaLocator(){
		return mediaLocator;
	}
	
	public boolean getProcessing(){
		return processing;
	}
	
	public void setProcessing(boolean processing){
		this.processing = processing;
		
	}
	
	public void setParent(AFrame parent){
		this.parent = parent;
	}
	
	public Component getParent(){
		return parent;
	}
}