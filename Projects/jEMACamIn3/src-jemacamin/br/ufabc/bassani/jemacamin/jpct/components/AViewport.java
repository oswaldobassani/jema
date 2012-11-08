package br.ufabc.bassani.jemacamin.jpct.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public abstract class AViewport extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2560930614540730952L;

	/* - Tamanha da tela e painel */
	protected int pWidth, pHeight;
	
	/* - 3D - Fabrica de objetos básicos do mundo */
	protected RenderFactory renderFactory;
	
	/* - 3D - Variáveis de execução */
	protected float lastFPS = -1;
	protected int countFrames = 0;
	protected long lastRenderTime = -1;
	
	protected AViewport(){
		pWidth = 640;
	    pHeight = 480;
		renderFactory = new RenderFactory(pWidth, pHeight);
		
		/* - 3D - */
		setPreferredSize(new Dimension(pWidth, pHeight));
		setMaximumSize(new Dimension(pWidth, pHeight));
		setMinimumSize(new Dimension(pWidth, pHeight));
		
		init();
		
		/* - 3D - Tarefa de renderização/update  */
		new Thread()
		{
			public void run()
			{
				for(; ;)
				{
					try{
						//FIXME: Determina o FPS
						Thread.sleep(30);
						
						/*
						 * Gus com Rotacao e Textura
						 * Sleep -> FPS
						 *     0 -> ~157
						 *    10 ->  ~87
						 *    50 ->  ~19
						 */
					}catch(Exception e) {}
					repaint();
				}
			}
		}.start();
	}
	
	protected abstract void init();
	
	/* - 3D - */
	
	/**
	 * Draws the model, along with any cursors, controls, etc,
	 * onto the graphics context.
	 */
	public synchronized void paintComponent(Graphics g)
	{
		renderFrame();
		displayFrame(g);		
	}

	/**
	 * Preenche o fundo com uma cor.
	 * 
	 * @param g
	 * @param width
	 * @param height
	 */
	public void drawBackground_FillColor(Graphics g, int width, int height)
	{
		g.setColor(new Color(0x00,0x25,0x0a));
		g.fillRect(0, 0, width, height);
	}
	
	/**
	 * Gria um grid na tela.
	 * 
	 * @param g
	 * @param width
	 * @param height
	 */
	public void drawBackground_DrawGridLines(Graphics g, int width, int height)
	{
		g.setColor(new Color(0x00,0x35,0x0e));
		//Draw the vertical lines.
		int i = 0,num = (width / 32) + 1;
		while(i<num)
		{
			g.drawLine(i * 32,0, i * 32,height);
			i++;
		}
		//Draw the horizontal lines.
		i = 0; num = (height / 32) + 1;
		while(i<num)
		{
			g.drawLine(0,i * 32, width,i * 32);
			i++;
		}
		//Draw origin crosshair
		g.setColor(Color.GRAY);
		g.drawLine(width/2, height/2-5, width/2, height/2+5);
		g.drawLine(width/2-5, height/2, width/2+5, height/2);
	}
	
	/**
	 * Renders the model onto the framebuffer.
	 */
	public abstract void renderFrame();

	/**
	 * Draws the framebuffer to the graphics context.
	 * 
	 * @param g graphics context.
	 */
	public void displayFrame(Graphics g)
	{
		renderFactory.frameBuffer.display(g);
	}
	
	/* - JPanel - Override de metodos básicos */
	
	@Override
	public int getHeight() {
		return pHeight;
	}

	@Override
	public int getWidth() {
		return pWidth;
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
		return new Dimension(pWidth, pHeight);
	}
	
}
