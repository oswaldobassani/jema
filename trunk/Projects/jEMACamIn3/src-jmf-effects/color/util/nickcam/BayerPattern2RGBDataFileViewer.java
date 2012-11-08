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
import java.awt.event.MouseEvent;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;

/*
 * Read data from digital camera on Firewire bus, convert to image, and display
 */
public class BayerPattern2RGBDataFileViewer extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4893755935904717522L;

	static final int ALPHA = 24;
	static final int RED   = 16;
	static final int GREEN = 8;
	static final int BLUE  = 0;

	byte[] RawPixels; // where to store raw pixel data from camera

	Image image;
	int w = 640;
	int h = 480;

	public int GetPix(int color, int pixels[], int x, int y, int w, int h) {
		if (x < 0) x = 0;
		if (x >= w) x = w-1;
		if (y < 0) y = 0;
		if (y >= h) y = h-1;

		int val = pixels[w * y + x];
		switch (color) {
		case ALPHA:
			val = val >> ALPHA;
			break;
		case RED:
			val = val >> RED;
			break;
		case GREEN:
			val = val >> GREEN;
			break;
		case BLUE:
			val = val >> BLUE;
			break;
		}
		return (val & 255);
	}

	public void PutPix(int val, int color, int pixels[], int x, int y, int w, int h) {
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

	void MakeImage(byte PixBuf[], int w, int h) {
		MakeImage_RGGB(PixBuf, w, h);
	}

	void MakeImage_RGGB(byte PixBuf[], int w, int h) {
		int bayers[] = new int[w * h];

		// clear pixel array (set alpha channel)
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				bayers[w * y + x] = (255 << ALPHA);
			}
		}

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

		image = createImage(new MemoryImageSource(w, h, bayers, 0, w));
	}

	protected String filePath = "imagens/casa/firewire/bytearray.data/";
	private int count = 0;

	boolean CommandCamera() {
		try {
			// Convert the temp buffer to a byte array 
			FileInputStream fos = null;
			try {
				fos = new FileInputStream(new File(filePath+"ByteArray_"+count+".data"));
			} catch (FileNotFoundException e) {
				count = 0;
				fos = new FileInputStream(new File(filePath+"ByteArray_"+count+".data"));
			}
			ObjectInputStream oos = new ObjectInputStream(fos);
			int size = 640*480*2;
			if(RawPixels==null || RawPixels.length!=size){
				RawPixels = new byte[size];
			}
			RawPixels = (byte[])oos.readObject();
			
			oos.close();
			fos.close();
			count++;

			System.out.println("Lendo arquivo seq "+count+", size "+size);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	boolean SnapPhoto() {
		return GetPhoto();
	}

	boolean GetPhoto() {
		image = null;
		if (CommandCamera()) {
			// Turn raw camera pixels into "real" image
			MakeImage(RawPixels, w, h);
			return true;
		}
		return false;
	}

	public void init() {
		setBackground(Color.black);

		enableEvents(AWTEvent.MOUSE_EVENT_MASK);

		GetPhoto();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	public void paint(Graphics g) {
		g.setColor(Color.green);
		Font textFont = new Font("TimesRoman", Font.PLAIN, 14);
		g.setFont(textFont);

		if (RawPixels == null) {
			g.drawString("Camera access error! ()", 10, 30);
		} else {
			if (image == null) {
				g.drawString("Can't make image ()", 10, 30);
			} else {
				Insets insets = getInsets();
				int x = insets.left, y = insets.top;

				int gw  = getSize().width;
				int gh = getSize().height;
				int w = gw;
				int h = gh;
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
					MakeImage(RawPixels, w, h);
				}
			} else if ((mods & MouseEvent.BUTTON1_MASK) != 0){
				System.out.println("Left button pressed");
			} else if ((mods & MouseEvent.BUTTON3_MASK) != 0) {
				System.out.println("Right button pressed");
				SnapPhoto();
			}
			repaint(); 
			break;
		}
		super.processMouseEvent(e);
	}

}
