package br.bassani.tetris.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.net.MalformedURLException;

import javax.media.Buffer;
import javax.media.ConfigureCompleteEvent;
import javax.media.Control;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NotConfiguredError;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.UnsupportedPlugInException;
import javax.media.control.TrackControl;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.renderer.VideoRenderer;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class PainelBackgroundVideo extends JPanel implements VideoRenderer,	ControllerListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5233661991903182436L;
	
	private static final String name = "PainelBackgroundVideoRender";
	
	private int painelHeight;
	private int painelWidth;
	
	Processor processor;
    int[] waitSync = new int[0];
    boolean stateTransOK = true;
    
    protected RGBFormat inputFormat;
    protected RGBFormat supportedRGB;
    protected Format[] supportedFormats;
    
    protected Buffer lastBuffer = null;
    
    protected int inWidth = 0;
    protected int inHeight = 0;
    protected boolean started = false;
    protected Object lastData = null;

    int count = 0;
    boolean firstFrame;
    int btype = 0;
    boolean byRef = true;
    
    Image imagemVideo;
	
	public PainelBackgroundVideo(){
		
		supportedRGB =  new RGBFormat(null,
			      Format.NOT_SPECIFIED,
			      Format.byteArray,
			      Format.NOT_SPECIFIED,
			      24,
			      3, 2, 1,
			      3, Format.NOT_SPECIFIED,
			      Format.TRUE,
			      Format.NOT_SPECIFIED);

		supportedFormats = new VideoFormat[] {supportedRGB };
		firstFrame = true;

		String os = System.getProperty("os.name");
		System.out.println("running on " + os);
		if ( os.startsWith("W") || os.startsWith("w")) {
		  btype = BufferedImage.TYPE_3BYTE_BGR;
		  byRef = true;
		}else if (os.startsWith("S") || os.startsWith("s")){
		  btype = BufferedImage.TYPE_4BYTE_ABGR;
		  byRef = true;
		} else {
		  btype = BufferedImage.TYPE_3BYTE_BGR;
		  byRef = false;
		}
		
		/*
		btype = BufferedImage.TYPE_4BYTE_ABGR;
		byRef = true;
		*/

		abreMidia();
	}
	
    public void abreMidia(){
		
		File f = null;
		String url = null;
		
		//url = "file:video.mov";
		//url = "file:Log_Home_RGB24_T0.avi";
		//url = "vfw://0";
		//url = "v4l://0";
		//f = new File("G:\\Videos\\VideoIn_Orbit_JMF\\VideoIn320x240_RGB.mov");
		//f = new File("G:\\Videos\\VideoIn_Orbit_JMF\\VideoIn320x240_H263.mov");
		//f = new File("G:\\Videos\\VideoIn_Orbit_JMF\\VideoIn320x240_JPEG.mov");
		//f = new File("G:\\Videos\\VideoIn_Orbit_JMF\\VideoIn640x480_RGB.mov");
		//f = new File("G:\\Videos\\VideoIn_Orbit_JMF\\VideoIn640x480_H263.mov");
		
	//	f = new File("videos/casa/usb/Log_Home_JPEG_T0.mov");
		f = new File("videos/ufabc/firewire/chessboard-artag-estereo/camera1.mov");
		
		if(url==null & f!=null){
			try {
				url = f.toURL().toString();
			} catch (MalformedURLException e) {}
		}
    	
    	MediaLocator ml = new MediaLocator(url);
	    if ( ml == null ) {
			System.out.println("can not access url = " + url);
			System.exit(0);
	    }

	    abreMidia(ml);
    }
	
    public boolean abreMidia(MediaLocator ml) {
    	try {
    	    processor = Manager.createProcessor(ml);
    	} catch (Exception ex) {
    	    System.out.println("failed to create a processor for movie " + ml);
    	    return false;
    	}

    	processor.addControllerListener(this);
    	
    	processor.configure();
    	
    	if ( !waitForState(Processor.Configured)) {
    	    System.out.println("Failed to configure the processor");
    	    return false;
    	}
    	
    	// use processor as a player
    	processor.setContentDescriptor(null);
    	
    	// obtain the track control
    	TrackControl[] tc = processor.getTrackControls();

    	if ( tc == null ) {
    	    System.out.println("Failed to get the track control from processor");
    	    return false;
    	}

    	TrackControl vtc = null;
    	
    	for ( int i =0; i < tc.length; i++ ) {
    	    if (tc[i].getFormat() instanceof VideoFormat ) {
    		vtc = tc[i];
    		break;
    	    }
    	    
    	}

    	if ( vtc == null ) {
    	    System.out.println("can't find video track");
    	    return false;
    	}
    	
   	    try {
			vtc.setRenderer(this);
		} catch (UnsupportedPlugInException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConfiguredError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	    
    	// prefetch
    	processor.prefetch();
    	if ( !waitForState(Processor.Prefetched)) {
    	    System.out.println("Failed to prefech the processor");
    	    return false;
    	}
    	System.out.println("end of prefetch");

    	return true;
    }
    
    public void iniciaPlayer(){
    	processor.start();
    }
    
    public void terminaPlayer(){
    	processor.stop();
    	processor.close();
    }
	
	public Component getComponent() {
		return this;
	}

	public boolean setComponent(Component c) {
		if(c==this) return true;
		return false;
	}

	public Format[] getSupportedInputFormats() {
		return supportedFormats;
	}

	public Format setInputFormat(Format format) {
        if ( format != null && format instanceof RGBFormat &&
                format.matches(supportedRGB)) {
               
               inputFormat = (RGBFormat) format;
               Dimension size = inputFormat.getSize();
               painelWidth = inWidth = size.width;
               painelHeight = inHeight = size.height;
   	    // System.out.println("in setInputFormat = " + format);
               return format;
           } else
               return null;
	}

	public void start() {
		started = true;
	}

	public void stop() {
		started = false;
	}

	public int process(Buffer buffer) {
    	
    	if ( buffer.getLength() <= 0 ) 
    	    return BUFFER_PROCESSED_OK;

    	if ( count < 0 ) {
    	    count ++;
    	    return BUFFER_PROCESSED_OK;
    	}

    	count++;
    	System.out.println("count = " + count);
    	
    	byte[] rawData =(byte[])(buffer.getData());

    	// System.out.println("inWidth = " + inWidth);
    	// System.out.println("inHeight = " + inHeight);

    	BufferedImage bimg = new BufferedImage(inWidth,inHeight, btype);
    	byte[] byteData = ((DataBufferByte)bimg.getRaster().getDataBuffer()).getData();

    	int outPixelIndex, inPixelIndex;
    	byte alpha_1 = (byte)0xff;
    	int lineStride = 3 * inWidth;
    	
    	int x, y;
    	inPixelIndex = 0;
    	outPixelIndex = 0;
    	
    	boolean filp_vertical = true;
    	boolean filp_horizontal = false;
    	
    	if ( btype == BufferedImage.TYPE_3BYTE_BGR ) {
    	    for ( y = 0; y < inHeight; y++ ) { 
    	    	for ( x = 0; x < inWidth; x++) {
    	    		inPixelIndex = y * lineStride + x * 3;
    	    		if(filp_vertical){
    	    			if(filp_horizontal){
    	    				outPixelIndex = (inHeight - y - 1) * lineStride + (inWidth - x - 1) * 3;
    	    			}else{
    	    				outPixelIndex = (inHeight - y - 1) * lineStride + x * 3;
    	    			}
    	    		}else{
    	    			if(filp_horizontal){
    	    				outPixelIndex = y * lineStride + (inWidth - x - 1) * 3;
    	    			}else{
    	    				outPixelIndex = y * lineStride + x * 3;
    	    			}
    	    		}
    	    		
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];
    	    	}
    	    }
    	} else if ( btype == BufferedImage.TYPE_4BYTE_ABGR ) {
    		int lineStrideOut = 4 * inWidth;
    		for ( y = 0; y < inHeight; y++ ) { 
    	    	for ( x = 0; x < inWidth; x++) {
    	    		inPixelIndex = y * lineStride + x * 3;
    	    		if(filp_vertical){
    	    			if(filp_horizontal){
    	    				outPixelIndex = (inHeight - y - 1) * lineStrideOut + (inWidth - x - 1) * 4;
    	    			}else{
    	    				outPixelIndex = (inHeight - y - 1) * lineStrideOut + x * 4;
    	    			}
    	    		}else{
    	    			if(filp_horizontal){
    	    				outPixelIndex = y * lineStrideOut + (inWidth - x - 1) * 4;
    	    			}else{
    	    				outPixelIndex = y * lineStrideOut + x * 4;
    	    			}
    	    		}
    	    		byteData[outPixelIndex++] = alpha_1;
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];
    	    	}
    	    }
    	}
    	
    	imagemVideo = bimg.getScaledInstance(inWidth, inHeight, BufferedImage.SCALE_FAST);
    	
    	if ( firstFrame ) {
    	    firstFrame = false;
    	    if(this.getParent()!=null){
    	    	if(this.getParent() instanceof JDialog){
    	    		((JDialog)this.getParent()).pack();
    	    	}
    	    }
    	}
    	
    	repaint();
    	
    	// System.out.println("in doProcess");
    	return BUFFER_PROCESSED_OK;
	}

	public void open() throws ResourceUnavailableException {
		firstFrame = true;
    	count = 0;
	}

	public void close() {
		// TODO Auto-generated method stub
	}

	public void reset() {
		// TODO Auto-generated method stub
	}
	
	public String getName() {
        return name;
    }

	public Object[] getControls() {
		//No controls
        return (Object[]) new Control[0];
	}

	public Object getControl(String controlType) {
		try {
            Class  cls = Class.forName(controlType);
            Object cs[] = getControls();
            for (int i = 0; i < cs.length; i++) {
               if (cls.isInstance(cs[i]))
                  return cs[i];
            }
            return null;
         } catch (Exception e) {   // no such controlType or such control
           return null;
         }
	}

	public void controllerUpdate(ControllerEvent evt) {
		if ( evt instanceof ConfigureCompleteEvent ||
	    	     evt instanceof RealizeCompleteEvent ||
	    	     evt instanceof PrefetchCompleteEvent ) {
	    	    synchronized (waitSync) {
	    		stateTransOK = true;
	    		waitSync.notifyAll();
	    	    }
	    	} else if ( evt instanceof ResourceUnavailableEvent) {
	    	    synchronized (waitSync) {
	    		stateTransOK = false;
	    		waitSync.notifyAll();
	    	    }
	    	} else if ( evt instanceof EndOfMediaEvent) {
	                processor.setMediaTime(new Time(0));
	    	    processor.start();
	    	    // p.close();
	    	    // System.exit(0);
	    	}
	}
	
	public boolean waitForState(int state) {
    	synchronized (waitSync) {
    	    try {
    		while ( processor.getState() != state && stateTransOK ) {
    		    waitSync.wait();
    		}
    	    } catch (Exception ex) {}
    	    
    	    return stateTransOK;
    	}
    }

	@Override
	public void paint(Graphics g) {
		if(imagemVideo==null){
			g.setColor(Color.white);
			g.fillRect(0,0,inWidth,inHeight);
		}else{
			g.drawImage(imagemVideo ,0,0, this);
		}
	}

	@Override
	public int getHeight() {
		return painelHeight;
	}
	@Override
	public int getWidth() {
		return painelWidth;
	}
	@Override
	public Dimension getMaximumSize() {
		return getSize();
	}
	@Override
	public Dimension getMinimumSize() {
		return getSize();
	}
	@Override
	public Dimension getPreferredSize() {
		return getSize();
	}
	@Override
	public Dimension getSize(Dimension size) {
		size = getSize();
		return getSize();
	}
	@Override
	public Dimension getSize() {
		return new Dimension(painelWidth, painelHeight);
	}

}