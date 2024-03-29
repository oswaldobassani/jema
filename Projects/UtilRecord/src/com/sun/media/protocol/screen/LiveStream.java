/*
 * @(#)LiveStream.java	1.2 01/03/02
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */


package com.sun.media.protocol.screen;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

public class LiveStream implements PushBufferStream, Runnable {

    protected ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW);
    protected int maxDataLength;
    protected int [] data;
    protected Dimension size;
    protected RGBFormat rgbFormat;
    protected boolean started;
    protected Thread thread;
    protected float frameRate = 1f;
    protected BufferTransferHandler transferHandler;
    protected Control [] controls = new Control[0];
    protected int x, y, width, height;

    protected Robot robot = null;

    public LiveStream(MediaLocator locator) {
	try {
	    parseLocator(locator);
	} catch (Exception e) {
	    System.err.println(e);
	}
	//size = Toolkit.getDefaultToolkit().getScreenSize();
	size = new Dimension(width, height);
	try {
	    robot = new Robot();
	} catch (AWTException awe) {
	    throw new RuntimeException("");
	}
	maxDataLength = size.width * size.height * 3;
	rgbFormat = new RGBFormat(size, maxDataLength,
				  Format.intArray,
				  frameRate,
				  32,
				  0xFF0000, 0xFF00, 0xFF,
				  1, size.width,
				  VideoFormat.FALSE,
				  Format.NOT_SPECIFIED);
	
	// generate the data
	data = new int[maxDataLength];
	thread = new Thread(this, "Screen Grabber");
    }

    protected void parseLocator(MediaLocator locator) {
	String rem = locator.getRemainder();
	// Strip off starting slashes
	while (rem.startsWith("/") && rem.length() > 1)
	    rem = rem.substring(1);
	StringTokenizer st = new StringTokenizer(rem, "/");
	if (st.hasMoreTokens()) {
	    // Parse the position
	    String position = st.nextToken();
	    StringTokenizer nums = new StringTokenizer(position, ",");
	    String stX = nums.nextToken();
	    String stY = nums.nextToken();
	    String stW = nums.nextToken();
	    String stH = nums.nextToken();
	    x = Integer.parseInt(stX);
	    y = Integer.parseInt(stY);
	    width = Integer.parseInt(stW);
	    height = Integer.parseInt(stH);
	}
	if (st.hasMoreTokens()) {
	    // Parse the frame rate
	    String stFPS = st.nextToken();
	    frameRate = (Double.valueOf(stFPS)).floatValue();
	}
    }
    
    /***************************************************************************
     * SourceStream
     ***************************************************************************/
    
    public ContentDescriptor getContentDescriptor() {
	return cd;
    }

    public long getContentLength() {
	return LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
	return false;
    }

    /***************************************************************************
     * PushBufferStream
     ***************************************************************************/

    int seqNo = 0;
    
    public Format getFormat() {
	return rgbFormat;
    }

    public void read(Buffer buffer) throws IOException {
	synchronized (this) {
	    Object outdata = buffer.getData();
	    if (outdata == null || !(outdata.getClass() == Format.intArray) ||
		((int[])outdata).length < maxDataLength) {
		outdata = new int[maxDataLength];
		buffer.setData(outdata);
	    }
	    buffer.setFormat( rgbFormat );
	    buffer.setTimeStamp( (long) (seqNo * (1000 / frameRate) * 1000000) );
	    BufferedImage bi = robot.createScreenCapture(
		new Rectangle(x, y, width, height));
	    bi.getRGB(0, 0, width, height,
		      (int[])outdata, 0, width);
	    buffer.setSequenceNumber( seqNo );
	    buffer.setLength(maxDataLength);
	    buffer.setFlags(Buffer.FLAG_KEY_FRAME);
	    buffer.setHeader( null );
	    seqNo++;
	}
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
	synchronized (this) {
	    this.transferHandler = transferHandler;
	    notifyAll();
	}
    }

    void start(boolean started) {
	synchronized ( this ) {
	    this.started = started;
	    if (started && !thread.isAlive()) {
		thread = new Thread(this);
		thread.start();
	    }
	    notifyAll();
	}
    }

    /***************************************************************************
     * Runnable
     ***************************************************************************/

    public void run() {
	while (started) {
	    synchronized (this) {
		while (transferHandler == null && started) {
		    try {
			wait(1000);
		    } catch (InterruptedException ie) {
		    }
		} // while
	    }

	    if (started && transferHandler != null) {
		transferHandler.transferData(this);
		try {
		    Thread.currentThread().sleep( 10 );
		} catch (InterruptedException ise) {
		}
	    }
	} // while (started)
    } // run

    // Controls
    
    public Object [] getControls() {
	return controls;
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
}
