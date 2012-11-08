package br.ufabc.bassani.jemacamin.jpct.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.MemoryImageSource;

import javax.media.Buffer;
import javax.media.Codec;
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

import br.bassani.jmf.efeitos.CodecsDialog;
import br.bassani.jmf.gui.info.CodecPack;
import br.bassani.jmf.gui.info.UrlInfo;

public abstract class VideoPlayerViewport extends AViewport implements VideoRenderer, ControllerListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8720785463249237230L;

	/* - Video - Variáveis do sistema de video */
	
	/* - Video - Variáveis do input e codecs a utilizar */
	private UrlInfo urlInfo;
	private CodecPack codecsPack;
	
	/* - Video - Nome do PlugIn de visualização */
	private static final String name = "VideoPlayerViewport";
	
	/* - Video - Processador de midia */
	private Processor processor;
	private int[] waitSync = new int[0];
	private boolean stateTransOK = true;
    
	/* - Video - Formatos a serem utilizados */
    protected RGBFormat inputFormat;
    protected RGBFormat supportedFormat;
    protected Format[] supportedFormats;
    
    /* - Video - Outras variáveis */
    
    protected Buffer lastBuffer = null;
    
    protected int videoWidth = 0;
    protected int videoHeight = 0;
    protected boolean started = false;
    protected Object lastData = null;

    private int count = 0;
    private boolean firstFrame;
    private int btype;
    //private boolean byRef = true;
    
    private Image imagemVideo;
	
	public VideoPlayerViewport() {
		super();
	}

	/* (non-Javadoc)
	 * @see br.ufabc.bassani.jemacamin.jpct.components.AViewport#init()
	 */
	@Override
	protected void init() {
		System.out.println("init() - Sistema Video Player Viewport");
		/* - Video - */
        
        /*
		 * RGBFormat(java.awt.Dimension size, int maxDataLength, java.lang.Class dataType, float frameRate,
		 * 		int bitsPerPixel, int red, int green, int blue, 
		 * 		int pixelStride, int lineStride, int flipped, int endian) 
		 */
        
		supportedFormat =  new RGBFormat(new Dimension(640,480),
			      Format.NOT_SPECIFIED,
			      Format.byteArray,
			      15.0f,
			      24,
			      3, 2, 1,
			      3, Format.NOT_SPECIFIED,
			      Format.TRUE,
			      Format.NOT_SPECIFIED);
		
		/*
		supportedFormat =  new RGBFormat(null,
			      Format.NOT_SPECIFIED,
			      Format.byteArray,
			      15.0f,
			      24,
			      3, 2, 1,
			      3, Format.NOT_SPECIFIED,
			      Format.TRUE,
			      Format.NOT_SPECIFIED);*/
		
        /* 
        supportedRGB =  new RGBFormat(null,
			      Format.NOT_SPECIFIED,
			      Format.intArray,
			      Format.NOT_SPECIFIED,
			      32,
			      0x00FF0000, 0x0000FF00, 0x000000FF,
			      1, Format.NOT_SPECIFIED,
			      Format.TRUE,
			      Format.NOT_SPECIFIED); */

		supportedFormats = new VideoFormat[] {supportedFormat };
		firstFrame = true;

		String os = System.getProperty("os.name");
		System.out.println("running on " + os);
		if ( os.startsWith("W") || os.startsWith("w")) {
		  btype = BufferedImage.TYPE_3BYTE_BGR;
		  //byRef = true;
		}else if (os.startsWith("S") || os.startsWith("s")){
		  btype = BufferedImage.TYPE_4BYTE_ABGR;
		  //byRef = true;
		} else {
		  btype = BufferedImage.TYPE_3BYTE_BGR;
		  //byRef = false;
		}
		System.out.println("Tipo de buffer (by OS) - btype: " + btype);

		//abreMidia();
		
		System.out.println("init() - Sistema Video Player Viewport - Concluído!");
	}

	/**
	 * Desenha o frame atual do video no gráfico ativo, caso este exista.
	 * 
	 * @param g
	 * @param width
	 * @param height
	 */
	public void drawBackground_VideoFrame(Graphics g, int width, int height)
	{
		if(imagemVideo!=null){
			//Draw Video
			g.drawImage(imagemVideo ,0,0, this);
		}
	}
	
	/* - Video - */
	
	public void setMidiaInput(UrlInfo _urlInfo){
		urlInfo = _urlInfo;
	}
	
	public void setCodecs(CodecPack _codecsPack){
		codecsPack = _codecsPack;
	}
	
	public void setMidiaInputAndCodecs(UrlInfo _urlInfo, CodecPack _codecsPack){
		urlInfo = _urlInfo;
		codecsPack = _codecsPack;
	}
	
	public void abreMidia(UrlInfo _urlInfo, CodecPack _codecsPack){
		setMidiaInputAndCodecs(_urlInfo, _codecsPack);
		abreMidia();
	}

	public void abreMidia(){
		if(codecsPack!=null){
			codecs = codecsPack.codecs;
		}else{
			codecs = new Codec[0];
		}
    	if(urlInfo==null) return;
    	MediaLocator ml = new MediaLocator(urlInfo.url);
	    if ( ml == null ) {
			System.out.println("can not access url = " + urlInfo.url);
			// System.exit(0);
	    } else {
	    	abreMidia(ml);
	    }
    }
	
    private boolean abreMidia(MediaLocator ml) {
    	if(processor!=null){
    		try {
    			System.out.println("Parando o processador de media anterior ...");
				terminaPlayer();
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println("Nao foi possivel parar o processador de media: " + e);
			}
    		processor = null;
    	}
    	
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
    	    	if (tc[i] instanceof TrackControl ) {
    	    		vtc = tc[i];
    	    		break;
    	    	}
    	    }
    	}

    	if ( vtc == null ) {
    	    System.out.println("can't find video track");
    	    return false;
    	}
    	
   	    try {
			vtc.setRenderer(this);
		} catch (UnsupportedPlugInException e) {
			e.printStackTrace();
		} catch (NotConfiguredError e) {
			e.printStackTrace();
		}

		System.out.println(">> adicionaCodecs");
		
		adicionaCodecs(vtc);
   	    
		System.out.println(">> prefetch");
		
    	// prefetch
    	processor.prefetch();
    	if ( !waitForState(Processor.Prefetched)) {
    	    System.out.println("Failed to prefech the processor");
    	    return false;
    	}
    	System.out.println("end of prefetch");

    	return true;
    }
    
    private Codec[] codecs;
    private void adicionaCodecs(TrackControl videoTrack){
    	if(codecs==null) return;
    	if(codecs.length==0) return;
    	
    	//Instantiate and set the frame access codecs to the data flow path.
    	try {
    		if(true){
    			CodecsDialog codecDialog = new CodecsDialog(codecs);
    			codecDialog.setLocation(10, 10);
    		}
    	    videoTrack.setCodecChain(codecs);
    	} catch (UnsupportedPlugInException e) {
    	    System.err.println("The processor does not support effects.");
    	}
    }
    
    public void iniciaPlayer(){
    	processor.start();
    }
    
    public void terminaPlayer(){
    	processor.stop();
    	processor.close();
    }

	/* - VideoRender - Metodos da interface do video render */

	/* (non-Javadoc)
	 * @see javax.media.renderer.VideoRenderer#getComponent()
	 */
	public Component getComponent() {
		return this;
	}

	/* (non-Javadoc)
	 * @see javax.media.renderer.VideoRenderer#setComponent(java.awt.Component)
	 */
	public boolean setComponent(Component c) {
		if(c==this) return true;
		return false;
	}
	
	public Component getControlComponent(){
		return processor.getControlPanelComponent();
	}

	/* (non-Javadoc)
	 * @see javax.media.Renderer#getSupportedInputFormats()
	 */
	public Format[] getSupportedInputFormats() {
		return supportedFormats;
	}

	/* (non-Javadoc)
	 * @see javax.media.Renderer#setInputFormat(javax.media.Format)
	 */
	public Format setInputFormat(Format format) {
		System.out.println("setInputFormat Format = " + format);
        if(format != null && format instanceof RGBFormat){
        	if(format.matches(supportedFormat)) {
	           inputFormat = (RGBFormat) format;
	           Dimension size = inputFormat.getSize();
	           pWidth = videoWidth = size.width;
	           pHeight = videoHeight = size.height;
	           /*
	           if(size.width==640 && size.height==480*2){
	        	   pWidth = videoWidth = 640;
	        	   pHeight = videoHeight = 480;
	           }*/
	           
	           System.out.println("setInputFormat painelWidth = " + pWidth);
	           System.out.println("setInputFormat painelHeight = " + pHeight);
	           System.out.println("setInputFormat inWidth = " + videoWidth);
	           System.out.println("setInputFormat inWidth = " + videoWidth);
	           System.out.println("setInputFormat return = " + format);
	           return format;
        	}else{
        		return null;
        		/*
        		inputFormat = (RGBFormat) format;
        		Dimension size = inputFormat.getSize();
        		size.setSize(640, 480);
        		pWidth = videoWidth = size.width;
        		pHeight = videoHeight = size.height;

        		System.out.println("setInputFormat (*) painelWidth = " + pWidth);
        		System.out.println("setInputFormat (*) painelHeight = " + pHeight);
        		System.out.println("setInputFormat (*) inWidth = " + videoWidth);
        		System.out.println("setInputFormat (*) inWidth = " + videoWidth);
        		System.out.println("setInputFormat (*) return = " + format);
        		return format;*/
        	}
       } else {
           return null;
       }
	}

	/* (non-Javadoc)
	 * @see javax.media.Renderer#start()
	 */
	public void start() {
		started = true;
	}

	/* (non-Javadoc)
	 * @see javax.media.Renderer#stop()
	 */
	public void stop() {
		started = false;
	}
	
	/* Variáveis para o metodo process(Buffer) */
	
	private BufferedImage bimg = null;
	private byte[] byteData;
	private int[] pixData;
	
	private long t0, t1;

	/* (non-Javadoc)
	 * @see javax.media.Renderer#process(javax.media.Buffer)
	 */
	public int process(Buffer buffer) {
		t0 = System.currentTimeMillis();
		
    	if ( buffer.getLength() <= 0 ) {
    		System.err.println(" buffer.getLength() = " +  buffer.getLength());
    	    return BUFFER_PROCESSED_OK;
    	}

    	if ( count < 0 ) {
    		System.err.println("count = " + count);
    	    count ++;
    	    return BUFFER_PROCESSED_OK;
    	}

    	count++;
    	//System.out.println("count = " + count);
    	
    	byte[] rawData = (byte[])(buffer.getData());

    	// System.out.println("inWidth = " + inWidth);
    	// System.out.println("inHeight = " + inHeight);
    	
    	if(bimg==null){
    		System.out.println("Tipo de buffer (BImage) - btype: "+btype);
    		System.out.println("");
    		System.out.println("\t TYPE_4BYTE_ABGR:     "+BufferedImage.TYPE_4BYTE_ABGR);
    		System.out.println("\t TYPE_4BYTE_ABGR_PRE: "+BufferedImage.TYPE_4BYTE_ABGR_PRE);
    		System.out.println("\t TYPE_3BYTE_BGR:      "+BufferedImage.TYPE_3BYTE_BGR);
    		System.out.println("\t TYPE_INT_ARGB:       "+BufferedImage.TYPE_INT_ARGB);
    		System.out.println("");
    		
    		if(btype==0){
    			// FIXME: Verificar btype = 0 !?
    			// A varial não setou devidamente.
    			btype = BufferedImage.TYPE_3BYTE_BGR;
    		}
    		
    		// if(videoWidth==640 && videoHeight==2*480) videoHeight = 480;
    		bimg = new BufferedImage(videoWidth, videoHeight, btype);
    		byteData = ((DataBufferByte)bimg.getRaster().getDataBuffer()).getData();
    		pixData = new int[videoWidth * videoHeight];
    	}

    	int outPixelIndex, inPixelIndex;
    	byte alpha_1 = (byte)0xff;
    	int lineStride = 3 * videoWidth;
    	
    	int x, y;
    	inPixelIndex = 0;
    	outPixelIndex = 0;
    	
    	boolean filp_vertical = true;
    	boolean filp_horizontal = false;
    	
    	int pixDataPos = videoWidth * videoHeight-1;
    	
    	int r,g,b;
    	
    	if ( btype == BufferedImage.TYPE_3BYTE_BGR ) {
    	    for ( y = 0; y < videoHeight; y++ ) { 
    	    	for ( x = 0; x < videoWidth; x++) {
    	    		inPixelIndex = y * lineStride + x * 3;
    	    		if(filp_vertical){
    	    			if(filp_horizontal){
    	    				outPixelIndex = (videoHeight - y - 1) * lineStride + (videoWidth - x - 1) * 3;
    	    			}else{
    	    				outPixelIndex = (videoHeight - y - 1) * lineStride + x * 3;
    	    			}
    	    		}else{
    	    			if(filp_horizontal){
    	    				outPixelIndex = y * lineStride + (videoWidth - x - 1) * 3;
    	    			}else{
    	    				outPixelIndex = y * lineStride + x * 3;
    	    			}
    	    		}
    	    		
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];//B
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];//G
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];//R
    	    		
    	    		r = rawData[inPixelIndex-1]&255;
    	    		g = rawData[inPixelIndex-2]&255;
    	    		b = rawData[inPixelIndex-3]&255;
    	    		//if(r<0 || g<0 || b<0) System.out.println("(r,g,b)=("+r+","+g+","+b+")");
    	    		
    	    		pixData[pixDataPos--] = (255 << 24) | (r << 16) | (g << 8) | (b);
    	    	}
    	    }
    	} else if ( btype == BufferedImage.TYPE_4BYTE_ABGR ) {
    		int lineStrideOut = 4 * videoWidth;
    		for ( y = 0; y < videoHeight; y++ ) { 
    	    	for ( x = 0; x < videoWidth; x++) {
    	    		inPixelIndex = y * lineStride + x * 3;
    	    		if(filp_vertical){
    	    			if(filp_horizontal){
    	    				outPixelIndex = (videoHeight - y - 1) * lineStrideOut + (videoWidth - x - 1) * 4;
    	    			}else{
    	    				outPixelIndex = (videoHeight - y - 1) * lineStrideOut + x * 4;
    	    			}
    	    		}else{
    	    			if(filp_horizontal){
    	    				outPixelIndex = y * lineStrideOut + (videoWidth - x - 1) * 4;
    	    			}else{
    	    				outPixelIndex = y * lineStrideOut + x * 4;
    	    			}
    	    		}
    	    		byteData[outPixelIndex++] = alpha_1;//A
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];//B
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];//G
    	    		byteData[outPixelIndex++] = rawData[inPixelIndex++];//R
    	    		
    	    		r = rawData[inPixelIndex-1]&255;
    	    		g = rawData[inPixelIndex-2]&255;
    	    		b = rawData[inPixelIndex-3]&255;
    	    		//if(r<0 || g<0 || b<0) System.out.println("(r,g,b)=("+r+","+g+","+b+")");
    	    		
    	    		pixData[pixDataPos--] = (255 << 24) | (r << 16) | (g << 8) | (b);
    	    	}
    	    }
    	} else {
    		System.out.println("Tipo de buffer desconhecido - btype: "+btype);
    	}
    	
    	// - Metodo não otimizado - (Nao Usar)
    	// imagemVideo = bimg.getScaledInstance(inWidth, inHeight, BufferedImage.SCALE_FAST);
    	// - Mode testado no br.ufabc.bassani.jpct.video.components - Metodo Otimizado
    	imagemVideo = createImage(new MemoryImageSource(videoWidth, videoHeight, pixData, 0, videoWidth));
    	
    	if ( firstFrame ) {
    	    firstFrame = false;
    	    if(this.getParent()!=null){
    	    	if(this.getParent() instanceof JDialog){
    	    		((JDialog)this.getParent()).pack();
    	    	}
    	    }
    	}
    	
    	repaint();
    	
    	t1 = System.currentTimeMillis();
    	System.out.println("(t1-t0) = " + (t1-t0));
    	System.out.println("count = " + count);
    	System.out.println("seqNb = " + buffer.getSequenceNumber());
    	
    	
    	return BUFFER_PROCESSED_OK;
	}
	
	/* (non-Javadoc)
	 * @see javax.media.PlugIn#getName()
	 */
	public String getName() {
        return name;
    }

	/* (non-Javadoc)
	 * @see javax.media.PlugIn#open()
	 */
	public void open() throws ResourceUnavailableException {
		firstFrame = true;
    	count = 0;
	}
	
	/* (non-Javadoc)
	 * @see javax.media.PlugIn#close()
	 */
	public void close() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.media.PlugIn#reset()
	 */
	public void reset() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.media.Controls#getControl(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
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

	/* (non-Javadoc)
	 * @see javax.media.Controls#getControls()
	 */
	public Object[] getControls() {
		// No controls
        return (Object[]) new Control[0];
	}

	/* (non-Javadoc)
	 * @see javax.media.ControllerListener#controllerUpdate(javax.media.ControllerEvent)
	 */
	public void controllerUpdate(ControllerEvent evt) {
		System.out.println("controllerUpdate: "+evt);
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

}
