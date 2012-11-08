/* 
 * PROJECT: NyARToolkit JOGL sample program.
 * --------------------------------------------------------------------------------
 * The MIT License
 * Copyright (c) 2008 nyatla
 * airmail(at)ebony.plala.or.jp
 * http://nyatla.jp/nyartoolkit/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package jp.nyatla.nyartoolkit.jogl.sample;

import java.awt.event.*;
import java.awt.*;
import javax.media.Buffer;
import javax.media.opengl.*;

import com.sun.opengl.util.*;
import jp.nyatla.nyartoolkit.*;
import jp.nyatla.nyartoolkit.core.*;
import jp.nyatla.nyartoolkit.core.param.*;
import jp.nyatla.nyartoolkit.core.transmat.*;
import jp.nyatla.nyartoolkit.detector.*;
import jp.nyatla.nyartoolkit.jmf.utils.*;
import jp.nyatla.nyartoolkit.jogl.utils.*;

/**
 * simpleLiteと同じようなテストプログラム 出来る限りARToolKitのサンプルと似せて作ってあります。 
 * 最も一致する"Hiro"マーカーを一つ選択して、その上に立方体を表示します。
 * 
 */
public class JavaSimpleLite implements GLEventListener, JmfCaptureListener
{
	private final static int SCREEN_X = 640;

	private final static int SCREEN_Y = 480;

	private Animator _animator;

	private JmfNyARRaster_RGB _cap_image;

	private JmfCaptureDevice _capture;

	private GL _gl;

	// NyARToolkit関係
	private NyARSingleDetectMarker _nya;

	private NyARParam _ar_param;

	private Object _sync_object=new Object();
	private double[] _camera_projection = new double[16];

	public JavaSimpleLite(NyARParam i_param, NyARCode i_ar_code) throws NyARException
	{
		this._ar_param = i_param;

		Frame frame = new Frame("NyARToolkit["+this.getClass().getName()+"]");

		
		// キャプチャの準備
		JmfCaptureDeviceList devlist = new JmfCaptureDeviceList();
		this._capture = devlist.getDevice(0);
		if (!this._capture.setCaptureFormat(SCREEN_X, SCREEN_Y, 30.0f)) {
			throw new NyARException();
		}
		this._capture.setOnCapture(this);
		//JMFラスタオブジェクト
		this._cap_image = new JmfNyARRaster_RGB(this._capture.getCaptureFormat());
		
		// NyARToolkitの準備
		this._nya = new NyARSingleDetectMarker(this._ar_param, i_ar_code, 80.0,this._cap_image.getBufferType());
		this._nya.setContinueMode(true);// ここをtrueにすると、transMatContinueモード（History計算）になります。
		
		// 3Dを描画するコンポーネント
		GLCanvas canvas = new GLCanvas();
		frame.add(canvas);
		canvas.addGLEventListener(this);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		frame.setVisible(true);
		Insets ins = frame.getInsets();
		frame.setSize(SCREEN_X + ins.left + ins.right, SCREEN_Y + ins.top + ins.bottom);
		canvas.setBounds(ins.left, ins.top, SCREEN_X, SCREEN_Y);
	}

	public void init(GLAutoDrawable drawable)
	{
		this._gl = drawable.getGL();
		this._gl.glEnable(GL.GL_DEPTH_TEST);
		this._gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// NyARToolkitの準備
		try {
			// キャプチャ開始
			_capture.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// カメラパラメータの計算(1mm=1.0)
		NyARGLUtil.toCameraFrustumRH(this._ar_param,1,10,10000,this._camera_projection);
		this._animator = new Animator(drawable);
		this._animator.start();
		return;
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		_gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		_gl.glViewport(0, 0, width, height);

		// 視体積の設定
		_gl.glMatrixMode(GL.GL_PROJECTION);
		_gl.glLoadIdentity();
		// 見る位置
		_gl.glMatrixMode(GL.GL_MODELVIEW);
		_gl.glLoadIdentity();
	}

	private boolean _is_marker_exist=false;
	private NyARTransMatResult __display_transmat_result = new NyARTransMatResult();

	private double[] __display_wk = new double[16];

	public void display(GLAutoDrawable drawable)
	{
		NyARTransMatResult transmat_result = __display_transmat_result;
		if (!_cap_image.hasBuffer()) {
			return;
		}
		// 背景を書く
		this._gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear the buffers for new frame.
		try{
			synchronized(this._sync_object){
				NyARGLDrawUtil.drawBackGround(this._gl,this._cap_image, 1.0);			
				// マーカーがあれば、立方体を描画
				if (this._is_marker_exist){
					// マーカーの一致度を調査するならば、ここでnya.getConfidence()で一致度を調べて下さい。
					// Projection transformation.
					this._gl.glMatrixMode(GL.GL_PROJECTION);
					this._gl.glLoadMatrixd(_camera_projection, 0);
					this._gl.glMatrixMode(GL.GL_MODELVIEW);
					// Viewing transformation.
					this._gl.glLoadIdentity();
					// 変換行列を取得
					this._nya.getTransmationMatrix(transmat_result);
					// 変換行列をOpenGL形式に変換
					NyARGLUtil.toCameraViewRH(transmat_result,1, this.__display_wk);
					this._gl.glLoadMatrixd(this.__display_wk, 0);		
					//立方体を描画
					this._gl.glPushMatrix(); // Save world coordinate system.
					this._gl.glTranslatef(0.0f, 0.1f,20); // Place base of cube on marker surface.
					this._gl.glDisable(GL.GL_LIGHTING); // Just use colours.
					NyARGLDrawUtil.drawColorCube(this._gl,40);
					this._gl.glPopMatrix(); // Restore world coordinate system.
				}
			}
			Thread.sleep(1);// タスク実行権限を一旦渡す
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public void onUpdateBuffer(Buffer i_buffer)
	{
		try {
			synchronized (this._sync_object) {
				this._cap_image.setBuffer(i_buffer);
				this._is_marker_exist =this._nya.detectMarkerLite(this._cap_image, 110);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{
	}

	private final static String CARCODE_FILE = "../../Data/patt.hiro";

	private final static String PARAM_FILE = "../../Data/camera_para.dat";

	public static void main(String[] args)
	{
		try {
			NyARParam param = new NyARParam();
			param.loadARParamFromFile(PARAM_FILE);
			param.changeScreenSize(SCREEN_X, SCREEN_Y);

			NyARCode code = new NyARCode(16, 16);
			code.loadARPattFromFile(CARCODE_FILE);

			new JavaSimpleLite(param, code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
}
