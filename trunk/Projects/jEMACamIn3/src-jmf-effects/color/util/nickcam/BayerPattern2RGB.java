package color.util.nickcam;
/*
 * Baseado em: http://lamspeople.epfl.ch/decotignie/nickcam/nickcam.java
 * 
 * Applet HTML Code:
 * <h1>PicoWebCam</h1>
 * <applet name="NickCam" codebase="." code="nickcam" width=660 height=480 background="#000000">
 *  <param name=grab value="grabcam.cgi">
 *  <param name=zero value="resetcam.cgi">
 *  <param name=upload value="readcam.cgi">
 *  <param name=sharpness value="0.75">
 * </applet>
 * <a href="http://www.picoweb.net/">www.picoweb.net</a>
 */

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import br.ufabc.bassani.jmf.firewire.media.protocol.fwc.FireWireCamera;


/*
 * Read data from digital camera on Firewire bus, convert to image, and display
 */
public class BayerPattern2RGB extends Applet implements KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6701783009807144071L;

	static final int ALPHA = 24;
	static final int RED   = 16;
	static final int GREEN = 8;
	static final int BLUE  = 0;

	static final int NONE     = 0;
	static final int FILL     = 1 << 0;
	static final int SHARPEN  = 1 << 1;
	static final int ALL      = (FILL | SHARPEN);

	String ErrMsg = "???";
	byte[] RawPixels; // where to store raw pixel data from camera

	Image image;
	int w = 640;
	int h = 480;
	int mag = 1;
	int action = ALL;

	public int GetPix(int color,
			int pixels[], int x, int y,
			int w, int h) {
		if (x < 0) x = 0;
		if (x >= w) x = w-1;
		if (y < 0) y = 0;
		if (y >= h) y = h-1;

		int val = pixels[w * y + x];
		switch (color) {
		case ALPHA: val = val >> ALPHA; break;
		case RED  : val = val >> RED; break;
		case GREEN: val = val >> GREEN; break;
		case BLUE : val = val >> BLUE; break;
		}
		return (val & 255);
	}

	public void PutPix(int val, int color,
			int pixels[], int x, int y,
			int w, int h) {
		int nval;

		if (x < 0) x = 0;
		if (x >= w) x = w-1;
		if (y < 0) y = 0;
		if (y >= h) y = h-1;
		if (val < 0) val = 0;
		if (val > 255) val = 255;

		switch (color) {
		case ALPHA:
			nval = (pixels[w * y + x] & (~(255 << ALPHA))) | (val << ALPHA);
			break;
		case RED  :
			nval = (pixels[w * y + x] & (~(255 << RED))) | (val << RED);
			break;
		case GREEN:
			nval = (pixels[w * y + x] & (~(255 << GREEN))) | (val << GREEN);
			break;
		case BLUE :
			nval = (pixels[w * y + x] & (~(255 << BLUE))) | (val << BLUE);
			break;
		default:
			nval = pixels[w * y + x];
		break;
		}
		pixels[w * y + x] = nval;
	}

	void MakeImage(int action, byte PixBuf[], int w, int h) {
		if(false){
			MakeImage_GRBG(action, PixBuf, w, h);
		}else{
			MakeImage_RGGB(PixBuf, w, h);
		}
	}

	void MakeImage_GRBG(int action, byte PixBuf[], int w, int h) {
		int bayers[] = new int[w * h];
		int sharp_bayers[] = new int[w * h];

		// clear pixel array (set alpha channel)
		int OFF = 7;
		for (int y = 0; y < h; ++y) {
			for (int x = 0; x < w; ++x) {
				bayers[w * y + x] = 255 << ALPHA;
				sharp_bayers[w * y + x] = 255 << ALPHA;
			}
		}

		// - GRBG -
		//
		//  Bayer Color Pattern (camera pixels)
		//
		//   +--------+--------+
		//   |        |        |
		//   |        |        |
		//   | Green1 |   Red  |
		//   |        |        |
		//   |        |        |
		//   +--------+--------+
		//   |        |        |
		//   |        |        |
		//   |  Blue  | Green2 |
		//   |        |        |
		//   |        |        |
		//   +--------+--------+
		//
		//  Read from camera:
		//
		//  (1) One row of red pixels values (0-255)
		//  (2) One row of green1 pixels values (0-255)
		//  (3) One row of green2 pixels values (0-255)
		//  (4) One row of blue pixels values (0-255)
		//

		//
		// Unscramble into sparse pixel array
		//

		// red
		for (int y = 0; y < h; ++y) {
			int xoff = 0;
			for (int x = 0; x < w; ++x) {
				if (((y&1) == 0) && (x < (w / 2))) {
					int off = y * w + x + OFF;
					int val = ((int)PixBuf[off]) & 255;
					PutPix(val, RED, bayers, (xoff+1)+2, y, w, h);
					xoff += 2;
				}
			}
		}

		// green1
		for (int y = 0; y < h; y += 1) {
			int xoff = 0;
			for (int x = 0; x < w; x += 1) {
				if (((y&1) == 0) && (x >= (w / 2))) {
					int off = y * w + x + OFF;
					int val = ((int)PixBuf[off]) & 255;
					PutPix(val, GREEN, bayers, xoff-2, y, w, h);
					xoff += 2;
				}
			}
		}

		// green2
		for (int y = 0; y < h; y += 1) {
			int xoff = 0;
			for (int x = 0; x < w; x += 1) {
				if (((y&1) == 1) && (x < (w / 2))) {
					int off = y * w + x + OFF;
					int val = ((int)PixBuf[off]) & 255;
					PutPix(val, GREEN, bayers, (xoff+1)+2, y, w, h);
					xoff += 2;
				}
			}
		}

		// blue
		for (int y = 0; y < h; y += 1) {
			int xoff = 0;
			for (int x = 0; x < w; x += 1) {
				if (((y&1) == 1) && (x >= (w / 2))) {
					int off = y * w + x + OFF;
					int val = ((int)PixBuf[off]) & 255;
					PutPix(val, BLUE, bayers, xoff-2, y, w, h);
					xoff += 2;
				}
			}
		}

		//
		// Fill in missing pixels in Bayer array
		//
		if ((action & FILL) != 0) for (int y = 0; y < h; ++y) {
			for (int x = 0; x < w; ++x) {
				int val;
				// Fill in missing green pixels
				if ((((x&1) == 1) && ((y&1) == 0)) ||
						(((x&1) == 0) && ((y&1) == 1))) {
					val = (GetPix(GREEN, bayers, x  , y-1, w, h)
							+ GetPix(GREEN, bayers, x-1, y  , w, h)
							+ GetPix(GREEN, bayers, x+1, y  , w, h)
							+ GetPix(GREEN, bayers, x  , y+1, w, h)) >> 2;
					PutPix(val, GREEN, bayers, x, y, w, h);
				}

				// Fill in missing red/blue pixels (same line)
				if (((x&1) == 0) && ((y&1) == 0)) {  // green1 (need red/blue)
					val = (GetPix(RED, bayers, x-1, y  , w, h)
							+ GetPix(RED, bayers, x+1, y  , w, h)) >> 1;
					PutPix(val, RED, bayers, x, y, w, h);

					val = (GetPix(BLUE, bayers, x  , y-1, w, h)
							+ GetPix(BLUE, bayers, x  , y+1, w, h)) >> 1;
					PutPix(val, BLUE, bayers, x, y, w, h);
				}

				if (((x&1) == 1) && ((y&1) == 1)) {  // green2 (need red/blue)
					val = (GetPix(BLUE, bayers, x-1, y  , w, h)
							+ GetPix(BLUE, bayers, x+1, y  , w, h)) >> 1;
					PutPix(val, BLUE, bayers, x, y, w, h);

					val = (GetPix(RED, bayers, x  , y-1, w, h)
							+ GetPix(RED, bayers, x  , y+1, w, h)) >> 1;
					PutPix(val, RED, bayers, x, y, w, h);
				}

				// Fill in missing red/blue pixels (different lines)
				if (((x&1) == 0) && ((y&1) == 1)) { // blue (need red)
					val = (GetPix(RED, bayers, x+1, y-1, w, h)
							+ GetPix(RED, bayers, x-1, y-1, w, h)
							+ GetPix(RED, bayers, x+1, y+1, w, h)
							+ GetPix(RED, bayers, x-1, y+1, w, h)) >> 2;
					PutPix(val, RED, bayers, x, y, w, h);
				}
				if (((x&1) == 1) && ((y&1) == 0)) { // red (need blue)
					val = (GetPix(BLUE, bayers, x+1, y-1, w, h)
							+ GetPix(BLUE, bayers, x-1, y-1, w, h)
							+ GetPix(BLUE, bayers, x+1, y+1, w, h)
							+ GetPix(BLUE, bayers, x-1, y+1, w, h)) >> 2;
					PutPix(val, BLUE, bayers, x, y, w, h);
				}
			}
		}

		// Sharpen image
		if ((action & SHARPEN) != 0) {
			double f = 0.75;  // sharpness (0=none)
			String s = getParameter("sharpness");
			if (s != null) {
				try {
					f = Float.valueOf(s).floatValue();
				} catch (Exception e) {
				}
			}
			for (int y = 0; y < h; ++y) {
				for (int x = 0; x < w; ++x) {
					for (int i = 0; i < 3; ++i) {
						int color = 0;
						switch (i) {
						case 0: color = RED; break;
						case 1: color = GREEN; break;
						case 2: color = BLUE; break;
						}
						int val = ((int)(
								GetPix(color, bayers, x-1, y  , w, h) * -f
								+ GetPix(color, bayers, x  , y-1, w, h) * -f
								+ GetPix(color, bayers, x  , y  , w, h) * (1+4*f)
								+ GetPix(color, bayers, x  , y+1, w, h) * -f
								+ GetPix(color, bayers, x-1, y  , w, h) * -f
						));
						PutPix(val, color, sharp_bayers, x, y, w, h);
					}
				}
			}
			for (int y = 0; y < h; ++y) {
				for (int x = 0; x < w; ++x) {
					bayers[w * y + x] = sharp_bayers[w * y + x];
				}
			}
		}

		// Make a black border
		for (int y = 0; y < h; y += 1) {
			int size = 4;
			for (int x = 0; x < w; x += 1) {
				if (x < size || x >= w - size)
					bayers[w * (y) + (x)] = 255 << 24;
				if (y < size || y >= h - size)
					bayers[w * (y) + (x)] = 255 << 24;
			}
		}

		image = createImage(new MemoryImageSource(w, h, bayers, 0, w));
	}

	void MakeImage_RGGB(byte PixBuf[], int w, int h) {
		int bayers[] = new int[w * h];

		long t0, t1;
		t0 = System.currentTimeMillis();
		
		// clear pixel array (set alpha channel)
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				bayers[w * y + x] = (255 << ALPHA);
			}
		}
		
		t1 = System.currentTimeMillis();
		System.out.println("Convert: alpha loop \t "+(t1-t0)+" ms.");
		t0 = System.currentTimeMillis();

		// - RGGB -
		//
		//  Bayer Color Pattern (camera pixels)
		//
		//   +--------+--------+
		//   |        |        |
		//   |        |        |
		//   | Red    | Green1 |
		//   |        |        |
		//   |        |        |
		//   +--------+--------+
		//   |        |        |
		//   |        |        |
		//   | Green2 |  Blue  |
		//   |        |        |
		//   |        |        |
		//   +--------+--------+
		//
		//  Read from camera:
		//
		//  (1) One row of red pixels values (0-255)
		//  (2) One row of green1 pixels values (0-255)
		//  (3) One row of green2 pixels values (0-255)
		//  (4) One row of blue pixels values (0-255)
		//

		//
		// Unscramble into sparse pixel array
		//

		// red
		for (int y = 0; y < (1*h); y++) {
			if ((y&1)==0){
				for (int x = 0; x < (1*w); x++) {
					if ((x&1)==0) {
						int off = y * w + x;
						int val = ((int)PixBuf[off]) & 255;
						PutPix(val, RED, bayers, (x/1), (y/1), w, h);
					}
				}
			}
		}
		
		t1 = System.currentTimeMillis();
		System.out.println("Convert: red loop \t "+(t1-t0)+" ms.");
		t0 = System.currentTimeMillis();

		// green 1
		for (int y = 0; y < (1*h); y++) {
			if ((y&1)==0){
				for (int x = 0; x < (1*w); x++) {
					if ((x&1)==1) {
						int off = y * w + x;
						int val = ((int)PixBuf[off]) & 255;
						PutPix(val, GREEN, bayers, (x/1), (y/1), w, h);
					}
				}
			}
		}
		
		t1 = System.currentTimeMillis();
		System.out.println("Convert: green1 loop \t "+(t1-t0)+" ms.");
		t0 = System.currentTimeMillis();

		// green 2
		for (int y = 0; y < (1*h); y++) {
			if ((y&1)==1){
				for (int x = 0; x < (1*w); x++) {
					if ((x&1)==0) {
						int off = y * w + x;
						int val = ((int)PixBuf[off]) & 255;
						PutPix(val, GREEN, bayers, (x/1), (y/1), w, h);
					}
				}
			}
		}
		
		t1 = System.currentTimeMillis();
		System.out.println("Convert: green2 loop \t "+(t1-t0)+" ms.");
		t0 = System.currentTimeMillis();

		// blue
		for (int y = 0; y < (1*h); y++) {
			if ((y&1)==1){
				for (int x = 0; x < (1*w); x++) {
					if ((x&1)==1) {
						int off = y * w + x;
						int val = ((int)PixBuf[off]) & 255;
						PutPix(val, BLUE, bayers, (x/1), (y/1), w, h);
					}
				}
			}
		}
		
		t1 = System.currentTimeMillis();
		System.out.println("Convert: blue loop \t "+(t1-t0)+" ms.");
		t0 = System.currentTimeMillis();

		//
		// Fill in missing pixels in Bayer array
		//

		for (int y = 0; y < (1*h); y++) {
			for (int x = 0; x < (1*w); x++) {
				int val;

				// Fill in missing green pixels

				if (((((y&1)==0)) && (x&1)==0) ||
						((y&1)==1) && (((x&1)==1))) {
					val = (GetPix(GREEN, bayers, x  , y-1, w, h)
							+ GetPix(GREEN, bayers, x-1, y  , w, h)
							+ GetPix(GREEN, bayers, x+1, y  , w, h)
							+ GetPix(GREEN, bayers, x  , y+1, w, h)) >> 2;
					PutPix(val, GREEN, bayers, x, y, w, h);
				}

				// Fill in missing red/blue pixels (same line)

				if (((y&1)==0) && ((x&1)==1)) {  // green1 (need red/blue)
					val = (GetPix(RED, bayers, x-1, y  , w, h)
							+ GetPix(RED, bayers, x+1, y  , w, h)) >> 1;
					PutPix(val, RED, bayers, x, y, w, h);

					val = (GetPix(BLUE, bayers, x  , y-1, w, h)
							+ GetPix(BLUE, bayers, x  , y+1, w, h)) >> 1;
					PutPix(val, BLUE, bayers, x, y, w, h);
				}

				if (((y&1)==1) && ((x&1)==0)) {  // green2 (need red/blue)
					val = (GetPix(BLUE, bayers, x-1, y  , w, h)
							+ GetPix(BLUE, bayers, x+1, y  , w, h)) >> 1;
					PutPix(val, BLUE, bayers, x, y, w, h);

					val = (GetPix(RED, bayers, x  , y-1, w, h)
							+ GetPix(RED, bayers, x  , y+1, w, h)) >> 1;
					PutPix(val, RED, bayers, x, y, w, h);
				}

				// Fill in missing red/blue pixels (different lines)

				if (((y&1)==1) && ((x&1)==1)) { // blue (need red)
					val = (GetPix(RED, bayers, x+1, y-1, w, h)
							+ GetPix(RED, bayers, x-1, y-1, w, h)
							+ GetPix(RED, bayers, x+1, y+1, w, h)
							+ GetPix(RED, bayers, x-1, y+1, w, h)) >> 2;
					PutPix(val, RED, bayers, x, y, w, h);
				}

				if (((y&1)==0) && ((x&1)==0)) { // red (need blue)
					val = (GetPix(BLUE, bayers, x+1, y-1, w, h)
							+ GetPix(BLUE, bayers, x-1, y-1, w, h)
							+ GetPix(BLUE, bayers, x+1, y+1, w, h)
							+ GetPix(BLUE, bayers, x-1, y+1, w, h)) >> 2;
					PutPix(val, BLUE, bayers, x, y, w, h);
				}
			}
		}
		
		t1 = System.currentTimeMillis();
		System.out.println("Convert: extras loop \t "+(t1-t0)+" ms.");
		t0 = System.currentTimeMillis();

		image = createImage(new MemoryImageSource(w, h, bayers, 0, w));
	}

	protected FireWireCamera fwc = new FireWireCamera ();
	private int count = 0;

	boolean CommandCamera(boolean saveData) {
		try {
			System.out.println("fwc.getFrame");
			// Convert the temp buffer to a byte array 
			RawPixels = fwc.getFrame();

			if(false){
				FileOutputStream fos = new FileOutputStream(new File("ByteArray_"+count+".data"));
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(RawPixels);
				oos.flush();
				oos.close();
				fos.flush();
				fos.close();
			}
			count++;

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean SnapPhoto() {
		return GetPhoto(true);
	}

	boolean GetPhoto() {
		return GetPhoto(false);
	}
	
	boolean GetPhoto(boolean saveData) {
		image = null;
		if (CommandCamera(saveData)) {
			// Turn raw camera pixels into "real" image
			MakeImage(ALL, RawPixels, w, h);
			return true;
		}
		return false;
	}

	public void init() {
		setBackground(Color.black);
		
		addKeyListener(this);

		enableEvents(AWTEvent.MOUSE_EVENT_MASK|AWTEvent.KEY_EVENT_MASK);

		System.out.println("fwc.configure");
		fwc.configure ();

		System.out.println("fwc.start");
		fwc.start();

		GetPhoto();
	}

	@Override
	public void destroy() {
		super.destroy();
		System.out.println("fwc.stop");
		fwc.stop();
		System.out.println("fwc.cleanUp");
		fwc.cleanUp();
	}

	public void paint(Graphics g) {
		g.setColor(Color.green);
		Font textFont = new Font("TimesRoman", Font.PLAIN, 14);
		g.setFont(textFont);

		if (RawPixels == null) {
			g.drawString("Camera access error! ("+ErrMsg+")",
					10, 30);
		} else {
			if (image == null) {
				g.drawString("Can't make image ("+ErrMsg+")", 10, 30);
			} else {
				Insets insets = getInsets();
				int x = insets.left, y = insets.top;
				
				int gw  = getSize().width;
				int gh = getSize().height;
				int w = (mag * gw) / 4;
				int h = (mag * gh) / 4;
				x += (gw - w) / 2;
				y += (gh - h) / 2;
				g.drawImage(image, x, y, w, h, this);
			}
		}
	}

	public void processMouseEvent(MouseEvent e) {
		switch(e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			int mods = e.getModifiers();
			System.out.println("mods="+mods);
			if ((mods & MouseEvent.BUTTON2_MASK) != 0) {
				System.out.println("Middle button pressed");
				if (RawPixels != null) {
					switch (action) {
					case ALL: action = NONE; break;
					case NONE: action = FILL; break;
					case FILL: action = ALL; break;
					}
					MakeImage(action, RawPixels, w, h);
				}
			} else if ((mods & MouseEvent.BUTTON1_MASK) != 0){
				System.out.println("Left button pressed");
				mag *= 2;
				if (mag > 4) mag = 1;
			} else if ((mods & MouseEvent.BUTTON3_MASK) != 0) {
				System.out.println("Right button pressed");
				SnapPhoto();
			}
			repaint(); 
			break;
		}
		super.processMouseEvent(e);
	}
	
	

	public void keyPressed(KeyEvent e) {
		//processMyKeyEvent(e);
	}

	public void keyReleased(KeyEvent e) {
		//processMyKeyEvent(e);
	}

	public void keyTyped(KeyEvent e) {
		//processMyKeyEvent(e);
	}

	public void processKeyEvent(KeyEvent e) {
		System.out.println("Event: " + e.getID()+" "+e.getKeyChar());
		
		System.out.println("Event: " + KeyEvent.KEY_TYPED);
		
		switch(e.getID()){
		case KeyEvent.KEY_TYPED:
			if(e.getKeyChar()=='s'){
				if(loopThread==null || !loopThread.isAtivo()){
					loopThread = new LoopCamera();
					loopThread.start();
				}else{
					loopThread.setInativo();
					loopThread = null;
				}
				repaint();
				break;
			}
		}
	}
	
	private LoopCamera loopThread;
	
	class LoopCamera extends Thread {
		boolean ativo = true;
		public void run() {
			System.out.println(" - run - ");
			long start = System.currentTimeMillis();
			int count = 0;
			while(ativo){
				System.out.println(" - run call SnapPhoto - ");
				SnapPhoto();
				repaint();
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				} catch (Exception e) {
					e.printStackTrace();
				}
				count++;
			}
			long end = System.currentTimeMillis();
			System.out.println(" - run end - ");
			System.out.println(" - run FPS : "+(count/((end-start)/1000)));
		}
		public boolean isAtivo(){
			return ativo;
		}
		public void setInativo(){
			ativo = false;
		}
	}

}
